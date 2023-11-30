package es.MiHipotecaApp.TFG;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import de.hdodenhof.circleimageview.CircleImageView;

public class TerminosCondiciones extends AppCompatActivity {

    private CircleImageView close_icon_terminos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminos_condiciones);
        close_icon_terminos = findViewById(R.id.close_icon_terminos);

        close_icon_terminos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { finish(); }
        });
    }
}