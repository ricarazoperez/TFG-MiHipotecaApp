package es.MiHipotecaApp.TFG.UsuarioRegistrado.HipotecasSeguimiento;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.MiHipotecaApp.TFG.PaginaPrincipal;
import es.MiHipotecaApp.TFG.R;
import es.MiHipotecaApp.TFG.Transfers.HipotecaSegFija;
import es.MiHipotecaApp.TFG.Transfers.HipotecaSegMixta;
import es.MiHipotecaApp.TFG.Transfers.HipotecaSegVariable;
import es.MiHipotecaApp.TFG.Transfers.HipotecaSeguimiento;

public class AmortizarAntes extends AppCompatActivity {

    private TextView titulo_hipoteca;
    private CheckBox check_comision;
    private EditText edit_comision;
    private CheckBox check_amort_total;
    private CheckBox check_amort_parcial;
    private TextView label_info_amort_parcial;
    private CheckBox check_reducir_cuota;
    private CheckBox check_reducir_plazo;
    private EditText edit_dinero_a_amortizar;
    private EditText edit_reduccion_plazo_meses;
    private ImageButton btn_info_dinero_amort;
    private ImageButton btn_info_reduccion_plazo;

    private Button amortizar_antes;



    private TextView cantidad_capital_amortizado;
    private TextView capital_pendiente_antiguo;
    private TextView capital_pendiente_nuevo;
    private TextView total_comision;
    private TextView cuota_plazo_antigua_valor;
    private TextView cuota_plazo_nueva_valor;
    private TextView cuota_plazo_antigua_tv;
    private TextView cuota_plazo_nueva_tv;

    //Utilizar este layout tambien para num cuotas anterior vs num_cuotas
    private LinearLayout layout_cuotaplazo_antigua_vs_nueva;
    private LinearLayout layout_amort_parcial;

    private LinearLayout layout_reducir_cuota;
    private LinearLayout layout_reducir_plazo;
    private CircleImageView close_icon;

    private HipotecaSeguimiento hip;
    private HashMap<Integer, List<Object>> amortizaciones_hip;

    private List<Double> euribors;

    private double capital_pendiente_actual;
    private String capital_pendiente_actual_formateado;
    private String cuota_mensual_actual;
    private int plazo_actual;
    private double cantidad_amortizada;
    int meses_reducidos;

