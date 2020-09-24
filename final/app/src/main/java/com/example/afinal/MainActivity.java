package com.example.afinal;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MainActivity extends AppCompatActivity {
    Intent foregroundServiceIntent;
    private ListView noticeListView;
    private NoticeListAdapter adapter;
    private List<Notice> noticeList;
    private static String CHANNEL_ID = "channel1";
    private static String CHANEL_NAME = "Channel1";
    NotificationManager manager;
    NotificationCompat.Builder builder;
    String noticeContent, noticeName, noticeDate;
    UndeadService hs;
    String phone;
    private static String TAG = "휴대폰 정보 가져오기";
    private static String[] permission_list = {
            Manifest.permission.READ_PHONE_STATE
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_PHONE_STATE}, 100);
        }



        noticeListView = (ListView) findViewById(R.id.noticeListView);
        noticeList = new ArrayList<Notice>();

        adapter = new NoticeListAdapter(getApplicationContext(), noticeList);
        //adapter.notifyDataSetChanged();
        noticeListView.setAdapter(adapter);

        new BackgroundTask().execute();

        if (null == UndeadService.serviceIntent) {
            foregroundServiceIntent = new Intent(this, UndeadService.class);
            getPhoneNum();
            foregroundServiceIntent.putExtra("phone", phone);
            Log.e("activity", phone);
            startService(foregroundServiceIntent);
            Toast.makeText(getApplicationContext(), "start service", Toast.LENGTH_LONG).show();

        } else {
            foregroundServiceIntent = UndeadService.serviceIntent;
            Toast.makeText(getApplicationContext(), "already", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != foregroundServiceIntent) {
            stopService(foregroundServiceIntent);
            foregroundServiceIntent = null;
        }
    }


    class BackgroundTask extends AsyncTask<Void, Void, String> {
        String target;

        @Override
        protected void onPreExecute() {
            target = "http://192.168.0.18/Notice2.php";

        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                getPhoneNum();
                URL url = new URL(target);
                String postParameters = "phone="+phone;
                Log.e("postphone", postParameters);
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
            try {
                String TAG = "test";
                Log.e("result : ", result);
                JSONObject jsonObject = new JSONObject(result);

                JSONArray jsonArray = jsonObject.getJSONArray("response");
                int count = 0;
                while (count < jsonArray.length()) {
                    Log.e(TAG, "here");

                    JSONObject object = jsonArray.getJSONObject(count);
                    String name=object.getString("name");
                    String level=object.getString("exceed_limit_level");
                    noticeContent = name+ " "+level+" 기준 초과";
                    noticeName = object.getString("sens_code");
                    noticeDate = object.getString("measure_date");
                    Notice notice = new Notice(noticeContent, noticeName, noticeDate);
                    noticeList.add(notice);
                    adapter.notifyDataSetChanged();
                    //noticeListView.setAdapter(adapter);
                    count++;


                    Log.e(TAG, "notice" + noticeContent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
    @SuppressLint({"MissingPermission", "HardwareIds"})

    public void getPhoneNum() {

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phone = tm.getLine1Number();

        if (phone != null) {
            phone = phone.replace("+82", "0");
        }
        Log.d(TAG, "전화번호 : [ getLine1Number ] >>> "+tm.getLine1Number());
    }

}






