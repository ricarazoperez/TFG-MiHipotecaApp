package es.MiHipotecaApp.TFG.ChatUsuarios;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.MiHipotecaApp.TFG.R;
import es.MiHipotecaApp.TFG.Transfers.Message;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.ViewHolder> {
    private List<Message> messages;

    public MessageListAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new ViewHolder(view);
        /*
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;

        if (viewType == 0) {
            view = inflater.inflate(R.layout.received_message_layout, parent, false);
            return new ReceivedMessageViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.sent_message_layout, parent, false);
            return new SentMessageViewHolder(view);
        }*/
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.textViewMessage.setText(message.getText());
        holder.textViewSender.setText(message.getSender());
        holder.textViewSentAt.setText(message.getSentAt().toString());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewMessage;
        public TextView textViewSender;
        public TextView textViewSentAt;

        public ViewHolder(View view) {
            super(view);
            textViewMessage = view.findViewById(R.id.textViewMessage);
            textViewSender = view.findViewById(R.id.textViewSender);
            textViewSentAt = view.findViewById(R.id.textViewSentAt);
        }
    }
}

