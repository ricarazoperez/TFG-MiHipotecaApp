package es.MiHipotecaApp.TFG.UsuarioRegistrado.HipotecasSeguimiento;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.skydoves.balloon.ArrowOrientation;
import com.skydoves.balloon.ArrowPositionRules;
import com.skydoves.balloon.Balloon;
import com.skydoves.balloon.BalloonAnimation;
import com.skydoves.balloon.BalloonSizeSpec;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.MiHipotecaApp.TFG.PaginaPrincipal;
import es.MiHipotecaApp.TFG.R;
import es.MiHipotecaApp.TFG.Transfers.HipotecaSegFija;
import es.MiHipotecaApp.TFG.Transfers.HipotecaSegMixta;
import es.MiHipotecaApp.TFG.Transfers.HipotecaSegVariable;
import es.MiHipotecaApp.TFG.Transfers.HipotecaSeguimiento;

public class EditarHipotecaSeguimiento extends AppCompatActivity {
    private final String TAG = "SEGUIMIENTO ACTIVITY";

    private HipotecaSeguimiento hip;
    //Campos fijos

    private CircleImageView closeIcon;
    private Spinner sp_bancos;

    private CheckBox check_parte_fija_variable;
    private EditText gastos_totales;

    private EditText vinculaciones_anio, vinculaciones_valor;
    private EditText nombre_hipoteca;

    private TextView txt_edit_hipoteca, txt_edit_comunidad, txt_edit_tipo_vivienda, txt_edit_antiguedad_vivienda, txt_edit_precio_vivienda, txt_edit_cant_abonada, txt_edit_plazo_hip, txt_edit_inicio_hip, txt_edit_tipo_hip;

    private ImageButton btn_info_vinculaciones;
    //campos variables
    //Hipoteca Fija
    private TextView label_porcentaje_fijo;
    private EditText edit_porcentaje_fijo;
    //Hipoteca Variable
    private TextView label_duracion_primer_porcentaje;
    private EditText edit_duracion_primer_porcentaje;
    private TextView label_primer_porcentaje;
    private EditText edit_primer_porcentaje;
    private TextView label_diferencial_variable;
    private EditText edit_diferencial_variable;
    //Hipoteca mixta
    private TextView label_anios_fija;
    private EditText edit_anios_fija;
    private TextView label_porcentaje_fijo_mix;
    private EditText edit_porcentaje_fijo_mix;
    private TextView label_diferencial_mixto;
    private EditText edit_diferencial_mixto;
    //Campos comun a mixta y variable
    private TextView label_cuando_revision;
    private CheckBox check_seis_meses;
    private CheckBox check_un_anio;
    private Button btn_editar_hipoteca;

    //Bases de datos

    private FirebaseFirestore db;

    private FirebaseAuth auth;

