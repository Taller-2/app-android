package ar.uba.fi.mercadolibre.notifications;

import android.content.Intent;

import java.util.Map;

import ar.uba.fi.mercadolibre.activity.ChatActivity;

class ChatMessage extends FirebaseMessage {
    ChatMessage(Map<String, String> data) {
        super(data);
    }

    @Override
    Class<?> getActivityClass() {
        return ChatActivity.class;
    }

    @Override
    Intent setIntentExtras(Intent i) {
        String purchase_id = data.get("purchase_id");
        i.putExtra("chat_room", purchase_id);
        return i;
    }
}
