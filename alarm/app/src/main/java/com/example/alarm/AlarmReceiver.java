package com.example.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent in = new Intent(context, UndeadService.class);
            final String userID=intent.getExtras().getString("userID");

            in.putExtra("userID",userID);
            context.startForegroundService(in);
        } else {
            Intent in = new Intent(context, UndeadService.class);
            final String userID=intent.getExtras().getString("userID");

            in.putExtra("userID",userID);
            context.startService(in);
        }

    }
}