    private String[] comunidades;
    private String[] comunidades_base_datos;
    private int aniosHastaAhora;
    private DecimalFormat formato;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_hipoteca_seguimiento);
        comunidades = new String[]{"Andalucía", "Aragón", "Asturias", "Baleares", "Canarias", "Cantabria", "Castilla La Mancha", "Castilla León", "Cataluña", "Ceuta", "Comunidad de Madrid", "Comunidad Valenciana", "Extremadura", "Galicia", "La Rioja", "Melilla", "Murcia", "Navarra", "País Vasco"};
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, comunidades);
        comunidades_base_datos = new String[]{"Andalucía", "Aragón", "Asturias", "Baleares", "Canarias", "Cantabria", "Castilla_La_Mancha", "Castilla_León", "Cataluña", "Ceuta", "Madrid", "Comunidad_Valenciana", "Extremadura", "Galicia", "La_Rioja", "Melilla", "Murcia", "Navarra", "País_Vasco"};

        // Establecer el formato a dos decimales
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setGroupingSeparator('.'); // Separador de miles
        simbolos.setDecimalSeparator(','); // Separador decimal
        formato = new DecimalFormat("#,##0.00", simbolos);

        initUI();
        rellenarCampos();
        Eventos();
        db   = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    private void initUI() {
        //Campos fijos
        closeIcon = findViewById(R.id.close_icon_seg_edit);
        check_parte_fija_variable=findViewById(R.id.check_parte_fija_variable_edit);
        gastos_totales=findViewById(R.id.edit_gastos_totales_edit);
        vinculaciones_anio=findViewById(R.id.edit_anio_vinculacion);
        vinculaciones_valor=findViewById(R.id.edit_dinero_vinculacion);
        nombre_hipoteca=findViewById(R.id.edit_nombre_hipoteca_edit);
        txt_edit_hipoteca = findViewById(R.id.txt_edit_hipoteca);
        txt_edit_comunidad = findViewById(R.id.txt_edit_comunidad);
        txt_edit_tipo_vivienda = findViewById(R.id.txt_edit_tipo_vivienda);
        txt_edit_antiguedad_vivienda = findViewById(R.id.txt_edit_antiguedad_vivienda);
        txt_edit_precio_vivienda = findViewById(R.id.txt_edit_precio_vivienda);
        txt_edit_cant_abonada = findViewById(R.id.txt_edit_cant_abonada);
        txt_edit_plazo_hip = findViewById(R.id.txt_edit_plazo_hip);
        txt_edit_inicio_hip = findViewById(R.id.txt_edit_inicio_hip);
        txt_edit_tipo_hip = findViewById(R.id.txt_edit_tipo_hip);
        btn_info_vinculaciones = findViewById(R.id.btn_info_vinculaciones);

        //campos variables
        //Hipoteca Fija
        label_porcentaje_fijo=findViewById(R.id.label_porcentaje_fijo_edit);
        edit_porcentaje_fijo=findViewById(R.id.edit_porcentaje_fijo_edit);
        //Hipoteca Variable
        label_duracion_primer_porcentaje=findViewById(R.id.label_duracion_primer_porcentaje_variable_edit);
        edit_duracion_primer_porcentaje=findViewById(R.id.edit_duracion_primer_porcentaje_variable_edit);
        label_primer_porcentaje=findViewById(R.id.label_primer_porcentaje_variable_edit);
        edit_primer_porcentaje=findViewById(R.id.edit_primer_porcentaje_variable_edit);
        label_diferencial_variable=findViewById(R.id.label_diferencial_variable_edit);
        edit_diferencial_variable=findViewById(R.id.edit_porcentaje_diferencial_variable_edit);
        //Hipoteca mixta
        label_anios_fija=findViewById(R.id.label_cuantos_anios_fijo_mixta_edit);
        edit_anios_fija=findViewById(R.id.edit_anios_fijos_mixta_edit);
        label_porcentaje_fijo_mix=findViewById(R.id.label_porcentaje_fijo_mixta_edit);
        edit_porcentaje_fijo_mix=findViewById(R.id.edit_porcentaje_fijo_mixta_edit);
        label_diferencial_mixto=findViewById(R.id.label_diferencial_mixta_edit);
        edit_diferencial_mixto=findViewById(R.id.edit_porcentaje_diferencial_mixta_edit);
        //Campos comun a mixta y variable
        label_cuando_revision=findViewById(R.id.label_cada_cuanto_revision_edit);
        check_seis_meses=findViewById(R.id.checkBox_revision_seis_meses_edit);
        check_un_anio=findViewById(R.id.checkBox_revision_un_anio_edit);

        btn_editar_hipoteca = findViewById(R.id.btn_editar_hipoteca);

        sp_bancos = findViewById(R.id.sp_banco_nuevo_seg_edit);
        String [] bancos = {"ING","SANTANDER","BBVA","CAIXABANK","BANKINTER","EVO BANCO","SABADELL","UNICAJA","DEUTSCHE BANK","OPEN BANK","KUTXA BANK","IBERCAJA","ABANCA"};
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item,bancos){
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
        };;
        sp_bancos.setAdapter(adapter);

    }
    @Override
    public void onBackPressed() {
        AlertDialog dialogo = new AlertDialog.Builder(this)
                .setPositiveButton(getString(R.string.si_eliminar_cuenta), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.no_eliminar_cuenta), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setTitle("CANCELAR EDITAR SEGUIMIENTO").setMessage("¿Desea salir sin guardar cambios?").create();
        dialogo.show();
    }
    private void Eventos() {
        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialogo = new AlertDialog.Builder(EditarHipotecaSeguimiento.this)
                        .setPositiveButton(getString(R.string.si_eliminar_cuenta), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton(getString(R.string.no_eliminar_cuenta), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setTitle("CANCELAR EDITAR SEGUIMIENTO").setMessage("¿Desea salir sin guardar cambios?").create();
                dialogo.show();
            }
        });


        check_seis_meses.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) check_un_anio.setChecked(false);
                if(!check_un_anio.isChecked()) check_seis_meses.setChecked(true);
            }
        });

        check_un_anio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) check_seis_meses.setChecked(false);
                if(!check_seis_meses.isChecked()) check_un_anio.setChecked(true);
            }
        });

        check_parte_fija_variable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edit_duracion_primer_porcentaje.setVisibility(View.VISIBLE);
                    label_duracion_primer_porcentaje.setVisibility(View.VISIBLE);
                    edit_primer_porcentaje.setVisibility(View.VISIBLE);
                    label_primer_porcentaje.setVisibility(View.VISIBLE);
                } else{
                    edit_duracion_primer_porcentaje.setVisibility(View.GONE);
                    label_duracion_primer_porcentaje.setVisibility(View.GONE);
                    edit_primer_porcentaje.setVisibility(View.GONE);
                    label_primer_porcentaje.setVisibility(View.GONE);
                }
            }
        });

        btn_editar_hipoteca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comprobarCampos();
            }
        });

        btn_info_vinculaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Balloon balloon = new Balloon.Builder(getApplicationContext())
                        .setArrowSize(10)
                        .setArrowOrientation(ArrowOrientation.TOP)
                        .setArrowPositionRules(ArrowPositionRules.ALIGN_ANCHOR)
                        .setArrowPosition(0.5f)
                        .setWidth(BalloonSizeSpec.WRAP)
                        .setHeight(100)
                        .setTextSize(15f)
                        .setCornerRadius(4f)
                        .setAlpha(0.9f)
                        .setText("Puedes cambiar el valor desde el año 1 hasta el " + aniosHastaAhora + ".")
                        .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black))
                        .setTextIsHtml(true)
                        .setIconDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.info))
                        .setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white))
                        .setBalloonAnimation(BalloonAnimation.FADE)
                        .build();

                balloon.showAlignTop(btn_info_vinculaciones);
            }
        });

        vinculaciones_anio.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int anio = s.toString().equals("") ? 0 : Integer.parseInt(s.toString());
                if(anio > aniosHastaAhora && anio > 0) vinculaciones_anio.setError("El año no puede ser mayor que " + aniosHastaAhora);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void ActivarCampos(String hipoteca){

        switch (hipoteca){
            case "fija" :
                ModificarCamposFija(View.VISIBLE);
                ModificarCamposVariable(View.GONE);
                ModificarCamposMixta(View.GONE);
                ModificarCamposMixtaVariable(View.GONE);

            break;
            case "variable" :
                ModificarCamposFija(View.GONE);
                ModificarCamposVariable(View.VISIBLE);
                ModificarCamposMixta(View.GONE);
                ModificarCamposMixtaVariable(View.VISIBLE);

            break;
            default:
                ModificarCamposFija(View.GONE);
                ModificarCamposVariable(View.GONE);
                ModificarCamposMixta(View.VISIBLE);
                ModificarCamposMixtaVariable(View.VISIBLE);

            break;

        }
    }
    private void ModificarCamposFija(int view) {
        edit_porcentaje_fijo.setVisibility(view);
        label_porcentaje_fijo.setVisibility(view);

    }
    private void ModificarCamposVariable(int view) {
        check_parte_fija_variable.setVisibility(view);
        label_diferencial_variable.setVisibility(view);
        edit_diferencial_variable.setVisibility(view);

    }
    private void ModificarCamposMixta(int view) {
        edit_anios_fija.setVisibility(view);
        label_anios_fija.setVisibility(view);
        label_porcentaje_fijo_mix.setVisibility(view);
        edit_porcentaje_fijo_mix.setVisibility(view);
        label_diferencial_mixto.setVisibility(view);
        edit_diferencial_mixto.setVisibility(view);

    }
    private void ModificarCamposMixtaVariable(int view) {
        label_cuando_revision.setVisibility(view);
        check_seis_meses.setVisibility(view);
        check_un_anio.setVisibility(view);

    }
    private void comprobarCampos(){

        boolean camposCorrectos = true;

        //CAMPOS FIJOS
        if(hip.getTipo_hipoteca().equals("fija")){
            if(TextUtils.isEmpty(edit_porcentaje_fijo.getText())){
                edit_porcentaje_fijo.setError(getString(R.string.porcentaje_vacio));
                camposCorrectos = false;
            } else {
                if (Double.parseDouble(edit_porcentaje_fijo.getText().toString()) <= 0) {
                    edit_porcentaje_fijo.setError(getString(R.string.porcentaje_mayor_igual_cero));
                    camposCorrectos = false;
                }
            }
        } //CAMPOS VARIABLES
        else if(hip.getTipo_hipoteca().equals("variable")){
            if(check_parte_fija_variable.isChecked()) {
                if (TextUtils.isEmpty(edit_duracion_primer_porcentaje.getText())) {
                    edit_duracion_primer_porcentaje.setError(getString(R.string.duracion_vacio));
                    camposCorrectos = false;
                } else {
                    if (Integer.parseInt(edit_duracion_primer_porcentaje.getText().toString()) <= 0) {
                        edit_duracion_primer_porcentaje.setError(getString(R.string.duracion_mayor_igual_cero));
                        camposCorrectos = false;
                    } else if(Integer.parseInt(edit_duracion_primer_porcentaje.getText().toString()) > (hip.getPlazo_anios() - 1) * 12){
                    edit_duracion_primer_porcentaje.setError(getString(R.string.meses_menor_plazo));
                    camposCorrectos = false;
                    }
                }
                if (TextUtils.isEmpty(edit_primer_porcentaje.getText())) {
                    edit_primer_porcentaje.setError(getString(R.string.porcentaje_vacio));
                    camposCorrectos = false;
                } else {
                    if (Double.parseDouble(edit_primer_porcentaje.getText().toString()) <= 0) {
                        edit_primer_porcentaje.setError(getString(R.string.porcentaje_mayor_igual_cero));
                        camposCorrectos = false;
                    }
                }
            }
            if (TextUtils.isEmpty(edit_diferencial_variable.getText())) {
                edit_diferencial_variable.setError(getString(R.string.porcentaje_vacio));
                camposCorrectos = false;
            } else {
                if (Double.parseDouble(edit_diferencial_variable.getText().toString()) <= 0) {
                    edit_diferencial_variable.setError(getString(R.string.porcentaje_mayor_igual_cero));
                    camposCorrectos = false;
                }
            }
        } //CAMPOS MIXTOS
        else{
            if(TextUtils.isEmpty(edit_anios_fija.getText())){
                edit_anios_fija.setError(getString(R.string.duracion_vacio));
                camposCorrectos = false;
            } else {
                if (Integer.parseInt(edit_anios_fija.getText().toString()) <= 0) {
                    edit_anios_fija.setError(getString(R.string.anios_mayor_cero));
                    camposCorrectos = false;
                } else if(Integer.parseInt(edit_anios_fija.getText().toString()) > hip.getPlazo_anios() - 1){
                    edit_anios_fija.setError(getString(R.string.anios_menor_plazo));
                    camposCorrectos = false;
                }
            }
            if(TextUtils.isEmpty(edit_porcentaje_fijo_mix.getText())){
                edit_porcentaje_fijo_mix.setError(getString(R.string.porcentaje_vacio));
                camposCorrectos = false;
            } else {
                if (Double.parseDouble(edit_porcentaje_fijo_mix.getText().toString()) <= 0) {
                    edit_porcentaje_fijo_mix.setError(getString(R.string.porcentaje_mayor_igual_cero));
                    camposCorrectos = false;
                }
            }
            if(TextUtils.isEmpty(edit_diferencial_mixto.getText())){
                edit_diferencial_mixto.setError(getString(R.string.porcentaje_vacio));
                camposCorrectos = false;
            } else {
                if (Double.parseDouble(edit_diferencial_mixto.getText().toString()) <= 0) {
                    edit_diferencial_mixto.setError(getString(R.string.porcentaje_mayor_igual_cero));
                    camposCorrectos = false;
                }
            }
        }
        if(TextUtils.isEmpty(nombre_hipoteca.getText())){
            nombre_hipoteca.setError(getString(R.string.nombre_hipoteca_vacio));
            camposCorrectos = false;
        }
        else {
            if(camposCorrectos){
                if(!nombre_hipoteca.getText().toString().equals(hip.getNombre())) {
                    Query hipotecas_usuario = db.collection("hipotecas_seguimiento").whereEqualTo("idUsuario", auth.getCurrentUser().getUid()).whereEqualTo("nombre", nombre_hipoteca.getText().toString());
                    //COMPROBACION DE QUE NO INTRODUCE UNA HIPOTECA CON EL MISMO NOMBRE EL MISMO USUARIO
                    hipotecas_usuario.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (!task.getResult().isEmpty() && task.getResult() != null)
                                    nombre_hipoteca.setError("Una de sus hipotecas ya tiene este nombre");
                                else editarHipoteca();
                            } else
                                Log.d(TAG, "Error en query hipotecas seg por nombre", task.getException());
                        }
                    });
                } else editarHipoteca();
            }else return;
        }
    }

    private void editarHipoteca(){
        Map<String, Object> nuevosDatos = new HashMap<>();

        nuevosDatos.put("nombre", nombre_hipoteca.getText().toString());

        double totalGastos;
        if(TextUtils.isEmpty(gastos_totales.getText())) totalGastos = 0;
        else totalGastos = Double.parseDouble(gastos_totales.getText().toString());
        nuevosDatos.put("totalGastos", totalGastos);

        List<Double> vinculaciones_hip = hip.getArrayVinculacionesAnual();
        if(!TextUtils.isEmpty(vinculaciones_anio.getText()) && !TextUtils.isEmpty(vinculaciones_valor.getText())) {
            vinculaciones_hip.set(Integer.parseInt(vinculaciones_anio.getText().toString()) - 1, Double.parseDouble(vinculaciones_valor.getText().toString()));
        } else if(TextUtils.isEmpty(vinculaciones_anio.getText()) && !TextUtils.isEmpty(vinculaciones_valor.getText())) {
            vinculaciones_anio.setError("Debes de incluir un año");
            return;
        } else if(!TextUtils.isEmpty(vinculaciones_anio.getText()) && TextUtils.isEmpty(vinculaciones_valor.getText())){
            vinculaciones_valor.setError("Debes de incluir un valor");
            return;
        }
        nuevosDatos.put("arrayVinculacionesAnual", vinculaciones_hip);

        nuevosDatos.put("banco_asociado", sp_bancos.getSelectedItem().toString());
        //Hipoteca Fija
        if(hip.getTipo_hipoteca().equals("fija")){
            nuevosDatos.put("porcentaje_fijo", Double.parseDouble(edit_porcentaje_fijo.getText().toString()));

        }else if (hip.getTipo_hipoteca().equals("variable")){
            int mesesFija = check_parte_fija_variable.isChecked() ? Integer.parseInt(edit_duracion_primer_porcentaje.getText().toString()) : 0;
            nuevosDatos.put("duracion_primer_porcentaje_variable", mesesFija);
            double primerPorc = check_parte_fija_variable.isChecked() ? Double.parseDouble(edit_primer_porcentaje.getText().toString()) : 0;
            nuevosDatos.put("primer_porcentaje_variable", primerPorc);
            nuevosDatos.put("porcentaje_diferencial_variable", Double.parseDouble(edit_diferencial_variable.getText().toString()));
            boolean revision_anual = true;
            if(check_seis_meses.isChecked()) revision_anual = false;
            nuevosDatos.put("revision_anual", revision_anual);
            //nuevaHip = new HipotecaSegVariable(nombre, comunidad, tipo_vivienda, antiguedad_vivienda, precio_viv, cant_abonada, plazo_hip, fecha_inicio, tipo_hipoteca, totalGastos, gastos_vin, banco_asociado, duracion_primer_porcentaje, primer_porc_variable, diferencial_variable, revision_anual);

        }else{
            boolean revision_anual = true;
            if(check_seis_meses.isChecked()) revision_anual = false;
            nuevosDatos.put("revision_anual", revision_anual);

            nuevosDatos.put("anios_fija_mixta", Integer.parseInt(edit_anios_fija.getText().toString()));
            nuevosDatos.put("porcentaje_fijo_mixta", Double.parseDouble(edit_porcentaje_fijo_mix.getText().toString()));
            nuevosDatos.put("porcentaje_diferencial_mixta", Double.parseDouble(edit_diferencial_mixto.getText().toString()));
        }

        // obtener una referencia a la colección
        CollectionReference hipotecasRef = db.collection("hipotecas_seguimiento");

        // crear una consulta para obtener el documento específico
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        Query query = hipotecasRef.whereEqualTo("nombre", hip.getNombre()).whereEqualTo("idUsuario", firebaseAuth.getCurrentUser().getUid());

        // ejecutar la consulta
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // actualizar el documento con los nuevos valores
                        hipotecasRef.document(document.getId()).update(nuevosDatos);
                        if(!nombre_hipoteca.getText().toString().equals(hip.getNombre())) cambiarNombreEnAmortizaciones();
                        else {
                            Toast.makeText(EditarHipotecaSeguimiento.this, getString(R.string.hipoteca_seguimiento_editada_exito), Toast.LENGTH_LONG).show();
                            Intent i = new Intent(EditarHipotecaSeguimiento.this, PaginaPrincipal.class);
                            startActivity(i);
                        }

                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void cambiarNombreEnAmortizaciones(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query amortizaciones_doc = db.collection("amortizaciones_anticipadas").whereEqualTo("idUsuario", FirebaseAuth.getInstance().getCurrentUser().getUid()).whereEqualTo("nombre_hipoteca", hip.getNombre());

        amortizaciones_doc.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            db.collection("amortizaciones_anticipadas").document(document.getId())
                                    .update("nombre_hipoteca", nombre_hipoteca.getText().toString())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(EditarHipotecaSeguimiento.this, getString(R.string.hipoteca_seguimiento_editada_exito), Toast.LENGTH_LONG).show();
                                            Intent i = new Intent(EditarHipotecaSeguimiento.this, PaginaPrincipal.class);
                                            startActivity(i);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error updating document", e);
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error getting documents.", e);
                    }
                });
    }

    public void rellenarCampos(){
        if(getIntent().getStringExtra("tipo_hipoteca").equals("fija")) hip = (HipotecaSegFija) getIntent().getSerializableExtra("hipoteca");
        else if (getIntent().getStringExtra("tipo_hipoteca").equals("variable")) hip = (HipotecaSegVariable) getIntent().getSerializableExtra("hipoteca");
        else hip = (HipotecaSegMixta) getIntent().getSerializableExtra("hipoteca");

        //txt_edit_hipoteca.setText("Editar " + hip.getNombre());
        int i = 0;
        while(i < comunidades_base_datos.length){
            if(hip.getComunidad_autonoma().equals(comunidades_base_datos[i])) break;
            i++;
        }
        txt_edit_comunidad.setText("Comunidad autónoma: " + comunidades[i]);


        String [] bancos = {"ING","SANTANDER","BBVA","CAIXABANK","BANKINTER","EVO BANCO","SABADELL","UNICAJA","DEUTSCHE BANK","OPEN BANK","KUTXA BANK","IBERCAJA","ABANCA"};
        int j = 0;
        while(j < bancos.length){
            if(hip.getBanco_asociado().equals(bancos[j])) break;
            j++;
        }
        sp_bancos.setSelection(j);

        txt_edit_tipo_vivienda.setText("Tipo de vivienda: " + hip.getTipo_vivienda());

        txt_edit_antiguedad_vivienda.setText("Estado de la vivienda: " + hip.getAntiguedad_vivienda());

        txt_edit_precio_vivienda.setText("Precio de la vivienda: " + formato.format(hip.getPrecio_vivienda()) + "€");
        txt_edit_cant_abonada.setText("Cantidad abonada: " + formato.format(hip.getCantidad_abonada()) + "€");
        txt_edit_plazo_hip.setText("Plazo de la hipoteca: " + hip.getPlazo_anios() + " años");

        //date
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        txt_edit_inicio_hip.setText("Fecha de inicio: " + formato.format(hip.getFecha_inicio()));

        txt_edit_tipo_hip.setText("Tipo de hipoteca: " + hip.getTipo_hipoteca());
        if(hip.getTipo_hipoteca().equals("variable")){
            ActivarCampos("variable");

            edit_duracion_primer_porcentaje.setText(Integer.toString(hip.getDuracion_primer_porcentaje_variable()));
            if(Integer.parseInt(edit_duracion_primer_porcentaje.getText().toString()) != 0){
                check_parte_fija_variable.setChecked(true);
                label_duracion_primer_porcentaje.setVisibility(View.VISIBLE);
                edit_duracion_primer_porcentaje.setVisibility(View.VISIBLE);
                label_primer_porcentaje.setVisibility(View.VISIBLE);
                edit_primer_porcentaje.setVisibility(View.VISIBLE);
            } else{
                check_parte_fija_variable.setChecked(false);
                label_duracion_primer_porcentaje.setVisibility(View.GONE);
                edit_duracion_primer_porcentaje.setVisibility(View.GONE);
                label_primer_porcentaje.setVisibility(View.GONE);
                edit_primer_porcentaje.setVisibility(View.GONE);
            }
            edit_primer_porcentaje.setText(Double.toString(hip.getPrimer_porcentaje_variable()));
            edit_diferencial_variable.setText(Double.toString(hip.getPorcentaje_diferencial_variable()));
            if(!hip.isRevision_anual()){
                check_seis_meses.setChecked(true);
                check_un_anio.setChecked(false);
            }
        } else if(hip.getTipo_hipoteca().equals("mixta")){
            ActivarCampos("mixta");
            if(!hip.isRevision_anual()){
                check_seis_meses.setChecked(true);
                check_un_anio.setChecked(false);
            }
            edit_anios_fija.setText(Integer.toString(hip.getAnios_fija_mixta()));
            edit_porcentaje_fijo_mix.setText(Double.toString(hip.getPorcentaje_fijo_mixta()));
            edit_diferencial_mixto.setText(Double.toString(hip.getPorcentaje_diferencial_mixta()));
        } else { //fija
            edit_porcentaje_fijo.setText(Double.toString(hip.getPorcentaje_fijo()));
        }

        gastos_totales.setText(Double.toString(hip.getTotalGastos()));

        aniosHastaAhora = hip.getPlazo_anios() - getAniosMesesRestantes(hip.getPlazo_anios(), getNumeroCuotaActual(hip.getFecha_inicio(), hip.getPlazo_anios())).get(0);
        if(aniosHastaAhora <= 0){
            vinculaciones_anio.setEnabled(false);
            vinculaciones_valor.setEnabled(false);
        } else btn_info_vinculaciones.setVisibility(View.VISIBLE);
        if(hip.getArrayVinculacionesAnual().size() > 0) {
            vinculaciones_anio.setText(hip.getArrayVinculacionesAnual().size() + "");
            vinculaciones_valor.setText(Double.toString(hip.getArrayVinculacionesAnual().get(hip.getArrayVinculacionesAnual().size() - 1)));
        }

        nombre_hipoteca.setText(hip.getNombre());

    }

    /** Esta funcion devuelve los años y meses que quedan de hipoteca**/
    public ArrayList<Integer> getAniosMesesRestantes(int plazo, int cuota){

        ArrayList<Integer> anios_meses = new ArrayList<>();
        int cuotasRestantes = (plazo * 12) - cuota;
        int anios = cuotasRestantes / 12;
        int meses = cuotasRestantes % 12;
        anios_meses.add(anios);
        anios_meses.add(meses);
        return anios_meses;
    }

    /** Devuelve el numero de cuota por el que va actualmente la hipoteca [1 - plazo_anios * 12 ] **/
    public int getNumeroCuotaActual(Date fecha_inicio, int plazo){
        Calendar inicio = Calendar.getInstance();
        inicio.setTime(fecha_inicio);
        // Dia actual
        Calendar actual = Calendar.getInstance();
        // En caso de que todavia no haya empezado el seguimiento de la hipoteca
        if(actual.compareTo(inicio) < 0) return 0;
        int difA = actual.get(Calendar.YEAR) - inicio.get(Calendar.YEAR);
        int numeroPagoActual = difA * 12 + actual.get(Calendar.MONTH) - inicio.get(Calendar.MONTH);

        // Si el dia es el mismo que el de pago, devuelve como si ya ha pagado esa cuota
        if(actual.get(Calendar.DAY_OF_MONTH) >= inicio.get(Calendar.DAY_OF_MONTH)) numeroPagoActual = numeroPagoActual + 1; //Se le sumaria 1 debido a que ya ha pasado el dia de pago del mes correspondiente
        //else return numeroPagoActual + 1;
        // Fin de hipoteca
        if (numeroPagoActual >= plazo * 12) numeroPagoActual = plazo * 12;
        return numeroPagoActual;

    }

}