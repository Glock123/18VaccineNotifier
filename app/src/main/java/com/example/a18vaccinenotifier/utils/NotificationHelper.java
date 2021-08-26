package com.example.a18vaccinenotifier.utils;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.a18vaccinenotifier.R;
import com.example.a18vaccinenotifier.reciever.NotificationReceiver;

public class NotificationHelper extends ContextWrapper {
    public String channelID;
    public static final String channelName = "Channel Name";
    private NotificationManager mManager;
    private String title, body;
    PendingIntent pIn;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationHelper(Context base, String title, String body, String channelID) {
        super(base);
        this.title = title;
        this.body = body;
        this.channelID = channelID;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
        Intent in = new Intent(base, NotificationReceiver.class);
        pIn = PendingIntent.getBroadcast(base, 0, in, 0);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(this.channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification() {

        return new NotificationCompat.Builder(getApplicationContext(), this.channelID)
                .setContentTitle(this.title)
                .setContentText(this.body)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(this.body))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .addAction(R.mipmap.ic_launcher, "COWIN WEBSITE", this.pIn)
                .setColor(Color.BLUE);
    }

}