    private DecimalFormat formato; // Establecer el formato a dos decimales
    private DecimalFormat formatoDouble;
    private final String TAG = "AmortizarAntes";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amortizar_antes);
        // Establecer el formato a dos decimales
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setGroupingSeparator('.'); // Separador de miles
        simbolos.setDecimalSeparator(','); // Separador decimal
        formato = new DecimalFormat("#,##0.00", simbolos);
        //Establece el formato de Double por defecto
        DecimalFormatSymbols simbolos2 = new DecimalFormatSymbols();
        simbolos2.setDecimalSeparator('.');
        formatoDouble = new DecimalFormat("#.##", simbolos2);

        if(getIntent().getStringExtra("tipo_hipoteca").equals("fija")) hip = (HipotecaSegFija) getIntent().getSerializableExtra("hipoteca");
        else if (getIntent().getStringExtra("tipo_hipoteca").equals("variable")) hip = (HipotecaSegVariable) getIntent().getSerializableExtra("hipoteca");
        else hip = (HipotecaSegMixta) getIntent().getSerializableExtra("hipoteca");
        initUI();

        cuota_mensual_actual = getIntent().getStringExtra("cuota_actual");
        amortizaciones_hip = (HashMap<Integer, List<Object>>) getIntent().getSerializableExtra("amortizaciones_anticipadas");
        euribors = (List<Double>) getIntent().getSerializableExtra("euribors");
        capital_pendiente_actual = hip.getCapitalPendienteTotalActual(hip.getNumeroCuotaActual(amortizaciones_hip), amortizaciones_hip, euribors);
        capital_pendiente_actual_formateado = formato.format(capital_pendiente_actual) + "€";
        plazo_actual = hip.getPlazoActual(amortizaciones_hip);
        meses_reducidos = 0;
        //Como empieza marcada la casilla de amortizacion total, se pone en capital amortizado el capital pendiente actual

        cantidad_capital_amortizado.setText("Cantidad a amortizar: " +  capital_pendiente_actual_formateado);
        capital_pendiente_antiguo.setText("Capital pdte anterior: " + capital_pendiente_actual_formateado);
        capital_pendiente_nuevo.setText("Capital pdte nuevo:  0€");
        cuota_plazo_antigua_valor.setText(cuota_mensual_actual);
        cuota_plazo_nueva_valor.setText(cuota_mensual_actual);
        eventos();
    }

    private void initUI(){
        titulo_hipoteca = findViewById(R.id.nombre_amort_anticipada_hip);
        titulo_hipoteca.setText(hip.getNombre());
        check_comision  = findViewById(R.id.check_comision_anticipada);
        edit_comision = findViewById(R.id.edit_porcentaje_comision_ant);
        check_amort_total = findViewById(R.id.check_amort_total);
        check_amort_parcial = findViewById(R.id.check_amort_parcial);
        label_info_amort_parcial = findViewById(R.id.label_info_amort_parcial);
        check_reducir_cuota = findViewById(R.id.check_reducir_cuota);
        check_reducir_plazo = findViewById(R.id.check_reducir_plazo);
        edit_dinero_a_amortizar = findViewById(R.id.edit_dinero_amort);
        edit_reduccion_plazo_meses = findViewById(R.id.edit_reduccion_plazo);

        btn_info_dinero_amort = findViewById(R.id.btn_info_dinero_amort);
        btn_info_reduccion_plazo = findViewById(R.id.btn_info_reduccion_plazo);
        layout_cuotaplazo_antigua_vs_nueva = findViewById(R.id.layout_cuotavsnueva);
        layout_amort_parcial = findViewById(R.id.layout_amort_parcial);
        layout_reducir_cuota = findViewById(R.id.layout_reducir_cuota);
        layout_reducir_plazo = findViewById(R.id.layout_reducir_plazo);
        cantidad_capital_amortizado = findViewById(R.id.tv_cantidad_amortizado);
        capital_pendiente_antiguo = findViewById(R.id.tv_capital_pendiente_amort_antiguo);
        capital_pendiente_nuevo = findViewById(R.id.tv_capital_pendiente_amort_nuevo);
        total_comision = findViewById(R.id.tv_comision);
        cuota_plazo_antigua_valor = findViewById(R.id.tv_cuota_plazo_antigua_valor);
        cuota_plazo_nueva_valor = findViewById(R.id.tv_cuota_plazo_nueva_valor);
        cuota_plazo_antigua_tv = findViewById(R.id.tv_cuota_plazo_antigua);
        cuota_plazo_nueva_tv = findViewById(R.id.tv_cuota_plazo_nueva);
        amortizar_antes = findViewById(R.id.btn_realizar_amortizacion_anticipada);
        close_icon = findViewById(R.id.close_icon_amort_ant);

    }

    private void eventos(){
        check_comision.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    edit_comision.setEnabled(true);
                } else{
                    edit_comision.setEnabled(false);
                    edit_comision.setText("");
                }
            }
        });

        edit_comision.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().equals("")) total_comision.setText("Comisión: 0€");
                else {
                    if(check_reducir_cuota.isChecked() && !edit_dinero_a_amortizar.getText().toString().equals(""))total_comision.setText("Comisión: " + formato.format(Double.parseDouble(edit_dinero_a_amortizar.getText().toString()) * (Double.parseDouble(edit_comision.getText().toString()) / 100)) + "€");
                    else if (check_reducir_plazo.isChecked() && !edit_reduccion_plazo_meses.getText().toString().equals("")) total_comision.setText("Comisión: " + formato.format(Double.parseDouble(edit_reduccion_plazo_meses.getText().toString()) * (Double.parseDouble(edit_comision.getText().toString()) / 100)) + "€");
                    else if(check_amort_total.isChecked())total_comision.setText("Comisión: " + formato.format(capital_pendiente_actual * (Double.parseDouble(edit_comision.getText().toString()) / 100))+ "€");
                    else total_comision.setText("Comisión: 0€");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        check_amort_total.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    if(!edit_comision.getText().toString().equals("")) total_comision.setText("Comisión: " + formato.format(capital_pendiente_actual * (Double.parseDouble(edit_comision.getText().toString()) / 100))  + "€");

                    check_amort_parcial.setChecked(false);
                    edit_dinero_a_amortizar.setText("");
                    edit_reduccion_plazo_meses.setText("");
                    layout_cuotaplazo_antigua_vs_nueva.setVisibility(View.GONE);
                    layout_amort_parcial.setVisibility(View.GONE);
                    layout_reducir_plazo.setVisibility(View.GONE);
                    layout_reducir_cuota.setVisibility(View.GONE);
                    label_info_amort_parcial.setVisibility(View.GONE);
                    cantidad_capital_amortizado.setText("Cantidad a amortizar: " + capital_pendiente_actual_formateado);
                    capital_pendiente_antiguo.setText("Capital pdte anterior: " + capital_pendiente_actual_formateado);
                    capital_pendiente_nuevo.setText("Capital pdte nuevo:  0€");
                }else{
                    if (!check_amort_parcial.isChecked()) check_amort_total.setChecked(true);
                }
            }
        });

        check_amort_parcial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    total_comision.setText("Comisión: 0€");
                    check_amort_total.setChecked(false);
                    label_info_amort_parcial.setVisibility(View.VISIBLE);
                    layout_amort_parcial.setVisibility(View.VISIBLE);
                    layout_cuotaplazo_antigua_vs_nueva.setVisibility(View.VISIBLE);
                    cantidad_capital_amortizado.setVisibility(View.GONE);
                    capital_pendiente_antiguo.setText("Capital pdte anterior: " + capital_pendiente_actual_formateado);
                    capital_pendiente_nuevo.setText("Capital pdte nuevo: " + capital_pendiente_actual_formateado);
                    if(check_reducir_cuota.isChecked()){
                        layout_reducir_cuota.setVisibility(View.VISIBLE);
                        layout_reducir_plazo.setVisibility(View.GONE);
                        cuota_plazo_antigua_tv.setText("Cuota Antigua");
                        cuota_plazo_nueva_tv.setText("Cuota Nueva");
                    }else{
                        layout_reducir_cuota.setVisibility(View.GONE);
                        layout_reducir_plazo.setVisibility(View.VISIBLE);
                        cuota_plazo_antigua_tv.setText("Plazo Antiguo");
                        cuota_plazo_nueva_tv.setText("Plazo Nuevo");
                    }
                }else{
                    if (!check_amort_total.isChecked()) check_amort_parcial.setChecked(true);
                }
            }
        });

        check_reducir_cuota.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    check_reducir_plazo.setChecked(false);
                    layout_reducir_plazo.setVisibility(View.GONE);
                    layout_reducir_cuota.setVisibility(View.VISIBLE);
                    cuota_plazo_antigua_tv.setText("Cuota Antigua");
                    cuota_plazo_antigua_valor.setText(cuota_mensual_actual);
                    cuota_plazo_nueva_tv.setText("Cuota Nueva");
                    cuota_plazo_nueva_valor.setText(cuota_mensual_actual);
                    edit_reduccion_plazo_meses.setText("");
                    edit_dinero_a_amortizar.setText("");
                    total_comision.setText("Comisión: 0€");
                    cantidad_capital_amortizado.setVisibility(View.GONE);
                    capital_pendiente_nuevo.setText("Capital pdte nuevo: " + capital_pendiente_actual_formateado);
                }else{
                    if (!check_reducir_plazo.isChecked()) check_reducir_cuota.setChecked(true);
                }
            }
        });

        check_reducir_plazo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    check_reducir_cuota.setChecked(false);
                    layout_reducir_plazo.setVisibility(View.VISIBLE);
                    layout_reducir_cuota.setVisibility(View.GONE);
                    cuota_plazo_antigua_tv.setText("Plazo Antiguo");
                    cuota_plazo_nueva_tv.setText("Plazo Nuevo");


                    cuota_plazo_antigua_valor.setText(plazo_actual + " meses");
                    cuota_plazo_nueva_valor.setText(plazo_actual + " meses");
                    edit_dinero_a_amortizar.setText("");
                    edit_reduccion_plazo_meses.setText("");
                    total_comision.setText("Comisión: 0€");
                }else{
                    if (!check_reducir_cuota.isChecked()) check_reducir_plazo.setChecked(true);
                }
            }
        });

        edit_dinero_a_amortizar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().equals("")) cuota_plazo_nueva_valor.setText(cuota_mensual_actual);
                else{

                   if(!edit_comision.getText().toString().equals("")) total_comision.setText("Comisión: " + formato.format(Double.parseDouble(edit_dinero_a_amortizar.getText().toString()) * (Double.parseDouble(edit_comision.getText().toString()) / 100)) + "€");


                    double porcentaje_aplicado  = hip.getPorcentajePorCuota(hip.getNumeroCuotaActual(amortizaciones_hip) + 1, amortizaciones_hip, euribors);//getIntent().getDoubleExtra("porcentaje_aplicado", -1);
                    int numero_cuotas_restantes = hip.getPlazoNumPago(hip.getNumeroCuotaActual(amortizaciones_hip) + 1, amortizaciones_hip); //getIntent().getIntExtra("cuotas_pendientes", -1);
                    double cantidad_pendiente = getIntent().getDoubleExtra("cantidad_pendiente", -1);
                    double capital_a_amortizar = Double.parseDouble(s.toString());
                    if (capital_a_amortizar > capital_pendiente_actual){
                        edit_dinero_a_amortizar.setError("El capital a amortizar no puede ser mayor que el capital pendiente");
                        capital_pendiente_nuevo.setText("Capital pdte nuevo: " + capital_pendiente_actual_formateado);
                    }
                    else{
                        double cantidad_pendiente_con_amortizacion = cantidad_pendiente - capital_a_amortizar;
                        double cuota_nueva = hip.getCuotaMensual(porcentaje_aplicado, cantidad_pendiente_con_amortizacion, numero_cuotas_restantes - (hip.getNumeroCuotaActual(amortizaciones_hip)));
                        cuota_plazo_nueva_valor.setText(formato.format(cuota_nueva) +"€");
                        cantidad_capital_amortizado.setVisibility(View.GONE);
                        double capital_pdte_nuevo = capital_pendiente_actual - capital_a_amortizar;
                        String cap_formateado = "Capital pdte nuevo: " + formato.format(capital_pdte_nuevo)  + "€";
                        capital_pendiente_nuevo.setText(cap_formateado);
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edit_reduccion_plazo_meses.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // calcular Y poner en cuota_plazo_antigua_valor y cuota_plazo_nueva_valor el plazo antiguo y el nuevo calculado

                if(s.toString().equals("")) cuota_plazo_nueva_valor.setText(plazo_actual + " meses");
                else{
                    if(!edit_comision.getText().toString().equals("")) total_comision.setText("Comisión: " + formato.format(Double.parseDouble(edit_reduccion_plazo_meses.getText().toString()) * (Double.parseDouble(edit_comision.getText().toString()) / 100)) + "€");

                    cantidad_amortizada = Double.parseDouble(s.toString());
                    if(cantidad_amortizada > capital_pendiente_actual) {
                        edit_reduccion_plazo_meses.setError("Como máximo puedes aportar " + capital_pendiente_actual_formateado + " de capital");
                        cuota_plazo_nueva_valor.setText(plazo_actual + " meses");
                    }else{
                        //calcular los meses a reducir
                        meses_reducidos = 0;
                        double cant_amort = cantidad_amortizada;
                        double capitalPdte = capital_pendiente_actual;

                        int siguienteNumeroCuota = hip.getNumeroCuotaActual(amortizaciones_hip) + 1;
                        double porcentaje_aplicado_siguiente_cuota = hip.getPorcentajePorCuota(siguienteNumeroCuota, amortizaciones_hip, euribors);
                        double cuotaSiguiente = hip.cogerCuotaActual(siguienteNumeroCuota, amortizaciones_hip, euribors);
                        double capSiguienteCuota = hip.getCapitalAmortizadoMensual(cuotaSiguiente, capitalPdte, porcentaje_aplicado_siguiente_cuota);

                        while(cant_amort >= capSiguienteCuota){
                            meses_reducidos++;
                            cant_amort -= capSiguienteCuota;
                            capitalPdte -= capSiguienteCuota;
                            porcentaje_aplicado_siguiente_cuota = hip.getPorcentajePorCuota(siguienteNumeroCuota + meses_reducidos, amortizaciones_hip, euribors);
                            cuotaSiguiente = hip.cogerCuotaActual(siguienteNumeroCuota + meses_reducidos, amortizaciones_hip, euribors);
                            capSiguienteCuota = hip.getCapitalAmortizadoMensual(cuotaSiguiente, capitalPdte, porcentaje_aplicado_siguiente_cuota);

                        }

                        cuota_plazo_nueva_valor.setText((plazo_actual - meses_reducidos) + " meses");
                        capital_pendiente_nuevo.setText("Capital pdte nuevo: " + formato.format(capital_pendiente_actual - cantidad_amortizada) + "€");
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        close_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialogo = new AlertDialog.Builder(AmortizarAntes.this)
                        .setPositiveButton(getString(R.string.si_eliminar_cuenta), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                Intent i = new Intent(AmortizarAntes.this, VisualizarHipotecaSeguimiento.class);
                                i.putExtra("tipo_hipoteca", hip.getTipo_hipoteca());
                                i.putExtra("hipoteca", hip);
                                startActivity(i);
                            }
                        })
                        .setNegativeButton(getString(R.string.no_eliminar_cuenta), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setTitle("CANCELAR AMORTIZACIÓN ANTICIPADA").setMessage("¿Desea dejar de hacer la amortización anticipada? Perderá todo su progreso").create();
                dialogo.show();
            }
        });

        amortizar_antes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(check_comision.isChecked() && TextUtils.isEmpty(edit_comision.getText())) edit_comision.setError("Debes introducir un porcentaje o desmarcar la casilla de comisión");
                else amortizar();
            }
        });

        btn_info_dinero_amort.setOnClickListener(new View.OnClickListener() {
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
                        .setText("La amortizacion se aplicará en la siguiente cuota.")
                        .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black))
                        .setTextIsHtml(true)
                        .setIconDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.info))
                        .setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white))
                        .setBalloonAnimation(BalloonAnimation.FADE)
                        .build();

                balloon.showAlignTop(btn_info_dinero_amort);
            }
        });

        btn_info_reduccion_plazo.setOnClickListener(new View.OnClickListener() {
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
                        .setText("La amortizacion se aplicará en la siguiente cuota.\nEl plazo se reduce en función del capital de las siguientes cuotas.")
                        .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black))
                        .setTextIsHtml(true)
                        .setIconDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.info))
                        .setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white))
                        .setBalloonAnimation(BalloonAnimation.FADE)
                        .build();

                balloon.showAlignTop(btn_info_reduccion_plazo);
            }
        });

    }

    private void amortizar(){

        double total_comision = 0;
        double porcentaje_comision = 0;
        if(!TextUtils.isEmpty(edit_comision.getText())) porcentaje_comision = Double.parseDouble(edit_comision.getText().toString());
        if(check_comision.isChecked()){
            if(!edit_dinero_a_amortizar.getText().toString().equals("")) total_comision = Double.parseDouble(edit_dinero_a_amortizar.getText().toString()) * (Double.parseDouble(edit_comision.getText().toString()) / 100);
            else if(!edit_reduccion_plazo_meses.getText().toString().equals("")) total_comision = Double.parseDouble(edit_reduccion_plazo_meses.getText().toString()) * (Double.parseDouble(edit_comision.getText().toString()) / 100);
            else total_comision = capital_pendiente_actual * (Double.parseDouble(edit_comision.getText().toString()) / 100);
        }

        int num_cuota_amortizacion = hip.getNumeroCuotaActual(amortizaciones_hip) + 1;
        List<Object> amortizacion = new ArrayList<>();
        //AMORTIZACION ANTICIPADA TOTAL
        if(check_amort_total.isChecked()){
            amortizacion.add("total");
            amortizacion.add(capital_pendiente_actual);
        }
        //AMORTIZACION ANTICIPADA PARCIAL
        else{
            //AMORTIZACION REDUCCION CUOTA
            if(check_reducir_cuota.isChecked()){
                amortizacion.add("parcial_cuota");
                amortizacion.add(Double.parseDouble(edit_dinero_a_amortizar.getText().toString()));
            }
            //AMORTIZACION REDUCCION PLAZO
            else{
                amortizacion.add("parcial_plazo");
                amortizacion.add(cantidad_amortizada);
                amortizacion.add(meses_reducidos);
            }
        }
        amortizacion.add(porcentaje_comision);
        registrar_amortizacion(num_cuota_amortizacion, amortizacion, total_comision);

    }

    private void registrar_amortizacion(int num_cuota, List<Object> amortizacion, double total_comision){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        //ACTUALIZAR amortizaciones_anticipadas

        CollectionReference amort_ref = db.collection("amortizaciones_anticipadas");

        Query query = amort_ref.whereEqualTo("nombre_hipoteca", hip.getNombre()).whereEqualTo("idUsuario", firebaseAuth.getCurrentUser().getUid());
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                if (!querySnapshot.isEmpty()) {
                    DocumentSnapshot docSnapshot = querySnapshot.getDocuments().get(0);
                    String docId = docSnapshot.getId();
                    DocumentReference docRef = amort_ref.document(docId);

                    Map<String, Object> nuevosDatos = new HashMap<>();
                    Map<String, Object> amortizaciones = new HashMap<>();
                    for (Integer clave : amortizaciones_hip.keySet()) {
                        List<Object> lista = amortizaciones_hip.get(clave);
                        amortizaciones.put(String.valueOf(clave), lista);
                    }
                    amortizaciones.put(String.valueOf(num_cuota), amortizacion);
                    nuevosDatos.put("amortizaciones_anticipadas", amortizaciones);

                    docRef.update(nuevosDatos).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // El documento se actualizó con éxito
                            Toast.makeText(AmortizarAntes.this, getString(R.string.amortizacion_anticipada_exito), Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Ocurrió un error al actualizar el documento
                            Log.e(TAG, "Error al actualizar amortizaciones");
                        }
                    });
                } else {
                    // No se encontraron documentos que cumplan con el criterio de búsqueda
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Ocurrió un error al leer los documentos
            }
        });

        //ACTUALIZAR comisiones
        if(total_comision > 0){
            Map<String, Object> nuevosDatos2 = new HashMap<>();
            nuevosDatos2.put("totalGastos", hip.getTotalGastos() + total_comision);
            CollectionReference hipotecasRef = db.collection("hipotecas_seguimiento");
            Query query2 = hipotecasRef.whereEqualTo("nombre", hip.getNombre()).whereEqualTo("idUsuario", firebaseAuth.getCurrentUser().getUid());
            // ejecutar la consulta
            query2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document2 : task.getResult()) {
                            // actualizar el documento con los nuevos valores
                            hipotecasRef.document(document2.getId()).update(nuevosDatos2);

                            Intent i = new Intent(AmortizarAntes.this, PaginaPrincipal.class);
                            i.putExtra("no_volver_atras", "no_atras");
                            startActivity(i);
                        }

                    } else {
                        Log.e("ERROR", " getting documents");
                    }
                }
            });
        }else{

            Intent i = new Intent(AmortizarAntes.this, PaginaPrincipal.class);
            startActivity(i);
        }


    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(AmortizarAntes.this, VisualizarHipotecaSeguimiento.class);
        i.putExtra("tipo_hipoteca", hip.getTipo_hipoteca());
        i.putExtra("hipoteca", hip);
        startActivity(i);
        finish();
    }
}