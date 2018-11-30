package ar.uba.fi.mercadolibre.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.model.Account;
import ar.uba.fi.mercadolibre.model.ChatMessage;

public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {
    private Account currentAccount;

    public ChatMessageAdapter(Context context, List<ChatMessage> messages, Account currentAccount) {
        super(context, 0, messages);
        this.currentAccount = currentAccount;
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        ChatMessage message = getItem(position);
        if (message == null) {
            Log.e("ChatMessageAdapter", "Message was null");
            return view;
        }

        if (view == null) {
            view = LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.message, parent, false);
        }

        TextView myMessageBody = view.findViewById(R.id.my_message_body);
        TextView theirMessageBody = view.findViewById(R.id.their_message_body);
        TextView theirName = view.findViewById(R.id.their_name);

        String senderUserID = message.senderUserID();
        if (senderUserID != null && senderUserID.equals(currentAccount.getID())) {
            myMessageBody.setText(message.getText());

            myMessageBody.setVisibility(View.VISIBLE);
            theirMessageBody.setVisibility(View.GONE);
            theirName.setVisibility(View.GONE);
        } else {
            theirMessageBody.setText(message.getText());
            theirName.setText(message.getName());

            myMessageBody.setVisibility(View.GONE);
            theirMessageBody.setVisibility(View.VISIBLE);
            theirName.setVisibility(View.VISIBLE);
        }
        return view;
    }
}
