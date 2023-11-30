package es.MiHipotecaApp.TFG.UsuarioRegistrado;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import es.MiHipotecaApp.TFG.R;
import es.MiHipotecaApp.TFG.SimularHipoteca.RecyclerAdapter;
import es.MiHipotecaApp.TFG.Transfers.Oferta;

public class custom_dialog_oferta  extends AppCompatDialogFragment {
    private Oferta oferta;
    private EditText et_nombre;

    private pasarDatos pasarDatos;
    private String tipo;

    private RecyclerAdapter.RecyclerHolder holder;
    public custom_dialog_oferta(Oferta oferta){
        this.oferta = oferta;
    }
    public custom_dialog_oferta(Oferta oferta, RecyclerAdapter.RecyclerHolder holder,String tipo){
        this.holder = holder;
        this.oferta = oferta;
        this.tipo = tipo;
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.oferta_dialog, container, false);
        et_nombre = view.findViewById(R.id.et_nombreOferta);
        Button boton_cancelar = view.findViewById(R.id.boton_cancelar);
        Button boton_guardar = view.findViewById(R.id.boton_guardar);
        boton_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombreOferta = et_nombre.getText().toString();
                pasarDatos.pasarNombre(nombreOferta,holder,oferta,tipo);
                dismiss();
            }
        });
        boton_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        return view;
    }
    public interface pasarDatos{
        void pasarNombre(String nombre,RecyclerAdapter.RecyclerHolder holder,Oferta oferta,String tipo);
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        pasarDatos = (pasarDatos)context;

    }

}
