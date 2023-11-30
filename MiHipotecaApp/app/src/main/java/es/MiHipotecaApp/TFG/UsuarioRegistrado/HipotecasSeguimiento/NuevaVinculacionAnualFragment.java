package es.MiHipotecaApp.TFG.UsuarioRegistrado.HipotecasSeguimiento;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import es.MiHipotecaApp.TFG.R;

public class NuevaVinculacionAnualFragment extends AppCompatDialogFragment {

    private EditText mGastosEditText;

    private NuevoAnioHipotecaListener mOnInputListener;


    public interface NuevoAnioHipotecaListener {
        void sendInput(double input);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nuevo_anio_hipoteca, container, false);

        TextView tituloTextView = view.findViewById(R.id.tituloTextView);

        TextView descripcionTextView = view.findViewById(R.id.descripcionTextView);

        mGastosEditText = view.findViewById(R.id.gastosEditText);

        Button introducirButton = view.findViewById(R.id.introducirButton);
        introducirButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double input;
                if (mGastosEditText.getText().toString().equals("")) {
                    input = 0;
                } else {
                    input = Double.parseDouble(mGastosEditText.getText().toString());
                }
                mOnInputListener.sendInput(input);
                dismiss();
            }
        });

        return view;
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
        try {
            mOnInputListener = (NuevoAnioHipotecaListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnInputListener");
        }
    }

}
