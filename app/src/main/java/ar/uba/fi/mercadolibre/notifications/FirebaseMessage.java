package ar.uba.fi.mercadolibre.notifications;

import android.content.Context;
import android.content.Intent;

import java.util.Map;


public abstract class FirebaseMessage {
    Map<String, String> data;
    FirebaseMessage(Map<String, String> data) {this.data = data;}

    public Intent getNotificationIntent(Context context) {
        Intent i = new Intent(context, getActivityClass());
        i = setIntentExtras(i);
        return i;
    }

    abstract Class<?> getActivityClass();
    abstract Intent setIntentExtras(Intent i);
}
