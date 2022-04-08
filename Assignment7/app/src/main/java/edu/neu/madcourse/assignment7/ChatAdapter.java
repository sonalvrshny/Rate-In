package edu.neu.madcourse.assignment7;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter {

    Context mContext;
    List<Messages> messagesList;
    int SEND = 1;
    int RECEIVE = 2;

    public ChatAdapter(Context mContext, List<Messages> messagesList) {
        this.mContext = mContext;
        this.messagesList = messagesList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SEND) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.sender_chat_layout, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.receiver_chat_layout, parent, false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Messages messages = messagesList.get(position);
        if (holder.getClass() == SenderViewHolder.class) {
            SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
            Uri uri = Uri.parse(messages.getMessage());
            Picasso.get().load(uri).into(senderViewHolder.senderImageCode);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(messages.getTimeStamp());
            Date d = c.getTime();
            DateFormat dFormat = DateFormat.getDateTimeInstance();
//            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            senderViewHolder.senderDate.setText(dFormat.format(d));
        } else {
            ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;
            Uri uri = Uri.parse(messages.getMessage());
            Picasso.get().load(uri).into(receiverViewHolder.receiverImageCode);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(messages.getTimeStamp());
            Date d = c.getTime();
            DateFormat dFormat = DateFormat.getDateTimeInstance();
//            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            receiverViewHolder.receiverDate.setText(dFormat.format(d));
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Messages message = messagesList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(message.getSenderId())) {
            return SEND;
        } else {
            return RECEIVE;
        }
    }

    class SenderViewHolder extends RecyclerView.ViewHolder {
        ImageView senderImageCode;
        TextView senderDate;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            senderImageCode = itemView.findViewById(R.id.senderImage);
            senderDate = itemView.findViewById(R.id.sender_date);
        }
    }

    class ReceiverViewHolder extends RecyclerView.ViewHolder {
        ImageView receiverImageCode;
        TextView receiverDate;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            receiverImageCode = itemView.findViewById(R.id.receiverImage);
            receiverDate = itemView.findViewById(R.id.receiver_date);
        }
    }
}
