package com.example.afinal;


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
            final String phone=intent.getExtras().getString("phone");

            in.putExtra("phone",phone);
            context.startForegroundService(in);
        } else {
            Intent in = new Intent(context, UndeadService.class);
            final String phone=intent.getExtras().getString("phone");

            in.putExtra("phone",phone);
            context.startService(in);
        }

    }
}
