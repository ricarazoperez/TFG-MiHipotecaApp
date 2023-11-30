package es.MiHipotecaApp.TFG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RecuperarContra extends AppCompatActivity {

    private Button btn_recuperar_contra;
    private EditText correo;
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_contra);
        initUI();
    }

    private void initUI(){
        correo = findViewById(R.id.edit_correo_recuperar_contra);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(this,R.id.edit_correo_recuperar_contra, Patterns.EMAIL_ADDRESS,R.string.correo_incorrecto);
        btn_recuperar_contra = findViewById(R.id.btn_recuperar_contrasenia);

        btn_recuperar_contra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (awesomeValidation.validate()) resetPassword();
                else correo.setError(getString(R.string.formato_correo_incorrecto));
            }
        });
    }

    private void resetPassword(){
        FirebaseAuth.getInstance().sendPasswordResetEmail(correo.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RecuperarContra.this, getString(R.string.mensaje_enviado_recuperar_contra), Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(RecuperarContra.this, MainActivity.class);
                    startActivity(i);
                } else {
                    Toast.makeText(RecuperarContra.this, getString(R.string.error_enviar_mensaje_recuperar_contra), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}