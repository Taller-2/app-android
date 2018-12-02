package ar.uba.fi.mercadolibre.notifications;

import android.content.Intent;

import java.util.Map;

import ar.uba.fi.mercadolibre.activity.UserSalesActivity;

class NewPurchaseMessage extends FirebaseMessage {
    NewPurchaseMessage(Map<String, String> data) {
        super(data);
    }

    @Override
    Class<?> getActivityClass() {
        return UserSalesActivity.class;
    }

    @Override
    Intent setIntentExtras(Intent i) {
        return i;
    }
}
