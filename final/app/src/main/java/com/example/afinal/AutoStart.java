package com.example.afinal;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.telephony.TelephonyManager;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AutoStart extends BroadcastReceiver {
    String phone;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(final Context context, Intent intent) {

        String action = intent.getAction();
        if(action.equals("android.intent.action.BOOT_COMPLETED")) {
            //phone=new MainActivity.getPhoneNum();
//            new Handler().postDelayed(new Runnable() {
//                // 3초 후에 실행
//                @Override public void run() {
//                    //Toast.makeText(context, "-- BootReceiver.onReceive", Toast.LENGTH_LONG).show();
//
//                    // BackgroundService
//                    Intent serviceLauncher = new Intent(context, UndeadService.class);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        context.startForegroundService(serviceLauncher);
//                    } else {
//                        context.startService(serviceLauncher);
//                    }
//                }
//            }, 3000);

            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                Intent in = new Intent(context, UndeadService.class);
//                final String phone=intent.getExtras().getString("phone");
//
//                in.putExtra("phone",phone);
//                context.startForegroundService(in);
//            } else {
//                Intent in = new Intent(context, UndeadService.class);
//                final String phone=intent.getExtras().getString("phone");
//
//                in.putExtra("phone",phone);
//                context.startService(in);
//            }

            context.startActivity(i);
        }
//        else {
//            Intent i = new Intent(context, MainActivity.class);
//            context.startForegroundService(i);
//        }
    }


//    @Override
//    public void getPhoneNum(Context context) {
//
//        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//
//        if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(context, new String[]{
//                    Manifest.permission.READ_PHONE_STATE}, 100);
//            getPhoneNum(context);
//        }
//        //else{
//        phone = tm.getLine1Number();
//        //}
//
//
//
//        if (phone != null) {
//            phone = phone.replace("+82", "0");
//            //phone = phone.replace("+","");
//        }
//        //Log.d(TAG, "전화번호 : [ getLine1Number ] >>> "+phone);
//    }
}
