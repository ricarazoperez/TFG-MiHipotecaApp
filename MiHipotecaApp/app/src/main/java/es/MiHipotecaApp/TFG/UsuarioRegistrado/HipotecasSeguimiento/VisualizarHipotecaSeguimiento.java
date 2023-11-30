package es.MiHipotecaApp.TFG.UsuarioRegistrado.HipotecasSeguimiento;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.LegendLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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
import es.MiHipotecaApp.TFG.UsuarioRegistrado.CustomDialogoPremium;
import es.MiHipotecaApp.TFG.UsuarioRegistrado.HipotecasSeguimiento.Graficos.grafico_gastos_totales;
import es.MiHipotecaApp.TFG.UsuarioRegistrado.HipotecasSeguimiento.Graficos.grafico_intereses_capital;

public class VisualizarHipotecaSeguimiento extends AppCompatActivity implements NuevaVinculacionAnualFragment.NuevoAnioHipotecaListener {
    private final String TAG = "VIS_HIP_ACTIVITY";
    
    private CircleImageView close_icon_seg;
    private TextView nombre_hipoteca;
    private TextView tipo_hipoteca_seg;
    private ImageView logo_banco_seg;
    private TextView comunidad_autonoma_seg;
    private TextView dinero_restante_a_pagar;
    private TextView anios_restantes_hipoteca;
    private TextView mes_actual_cuota;

    private TextView capital_cuota_mensual;
    private TextView intereses_cuota_mensual;
    private TextView numero_cuota_actual;
    private TextView cuota_mensual_seguimiento;

    private TextView titulo_grafico;
    private AnyChartView grafico;

    private TextView texto_si_no_premium;

    private LinearLayout tabla_valores_grafico;
    private TextView capital_amortizado;
    private TextView capital_pendiente;
    private TextView intereses_pagados;
    private TextView intereses_pendientes;

    private HipotecaSeguimiento hip;
    private HashMap<Integer, List<Object>> amortizaciones_anticipadas;

    private List<Double> euribors;
    private Button btn_cuadro_amortizacion;
    private Button btn_amortizar_antes;

    private ImageButton info_dinero_restante;
    private ImageView info_cuota;

    private LinearLayout layout_porcentaje_aplicado;

    private TextView porcentaje_aplicado_valor;
    private LinearLayout info_grafico;
    private LinearLayout layout_cuota_seguimiento;
    private LinearLayout layout_amortizacion_anticipada;
    private LinearLayout layout_capital_intereses1;
    private LinearLayout layout_capital_intereses2;
    private TextView amortizacion_anticipada_valor;
    private ImageButton btn_grafico_gastos_totales;
    private ImageButton btn_grafico_intereses_capital;

    private String cuotaFormateada;
    private double porcentaje_aplicado;
    private int numero_cuotas_restantes;
    private double cantidad_pendiente;

    private int numero_cuotas_pagadas;


    private ProgressBar progressBar;

    private TextView titulo_seguimiento_hipoteca, txt_tipo_hipoteca_seguimiento, txt_banco_hipoteca, txt_com_autonoma_hipoteca, txt_dinero_falta_pagar, txt_tiempo_restante, txt_cuota_de;
    private LinearLayout layout_info_cuota_hip, layout_btns_graficos;
    private TableLayout tabla_seg_amortizacion;
    private TextView txt_no_amortizaciones;
    private ImageView btn_borrar_amort;
    private String[] comunidades = new String[]{"Andalucía", "Aragón", "Asturias", "Baleares", "Canarias", "Cantabria", "Castilla La Mancha", "Castilla León", "Cataluña", "Ceuta", "Comunidad de Madrid", "Comunidad Valenciana", "Extremadura", "Galicia", "La Rioja", "Melilla", "Murcia", "Navarra", "País Vasco"};
    private String[] comunidades_base_datos = new String[]{"Andalucía", "Aragón", "Asturias", "Baleares", "Canarias", "Cantabria", "Castilla_La_Mancha", "Castilla_León", "Cataluña", "Ceuta", "Madrid", "Comunidad_Valenciana", "Extremadura", "Galicia", "La_Rioja", "Melilla", "Murcia", "Navarra", "País_Vasco"};

