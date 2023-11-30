package es.MiHipotecaApp.TFG;



import android.content.Context;

import android.util.Log;


import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;



import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import es.MiHipotecaApp.TFG.Transfers.Euribor;

public class EuriborMens extends Worker {


    RequestQueue requestQueue;

    private FirebaseFirestore db;

    Context context;

    private final String TAG = "EURIBOR ACTIVITY";

    public EuriborMens(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        requestQueue = Volley.newRequestQueue( context,new HurlStack());
        db = FirebaseFirestore.getInstance();
        this.context=context;
    }

    @NonNull
    @Override
    public Result doWork() {
            ActualizarEuribor();
        return Result.success() ;
    }
    private void  ActualizarEuribor(){
        String ip= context.getString(R.string.ip);
        String url = ip +"/Euribor";
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray mJsonArray = null;
                        try {
                            mJsonArray = response.getJSONArray("Eur_dos_ult_meses");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        for (int i = 0; i < mJsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = mJsonArray.getJSONObject(i);
                                String anio = jsonObject.getString("anio");
                                String mes = jsonObject.getString("mes");
                                Double valor = jsonObject.getDouble("valor");
                                Euribor eu=new Euribor(anio,mes,valor);
                                db.collection("euribor")
                                        .whereEqualTo("anio", anio)
                                        .whereEqualTo("mes", mes)
                                        .get()
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                QuerySnapshot querySnapshot = task.getResult();
                                                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                                    // El documento ya existe en la base de datos
                                                    Log.d(TAG, "El documento ya existe en la base de datos");
                                                } else {
                                                    // El documento no existe en la base de datos, agrégalo
                                                    db.collection("euribor_prueba")
                                                            .add(eu)
                                                            .addOnSuccessListener(documentReference -> {
                                                                Log.d(TAG, "euribor registrado con exito en Firestore");
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                Log.w(TAG, "Error al registrar euribor en Firestore", e);
                                                            });
                                                }
                                            } else {
                                                Log.w(TAG, "Error al consultar euribor en Firestore", task.getException());
                                            }
                                        });
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    }
                    }, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {


                        Log.d("PETICIONES", error.toString());
                    }
                }
        );
                request.setRetryPolicy(new DefaultRetryPolicy(
                60000, // segundos
                0, // 1 reintentos
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                VolleySingleton.getInstance(context).addToRequestQueue(request);
                }
        }




