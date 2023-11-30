package es.MiHipotecaApp.TFG.UsuarioRegistrado.HipotecasSeguimiento;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import es.MiHipotecaApp.TFG.R;
import es.MiHipotecaApp.TFG.Transfers.HipotecaSeguimiento;

public class AdaptadorHipotecasSeguimiento extends RecyclerView.Adapter<AdaptadorHipotecasSeguimiento.ViewHolderHipotecasSeguimiento> implements View.OnClickListener{

    private ArrayList<HipotecaSeguimiento> hipotecasSeg;
    private View.OnClickListener listener;

    public AdaptadorHipotecasSeguimiento(ArrayList<HipotecaSeguimiento> hipotecasSeg) {
        this.hipotecasSeg = hipotecasSeg;
    }

    public HipotecaSeguimiento getItem(int position){
        return hipotecasSeg.get(position);
    }

    @NonNull
    @Override
    public AdaptadorHipotecasSeguimiento.ViewHolderHipotecasSeguimiento onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hipoteca_seguimiento, null, false);
        view.setOnClickListener(this);
        return new ViewHolderHipotecasSeguimiento(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorHipotecasSeguimiento.ViewHolderHipotecasSeguimiento holder, int position) {
        if(position == hipotecasSeg.size() - 1) {
            holder.fotoHipoteca.setImageResource(R.drawable.boton_anadir_hipoteca_seg);
            holder.titulo_tarjeta_hipoteca.setText("Añadir hipoteca nueva");
            holder.tipo_hipoteca.setVisibility(View.GONE);

        }
        else {
            holder.tituloHipoteca.setText(hipotecasSeg.get(position).getNombre());
            String resourceName = ponerLogoBanco(hipotecasSeg.get(position).getBanco_asociado());
            int resId = holder.itemView.getContext().getResources().getIdentifier(resourceName, "drawable", holder.itemView.getContext().getPackageName());
            holder.fotoHipoteca.setImageResource(resId);
            holder.tipo_hipoteca.setText("Tipo: " + hipotecasSeg.get(position).getTipo_hipoteca());
        }

    }

    @Override
    public int getItemCount() {
        return hipotecasSeg.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(listener != null) {
            listener.onClick(view);
        }
    }

    public void setHipotecasSeg(ArrayList<HipotecaSeguimiento> hipotecasSeg) {
        this.hipotecasSeg = hipotecasSeg;
    }

    private String ponerLogoBanco(String banco){
        String banco_logo = "";
        switch (banco){
            case "ING":
                banco_logo = "logo_ing";
                break;
            case "SANTANDER":
                banco_logo = "logo_santander";
                break;
            case "BBVA":
                banco_logo = "logo_bbva";
                break;
            case "CAIXABANK":
                banco_logo = "logo_caixabank";
                break;
            case "BANKINTER":
                banco_logo = "logo_bankinter";
                break;
            case "EVO BANCO":
                banco_logo = "logo_evo_banco";
                break;
            case "SABADELL":
                banco_logo = "logo_sabadell";
                break;
            case "UNICAJA":
                banco_logo = "logo_unicaja";
                break;
            case "DEUTSCHE BANK":
                banco_logo = "logo_deutsche_bank";
                break;
            case "OPEN BANK":
                banco_logo = "logo_open_bank";
                break;
            case "KUTXA BANK":
                banco_logo = "logo_kutxa_bank";
                break;
            case "IBERCAJA":
                banco_logo = "logo_ibercaja";
                break;
            case "ABANCA":
                banco_logo = "logo_abanca";
                break;
        }
        return banco_logo;
    }
    public class ViewHolderHipotecasSeguimiento extends RecyclerView.ViewHolder{

        TextView tituloHipoteca;
        ImageView fotoHipoteca;
        TextView titulo_tarjeta_hipoteca;
        TextView tipo_hipoteca;

        public ViewHolderHipotecasSeguimiento(@NonNull View itemView) {
            super(itemView);
            tituloHipoteca = itemView.findViewById(R.id.titulo_tarjeta_hipoteca);
            fotoHipoteca   = itemView.findViewById(R.id.logo_banco_tarjeta);
            titulo_tarjeta_hipoteca = itemView.findViewById(R.id.titulo_tarjeta_hipoteca);
            tipo_hipoteca = itemView.findViewById(R.id.tipo_hipoteca);

        }
    }
}
