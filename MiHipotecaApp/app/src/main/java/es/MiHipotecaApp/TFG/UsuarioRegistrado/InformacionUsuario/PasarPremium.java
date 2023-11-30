package es.MiHipotecaApp.TFG.UsuarioRegistrado.InformacionUsuario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.MiHipotecaApp.TFG.R;


public class PasarPremium extends AppCompatActivity {
    private final String TAG = "OBTENICION PREMIUM";

    private ViewFlipper vf_1;
    private ViewFlipper vf_2;
    private Button btn_pasar_premium;
    private TextView plan_actual;
    private TextView titulo_actividad;
    private TextView coste_premium_tachado;
    private LinearLayout linear_layout_precio;
    private FirebaseAuth currentUser;
    private FirebaseFirestore db;
    private boolean premium;
    private CircleImageView close_icon;

    private String pasate = "PASAR A PREMIUM";
    private String mejorar = "¿Desea mejorar su plan a premium?";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasar_premium);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance();

        btn_pasar_premium = findViewById(R.id.btn_pasar_premium);
        plan_actual = findViewById(R.id.plan_actual);
        titulo_actividad = findViewById(R.id.titulo_actividad);
        coste_premium_tachado = findViewById(R.id.coste_premium_tachado);
        linear_layout_precio = findViewById(R.id.linear_layout_precio);
        vf_1 = findViewById(R.id.view_flipper);
        vf_2 = findViewById(R.id.view_flipper2);
        close_icon = findViewById(R.id.close_icon_premium);

        premium = false;

        initUI();

    }

    private void initUI(){

        vf_1.setFlipInterval(3000);
        vf_1.startFlipping();
        vf_2.setFlipInterval(3000);
        vf_2.startFlipping();

        //Tacha el precio no rebajado
        coste_premium_tachado.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);


        String userMail = currentUser.getCurrentUser().getEmail();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("usuarios").whereEqualTo("correo", userMail);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                    if(document.getBoolean("premium")) {
                        plan_actual.setText("MiHipotecaApp Premium");
                        btn_pasar_premium.setText("CANCELAR SUSCRIPCIÓN");
                        titulo_actividad.setText("Tu plan premium");
                        linear_layout_precio.setVisibility(View.VISIBLE);
                        premium = true;
                        pasate = "Volver a MiHipotecaApp Free";
                        mejorar = "¿Desea volver a MiHipotecaApp Free?";
                    }

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
        btn_pasar_premium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialogo = new AlertDialog.Builder(PasarPremium.this)
                        .setPositiveButton(getString(R.string.si_eliminar_cuenta), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Map<String, Object> nuevosDatos = new HashMap<>();
                                nuevosDatos.put("premium", !premium);

                                CollectionReference usuariosRef = db.collection("usuarios");

                                Query query = usuariosRef.whereEqualTo("correo", currentUser.getCurrentUser().getEmail());

                                // ejecutar la consulta
                                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                // actualizar el documento con los nuevos valores
                                                usuariosRef.document(document.getId()).update(nuevosDatos);
                                                Toast.makeText(PasarPremium.this, "Su plan ha sido actualizado con éxito", Toast.LENGTH_LONG).show();
                                                finish();
                                            }
                                        } else {
                                            Log.d(TAG, "Error getting documents: ", task.getException());
                                        }
                                    }
                                });                            }
                        })
                        .setNegativeButton(getString(R.string.no_eliminar_cuenta), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setTitle(pasate).setMessage(mejorar).create();
                dialogo.show();
            }
        });


        close_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish(); }
        });
    }
    public void previousView(View v){
        vf_1.showPrevious();
        vf_2.showPrevious();
    }
    public void nextView(View v){
        vf_1.showNext();
        vf_2.showNext();
    }
}