package es.MiHipotecaApp.TFG.ChatUsuarios;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.MiHipotecaApp.TFG.R;
import es.MiHipotecaApp.TFG.Transfers.Message;

public class ChatActivity extends AppCompatActivity {
    private final String TAG = "CHAT ACTIVITY";
    private FirebaseFirestore db;
    private EditText editTextMessage;
    private Button buttonSend;
    private RecyclerView recyclerViewMessages;
    private MessageListAdapter messageListAdapter;
    private List<Message> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize views
        editTextMessage = findViewById(R.id.messageInput);
        buttonSend = findViewById(R.id.sendButton);
        recyclerViewMessages = findViewById(R.id.messageRecyclerView);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize message list adapter and set it to RecyclerView
        messageListAdapter = new MessageListAdapter(messages);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageListAdapter);

        // Load messages from Firestore
        loadMessages();

        // Set click listener for send button
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get current user
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    Toast.makeText(ChatActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get message text
                String text = editTextMessage.getText().toString().trim();
                if (TextUtils.isEmpty(text)) {
                    return;
                }

                // Send message to Firestore
                sendMessage(text, currentUser.getDisplayName());

                // Clear message input
                editTextMessage.setText("");
            }
        });
    }

    private void sendMessage(String text, String sender) {
        Message message = new Message(text, sender, new Date());
        db.collection("messages").add(message)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Message sent: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error sending message", e);
                    }
                });
    }

    private void loadMessages() {
        db.collection("messages").orderBy("sentAt").limit(100)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent( QuerySnapshot value, FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e(TAG, "Error getting messages", error);
                            return;
                        }

                        messages.clear();
                        for (QueryDocumentSnapshot document : value) {
                            Message message = document.toObject(Message.class);
                            messages.add(message);
                        }
                        messageListAdapter.notifyDataSetChanged();
                    }
                });
    }
}
