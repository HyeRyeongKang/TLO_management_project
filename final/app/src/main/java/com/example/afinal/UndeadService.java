package com.example.afinal;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import android.os.PowerManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class UndeadService extends Service {
    public static Intent serviceIntent = null;
    private static String CHANNEL_ID = "channel1";
    private static String CHANEL_NAME = "Channel1";
    NotificationManager manager;
    NotificationCompat.Builder builder;
    public String noticeContent, noticeName, noticeDate;
    public String exceed_limit_level, measure_date, name;
    int i = 0;
    private boolean isStop;
    Handler handler = new Handler();

    String phone;
    String TAG = "여기라고오오ㅗ";
    String[] permission_list = {
            Manifest.permission.READ_PHONE_STATE
    };

    public int onStartCommand(Intent intent, int flags, int startId) {

        serviceIntent = intent;
        phone = intent.getStringExtra("phone");
        Log.e("phone start", phone);

        initializeNotification();
        Log.e("동작은 ", "here");
        Thread counter = new Thread(new Counter());
        counter.start();

        return START_STICKY;
    }

    public void initializeNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.bigText("스마트 자동계측 시스템 동작중입니다.");
        style.setBigContentTitle(null);
        style.setSummaryText("시스템 동작중");
        builder.setContentText(null);
        builder.setContentTitle(null);
        builder.setOngoing(true);
        builder.setStyle(style);
        builder.setWhen(0);
        builder.setShowWhen(false);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(pendingIntent);
        //NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(new NotificationChannel("1", "undead_service", NotificationManager.IMPORTANCE_NONE));
        }
        Notification notification = builder.build();
        startForeground(1, notification);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isStop = true;
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("phone", phone);
        sendBroadcast(intent);
        Log.e("끊기면 ", "여기");
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class Counter implements Runnable {

        @Override
        public void run() {

            while (true) {
                if (isStop) {
                    break;
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        new BackgroundTask().execute();
                        //showNoti();
                        //Toast.makeText(getApplicationContext(), count + "", Toast.LENGTH_SHORT).show();
                        //Log.e("COUNT", count + " ");
                        //count++;
                    }
                });

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            target = "http://192.168.0.18/checklist2.php";
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {

                URL url = new URL(target);
                String postParameters = "phone=" + phone;
                Log.e("service phone", postParameters);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(20000);
                httpURLConnection.setConnectTimeout(20000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();

                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e) {

                Log.e(TAG, "GetData : Error ", e);
                String errorString = e.toString();

                return null;
            }
        }

        @Override
        public void onProgressUpdate(Void... values) {
            super.onProgressUpdate();
        }


        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                Log.e("결과", result);

                JSONObject reobject=new JSONObject(result);
                JSONArray jsonArray = reobject.getJSONArray("response");
                //JSONObject obj=reobject.getJSONObject("response");
                String success=jsonArray.getString(0);
               if (!success.equals("false")) {
                   //JSONArray jsonArray = reobject.getJSONArray("response");
                   Log.e("jsonarray", String.valueOf(jsonArray));
                   int cnt = 0;
                   while (cnt < jsonArray.length()) {

                       JSONObject object = jsonArray.getJSONObject(cnt);
                       phone = object.getString("phone");
                       Log.e("이름", phone);
                       exceed_limit_level = object.getString("exceed_limit_level");
                       measure_date = object.getString("measure_date");
                       name = object.getString("name");

                       showNoti(exceed_limit_level, measure_date, name);
                       cnt++;
                   }
               }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void showNoti(String level, String date, String name) {
        builder = null;
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //버전 오레오 이상일 경우
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                    new NotificationChannel(
                            CHANNEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT));
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        } else {
            builder = new NotificationCompat.Builder(this);
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("phone", phone);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) (System.currentTimeMillis() / 1000), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        noticeContent = name + "에서 " + "level " + level + " " + "기준초과";
        noticeDate = "20" + date.substring(0, 2) + "." + date.substring(2, 4) + "." + date.substring(4, 6) + " " + date.substring(6) + "시";
        builder.setContentTitle(noticeContent); //알림창 메시지
        builder.setContentText(noticeDate);

        builder.setSmallIcon(R.drawable.ic_baseline_fiber_new_24); //알림창 아이콘
        builder.setAutoCancel(true); //알림창 터치시 상단 알림상태창에서 알림이 자동으로 삭제되게 합니다.
        builder.setContentIntent(pendingIntent);//pendingIntent를 builder에 설정 해줍니다. //알림창 터치시 인텐트가 전달할 수 있도록 해줍니다.
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        Notification notification = builder.build(); //알림창 실행
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "My:Tag");
        wakeLock.acquire(5000);
        manager.notify(i, notification);
        i++;
    }

}
