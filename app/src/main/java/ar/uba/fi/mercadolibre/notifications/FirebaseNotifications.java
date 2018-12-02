package ar.uba.fi.mercadolibre.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ar.uba.fi.mercadolibre.R;

public class FirebaseNotifications extends FirebaseMessagingService {
    private final static String CHANNEL_ID = "ComprameChannel";
    private final static String KEY_TITLE = "title";
    private final static String KEY_MESSAGE = "message";
    private final static String KEY_TYPE = "type";

    private enum NotificationType {
        CHAT, NEW_PURCHASE, NEW_QUESTION, NEW_ANSWER
    }
    private HashMap<String, NotificationType> types;

    @Override
    public void onCreate() {
        types = new HashMap<>();
        types.put("chat", NotificationType.CHAT);
        types.put("new_question", NotificationType.NEW_QUESTION);
        types.put("new_answer", NotificationType.NEW_ANSWER);
        types.put("product_sold", NotificationType.NEW_PURCHASE);
    }

    NotificationManager notificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("Message", remoteMessage.getData().values().iterator().next());

        // Basically I stole all of this code, but it works
        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels();
        }
        int notificationId = new Random().nextInt(60000);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_shopping_cart_24px)
                .setContentTitle(remoteMessage.getData().get(KEY_TITLE))
                .setContentText(remoteMessage.getData().get(KEY_MESSAGE))
                .setAutoCancel(true)
                .setSound(defaultSoundUri);

        String messageType = remoteMessage.getData().get(KEY_TYPE);
        FirebaseMessage message = getMessageFromType(messageType, remoteMessage.getData());
        if (message != null) {
            Intent i = message.getNotificationIntent(getApplicationContext());
            notificationBuilder.setContentIntent(PendingIntent.getActivity(getApplicationContext(), notificationId, i, 0));
        }
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationId, notificationBuilder.build());
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(){
        CharSequence adminChannelName = getString(R.string.notifications_admin_channel_name);
        String adminChannelDescription = getString(R.string.notifications_admin_channel_description);

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }

    private FirebaseMessage getMessageFromType(String type, Map<String, String> data) {
        NotificationType notificationType = types.get(type);
        switch (notificationType) {
            case CHAT: return new ChatMessage(data);
            case NEW_QUESTION: return new NewQuestionMessage(data);
            case NEW_PURCHASE: return new NewPurchaseMessage(data);
            case NEW_ANSWER: return new NewAnswerMessage(data);
            default: return null;
        }
    }
}