package es.MiHipotecaApp.TFG.ChatUsuarios;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.BreakIterator;

import es.MiHipotecaApp.TFG.R;

public class ReceivedMessageViewHolder extends MessageListAdapter.ViewHolder {
    public TextView textViewSender;
    public TextView textViewMessage;
    public BreakIterator textViewSentAt;

    public ReceivedMessageViewHolder(View view) {
        super(view);
        textViewSender = itemView.findViewById(R.id.textViewSender);
        textViewMessage = itemView.findViewById(R.id.textViewMessage);
    }
}



