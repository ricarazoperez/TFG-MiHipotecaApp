package es.MiHipotecaApp.TFG.UsuarioRegistrado.HipotecasSeguimiento.Graficos;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.View;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.enums.LegendLayout;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.MiHipotecaApp.TFG.R;
import es.MiHipotecaApp.TFG.Transfers.HipotecaSegFija;
import es.MiHipotecaApp.TFG.Transfers.HipotecaSegMixta;
import es.MiHipotecaApp.TFG.Transfers.HipotecaSegVariable;
import es.MiHipotecaApp.TFG.Transfers.HipotecaSeguimiento;


public class grafico_intereses_capital extends AppCompatActivity {

    private AnyChartView grafico;
    private HipotecaSeguimiento hip;
    private CircleImageView closeIcon;

    private HashMap<Integer, List<Object>> amortizaciones_hip;

    private List<Double> euribors;
    private DecimalFormat formato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grafico_intereses_capital);
        // Establecer el formato a dos decimales
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setDecimalSeparator('.');
        formato = new DecimalFormat("#.##", simbolos);

        initUI();
        construirGraficoLineas();
        eventos();
    }

    private void initUI(){
        grafico = findViewById(R.id.grafico_intereses_capital);
        if(getIntent().getStringExtra("tipo_hipoteca").equals("fija")) hip = (HipotecaSegFija) getIntent().getSerializableExtra("hipoteca");
        else if (getIntent().getStringExtra("tipo_hipoteca").equals("variable")) hip = (HipotecaSegVariable) getIntent().getSerializableExtra("hipoteca");
        else hip = (HipotecaSegMixta) getIntent().getSerializableExtra("hipoteca");
        amortizaciones_hip = (HashMap<Integer, List<Object>>) getIntent().getSerializableExtra("amortizaciones_anticipadas");
        euribors = (List<Double>) getIntent().getSerializableExtra("euribors");
        closeIcon = findViewById(R.id.close_icon_graf_int);
    }

    public void construirGraficoLineas(){


        List<DataEntry> capitalAnual = new ArrayList<>();
        List<DataEntry> interesesAnuales = new ArrayList<>();
        List<DataEntry> cuotaAnual = new ArrayList<>();
        List<DataEntry> vinculacionesAnules = new ArrayList<>();

        Calendar anio = Calendar.getInstance();
        anio.setTime(hip.getFecha_inicio());

        int aniosActuales = hip.aniosActualesHipoteca(hip.getPlazoActual(amortizaciones_hip));

        for(int i = 1; i <= aniosActuales; i++){
            ArrayList<Double> valores = hip.getFilaCuadroAmortizacionAnual(anio.get(Calendar.YEAR) + i - 1, i, amortizaciones_hip, euribors);

            int anioActual = anio.get(Calendar.YEAR) + i - 1;
            capitalAnual.add(new ValueDataEntry(anioActual, Double.parseDouble(formato.format(valores.get(1)))));
            interesesAnuales.add(new ValueDataEntry(anioActual, Double.parseDouble(formato.format(valores.get(2)))));
            cuotaAnual.add(new ValueDataEntry(anioActual, Double.parseDouble(formato.format(valores.get(0)))));
            if(i <= hip.getArrayVinculacionesAnual().size() - 1) vinculacionesAnules.add(new ValueDataEntry(anioActual, hip.getPosArrayVinculacionesAnual(i)));
            else vinculacionesAnules.add(new ValueDataEntry(anioActual, hip.getPosArrayVinculacionesAnual(hip.getArrayVinculacionesAnual().size() - 1)));
        }

        Cartesian lineChart = AnyChart.line();
        lineChart.line(capitalAnual).name("Capital anual").stroke("3 #00ff00");
        lineChart.line(interesesAnuales).name("Intereses anuales").stroke("3 #ff0000");
        lineChart.line(cuotaAnual).name("Cuota anual").stroke("2 #0000ff");
        lineChart.line(vinculacionesAnules).name("Vinculaciones anuales").stroke("2 #ffc107");

        lineChart.legend().enabled(true);
        lineChart.legend().fontSize(14d);
        lineChart.legend().padding(10d, 10d, 10d, 10d);
        lineChart.legend().itemsLayout(LegendLayout.HORIZONTAL_EXPANDABLE);
        lineChart.legend().position("bottom");
        grafico.setChart(lineChart);
        grafico.invalidate();
    }

    public void eventos(){
        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}