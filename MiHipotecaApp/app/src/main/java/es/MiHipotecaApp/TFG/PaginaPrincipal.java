package es.MiHipotecaApp.TFG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import es.MiHipotecaApp.TFG.SimularHipoteca.CompararNuevaHipoteca;
import es.MiHipotecaApp.TFG.UsuarioRegistrado.CustomDialogoPremium;
import es.MiHipotecaApp.TFG.UsuarioRegistrado.HipotecasSeguimiento.NuevoSeguimiento;
import es.MiHipotecaApp.TFG.UsuarioRegistrado.HipotecasSeguimiento.TusHipotecas;
import es.MiHipotecaApp.TFG.UsuarioRegistrado.InformacionUsuario.InfoPerfilUsuario;

public class PaginaPrincipal extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "Pagina principal";

    private BottomNavigationView bottomNavigationView;
    private TusHipotecas tusHipotecasFragment           = new TusHipotecas();
    private InfoPerfilUsuario infoPerfilUsuarioFragment = new InfoPerfilUsuario();
    private CompararNuevaHipoteca compararHipotecaFragment           = new CompararNuevaHipoteca();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagina_principal);
        initUI();
    }

    private void initUI(){
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.mis_hipotecas);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.mis_hipotecas:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, tusHipotecasFragment).commit();
                return true;

            case R.id.aniadir_hipoteca:

                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser user = auth.getCurrentUser();
                String userMail = user.getEmail();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Query query = db.collection("usuarios").whereEqualTo("correo", userMail);

                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                            if(document.getBoolean("premium")) {
                                Intent i = new Intent(PaginaPrincipal.this, NuevoSeguimiento.class);
                                startActivity(i);
                            } else {

                                CollectionReference hipotecasRef = db.collection("hipotecas_seguimiento");
                                Query consultaHipotecasUsu = hipotecasRef.whereEqualTo("idUsuario", user.getUid());
                                consultaHipotecasUsu.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        int countHipotecas = queryDocumentSnapshots.getDocuments().size();
                                        if (countHipotecas < 1) {
                                            Intent i = new Intent(PaginaPrincipal.this, NuevoSeguimiento.class);
                                            startActivity(i);
                                        } else {
                                            //Toast.makeText(PaginaPrincipal.this, "ACTUALIZA A PREMIUM PARA TENER MAS HIPOTECAS DE SEGUIMIENTO", Toast.LENGTH_LONG).show();
                                            CustomDialogoPremium dialogo = new CustomDialogoPremium();
                                            dialogo.show(getSupportFragmentManager(), "dialogo");
                                        }
                                    }


                                });
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
                return true;

            case R.id.comparar_hipoteca:
                Intent j = new Intent(PaginaPrincipal.this, CompararNuevaHipoteca.class);
                startActivity(j);
                return true;

            case R.id.mi_perfil:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, infoPerfilUsuarioFragment).commit();
                return true;


        }
        return false;
    }

    @Override
    public void onBackPressed() {

    }
}