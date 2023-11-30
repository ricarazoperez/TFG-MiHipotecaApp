package es.MiHipotecaApp.TFG.SimularHipoteca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import de.hdodenhof.circleimageview.CircleImageView;
import es.MiHipotecaApp.TFG.R;
import es.MiHipotecaApp.TFG.Transfers.Oferta;
import es.MiHipotecaApp.TFG.UsuarioRegistrado.HipotecasSeguimiento.TusHipotecas;
import es.MiHipotecaApp.TFG.UsuarioRegistrado.custom_dialog_oferta;
import es.MiHipotecaApp.TFG.VolleySingleton;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MostrarOfertas extends AppCompatActivity implements custom_dialog_oferta.pasarDatos {

    Context context;
    private RecyclerView rvLista;
    private RecyclerAdapter adapter;
    private List<Oferta> lista_fija = new ArrayList<>();
    private List<Oferta> lista_varMix = new ArrayList<>();
    private Button btn_fijas;
    private Button btn_varMix;
    private Button btn_orderTae;
    private Button btn_orderCuota;
    private TextView tvEspera;
    private TextView txt_ordenar;
    private JSONObject datos;
    private  boolean detalles;

    private Spinner sp_bancos;
    private TextView txt_filtrarBancos;
    private Switch switchBusqueda;
    private boolean fija = true;
    private FirebaseFirestore db;

    private FirebaseAuth auth;
    private FirebaseUser user;

    private Handler handler = new Handler();
    private int currentIndex = 0;
    String URL_final;
    private String[] options = {"Obteniendo las mejores ofertas personalizadas", "Comparando con millones de posibilidades", "Esto podria llevar unos segundos..."};

    private CircleImageView close_icon_comparar_hip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_ofertas);
        context = this.getApplicationContext();
        db   = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        try {
            initGUI();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        URL_final = getIntent().getStringExtra("url");

        initValues();
        eventos();
    }


    private void initGUI() throws JSONException {
        rvLista = findViewById(R.id.recyclerOfertas);
        btn_fijas = findViewById(R.id.buttonFijas);
        btn_varMix = findViewById(R.id.buttonVariablesMixta);
        tvEspera = findViewById(R.id.tvEspera);
        txt_ordenar = findViewById(R.id.txt_ordernar);
        //Pasar los datos de una intent a otra
        Intent intent = getIntent();
        String jsonStr = intent.getStringExtra("datos");
        datos = new JSONObject(jsonStr);
        detalles = datos.getBoolean("detalles");
        sp_bancos = findViewById(R.id.sp_bancos);
        txt_filtrarBancos = findViewById(R.id.txt_filtrarBancos);
        switchBusqueda = findViewById(R.id.switchBusqueda);
        close_icon_comparar_hip = findViewById(R.id.close_icon_comparar_hip);
        btn_orderCuota = findViewById(R.id.btn_ordenarCuota);
        btn_orderTae = findViewById(R.id.btn_ordenarTae);
    }
    private void eventos(){

        btn_fijas.setAlpha(0.5f);
        btn_orderTae.setAlpha(0.5f);
        btn_orderCuota.setAlpha(1f);
        btn_fijas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter = new RecyclerAdapter(lista_fija,"fija",detalles,getSupportFragmentManager());
                rvLista.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                String[] bancosFija = bancosDiferentes(lista_fija);
                ArrayAdapter<String> adapterSpinner_fija = new ArrayAdapter(context, android.R.layout.simple_spinner_item, bancosFija);
                sp_bancos.setAdapter(adapterSpinner_fija);
                fija = true;
                btn_fijas.setAlpha(0.5f);
                btn_varMix.setAlpha(1f);

            }
        });

        btn_varMix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter = new RecyclerAdapter(lista_varMix,"varMix",detalles,getSupportFragmentManager());

                String[] bancosVarMixt = bancosDiferentes(lista_varMix);
                ArrayAdapter<String> adapterSpinner_varMix = new ArrayAdapter(context, android.R.layout.simple_spinner_item, bancosVarMixt);
                sp_bancos.setAdapter(adapterSpinner_varMix);
                rvLista.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                fija = false;
                btn_varMix.setAlpha(0.5f);
                btn_fijas.setAlpha(1f);
            }
        });
        btn_orderCuota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_orderCuota.setAlpha(0.5f);
                btn_orderTae.setAlpha(1f);
                //fijas
                for(Oferta oferta : lista_fija){
                    int euroIndex = oferta.getCuota().indexOf('€');
                    String numeroString = oferta.getCuota().substring(0, euroIndex).trim();
                    numeroString = numeroString.replace(".", "");
                    int numero = Integer.parseInt(numeroString);
                    oferta.setCuota_num(numero);
                }
                // Ordenar la lista en función de la cuota
                Collections.sort(lista_fija, new Comparator<Oferta>() {
                    public int compare(Oferta o1, Oferta o2) {
                        return Integer.compare(o1.getCuota_num(), o2.getCuota_num());
                    }
                });


                //varMix
                Pattern pattern = Pattern.compile("\\d+\\.?\\d*");
                for(Oferta oferta : lista_varMix){
                    Matcher matcher = pattern.matcher(oferta.getCuota_x());
                    if (matcher.find()) {
                        int cuota = Integer.parseInt(matcher.group());
                        oferta.setCuota_num(cuota);
                    }

                }
                // Ordenar la lista en función de la cuota
                Collections.sort(lista_varMix, new Comparator<Oferta>() {
                    public int compare(Oferta o1, Oferta o2) {
                        return Integer.compare(o1.getCuota_num(), o2.getCuota_num());
                    }
                });
                adapter.notifyDataSetChanged();
            }
        });

        btn_orderTae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_orderTae.setAlpha(0.5f);
                btn_orderCuota.setAlpha(1f);
                //fijas
                for(Oferta oferta : lista_fija){
                    int porcentajeIndex = oferta.getTae().indexOf('%');
                    String numeroString = oferta.getTae().substring(0, porcentajeIndex).trim();
                    numeroString = numeroString.replace(',', '.');
                    Double numero = Double.parseDouble(numeroString);
                    oferta.setTae_num(numero);
                }
                // Ordenar la lista en función de la cuota
                Collections.sort(lista_fija, new Comparator<Oferta>() {
                    public int compare(Oferta o1, Oferta o2) {
                        return Double.compare(o1.getTae_num(), o2.getTae_num());
                    }
                });

                //varMix
                for(Oferta oferta : lista_varMix){
                    int porcentajeIndex = oferta.getTae().indexOf('%');
                    String numeroString = oferta.getTae().substring(0, porcentajeIndex).trim();
                    numeroString = numeroString.replace(',', '.');
                    Double numero = Double.parseDouble(numeroString);
                    oferta.setTae_num(numero);
                }
                // Ordenar la lista en función de la cuota
                Collections.sort(lista_varMix, new Comparator<Oferta>() {
                    public int compare(Oferta o1, Oferta o2) {
                        return Double.compare(o1.getTae_num(), o2.getTae_num());
                    }
                });
                adapter.notifyDataSetChanged();
            }
        });
        switchBusqueda.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Aquí es donde puedes hacer algo con el estado del switch
                if (isChecked) {
                    //el switch está activado
                    if(fija){
                        String[] bancosFija = bancosDiferentes(lista_fija);
                        ArrayAdapter<String> adapterSpinner_fija = new ArrayAdapter(context, android.R.layout.simple_spinner_item, bancosFija){
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                ((TextView) view).setTextColor(getResources().getColor(R.color.grey_color));
                                return view;
                            }    @Override
                            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                View view = super.getDropDownView(position, convertView, parent);
                                ((TextView) view).setTextColor(getResources().getColor(R.color.background_aplicacion));
                                return view;
                            }
                        };
                        sp_bancos.setAdapter(adapterSpinner_fija);
                    }
                    else{
                        String[] bancosVarMixt = bancosDiferentes(lista_varMix);
                        ArrayAdapter<String> adapterSpinner_varMix = new ArrayAdapter(context, android.R.layout.simple_spinner_item, bancosVarMixt){
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                ((TextView) view).setTextColor(getResources().getColor(R.color.grey_color));
                                return view;
                            }    @Override
                            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                View view = super.getDropDownView(position, convertView, parent);
                                ((TextView) view).setTextColor(getResources().getColor(R.color.background_aplicacion));
                                return view;
                            }
                        };
                        sp_bancos.setAdapter(adapterSpinner_varMix);
                    }
                    sp_bancos.setVisibility(View.VISIBLE);

                } else {
                    sp_bancos.setVisibility(View.GONE);
                    if (fija){
                        adapter = new RecyclerAdapter(lista_fija,"fija",detalles,getSupportFragmentManager());
                        rvLista.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }else{
                        adapter = new RecyclerAdapter(lista_varMix,"varMix",detalles,getSupportFragmentManager());
                        rvLista.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
        sp_bancos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                List<Oferta> cuentasFiltradas = new ArrayList<>();
                String bancoSeleccionado = (String) adapterView.getItemAtPosition(i);
                if (fija) {
                    for(Oferta o : lista_fija){
                        if (o.getBanco().equals(bancoSeleccionado)) cuentasFiltradas.add(o);
                    }
                    adapter = new RecyclerAdapter(cuentasFiltradas,"fija",detalles,getSupportFragmentManager());
                }
                else{
                    for(Oferta o : lista_varMix){
                        if (o.getBanco().equals(bancoSeleccionado)) cuentasFiltradas.add(o);
                    }
                    adapter = new RecyclerAdapter(cuentasFiltradas,"varMix",detalles,getSupportFragmentManager());
                }
                rvLista.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        close_icon_comparar_hip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        handler.postDelayed(runnable, 10000);

    }
    private void initValues() {
        LinearLayoutManager manager = new LinearLayoutManager(MostrarOfertas.this);
        rvLista.setLayoutManager(manager);
        try {
            obtenerDatosBancos();
        }catch (Exception e){}
        Log.d("DETALLES", String.valueOf(detalles));
        if(detalles) adapter = new RecyclerAdapter(lista_fija,"fija",detalles,getSupportFragmentManager());
        else  adapter = new RecyclerAdapter(lista_fija,"fija",detalles,getSupportFragmentManager());
        rvLista.setAdapter(adapter);


    }
    private String[] bancosDiferentes(List<Oferta> lista){
        List<String> bancos = new ArrayList<>();
        String[] bancosFinal;
        for (int i = 0; i < lista.size(); i++){
            if (bancos.isEmpty()) bancos.add(lista.get(i).getBanco());
            else{
                if (!bancos.contains(lista.get(i).getBanco()))bancos.add(lista.get(i).getBanco());
            }
        }
        bancosFinal = bancos.toArray(new String[bancos.size()]);
        return bancosFinal;
    }

    private void obtenerDatosBancos() {
        String ip=context.getString(R.string.ip);
        Log.d("URL", URL_final);
        String url =ip+"/pruebaArray";
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL_final,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray mJsonArray = null;
                        try {
                            mJsonArray = response.getJSONArray("fija");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        cargarOfertasFija(mJsonArray);

                        try {
                            mJsonArray = response.getJSONArray("var_mixta");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        cargarOfertasVariableMixta(mJsonArray);

                        adapter.notifyDataSetChanged();
                        txt_filtrarBancos.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        btn_fijas.setVisibility(View.VISIBLE);
                        btn_varMix.setVisibility(View.VISIBLE);
                        btn_orderCuota.setVisibility(View.VISIBLE);
                        btn_orderTae.setVisibility(View.VISIBLE);
                        txt_ordenar.setVisibility(View.VISIBLE);
                        tvEspera.setVisibility(View.GONE);
                        switchBusqueda.setVisibility(View.VISIBLE);
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse!=null && error.networkResponse.statusCode == 200) {
                            // Manejar el código de respuesta 200 como una respuesta válida
                            JSONObject response = null;
                            try {
                                response = new JSONObject(new String(error.networkResponse.data, HttpHeaderParser.parseCharset(error.networkResponse.headers)));
                                // Procesar la respuesta aquí
                                JSONArray mJsonArray=null;
                                try {
                                    mJsonArray = response.getJSONArray("fija");
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                cargarOfertasFija(mJsonArray);
                                try {
                                    mJsonArray = response.getJSONArray("var_mixta");
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                                cargarOfertasVariableMixta(mJsonArray);

                            } catch (JSONException | UnsupportedEncodingException e) {
                                e.printStackTrace();
                                // Manejar cualquier error de análisis aquí
                            }
                            adapter.notifyDataSetChanged();
                            txt_filtrarBancos.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            btn_orderCuota.setVisibility(View.VISIBLE);
                            btn_orderTae.setVisibility(View.VISIBLE);
                            txt_ordenar.setVisibility(View.VISIBLE);
                            btn_fijas.setVisibility(View.VISIBLE);
                            btn_varMix.setVisibility(View.VISIBLE);
                            tvEspera.setVisibility(View.GONE);
                            switchBusqueda.setVisibility(View.VISIBLE);

                        } else {
                            Intent intent = new Intent(MostrarOfertas.this, TusHipotecas.class);
                            finish();
                            startActivity(intent);
                            Toast.makeText(MostrarOfertas.this, "Ha ocurrido un problema al conectar con el servidor", Toast.LENGTH_LONG).show();
                            Log.d("PETICIONES", error.toString());
                        }
                    }
        }
        );
        request.setRetryPolicy(new DefaultRetryPolicy(
                300000, // segundos
                0, // 1 reintentos
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(request);

    }

    private void cargarOfertasVariableMixta(JSONArray mJsonArray) {
        for (int i = 0; i < mJsonArray.length(); i++) {
            try {
                JSONObject jsonObject = mJsonArray.getJSONObject(i);
                String banco = jsonObject.getString("banco");
                String desc = jsonObject.getString("desc");
                String tin_x_anios = jsonObject.getString("tin_x_anios");
                String tin_resto = jsonObject.getString("tin_resto");
                String tae = jsonObject.getString("tae");
                String cuota_x_anios = jsonObject.getString("cuota_x_anios");
                String cuota_resto = jsonObject.getString("cuota_resto");
                if(detalles){
                    String vinculaciones = jsonObject.getString("vinculaciones");
                    Oferta oferta = new Oferta(banco,desc,tin_x_anios,tin_resto,tae,cuota_x_anios,cuota_resto,vinculaciones);
                    lista_varMix.add(oferta);
                }
                else{
                    Oferta oferta = new Oferta(banco,desc,tin_x_anios,tin_resto,tae,cuota_x_anios,cuota_resto);
                    lista_varMix.add(oferta);
                }


            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void cargarOfertasFija(JSONArray mJsonArray){


        for (int i = 0; i < mJsonArray.length(); i++) {
            try {
                JSONObject jsonObject = mJsonArray.getJSONObject(i);
                String banco = jsonObject.getString("banco");
                String desc = jsonObject.getString("desc");
                String tin = jsonObject.getString("tin");
                String tae = jsonObject.getString("tae");
                String cuota = jsonObject.getString("cuota");

                if(detalles){
                    String vinculaciones = jsonObject.getString("vinculaciones");
                    Oferta oferta = new Oferta(banco,desc,tin,tae,cuota,vinculaciones);
                    lista_fija.add(oferta);
                }
                else{
                    Oferta oferta = new Oferta(banco,desc,tin,tae,cuota);
                    lista_fija.add(oferta);
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Cambiamos el texto del TextView
            tvEspera.setText(options[currentIndex]);
            tvEspera.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            currentIndex = (currentIndex + 1) % options.length;

            // Volvemos a ejecutar la tarea después de 10 segundos
            handler.postDelayed(this, 7000);
        }
    };


    @Override
    public void pasarNombre(String nombre, RecyclerAdapter.RecyclerHolder holder, Oferta oferta,String tipo) {
        CollectionReference ofertasRef = db.collection("ofertas_guardadas");
        Query query = ofertasRef.whereEqualTo("nombreOferta" , nombre);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (!querySnapshot.isEmpty()) {
                    Toast.makeText(this,"El nombre de la oferta ya existe" ,Toast.LENGTH_SHORT).show();
                    System.out.println("El documento existe");
                    Log.w("GUARDAR","Documento Existente");
                } else {
                    String uid = user.getUid();
                    Map<String, Object> o = new HashMap<>();
                    o.put("idUser", uid);
                    o.put("banco", oferta.getBanco());
                    o.put("desc", oferta.getDesc());
                    o.put("tae", oferta.getTae());
                    o.put("nombreOferta" , nombre);
                    if(tipo.equals("fija")){
                        o.put("tin", oferta.getTin());
                        o.put("cuota", oferta.getCuota());
                        o.put("tipo","fija");
                    }
                    else{
                        o.put("tipo","varMixta");
                        o.put("tin_x_anios", oferta.getTin_x());
                        o.put("tin_resto", oferta.getTin_resto());
                        o.put("couta_x", oferta.getCuota_x());
                        o.put("cuota_resto", oferta.getCuota_resto());
                    }
                    if(detalles){
                        o.put("vinculaciones", oferta.getVinculaciones());
                    }
                    else o.put("vinculaciones" , "");
                    db.collection("ofertas_guardadas").add(o).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                            //Toast.makeText(MostrarOfertas., "Oferta guardada correctamente!", Toast.LENGTH_LONG).show();
                            Log.w("GUARDAR","Exito al guardar");
                            oferta.setGuardada(true);
                            holder.btn_guardar.setVisibility(View.GONE);
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("GUARDAR","Error al guardar oferta en Firestore: ");
                        }
                    });
                }
            } else {
                // Ha ocurrido un error al intentar acceder a la colección
                System.out.println("Error al acceder a la colección: " + task.getException());
            }
        });


    }
}



