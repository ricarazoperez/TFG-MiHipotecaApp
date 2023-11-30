package es.MiHipotecaApp.TFG.UsuarioRegistrado.InformacionUsuario;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;


import es.MiHipotecaApp.TFG.R;

public class custom_dialog_avatares  extends AppCompatDialogFragment {
    private customDialogInterface dialogoInterface;
    private ImageView imagen_perfil_1;
    private ImageView imagen_perfil_2;
    private ImageView imagen_perfil_3;
    private ImageView imagen_perfil_4;
    private ImageView imagen_perfil_5;

    private int avatar;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());


        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_dialog_avateres,null);
        builder.setView(view).setTitle("Seleccion avatar").
                setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogoInterface.setAvatares(avatar);
                    }
                });

        imagen_perfil_1 = view.findViewById(R.id.imagen_perfil_1);
        imagen_perfil_2 = view.findViewById(R.id.imagen_perfil_2);
        imagen_perfil_3 = view.findViewById(R.id.imagen_perfil_3);
        imagen_perfil_4 = view.findViewById(R.id.imagen_perfil_4);
        imagen_perfil_5 = view.findViewById(R.id.imagen_perfil_5);


        imagen_perfil_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                desmarcarImagen();
                avatar = 1;
                imagen_perfil_1.setImageAlpha(75);
            }
        });

        imagen_perfil_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                desmarcarImagen();
                avatar = 2;
                imagen_perfil_2.setImageAlpha(75);
            }
        });

        imagen_perfil_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                desmarcarImagen();
                avatar = 3;
                imagen_perfil_3.setImageAlpha(75);

            }
        });

        imagen_perfil_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                desmarcarImagen();
                avatar = 4;
                imagen_perfil_4.setImageAlpha(75);

            }
        });

        imagen_perfil_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                desmarcarImagen();
                avatar = 5;
                imagen_perfil_5.setImageAlpha(75);

            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dialogoInterface = (customDialogInterface) context;

    }

    public interface customDialogInterface {
         void setAvatares(int avatar);
    }

    public void desmarcarImagen(){
        switch (avatar) {
            case 1:
                imagen_perfil_1.setImageAlpha(255);
                break;
            case 2:
                imagen_perfil_2.setImageAlpha(255);
                break;
            case 3:
                imagen_perfil_3.setImageAlpha(255);
                break;
            case 4:
                imagen_perfil_4.setImageAlpha(255);
                break;
            case 5:
                imagen_perfil_5.setImageAlpha(255);
                break;
        }
    }

}
