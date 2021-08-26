package com.example.a18vaccinenotifier.reciever;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class resumeOnPowerOnReciever extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent!=null && intent.getAction()!=null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent in = new Intent(context, APICallsReciever.class);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, in, 0);
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() , 60000, alarmIntent);
        }
    }
}
