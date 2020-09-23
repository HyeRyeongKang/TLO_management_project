package com.example.alarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.List;

public class UndeadService extends Service {
    public static Intent serviceIntent = null;
    private static String CHANNEL_ID = "channel1";
    private static String CHANEL_NAME = "Channel1";
    NotificationManager manager;
    NotificationCompat.Builder builder;
    public String noticeContent, noticeName, noticeDate;
    public String exceed_limit_level, measure_date;
    int i=0;
    int count;
    private boolean isStop;
    Handler handler = new Handler();
    private NoticeListAdapter adapter;
    private List<Notice> noticeList;
    String userID;
    String TAG="여기라고오오ㅗ";
   // LoginRequest login;
    //String userID = login.userID;

    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceIntent = intent;
        userID = intent.getStringExtra("userID");
        //intent.putExtra("userID", userID);
        initializeNotification();
        Log.e("동작은 ","here");
        Thread counter = new Thread(new Counter());
        counter.start();

        return START_STICKY;
    }

    public void initializeNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.bigText("설정을 보려면 누르세요.");
        style.setBigContentTitle(null);
        style.setSummaryText("서비스 동작중");
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
        //serviceIntent = null;
        isStop = true;
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("userID", userID);
        sendBroadcast(intent);

        Log.e("끊기면 ","여기");
    }
//    @Override
//    public void onTaskRemoved(Intent rootIntent) {
//        super.onTaskRemoved(rootIntent);
//        final Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.add(Calendar.SECOND, 3);
//        Intent intent = new Intent(this, AlarmReceiver.class);
//        PendingIntent sender = PendingIntent.getBroadcast(this, 0,intent,0);
//        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
//        Log.e("노티 여기11111111","여기");
//    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class Counter implements Runnable {

        @Override
        public void run() {

            while (true) {
                if ( isStop ) {
                    break;
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        new BackgroundTask().execute();
                        //showNoti();
                        Toast.makeText(getApplicationContext(), count + "", Toast.LENGTH_SHORT).show();
                        Log.e("COUNT", count + " ");
                        count++;
                    }
                });

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class BackgroundTask extends AsyncTask<Void,Void, String>
    {
        String target;

        @Override
        protected void onPreExecute(){
            target = "http://192.168.0.18/checklist.php";
        }

        @Override
        protected String doInBackground(Void... voids) {
//            try{
//                URL url = new URL(target);
//                String postParameters = "userID="+userID;
//                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
//                InputStream inputStream = httpURLConnection.getInputStream();
//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                String temp;
//                StringBuilder stringBuilder = new StringBuilder();
//                while ((temp = bufferedReader.readLine()) != null)
//                {
//                    stringBuilder.append(temp + "\n");
//                }
//                bufferedReader.close();
//                inputStream.close();
//                httpURLConnection.disconnect();
//                return stringBuilder.toString().trim();
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//            return null;
            try {

                URL url = new URL(target);
                String postParameters = "userID="+userID;
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
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
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
        public void onProgressUpdate(Void... values){
            super.onProgressUpdate();
        }


        @Override
        public void onPostExecute(String result){
            super.onPostExecute(result);
            try{
                String TAG="test";

                JSONObject jsonObject = new JSONObject(result);
                Log.e("결과",result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");
                Log.e("ㅇㅇㅇㅇㅇㅇㅇㅇ" , String.valueOf(jsonArray));
                int cnt = 0;
                while (cnt < jsonArray.length())
                {
                    JSONObject object = jsonArray.getJSONObject(cnt);
                    userID = object.getString("userID");
                    Log.e("이름",userID);
                    exceed_limit_level = object.getString("exceed_limit_level");
                    measure_date = object.getString("measure_date");
                    showNoti();
                    cnt++;
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public void showNoti(){
        builder = null;
        manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        //버전 오레오 이상일 경우
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                    new NotificationChannel(
                            CHANNEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT));
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        } else{ builder = new NotificationCompat.Builder(this); }
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("userID",userID);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int)(System.currentTimeMillis()/1000), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentTitle(exceed_limit_level+"기준 초과"); //알림창 메시지
        builder.setContentText(userID);
        //builder.setContentTitle(name+" "+exeed_limit_level+"기준 초과"); //알림창 메시지
        //builder.setContentText(measure_date);
        builder.setSmallIcon(R.drawable.ic_baseline_fiber_new_24); //알림창 아이콘
        builder.setAutoCancel(true); //알림창 터치시 상단 알림상태창에서 알림이 자동으로 삭제되게 합니다.
        builder.setContentIntent(pendingIntent);//pendingIntent를 builder에 설정 해줍니다. //알림창 터치시 인텐트가 전달할 수 있도록 해줍니다.
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setPriority(Notification.PRIORITY_HIGH);
        builder.setDefaults(Notification.DEFAULT_SOUND);
        Notification notification = builder.build(); //알림창 실행
        manager.notify(i,notification);
        i++;
    }

}
