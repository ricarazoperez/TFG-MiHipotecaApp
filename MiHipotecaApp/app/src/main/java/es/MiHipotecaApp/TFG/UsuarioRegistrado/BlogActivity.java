package es.MiHipotecaApp.TFG.UsuarioRegistrado;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.MiHipotecaApp.TFG.R;
import es.MiHipotecaApp.TFG.Transfers.Noticia;

public class BlogActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Noticia> noticias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Aquí puedes obtener las noticias de una base de datos o de un servidor
        // Por ejemplo, aquí se agregan noticias de ejemplo:
        noticias = new ArrayList<>();
        noticias.add(new Noticia("Noticia 1", "Descripción de la noticia 1"));
        noticias.add(new Noticia("Noticia 2", "Descripción de la noticia 2"));
        noticias.add(new Noticia("Noticia 3", "Descripción de la noticia 3"));

        BlogAdapter adapter = new BlogAdapter(noticias);
        recyclerView.setAdapter(adapter);
    }
}
