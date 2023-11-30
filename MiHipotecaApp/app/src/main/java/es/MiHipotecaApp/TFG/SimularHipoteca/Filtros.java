package es.MiHipotecaApp.TFG.SimularHipoteca;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import es.MiHipotecaApp.TFG.R;

public class Filtros extends AppCompatActivity {

    private CircleImageView closeIcon;
    private Spinner sp_bancos;
    private SeekBar precio , primer_pago, plazo;
    private TextView tv_precio , tv_primer_pago, tv_plazo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros);
        closeIcon = findViewById(R.id.close_icon_filtr);
        sp_bancos = findViewById(R.id.sp_bancos);
        precio = findViewById(R.id.seekBar_precio);
        primer_pago = findViewById(R.id.seekBar_primer_pago);
        plazo = findViewById(R.id.seekBar_plazo_prestamo);
        tv_precio = findViewById(R.id.tv_precio);
        tv_primer_pago = findViewById(R.id.tv_primer_pago);
        tv_plazo = findViewById(R.id.tv_plazo_prestamo);
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
        };
        sp_bancos.setAdapter(adapter);
        eventos();
    }

    private void eventos() {
        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        precio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tv_precio.setText(String.valueOf(i));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        primer_pago.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tv_primer_pago.setText(String.valueOf(i));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        plazo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                tv_plazo.setText(String.valueOf(i));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
}