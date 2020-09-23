package com.example.httpservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.httpservice.HttpService.MyBinder;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    HttpService hs;
    boolean isService = false;
    private static String TAG="activity";
    static int counter=0;

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyBinder mb = (MyBinder) service;
            hs = mb.getService();

            isService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "disconnected");
            isService = false;
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button b1 = (Button) findViewById(R.id.button1);
        Button b2 = (Button) findViewById(R.id.button2);
        Button b3 = (Button) findViewById(R.id.button3);

        final String userID="khr6604";

        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { // 서비스 시작
                Intent intent = new Intent(
                        MainActivity.this, // 현재 화면
                        HttpService.class); // 다음넘어갈 컴퍼넌트

                bindService(intent, // intent 객체
                        conn, // 서비스와 연결에 대한 정의
                        Context.BIND_AUTO_CREATE);
                isService=true;
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { // 서비스 종료

                unbindService(conn); // 서비스 종료
                isService=false;
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {//서비스데이터확인
                final Timer timer=new Timer();
                final TimerTask tt=new TimerTask() {
                    @Override
                    public void run() {
                        Log.e("1번 task counter: ", String.valueOf(counter));
                        counter++;

                        Log.e(TAG, "service type: "+isService);
                        if (!isService) {
//                            Toast.makeText(getApplicationContext(),
//                                    "서비스중이 아닙니다, 데이터받을수 없음",
//                                    Toast.LENGTH_LONG).show();
                            timer.cancel();
                            return;
                        }
                        //int num = hs.getRan();//서비스쪽 메소드로 값 전달 받아 호출
                        Log.e(TAG, "userID check: "+userID);
                        String id=hs.getText(userID);
//                        Toast.makeText(getApplicationContext(),
//                                "받아온 데이터 : " + id,
//                                Toast.LENGTH_LONG).show();
                    }
                };

                timer.schedule(tt, 0, 10000);
            }
        });
    }
}