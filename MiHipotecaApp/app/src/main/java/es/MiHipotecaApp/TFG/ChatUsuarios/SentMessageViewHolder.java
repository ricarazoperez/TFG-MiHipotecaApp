package es.MiHipotecaApp.TFG.ChatUsuarios;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import es.MiHipotecaApp.TFG.R;

public class SentMessageViewHolder extends MessageListAdapter.ViewHolder {
    public TextView textViewMessageSent;
    public TextView textViewSentAt;

    public SentMessageViewHolder(View itemView) {
        super(itemView);
        textViewMessageSent = itemView.findViewById(R.id.textViewSender);
        textViewSentAt = itemView.findViewById(R.id.textViewSentAt);
    }
}