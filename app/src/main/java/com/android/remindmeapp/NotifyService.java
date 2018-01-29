package com.android.remindmeapp;

import android.app.Notification;
import android.support.v4.app.NotificationCompat;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;

/**
 * Created by Solo on 29.01.2018.
 */

public class NotifyService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(this.getApplicationContext(), MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent1, 0);

        Notification mNotify = new NotificationCompat.Builder(this)
                .setContentTitle("Drink some water!")
                .setContentText("This is a kind remind for you to drink some water!")
                .setSmallIcon(R.drawable.ic_small_notification)
                .setContentIntent(pIntent)
                .setSound(sound)
                .addAction(0, "Log Water", pIntent)
                .build();

        mNM.notify(1, mNotify);
    }
}
