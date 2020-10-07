package com.example.DAEJAK;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

public class AutoStart extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(final Context context, Intent intent) {

        String action = intent.getAction();
        if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            if( Build.VERSION.SDK_INT >= 26 )
            {
                // 안드로이드 8 이상에서는 foreground 서비스로 시작한다.
                Intent i = new Intent(context, UndeadService.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startService(i);
                context.startForegroundService( i );
            }
            else
            {
                Intent i = new Intent(context, UndeadService.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startService(i);
            }

            Intent i = new Intent(context, UndeadService.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(i);
        }
    }

}