package es.MiHipotecaApp.TFG.UsuarioRegistrado.HipotecasSeguimiento;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.MiHipotecaApp.TFG.R;
import es.MiHipotecaApp.TFG.SimularHipoteca.RecyclerAdapter;
import es.MiHipotecaApp.TFG.Transfers.Oferta;

public class TusOfertas extends AppCompatActivity implements RecyclerAdapter.actualizarInter {
    private RecyclerView rvLista;
    private RecyclerAdapter adapter;
    private List<Oferta> ofertasFija = new ArrayList<>();
    private List<Oferta> ofertasVarMix = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private Button btn_fijas, btn_varMix;
    private TusOfertas tusOfertas;
    private CircleImageView closeIcon;
    private TextView txt_noOfertas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tus_ofertas);
        rvLista = findViewById(R.id.recylcer_tusOfertas);
        btn_fijas = findViewById(R.id.buttonFijas);
        btn_varMix = findViewById(R.id.buttonVariablesMixta);
        closeIcon = findViewById(R.id.close_icon);
        db   = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        tusOfertas = this;
        cargarOfertas();
        eventos();
        LinearLayoutManager manager = new LinearLayoutManager(TusOfertas.this);
        rvLista.setLayoutManager(manager);
        Intent intent = getIntent();
        if(intent.hasExtra("tipo")){
            String jsonStr = intent.getStringExtra("tipo");
            if(jsonStr.equals("fija")){
                adapter = new RecyclerAdapter(ofertasFija,"fija",this);
                rvLista.setAdapter(adapter);
                btn_fijas.setAlpha(0.5f);
            }else{
                adapter = new RecyclerAdapter(ofertasVarMix,"varMix",this);
                rvLista.setAdapter(adapter);
                btn_varMix.setAlpha(0.5f);
            }
        }else{
            adapter = new RecyclerAdapter(ofertasFija,"fija",this);
            rvLista.setAdapter(adapter);
            btn_fijas.setAlpha(0.5f);
        }
        txt_noOfertas = findViewById(R.id.txt_infoNoOfer);
    }

    private void eventos() {
        btn_fijas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter = new RecyclerAdapter(ofertasFija,"fija",tusOfertas);
                rvLista.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                btn_fijas.setAlpha(0.5f);
                btn_varMix.setAlpha(1f);             }
        });

        btn_varMix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter = new RecyclerAdapter(ofertasVarMix,"varMix",tusOfertas);
                rvLista.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                btn_varMix.setAlpha(0.5f);
                btn_fijas.setAlpha(1f);
            }
        });
        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });
    }

    private void cargarOfertas() {
        String uid = user.getUid();
        CollectionReference ofertasRef = db.collection("ofertas_guardadas");
        // Crear una consulta para obtener los documentos con el UID del usuario
        Query query = ofertasRef.whereEqualTo("idUser", uid);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                    Log.d("DETALLES", "entra");
                    String banco = documentSnapshot.getString("banco");
                    String desc = documentSnapshot.getString("desc");
                    String tipo = documentSnapshot.getString("tipo");
                    String tae = documentSnapshot.getString("tae");
                    String vinculaciones = documentSnapshot.getString("vinculaciones");
                    String nombre = documentSnapshot.getString("nombreOferta");
                    Oferta o;
                    if(tipo.equals("fija")){
                        String cuota = documentSnapshot.getString("cuota");
                        String tin = documentSnapshot.getString("tin");
                        o = new Oferta(banco,desc,tin,tae,cuota,vinculaciones);
                        o.setNombre(nombre);
                        ofertasFija.add(o);
                    }
                    else{
                        String cuota_x = documentSnapshot.getString("couta_x");
                        String cuota_resto = documentSnapshot.getString("cuota_resto");
                        String tin_x = documentSnapshot.getString("tin_x_anios");
                        String tin_resto = documentSnapshot.getString("tin_resto");
                        o = new Oferta(banco,desc,tin_x,tin_resto,tae,cuota_x,cuota_resto,vinculaciones);
                        o.setNombre(nombre);
                        ofertasVarMix.add(o);
                    }

                }
                if(queryDocumentSnapshots.getDocuments().isEmpty())txt_noOfertas.setVisibility(View.VISIBLE);

                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    @Override
    public void actualizar(String tipo) {
        Intent intent = new Intent(this, TusOfertas.class);
        finish();
        intent.putExtra("tipo", tipo);
        startActivity(intent);
    }
}