    private DecimalFormat formato;
    private DecimalFormat formatoDouble;
    private boolean premium;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_hipoteca_seguimiento);
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
        cogerAmortizaciones();
    }

    private void initUI(){
        close_icon_seg            = findViewById(R.id.close_icon_seguimiento);
        dinero_restante_a_pagar   = findViewById(R.id.label_cantidad_pendiente_seguimiento_hip);
        nombre_hipoteca           = findViewById(R.id.nombre_seguimiento_hipoteca);
        tipo_hipoteca_seg         = findViewById(R.id.tipo_hipoteca_seguimiento);
        logo_banco_seg            = findViewById(R.id.logo_banco_seg);
        comunidad_autonoma_seg    = findViewById(R.id.comunidad_autonoma_seg);
        anios_restantes_hipoteca  = findViewById(R.id.label_anios_restantes_seguimiento_hip);
        mes_actual_cuota          = findViewById(R.id.nombre_mes_actual_seguimiento_hip);
        cuota_mensual_seguimiento = findViewById(R.id.cuota_mensual_seguimiento);
        capital_cuota_mensual     = findViewById(R.id.capital_cuota_mensual_hip);
        intereses_cuota_mensual   = findViewById(R.id.intereses_cuota_mensual_hip);
        numero_cuota_actual       = findViewById(R.id.numero_cuota_actual_hip);
        layout_amortizacion_anticipada = findViewById(R.id.layout_amortizacion_anticipada);
        amortizacion_anticipada_valor = findViewById(R.id.amortizacion_anticipada_valor);
        layout_cuota_seguimiento  = findViewById(R.id.layout_cuota_seguimiento);
        layout_capital_intereses1 = findViewById(R.id.layout_capital_intereses1);
        layout_capital_intereses2 = findViewById(R.id.layout_capital_intereses2);
        layout_porcentaje_aplicado = findViewById(R.id.layout_porcentaje_aplicado);
        porcentaje_aplicado_valor = findViewById(R.id.porcentaje_aplicado_valor);

        btn_cuadro_amortizacion              = findViewById(R.id.btn_cuadro_amortizacion);
        btn_amortizar_antes                  = findViewById(R.id.btn_amortizar_antes);
        info_dinero_restante                 = findViewById(R.id.btn_info_dinero_por_pagar);
        info_cuota                           = findViewById(R.id.btn_info_cuota);

        //GRÁFICOS
        titulo_grafico                       = findViewById(R.id.titulo_grafico);
        grafico                              = findViewById(R.id.grafico_seguimiento);
        tabla_valores_grafico                = findViewById(R.id.tabla_valores_grafico);
        capital_amortizado                   = findViewById(R.id.capital_amortizado_seguimiento_val);
        capital_pendiente                    = findViewById(R.id.capital_pendiente_seguimiento_val);
        intereses_pagados                    = findViewById(R.id.intereses_pagados_seguimiento_val);
        intereses_pendientes                 = findViewById(R.id.intereses_pendientes_seguimiento_val);
        info_grafico                         = findViewById(R.id.info_grafico);
        texto_si_no_premium                  = findViewById(R.id.texto_si_no_premium);
        btn_grafico_gastos_totales           = findViewById(R.id.btn_grafico_gastos_totales);
        btn_grafico_intereses_capital        = findViewById(R.id.btn_grafico_intereses_capital);

        progressBar                          = findViewById(R.id.progressBar_visualizar);

        titulo_seguimiento_hipoteca          = findViewById(R.id.titulo_seguimiento_hipoteca);
        txt_tipo_hipoteca_seguimiento        = findViewById(R.id.txt_tipo_hipoteca_seguimiento);
        txt_banco_hipoteca                   = findViewById(R.id.txt_banco_hipoteca);
        txt_com_autonoma_hipoteca            = findViewById(R.id.txt_com_autonoma_hipoteca);
        txt_dinero_falta_pagar               = findViewById(R.id.txt_dinero_falta_pagar);
        txt_tiempo_restante                  = findViewById(R.id.txt_tiempo_restante);
        txt_cuota_de                         = findViewById(R.id.txt_cuota_de);
        layout_info_cuota_hip                = findViewById(R.id.layout_info_cuota_hip);
        layout_btns_graficos                 = findViewById(R.id.layout_btns_graficos);
        tabla_seg_amortizacion               = findViewById(R.id.tabla_seg_amortizacion);
        txt_no_amortizaciones                = findViewById(R.id.txt_no_amortizaciones);
        btn_borrar_amort                     = findViewById(R.id.btn_borrar_amort);
    }

    private void cogerAmortizaciones(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        CollectionReference amortizacionesRef = db.collection("amortizaciones_anticipadas");

        Query query = amortizacionesRef.whereEqualTo("nombre_hipoteca", hip.getNombre()).whereEqualTo("idUsuario", firebaseAuth.getCurrentUser().getUid());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();
                    if(!querySnapshot.isEmpty()){
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                        Map<String, Object> data = documentSnapshot.getData();

                        Map<String, Object> amortizaciones = (Map<String, Object>) data.get("amortizaciones_anticipadas");

                        amortizaciones_anticipadas = new HashMap<>();
                        for (String clave : amortizaciones.keySet()) {
                            Integer claveInt = Integer.parseInt(clave);
                            List<Object> lista = (List<Object>) amortizaciones.get(clave);
                            amortizaciones_anticipadas.put(claveInt, lista);
                        }
                        if(!hip.getTipo_hipoteca().equals("fija")){
                            cogerEuribors();
                        }else{
                            rellenarUI();
                            rellenarTablaAmortizaciones();
                            eventos();
                            construirGraficoAportadoVsAFinanciar();
                        }
                    }
                }else Log.e(TAG, "Error al cargar amortizaciones anticipadas", task.getException());

            }
        });

    }

    private void cogerEuribors(){

        euribors = new ArrayList<>();

        //Coger todos los euribors para el plazo
        Calendar inicio = Calendar.getInstance();
        inicio.setTime(hip.getFecha_inicio());
        Calendar fechaActual = Calendar.getInstance();
        int anioInicio = inicio.get(Calendar.YEAR);
        final int[] anioInicioPrueba = {anioInicio};

        String mesInicio = inicio.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("es", "ES"));
        mesInicio = mesInicio.substring(0, 1).toUpperCase() + mesInicio.substring(1);
        final String[] mesInicioPrueba = {mesInicio};

        int anioActual = fechaActual.get(Calendar.YEAR);
        String mesActual = fechaActual.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("es", "ES"));
        mesActual = mesActual.substring(0, 1).toUpperCase() + mesActual.substring(1);

        //CASO HIPOTECA FUTURO: array con un solo valor
        int plazo = hip.getPlazoActual(amortizaciones_anticipadas);
        int aniosHip = plazo / 12;
        int mesesHip = plazo % 12;
        Calendar fechaFin = Calendar.getInstance();
        fechaFin.setTime(hip.getFecha_inicio());
        fechaFin.add(Calendar.YEAR, aniosHip);
        fechaFin.add(Calendar.MONTH, mesesHip);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference euriborRef = db.collection("euribor");
        //Caso hipoteca futura
        if(fechaActual.before(inicio)){
            //Meter solo el euribor actual

            euriborRef.whereEqualTo("anio", String.valueOf(anioActual)).whereEqualTo("mes", mesActual).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            euribors.add(documentSnapshot.getDouble("valor"));
                            rellenarUI();
                            rellenarTablaAmortizaciones();
                            eventos();
                            construirGraficoAportadoVsAFinanciar();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error al obtener euribor actual': ", e);
                        }
                    });

        }

        else{
            //Caso fin de hipoteca
            if (fechaActual.after(fechaFin)) {
                anioActual = fechaFin.get(Calendar.YEAR);
                mesActual = fechaFin.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("es", "ES"));
                mesActual = mesActual.substring(0, 1).toUpperCase() + mesActual.substring(1);
            }
            //meter desde anioInicio,mesInicio hasta anioActual, mesActual
            obtenerEuriborRecursivo(euriborRef, anioInicioPrueba, mesInicioPrueba, anioActual, mesActual, euribors);

        }
    }

    private void obtenerEuriborRecursivo(CollectionReference euriborRef, int[] anioInicioPrueba, String[] mesInicioPrueba, int anioActual, String mesActual, List<Double> euribors) {
        euriborRef.whereEqualTo("anio", String.valueOf(anioInicioPrueba[0])).whereEqualTo("mes", mesInicioPrueba[0]).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        euribors.add(documentSnapshot.getDouble("valor"));

                        // Ver si ha llegado al mes y año actual
                        if (anioInicioPrueba[0] == anioActual && mesInicioPrueba[0].equals(mesActual)) {
                            // Se han obtenido todos los valores de Euribor
                            rellenarUI();
                            rellenarTablaAmortizaciones();
                            eventos();
                            construirGraficoAportadoVsAFinanciar();

                        } else {
                            // Incrementar el mes y/o el año y hacer la siguiente consulta recursivamente
                            mesInicioPrueba[0] = incrementarMes(mesInicioPrueba[0]);
                            if (mesInicioPrueba[0].equals("Enero")) anioInicioPrueba[0]++;
                            obtenerEuriborRecursivo(euriborRef, anioInicioPrueba, mesInicioPrueba, anioActual, mesActual, euribors);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error al obtener euribor actual': ", e);
                    }
                });
    }


    private String incrementarMes(String mes){
        String siguienteMes = "";
        switch (mes){
            case "Enero":
                siguienteMes = "Febrero";
                break;
            case "Febrero":
                siguienteMes = "Marzo";
                break;
            case "Marzo":
                siguienteMes = "Abril";
                break;
            case "Abril":
                siguienteMes = "Mayo";
            break;
            case "Mayo":
                siguienteMes = "Junio";
            break;
            case "Junio":
                siguienteMes = "Julio";
            break;
            case "Julio":
                siguienteMes = "Agosto";
            break;
            case "Agosto":
                siguienteMes = "Septiembre";
            break;
            case "Septiembre":
                siguienteMes = "Octubre";
            break;
            case "Octubre":
                siguienteMes = "Noviembre";
            break;
            case "Noviembre":
                siguienteMes = "Diciembre";
            break;
            case "Diciembre":
                siguienteMes = "Enero";
            break;
        }
        return siguienteMes;
    }

    private void rellenarUI(){

        numero_cuotas_pagadas = hip.getNumeroCuotaActual(amortizaciones_anticipadas);
        info_cuota.setVisibility(View.GONE);
        if (numero_cuotas_pagadas >= hip.getPlazoActual(amortizaciones_anticipadas)) layout_porcentaje_aplicado.setVisibility(View.GONE);
        else{
            double porcentaje_aplicado = hip.getPorcentajePorCuota(numero_cuotas_pagadas + 1, amortizaciones_anticipadas, euribors);
            if(hip.getTipo_hipoteca().equals("fija")) porcentaje_aplicado_valor.setText( porcentaje_aplicado+ "%");
            else if(hip.getTipo_hipoteca().equals("variable")){
                if(numero_cuotas_pagadas + 1 <= hip.getDuracion_primer_porcentaje_variable()) porcentaje_aplicado_valor.setText(porcentaje_aplicado + "%");
                else{
                    if (porcentaje_aplicado == 0) porcentaje_aplicado_valor.setText("0%");
                    else porcentaje_aplicado_valor.setText(formato.format(porcentaje_aplicado - hip.getPorcentaje_diferencial_variable()) + "% + " + hip.getPorcentaje_diferencial_variable() + "%");
                }
            }else{
                if(numero_cuotas_pagadas + 1 <= hip.getAnios_fija_mixta() * 12) porcentaje_aplicado_valor.setText(porcentaje_aplicado + "%");
                else {
                    if (porcentaje_aplicado == 0) porcentaje_aplicado_valor.setText("0%");
                    else porcentaje_aplicado_valor.setText(formato.format(porcentaje_aplicado - hip.getPorcentaje_diferencial_mixta()) + "% + " + hip.getPorcentaje_diferencial_mixta() + "%");
                }
            }
        }


        //String numeroFormateado = formato.format(hip.getDineroRestanteActual(numero_cuotas_pagadas, amortizaciones_anticipadas, euribors))  + "€"; // Formatear el número
        double capitalPendiente  = hip.getCapitalPendienteTotalActual(hip.getNumeroCuotaActual(amortizaciones_anticipadas), amortizaciones_anticipadas, euribors);

        double interesesTotales    = hip.getInteresesHastaNumPago(hip.getPlazoActual(amortizaciones_anticipadas), amortizaciones_anticipadas, euribors);
        double interesesPagados    = hip.getInteresesHastaNumPago(hip.getNumeroCuotaActual(amortizaciones_anticipadas), amortizaciones_anticipadas, euribors);
        double interesesPendientes = interesesTotales - interesesPagados;
        double dineroRestante = interesesPendientes + capitalPendiente;

        dinero_restante_a_pagar.setText(formato.format(Math.abs(dineroRestante)));
        nombre_hipoteca.setText(hip.getNombre());
        tipo_hipoteca_seg.setText(hip.getTipo_hipoteca().substring(0, 1).toUpperCase() + hip.getTipo_hipoteca().substring(1));
        ponerLogoBanco();
        int i = 0;
        while(i < comunidades_base_datos.length){
            if(hip.getComunidad_autonoma().equals(comunidades_base_datos[i])) break;
            i++;
        }
        comunidad_autonoma_seg.setText(comunidades[i]);
        ArrayList<Integer> anios_meses = hip.getAniosMesesRestantes(amortizaciones_anticipadas);
        if(anios_meses.get(0) > 0) anios_restantes_hipoteca.setText(anios_meses.get(0) + " años " + anios_meses.get(1) + " meses");
        else anios_restantes_hipoteca.setText(anios_meses.get(1) + " meses");
        mes_actual_cuota.setText(hip.getNombreMesActual(amortizaciones_anticipadas));
        int plazoTotalActual = hip.getPlazoActual(amortizaciones_anticipadas);

        cantidad_pendiente = hip.getCapitalPendienteTotalActual(numero_cuotas_pagadas,amortizaciones_anticipadas, euribors);
        numero_cuotas_restantes = hip.getPlazoActual(amortizaciones_anticipadas) - numero_cuotas_pagadas;

        if(hip.getTipo_hipoteca().equals("fija")) {
            porcentaje_aplicado  = hip.getPorcentaje_fijo();
            info_dinero_restante.setVisibility(View.GONE);
        } else if(hip.getTipo_hipoteca().equals("variable")) {
            //Si cumple la condicion, esta aplicando el primer porcentaje fijado, en otro caso el diferencial + euribor
            porcentaje_aplicado  = hip.getNumeroCuotaActual(amortizaciones_anticipadas) < hip.getDuracion_primer_porcentaje_variable() ? hip.getPrimer_porcentaje_variable() : hip.getEuriborActual(euribors) + hip.getPorcentaje_diferencial_variable();
            if (porcentaje_aplicado < 0) porcentaje_aplicado = 0;
        } else {
            //Si cumple la condicion, esta en la fase fija, en otro en la variable
            porcentaje_aplicado  = hip.getNumeroCuotaActual(amortizaciones_anticipadas) <= hip.getAnios_fija_mixta() * 12 ? hip.getPorcentaje_fijo_mixta() : hip.getEuriborActual(euribors) + hip.getPorcentaje_diferencial_mixta();
            if (porcentaje_aplicado < 0) porcentaje_aplicado = 0;
        }

        if (hip.siguienteCuotaRevision(amortizaciones_anticipadas)) info_cuota.setVisibility(View.VISIBLE);

        if(anios_meses.get(0) <= 0 && anios_meses.get(1) <= 0){
            numero_cuotas_restantes = 0;
            info_cuota.setVisibility(View.GONE);
        }

        double cuota_mensual = hip.cogerCuotaActual(numero_cuotas_pagadas + 1, amortizaciones_anticipadas, euribors);
        layout_cuota_seguimiento.setVisibility(View.VISIBLE);
        layout_capital_intereses1.setVisibility(View.VISIBLE);
        layout_capital_intereses2.setVisibility(View.VISIBLE);
        //SI HAY AMORTIZACION EN LA SIGUIENTE CUOTA
        if(amortizaciones_anticipadas.containsKey(numero_cuotas_pagadas + 1)){
            double amortizacion_ant = (Double) amortizaciones_anticipadas.get(numero_cuotas_pagadas + 1).get(1);
            layout_amortizacion_anticipada.setVisibility(View.VISIBLE);
            if(amortizaciones_anticipadas.get(numero_cuotas_pagadas + 1).get(0).equals("total")) {
                layout_cuota_seguimiento.setVisibility(View.GONE);
                layout_capital_intereses1.setVisibility(View.GONE);
                layout_capital_intereses2.setVisibility(View.GONE);
            }
            amortizacion_anticipada_valor.setText(formato.format(amortizacion_ant) + "€");
        }else layout_amortizacion_anticipada.setVisibility(View.GONE);

        numero_cuota_actual.setText("Cuotas pagadas: " + numero_cuotas_pagadas + " / " + plazoTotalActual);

        cuotaFormateada = formato.format(cuota_mensual) + "€"; // Formatear el número
        cuota_mensual_seguimiento.setText(cuotaFormateada);

        //double capitalPendiente = hip.getCapitalPendienteTotalActual(hip.getNumeroCuotaActual(amortizaciones_anticipadas), amortizaciones_anticipadas, euribors);
        if(amortizaciones_anticipadas.containsKey(numero_cuotas_pagadas + 1)) capitalPendiente -= (Double) amortizaciones_anticipadas.get(numero_cuotas_pagadas + 1).get(1);
        String capitalFormateado = formato.format(Math.abs(hip.getCapitalAmortizadoMensual(cuota_mensual, capitalPendiente, porcentaje_aplicado))) + "€";
        capital_cuota_mensual.setText(capitalFormateado);

        String interesesFormateado = formato.format(Math.abs(hip.getInteresMensual(capitalPendiente, porcentaje_aplicado))) + "€";
        intereses_cuota_mensual.setText(interesesFormateado);
        setVisibility(View.VISIBLE);

        progressBar.setVisibility(View.GONE);

    }

    private void ponerLogoBanco(){
        switch (hip.getBanco_asociado()){
            case "ING":
                logo_banco_seg.setImageResource(R.drawable.logo_ing);
                break;
            case "SANTANDER":
                logo_banco_seg.setImageResource(R.drawable.logo_santander);
                break;
            case "BBVA":
                logo_banco_seg.setImageResource(R.drawable.logo_bbva);
                break;
            case "CAIXABANK":
                logo_banco_seg.setImageResource(R.drawable.logo_caixabank);
                break;
            case "BANKINTER":
                logo_banco_seg.setImageResource(R.drawable.logo_bankinter);
                break;
            case "EVO BANCO":
                logo_banco_seg.setImageResource(R.drawable.logo_evo_banco);
                break;
            case "SABADELL":
                logo_banco_seg.setImageResource(R.drawable.logo_sabadell);
                break;
            case "UNICAJA":
                logo_banco_seg.setImageResource(R.drawable.logo_unicaja);
                break;
            case "DEUTSCHE BANK":
                logo_banco_seg.setImageResource(R.drawable.logo_deutsche_bank);
                break;
            case "OPEN BANK":
                logo_banco_seg.setImageResource(R.drawable.logo_open_bank);
                break;
            case "KUTXA BANK":
                logo_banco_seg.setImageResource(R.drawable.logo_kutxa_bank);
                break;
            case "IBERCAJA":
                logo_banco_seg.setImageResource(R.drawable.logo_ibercaja);
                break;
            case "ABANCA":
                logo_banco_seg.setImageResource(R.drawable.logo_abanca);
                break;
            default:
                logo_banco_seg.setImageResource(R.drawable.logo_bancodesconocido);
                break;
        }
    }
    private void eventos(){

        compruebaSiVinculacionAnual();

        close_icon_seg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });
        btn_cuadro_amortizacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(VisualizarHipotecaSeguimiento.this, Cuadro_amortizacion.class);
                i.putExtra("hipoteca", hip);
                i.putExtra("tipo_hipoteca", hip.getTipo_hipoteca());
                i.putExtra("amortizaciones_anticipadas", (Serializable) amortizaciones_anticipadas);
                i.putExtra("euribors", (Serializable) euribors);
                startActivity(i);
            }
        });

        btn_amortizar_antes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Comprobar si la siguiente cuota es la ultima que no deje amortizar
                if(hip.getNumeroCuotaActual(amortizaciones_anticipadas) < hip.getPlazoActual(amortizaciones_anticipadas) - 1){
                    //Comprobar si ya hay una amortización para la siguiente cuota
                    if(amortizaciones_anticipadas.containsKey(hip.getNumeroCuotaActual(amortizaciones_anticipadas) + 1)){
                        Toast.makeText(VisualizarHipotecaSeguimiento.this, getString(R.string.amortizacion_existente), Toast.LENGTH_LONG).show();
                    }
                    else{
                        Intent i = new Intent(VisualizarHipotecaSeguimiento.this, AmortizarAntes.class);
                        i.putExtra("cuota_actual", cuotaFormateada);
                        i.putExtra("porcentaje_aplicado", porcentaje_aplicado);
                        i.putExtra("cuotas_pendientes", numero_cuotas_restantes);
                        i.putExtra("cantidad_pendiente", cantidad_pendiente);
                        i.putExtra("amortizaciones_anticipadas", (Serializable) amortizaciones_anticipadas);
                        i.putExtra("hipoteca", hip);
                        i.putExtra("tipo_hipoteca", hip.getTipo_hipoteca());
                        i.putExtra("euribors", (Serializable) euribors);
                        startActivity(i);
                        finish();
                    }
                } else if(hip.getNumeroCuotaActual(amortizaciones_anticipadas) + 1 == hip.getPlazoActual(amortizaciones_anticipadas) - 1) Toast.makeText(VisualizarHipotecaSeguimiento.this, getString(R.string.no_puede_amortizar), Toast.LENGTH_LONG).show();
                else Toast.makeText(VisualizarHipotecaSeguimiento.this, getString(R.string.no_puede_amortizar_fin), Toast.LENGTH_LONG).show();

            }
        });

        info_dinero_restante.setOnClickListener(new View.OnClickListener() {
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
                        .setText("El dinero restante está estimado con el Euribor actual manteniéndolo fijo el resto de años. Puede tener alguna modificación")
                        .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black))
                        .setTextIsHtml(true)
                        .setIconDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.info))
                        .setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white))
                        .setBalloonAnimation(BalloonAnimation.FADE)
                        .build();

                balloon.showAlignTop(info_dinero_restante);
            }
        });

        info_cuota.setOnClickListener(new View.OnClickListener() {
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
                        .setText("La cuota mostrada está en función del euribor aplicado actualmente. Los datos van a variar ya que la siguiente cuota hay que actualizarla con el nuevo Euribor")
                        .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black))
                        .setTextIsHtml(true)
                        .setIconDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.info))
                        .setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white))
                        .setBalloonAnimation(BalloonAnimation.FADE)
                        .build();

                balloon.showAlignTop(info_cuota);
            }
        });

        btn_grafico_gastos_totales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!premium){
                    CustomDialogoPremium dialogo = new CustomDialogoPremium();
                    dialogo.show(getSupportFragmentManager(), "dialogo");
                }else {
                    Intent i = new Intent(VisualizarHipotecaSeguimiento.this, grafico_gastos_totales.class);
                    i.putExtra("tipo_hipoteca", hip.getTipo_hipoteca());
                    i.putExtra("hipoteca", hip);
                    i.putExtra("amortizaciones_anticipadas", amortizaciones_anticipadas);
                    startActivity(i);
                }
            }
        });

        btn_grafico_intereses_capital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!premium){
                    CustomDialogoPremium dialogo = new CustomDialogoPremium();
                    dialogo.show(getSupportFragmentManager(), "dialogo");
                }else {
                    Intent i = new Intent(VisualizarHipotecaSeguimiento.this, grafico_intereses_capital.class);
                    i.putExtra("tipo_hipoteca", hip.getTipo_hipoteca());
                    i.putExtra("hipoteca", hip);
                    i.putExtra("amortizaciones_anticipadas", amortizaciones_anticipadas);
                    i.putExtra("euribors", (Serializable) euribors);
                    startActivity(i);
                }
            }
        });

        btn_borrar_amort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialogo = new AlertDialog.Builder(VisualizarHipotecaSeguimiento.this)
                        .setPositiveButton(getString(R.string.si_eliminar_cuenta), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                double porcentaje_comision;
                                String tipo_amort = (String) amortizaciones_anticipadas.get(numero_cuotas_pagadas + 1).get(0);
                                if (tipo_amort.equals("parcial_plazo")) porcentaje_comision = (Double) amortizaciones_anticipadas.get(numero_cuotas_pagadas + 1).get(3);
                                else porcentaje_comision = (Double) amortizaciones_anticipadas.get(numero_cuotas_pagadas + 1).get(2);
                                if (porcentaje_comision > 0) eliminarComision(porcentaje_comision, tipo_amort);
                                else eliminarUltimaAmortizacion();
                            }
                        })
                        .setNegativeButton(getString(R.string.no_eliminar_cuenta), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setTitle("ELIMINAR ÚLTIMA AMORTIZACIÓN ANTICIPADA").setMessage("¿Desea eliminar la última amortización anticipada?").create();
                dialogo.show();
            }
        });
     }


    public void construirGraficoAportadoVsAFinanciar(){


        titulo_grafico.setText("Aportado vs a financiar");
        double capitalPendiente  = hip.getCapitalPendienteTotalActual(hip.getNumeroCuotaActual(amortizaciones_anticipadas), amortizaciones_anticipadas, euribors);
        double capitalAmortizado = (hip.getPrecio_vivienda() - hip.getCantidad_abonada()) - capitalPendiente;


        double interesesTotales    = hip.getInteresesHastaNumPago(hip.getPlazoActual(amortizaciones_anticipadas), amortizaciones_anticipadas, euribors);
        double interesesPagados    = hip.getInteresesHastaNumPago(hip.getNumeroCuotaActual(amortizaciones_anticipadas), amortizaciones_anticipadas, euribors);
        double interesesPendientes = interesesTotales - interesesPagados;

        Pie pie = AnyChart.pie();
        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("CAPITAL AMORTIZADO", Double.parseDouble(formatoDouble.format(capitalAmortizado))));
        data.add(new ValueDataEntry("CAPITAL PENDIENTE", Double.parseDouble(formatoDouble.format(capitalPendiente))));
        data.add(new ValueDataEntry("INTERESES PENDIENTES", Double.parseDouble(formatoDouble.format(interesesPendientes))));
        data.add(new ValueDataEntry("INTERESES PAGADOS", Double.parseDouble(formatoDouble.format(interesesPagados))));
        pie.data(data);
        pie.labels().fontSize(18);
        pie.labels().position("outside");
        pie.connectorLength(20);
        pie.legend().enabled(true);
        pie.legend().fontSize(12d);
        pie.legend().padding(10d, 10d, 10d, 10d);
        pie.legend().itemsLayout(LegendLayout.HORIZONTAL_EXPANDABLE);
        pie.legend().position("bottom");
        //pie.width(400);
        grafico.setChart(pie);
        grafico.invalidate();


        capital_amortizado.setText("" + formato.format(Math.abs(capitalAmortizado)) + "€");
        capital_pendiente.setText("" + formato.format(Math.abs(capitalPendiente)) + "€");
        intereses_pagados.setText("" + formato.format(Math.abs(interesesPagados)) + "€");
        intereses_pendientes.setText("" + formato.format(Math.abs(interesesPendientes)) + "€");

        //COMPRUEBA SI EL USUARIO ES NO PREMIUM
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userMail = user.getEmail();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("usuarios").whereEqualTo("correo", userMail);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                    if(!document.getBoolean("premium")) {
                        //Al no ser premium no deja interactuar con los graficos
                        grafico.setAlpha(0.0f); //Hace que el grafico se vea menos
                        tabla_valores_grafico.setVisibility(View.GONE);
                        premium = false;
                        texto_si_no_premium.setVisibility(View.VISIBLE);
                        grafico.setClickable(false);
                    } else premium = true;

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

    }

    @Override
    public void sendInput(double input) {

        Map<String, Object> nuevosDatos = new HashMap<>();
        hip.getArrayVinculacionesAnual().add(input);
        nuevosDatos.put("arrayVinculacionesAnual", hip.getArrayVinculacionesAnual());


        FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                        Toast.makeText(VisualizarHipotecaSeguimiento.this, getString(R.string.vinculaciones_actualizdas), Toast.LENGTH_LONG).show();
                        /*Intent i = new Intent(VisualizarHipotecaSeguimiento.this, PaginaPrincipal.class);
                        startActivity(i);*/
                    }
                } else {
                    Log.e("ERROR", " getting documents");
                }
            }
        });

    }

    /** Muestra un dialogo en el que el usuario tiene que introducir la nueva cantidad de viculaciones anuales
        si es un nuevo año de hipoteca y el usuario no lo ha introducido aun **/
    public void compruebaSiVinculacionAnual(){
        Calendar fecha = Calendar.getInstance();
        fecha.setTime(hip.getFecha_inicio());
        int i = fecha.get(Calendar.DAY_OF_YEAR); //dia de inicio de la hipoteca + 1
        if(fecha.get(Calendar.YEAR)%4 == 0 && fecha.get(Calendar.DAY_OF_YEAR) > 59) i = i - 1; //comprueba si año bisiesto

        //calculo la variable aniosHastaAhora, que tiene el numero de años + 1  que llevamos de hipoteca
        int c = hip.getPlazo_anios() - getAniosMesesRestantes(hip.getPlazo_anios(), getNumeroCuotaActual(hip.getFecha_inicio(), hip.getPlazo_anios())).get(0);

        if(i <= Calendar.getInstance().get(Calendar.DAY_OF_YEAR) && hip.getArrayVinculacionesAnual().size() < c) {
            NuevaVinculacionAnualFragment fragment = new NuevaVinculacionAnualFragment();
            fragment.show(getSupportFragmentManager(), "NuevaVinculacionAnualFragment");
        }
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
        // Fin de hipoteca
        if (numeroPagoActual >= plazo * 12) numeroPagoActual = plazo * 12;
        return numeroPagoActual;

    }


    public void setVisibility(int visibility){
        dinero_restante_a_pagar.setVisibility(visibility);
        nombre_hipoteca.setVisibility(visibility);
        tipo_hipoteca_seg.setVisibility(visibility);
        logo_banco_seg.setVisibility(visibility);
        comunidad_autonoma_seg.setVisibility(visibility);
        anios_restantes_hipoteca.setVisibility(visibility);
        mes_actual_cuota.setVisibility(visibility);
        cuota_mensual_seguimiento.setVisibility(visibility);
        capital_cuota_mensual.setVisibility(visibility);
        intereses_cuota_mensual.setVisibility(visibility);
        numero_cuota_actual.setVisibility(visibility);

        btn_cuadro_amortizacion.setVisibility(visibility);
        btn_amortizar_antes.setVisibility(visibility);

        //GRÁFICOS
        titulo_grafico.setVisibility(visibility);
        grafico.setVisibility(visibility);
        capital_amortizado.setVisibility(visibility);
        capital_pendiente.setVisibility(visibility);
        intereses_pagados.setVisibility(visibility);
        intereses_pendientes.setVisibility(visibility);
        info_grafico.setVisibility(visibility);

        btn_grafico_gastos_totales.setVisibility(visibility);
        btn_grafico_intereses_capital.setVisibility(visibility);


        titulo_seguimiento_hipoteca.setVisibility(visibility);
        txt_tipo_hipoteca_seguimiento.setVisibility(visibility);
        txt_banco_hipoteca.setVisibility(visibility);
        txt_com_autonoma_hipoteca.setVisibility(visibility);
        txt_dinero_falta_pagar.setVisibility(visibility);
        txt_tiempo_restante.setVisibility(visibility);
        txt_cuota_de.setVisibility(visibility);
        layout_info_cuota_hip.setVisibility(visibility);
        layout_btns_graficos.setVisibility(visibility);
    }

    public void rellenarTablaAmortizaciones(){

        if(amortizaciones_anticipadas.size() > 0){
            txt_no_amortizaciones.setVisibility(View.GONE);
            tabla_seg_amortizacion.setVisibility(View.VISIBLE);
        }

        for (Map.Entry<Integer, List<Object>> amortizacion : amortizaciones_anticipadas.entrySet()) {

            // Crear una nueva fila y agregarla al TableLayout
            TableRow tableRow = new TableRow(this);
            tabla_seg_amortizacion.addView(tableRow);

            TextView numCuota = new TextView(this);
            numCuota.setText(Integer.toString(amortizacion.getKey()));
            numCuota.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tableRow.addView(numCuota);

            TextView tipo = new TextView(this);
            tipo.setText(amortizacion.getValue().get(0).toString());
            tipo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tableRow.addView(tipo);

            TextView capital = new TextView(this);
            capital.setText(amortizacion.getValue().get(1).toString());
            capital.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tableRow.addView(capital);

            TextView mesesReducidos = new TextView(this);
            if(amortizacion.getValue().get(0).toString().equals("parcial_plazo")) mesesReducidos.setText(amortizacion.getValue().get(2).toString());
            else mesesReducidos.setText("--");
            mesesReducidos.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tableRow.addView(mesesReducidos);

            TextView comision = new TextView(this);
            double porcentaje_comision = (Double) amortizacion.getValue().get(amortizacion.getValue().size() - 1);
            double comision_valor = ((Double) amortizacion.getValue().get(1) * porcentaje_comision) / 100;


            comision.setText(formato.format(comision_valor));
            comision.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tableRow.addView(comision);

        }

    }

    private void eliminarComision(double porcentaje_comision, String tipo_amort){


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference hipotecasRef = db.collection("hipotecas_seguimiento");
        Query query = hipotecasRef.whereEqualTo("nombre", hip.getNombre()).whereEqualTo("idUsuario", FirebaseAuth.getInstance().getCurrentUser().getUid());

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        double comision = ((Double) amortizaciones_anticipadas.get(numero_cuotas_pagadas + 1).get(1) * porcentaje_comision) / 100;
                        // Obtener la referencia al documento
                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                        DocumentReference docRef = document.getReference();
                        long totalGastos = document.getLong("totalGastos");
                        docRef.update("totalGastos", totalGastos - comision);
                        eliminarUltimaAmortizacion();
                    } else {
                        Log.d(TAG, "No se encontraron documentos");
                    }
                } else {
                    Log.d(TAG, "Error al obtener la hipoteca para borrar la comision de amortizacion anticipada: ", task.getException());
                }
            }
        });



    }

    public void eliminarUltimaAmortizacion() {

        amortizaciones_anticipadas.remove(numero_cuotas_pagadas + 1);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference amortizacionesRef = db.collection("amortizaciones_anticipadas");
        Query query_amort = amortizacionesRef.whereEqualTo("idUsuario", FirebaseAuth.getInstance().getCurrentUser().getUid()).whereEqualTo("nombre_hipoteca", hip.getNombre());
        query_amort.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (!querySnapshot.isEmpty()) {
                        DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                        Map<String, Object> nuevasAmortizaciones = new HashMap<>();

                        for (Map.Entry<Integer, List<Object>> entry : amortizaciones_anticipadas.entrySet()) {
                            String key = entry.getKey().toString();
                            List<Object> value = entry.getValue();
                            nuevasAmortizaciones.put(key, value);
                        }
                        documentSnapshot.getReference().update("amortizaciones_anticipadas", nuevasAmortizaciones).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(VisualizarHipotecaSeguimiento.this, "La amortización anticipada ha sido eliminada con éxito", Toast.LENGTH_LONG).show();
                                Intent i = new Intent(VisualizarHipotecaSeguimiento.this, PaginaPrincipal.class);
                                startActivity(i);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("ERROR", " al eliminar la ultima amortizacion", e);
                            }
                        });

                    }
                }
            }
        });


    }


}



