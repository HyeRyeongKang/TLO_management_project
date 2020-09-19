package com.example.httpservice;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import android.os.Vibrator;
import android.util.Log;

import java.util.Random;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.os.Build;

public class HttpService extends Service{
    private IBinder mIBinder = new MyBinder();

    private static String TAG="querytest";

    private String IP_ADDRESS="165.246.234.168/~ryeong";

    private TextView mTextViewResult;
    private ArrayList<ProblemData> mArrayList;
    private UsersAdapter mAdapter;
    //private RecyclerView mRecyclerView;
    ListView mListViewList;
    String mEditTextSearchKeyword;
    String mJsonString;

    static int counter=0;

    private static String CHANNEL_ID = "channel1";
    private static String CHANEL_NAME = "Channel1";
    NotificationManager manager;
    NotificationCompat.Builder builder;

    int count=0;

    class MyBinder extends Binder{
        HttpService getService(){
            return HttpService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("LOG", "onBind()");
        count=0;

        return mIBinder;
    }

    @Override
    public void onCreate() {
        Log.e("LOG", "onCreate()");
        mArrayList=new ArrayList<>();

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("LOG", "onStartCommand()");



        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.e("LOG", "onDestroy()");
        super.onDestroy();
    }

    String getText(String userid) {

        String userID=userid;
        mArrayList.clear();
        //mAdapter.notifyDataSetChanged();
        Log.e("LOG", "execute1");

        GetData task=new GetData();
        task.execute("http://" + IP_ADDRESS + "/querytest.php", userID);
        Log.e("LOG", "execute4");
        return userID;

        //return new Random().nextInt();
    }

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            Log.e("LOG", "execute2");
            super.onPreExecute();
            Log.e("LOG", "execute3");
//            progressDialog = ProgressDialog.show(HttpService.this,
////                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("LOG", "execute5");

            //progressDialog.dismiss();
            //TextView mTextViewResult = (TextView) findViewById(R.id.textView_main_result);
            //mTextViewResult.setText(result);
            String[] col=result.split("\\{");
            Log.d(TAG, "response - " + result);
            Log.d(TAG, "response - " + col.length);
            int problem_num=col.length-2;
            Log.d(TAG, problem_num+"개의 문제가 발생하였습니다.");

            if (problem_num>=0) {

            }

            if (result == null){

                mTextViewResult.setText(errorString);
            }

            else if (problem_num != -1) {

                mJsonString = result;
                Log.e("LOG", "execute6");
                showResult();
            }
            //Log.e("LOG", "execute4");
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = "userID="+params[1];


            try {

                URL url = new URL(serverURL);
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
                errorString = e.toString();

                return null;
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
        intent.putExtra("name","알림");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 101, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentTitle("알림"); //알림창 메시지
        builder.setContentText("내용"); //알림창 아이콘
        builder.setSmallIcon(R.drawable.ic_fiber_new_black_24dp); //알림창 터치시 상단 알림상태창에서 알림이 자동으로 삭제되게 합니다.
        builder.setAutoCancel(true); //pendingIntent를 builder에 설정 해줍니다. //알림창 터치시 인텐트가 전달할 수 있도록 해줍니다.
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build(); //알림창 실행
        manager.notify(1, notification);
    }

    private void showResult() {
        String TAG_JSON="kang";
        String TAG_ID = "userID";
        String TAG_NAME = "name";
        String TAG_LEVEL ="exceed_limit_level";

        try {
            Log.e(TAG, "Check result: "+mJsonString);
            JSONObject jsonObject = new JSONObject(mJsonString);
            Log.e(TAG, "check object: "+jsonObject);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            Log.e(TAG, "check array: "+jsonArray);
            Log.e(TAG, "Check result: "+jsonArray.length());

            for(int i=0;i<jsonArray.length();i++){
                JSONObject item = jsonArray.getJSONObject(i);

                String id = item.getString(TAG_ID);
                String name = item.getString(TAG_NAME);
                String level = item.getString(TAG_LEVEL);

                ProblemData problemData = new ProblemData();

                problemData.set_id("id: "+id);
                problemData.set_name(name);
                problemData.set_level(level);

                mArrayList.add(problemData);

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
                intent.putExtra("name","알림");
                //(int)(System.currentTimeMillis()/1000)
                PendingIntent pendingIntent = PendingIntent.getActivity(this, (int)(System.currentTimeMillis()/1000), intent, PendingIntent.FLAG_CANCEL_CURRENT);

                builder.setContentTitle(id); //알림창 메시지
                builder.setContentText(level); //알림창 아이콘
                builder.setSmallIcon(R.drawable.ic_fiber_new_black_24dp); //알림창 터치시 상단 알림상태창에서 알림이 자동으로 삭제되게 합니다.
                builder.setAutoCancel(true); //pendingIntent를 builder에 설정 해줍니다. //알림창 터치시 인텐트가 전달할 수 있도록 해줍니다.
                builder.setContentIntent(pendingIntent);
                Log.d(TAG, "noti check: "+level);
                Notification notification = builder.build(); //알림창 실행
                manager.notify(count, notification);
                count++;

                Log.d(TAG, id);
                Log.d(TAG, level);
            }

        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }
    }
}


