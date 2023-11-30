package es.MiHipotecaApp.TFG.Transfers;

import java.util.Date;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Message {
    private String text;
    private String sender;
    private Date sentAt;

    FirebaseAuth fb;
    FirebaseUser user;

    public Message() {}

    public Message(String text, String sender, Date sentAt) {
        this.text = text;
        this.sender = sender;
        this.sentAt = sentAt;
    }

    public String getText() {
        return text;
    }

    public String getSender() {
        return sender;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public boolean isSentByCurrentUser() {
        FirebaseUser currentUser= fb.getCurrentUser();
        return  currentUser != null && currentUser.getUid().equals(sender);
    }
}
