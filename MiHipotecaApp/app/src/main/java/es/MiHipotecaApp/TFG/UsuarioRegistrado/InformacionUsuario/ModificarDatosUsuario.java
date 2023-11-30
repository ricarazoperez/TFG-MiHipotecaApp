package es.MiHipotecaApp.TFG.UsuarioRegistrado.InformacionUsuario;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.MiHipotecaApp.TFG.R;

public class ModificarDatosUsuario extends AppCompatActivity implements custom_dialog_avatares.customDialogInterface {
    private final String TAG = "OBTENCION USUARIO";
    private EditText et_nombre, et_pass, et_confPass;
    private ImageView iv_cambio_avatar;
    private ImageView imagenPerfil;
    private Button btn_aplicar_cambios;
    private Button btn_pasar_a_premium;
    private FirebaseFirestore db;
    private FirebaseAuth currentUser;
    private TextView correo_usuario;
    private StorageReference storageRefer;
    private Long imgPerfil;
    private CircleImageView close_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_modificar_datos_usuario);

        et_nombre           = findViewById(R.id.edit_nuevo_nombre_registro);
        et_pass             = findViewById(R.id.edit_contra_registro);
        et_confPass         = findViewById(R.id.edit_repetir_contra_registro);
        iv_cambio_avatar    = findViewById(R.id.btn_mod_image);
        imagenPerfil        = findViewById(R.id.imagenPerfil);
        btn_aplicar_cambios = findViewById(R.id.btn_aplicar_cambios);
        btn_pasar_a_premium = findViewById(R.id.btn_pasar_a_premium);
        correo_usuario      = findViewById(R.id.correo_electronico_usuario);
        close_icon = findViewById(R.id.close_icon_mod_perfil);

        getInfoUsuario();
        eventos();
    }
    public void eventos(){
        iv_cambio_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                custom_dialog_avatares custom = new custom_dialog_avatares();
                custom.show(getSupportFragmentManager(),"Custom Dialog");
            }
        });
        btn_aplicar_cambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modificarUsuarioBD();
            }
        });

        btn_pasar_a_premium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ModificarDatosUsuario.this, PasarPremium.class);
                startActivity(i);
            }
        });
        close_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish(); }
        });
    }
    public void modificarUsuarioBD(){
        String userMail = currentUser.getCurrentUser().getEmail();
        CollectionReference user  = db.collection("usuarios");
        Query query = user.whereEqualTo("correo", userMail);
        //Comprobamos si el campo nombre no está vacio
        if(!TextUtils.isEmpty(et_nombre.getText().toString())){
            if(!TextUtils.isEmpty(et_pass.getText().toString()) && (!TextUtils.isEmpty(et_confPass.getText().toString()))){
                if(contraseniasCoinciden()){
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("avatar" , imgPerfil);
                    updates.put("nombre" ,et_nombre.getText().toString());
                    updates.put("password" ,et_pass.getText().toString());
                    query.get().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            //Primer elemento que cumpla la condicion
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            //Obtener la referencia al documento
                            DocumentReference docRef = user.document(document.getId());
                            docRef.update(updates).addOnCompleteListener(updateTask ->{
                                if(updateTask.isSuccessful()){
                                    Toast.makeText(ModificarDatosUsuario.this, "Usuario modificado correctamente", Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(ModificarDatosUsuario.this, "Usuario no modificado", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else{
                            Log.d("Modificar Datos Usuario", "Error al obtener los documentos: ", task.getException());
                        }
                    });
                }

            }else{
                Map<String, Object> updates = new HashMap<>();
                updates.put("avatar" , imgPerfil);
                updates.put("nombre" ,et_nombre.getText().toString());
                query.get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        //Primer elemento que cumpla la condicion
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        //Obtener la referencia al documento
                        DocumentReference docRef = user.document(document.getId());
                        docRef.update(updates).addOnCompleteListener(updateTask ->{
                            if(updateTask.isSuccessful()){
                                Toast.makeText(ModificarDatosUsuario.this, "Usuario modificado correctamente", Toast.LENGTH_LONG).show();

                            }
                            else{
                                Toast.makeText(ModificarDatosUsuario.this, "Usuario no modificado", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    else{
                        Log.d("Modificar Datos Usuario", "Error al obtener los documentos: ", task.getException());
                    }
                });
            }

        }else{
            Map<String, Object> updates = new HashMap<>();
            updates.put("avatar" , imgPerfil);
            query.get().addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    //Primer elemento que cumpla la condicion
                    DocumentSnapshot document = task.getResult().getDocuments().get(0);
                    //Obtener la referencia al documento
                    DocumentReference docRef = user.document(document.getId());
                    docRef.update(updates).addOnCompleteListener(updateTask ->{
                        if(updateTask.isSuccessful()){
                            Toast.makeText(ModificarDatosUsuario.this, "Usuario modificado correctamente", Toast.LENGTH_LONG).show();

                        }
                        else{
                            Toast.makeText(ModificarDatosUsuario.this, "Usuario no modificado", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else{
                    Log.d("Modificar Datos Usuario", "Error al obtener los documentos: ", task.getException());
                }
            });
        }

    }
    public boolean contraseniasCoinciden(){
        String contra = et_pass.getText().toString();
        String confirm = et_confPass.getText().toString();
        return contra == confirm;
    }
    public void getInfoUsuario(){
        String userMail = currentUser.getCurrentUser().getEmail();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("usuarios").whereEqualTo("correo", userMail);


        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                    imgPerfil = document.getLong("avatar");
                    setImagenPerfil(imgPerfil.intValue());

                    //Pone el correo electronico del usuario
                    correo_usuario.setText(document.getString("correo"));

                    //Pone el nombre del usuario en el campo del formulario
                    et_nombre.setText(document.getString("nombre"));

                    //Cambia el texto del boton de "pasar a premium" a "ver planes" en funcion de si el usuario es premium o no
                    if(document.getBoolean("premium"))
                        btn_pasar_a_premium.setText("VER TU PLAN ACTUAL");

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

    }
    public void setImagenPerfil(int avatar){
        switch (avatar) {
            case 1:
                imagenPerfil.setImageResource(R.drawable.avatar1);

                break;
            case 2:
                imagenPerfil.setImageResource(R.drawable.avatar2);
                break;
            case 3:
                imagenPerfil.setImageResource(R.drawable.avatar3);
                break;
            case 4:
                imagenPerfil.setImageResource(R.drawable.avatar4);
                break;
            default:
                imagenPerfil.setImageResource(R.drawable.avatar5);
        }

    }

    @Override
    public void setAvatares(int avatar) {
        Log.e("Modificar datos usuario", ""+ avatar);
        switch (avatar){
            case 1:
                imagenPerfil.setImageResource(R.drawable.avatar1);

                break;
            case 2:
                imagenPerfil.setImageResource(R.drawable.avatar2);
                break;
            case 3:
                imagenPerfil.setImageResource(R.drawable.avatar3);
                break;
            case 4:
                imagenPerfil.setImageResource(R.drawable.avatar4);
                break;
            default:
                imagenPerfil.setImageResource(R.drawable.avatar5);
        }
        imgPerfil = Long.valueOf(avatar);

    }
    private void ElegirImagenPerfil()
    {
        //crear una instancia que haga referencia al almacenamiento
        storageRefer=FirebaseStorage.getInstance().getReference();
        //String archive="profile_"+

    }
}