package es.MiHipotecaApp.TFG.UsuarioRegistrado.HipotecasSeguimiento;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Calendar;

import es.MiHipotecaApp.TFG.R;
import es.MiHipotecaApp.TFG.Transfers.HipotecaSeguimiento;

public class custom_dialog_anios extends AppCompatDialogFragment {

    private SeekBar seekBar_elegir_anio;
    private TextView value_seek_bar;
    private customDialogInterface dialogoInterface;
    private HipotecaSeguimiento hip;
    private View view;

    public custom_dialog_anios(HipotecaSeguimiento hip){
        this.hip = hip;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.custom_dialog_anios,null);

        seekBar_elegir_anio  = view.findViewById(R.id.seekBar_elegir_anio);
        value_seek_bar = view.findViewById(R.id.value_seek_bar);

        Calendar inicio = Calendar.getInstance();
        inicio.setTime(hip.getFecha_inicio());
        // Dia actual
        Calendar actual = Calendar.getInstance();
        int year = actual.get(Calendar.YEAR);

        /**Si el año de inicio de la hipoteca es futuro, se pone ese valor por defecto en la seekBar,
           si no se pone el año actual */
        if(inicio.compareTo(actual) > 0) seekBar_elegir_anio.setProgress(year);
        else seekBar_elegir_anio.setProgress(actual.get(Calendar.YEAR));

        //Da valor al textView donde aparece el valor de la seekbar
        value_seek_bar.setText(Integer.toString(year));

        eventos(builder, year);

        return builder.create(); //Aquí se llama al método show() para mostrar el diálogo
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dialogoInterface = (custom_dialog_anios.customDialogInterface) context;

    }

    public interface customDialogInterface {
        void setAnio(int setAnio);
    }

    public void eventos(AlertDialog.Builder builder, int year){
        //Se encarga de actualizar el valor del textView del dialogo en funcion del uso de la seekBar
        seekBar_elegir_anio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Actualizar el valor del TextView con el progreso actual del SeekBar
                value_seek_bar.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // No es necesario implementar este método, pero lo incluyo por completitud
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // No es necesario implementar este método, pero lo incluyo por completitud
            }
        });

        int finalYear = year;
        builder.setView(view) //Aquí se añade la vista del layout personalizado
                .setTitle("Selecciona un año")
                .setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Comprueba que la fecha establecida esta dentro del rango posible
                        if (seekBar_elegir_anio.getProgress() >= finalYear && seekBar_elegir_anio.getProgress() <= finalYear + hip.getPlazo_anios())
                            dialogoInterface.setAnio(seekBar_elegir_anio.getProgress());
                        else Toast.makeText(getContext(), "Fecha introducida incorrecta", Toast.LENGTH_LONG).show();

                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });

    }

}