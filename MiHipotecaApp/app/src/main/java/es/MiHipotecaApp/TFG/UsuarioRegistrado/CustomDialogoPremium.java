package es.MiHipotecaApp.TFG.UsuarioRegistrado;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import es.MiHipotecaApp.TFG.R;
import es.MiHipotecaApp.TFG.UsuarioRegistrado.InformacionUsuario.PasarPremium;

public class CustomDialogoPremium extends DialogFragment {

    public CustomDialogoPremium() {}

    public static CustomDialogoPremium newInstance() {
        return new CustomDialogoPremium();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialogo_premium, null);
        builder.setView(view);

        ImageView cerrar = view.findViewById(R.id.dialog_close_button);
        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button botonPremium = view.findViewById(R.id.dialog_premium_button);
        botonPremium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent i = new Intent(getActivity(), PasarPremium.class);
                startActivity(i);
            }
        });

        return builder.create();
    }
}