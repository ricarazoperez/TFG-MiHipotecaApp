package es.MiHipotecaApp.TFG.UsuarioRegistrado.InformacionUsuario;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import de.hdodenhof.circleimageview.CircleImageView;
import es.MiHipotecaApp.TFG.MainActivity;
import es.MiHipotecaApp.TFG.R;

public class InfoPerfilUsuario extends Fragment {

    private LinearLayout eliminar_cuenta;
    private LinearLayout modificar_datos;
    private LinearLayout informar_problema;
    private LinearLayout pasar_a_premium;
    private LinearLayout pol_privacidad;
    private LinearLayout acerca_de;
    private LinearLayout cerrar_sesion;
    private FirebaseAuth firebaseAuth;
    private CircleImageView imagen_perfil;
    private Long imgPerfil;


    public InfoPerfilUsuario(){

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();

        View view = inflater.inflate(R.layout.fragment_info_perfil_usuario, container, false);
        initUI(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getAvatar();
    }

    private void initUI(View view) {
        eliminar_cuenta = view.findViewById(R.id.eliminar_cuenta);
        modificar_datos = view.findViewById(R.id.btn_modificar_datos);
        informar_problema = view.findViewById(R.id.btn_notificar_problema);
        pasar_a_premium = view.findViewById(R.id.btn_pasar_a_premium);
        cerrar_sesion = view.findViewById(R.id.cerrar_sesion);
        imagen_perfil = view.findViewById(R.id.imagen_perfil_usuario);
        pol_privacidad = view.findViewById(R.id.btn_pol_privacidad);
        acerca_de = view.findViewById(R.id.btn_acercaDe);
        eliminar_cuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialogo = new AlertDialog.Builder(getActivity())
                        .setPositiveButton(getString(R.string.si_eliminar_cuenta), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                eliminarUsuario();
                                //Redirigir a iniciar sesion
                            }
                        })
                        .setNegativeButton(getString(R.string.no_eliminar_cuenta), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setTitle("ELIMINAR CUENTA").setMessage("¿Desea eliminar la cuenta?").create();
                dialogo.show();
            }
        });

        modificar_datos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), ModificarDatosUsuario.class);
                startActivity(i);
            }
        });

        informar_problema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), InformarDeUnProblema.class);
                startActivity(i);
            }
        });

        pasar_a_premium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), PasarPremium.class);
                startActivity(i);
            }
        });
        cerrar_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.sesion_cerrada), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        pol_privacidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity().getApplicationContext(), PoliticaPrivacidad.class);
                startActivity(i);
            }
        });
        acerca_de.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity().getApplicationContext(), AcercaDe.class);
                startActivity(i);
            }
        });
    }

    public void getAvatar(){
        String userMail = firebaseAuth.getCurrentUser().getEmail();
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
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

    }

    public void setImagenPerfil(int avatar){
        switch (avatar) {
            case 1:
                imagen_perfil.setImageResource(R.drawable.avatar1);

                break;
            case 2:
                imagen_perfil.setImageResource(R.drawable.avatar2);
                break;
            case 3:
                imagen_perfil.setImageResource(R.drawable.avatar3);
                break;
            case 4:
                imagen_perfil.setImageResource(R.drawable.avatar4);
                break;
            default:
                imagen_perfil.setImageResource(R.drawable.avatar5);
        }

    }

    public void eliminarUsuario(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usuario = db.collection("usuarios");
        Query query = usuario.whereEqualTo("correo", firebaseAuth.getCurrentUser().getEmail());


        CollectionReference hipotecasRef = db.collection("hipotecas_seguimiento");
        Query query_hipoteca = hipotecasRef.whereEqualTo("idUsuario", firebaseAuth.getCurrentUser().getUid());
        query_hipoteca.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document : queryDocumentSnapshots) {
                    document.getReference().delete();
                }
                CollectionReference amortizacionesRef = db.collection("amortizaciones_anticipadas");
                Query query_amort = amortizacionesRef.whereEqualTo("idUsuario", firebaseAuth.getCurrentUser().getUid());
                query_amort.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                            document.getReference().delete();
                        }
                        CollectionReference ofertasRef = db.collection("ofertas_guardadas");
                        Query query_ofertas = ofertasRef.whereEqualTo("idUser", firebaseAuth.getCurrentUser().getUid());
                        query_ofertas.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                for (DocumentSnapshot document : queryDocumentSnapshots) {
                                    document.getReference().delete();
                                }
                                //FirebaseAuth.getInstance().signOut();

                                FirebaseAuth.getInstance().getCurrentUser().delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {


                                                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                QuerySnapshot querySnapshot = task.getResult();
                                                                if (!querySnapshot.isEmpty()) {
                                                                    DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                                                                    documentSnapshot.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void aVoid) {
                                                                                    // Documento eliminado correctamente
                                                                                    Toast.makeText(getActivity(), getString(R.string.usuario_borrado_exito), Toast.LENGTH_LONG).show();
                                                                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                                    startActivity(intent);
                                                                                    getActivity().finish();
                                                                                }
                                                                            })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {
                                                                                    // Error al eliminar el documento
                                                                                    Log.e("ERROR", " al eliminar usuario", e);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        }
                                                    });
                                                }else {
                                                    Log.e("ERROR", " al eliminar usuario", task.getException());

                                                }
                                            }
                                        });



                            }
                        });
                    }
                });
            }
        });




    }

}