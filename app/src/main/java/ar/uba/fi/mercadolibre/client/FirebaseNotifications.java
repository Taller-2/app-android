package ar.uba.fi.mercadolibre.client;

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

import java.util.Random;

import ar.uba.fi.mercadolibre.R;
import ar.uba.fi.mercadolibre.activity.ChatActivity;

public class FirebaseNotifications extends FirebaseMessagingService {
    private final static String CHANNEL_ID = "ComprameChannel";
    private final static String KEY_TITLE = "title";
    private final static String KEY_MESSAGE = "message";
    private final static String PURCHASE_ID = "purchase_id";
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

        Intent i = new Intent(getApplicationContext(), ChatActivity.class);
        i.putExtra("chat_room", remoteMessage.getData().get(PURCHASE_ID));
        notificationBuilder.setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, i, 0));
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
}