package es.MiHipotecaApp.TFG.UsuarioRegistrado.InformacionUsuario;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import de.hdodenhof.circleimageview.CircleImageView;
import es.MiHipotecaApp.TFG.R;

public class PoliticaPrivacidad extends AppCompatActivity {

    private CircleImageView close_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_politica_privacidad);

        close_icon = findViewById(R.id.close_icon_pol_priv);

        close_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish(); }
        });
    }
}