package com.example.alarm;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import android.util.Log;

public class LoginActivity extends AppCompatActivity {

    private AlertDialog dialog;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        //로그인 추가 부분
//        if (null != UndeadService.serviceIntent) {
//            Intent Aintent = new Intent(this, MainActivity.class);
//            String userID=UndeadService.serviceIntent.getExtras().getString("userID");
//            Aintent.putExtra("userID", userID);
//            startActivity(Aintent);
//        }
//        //로그인 추가 여기까지

        final EditText idText = (EditText) findViewById(R.id.idText);
        final EditText passwordText = (EditText) findViewById(R.id.passwordText);
        final Button loginButton = (Button) findViewById(R.id.loginButton);


        loginButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                final String userID = idText.getText().toString();
                String userPassword = passwordText.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("res", response);
                            String login="fail\n";
                            JSONObject jsonResponse = new JSONObject(response);
                            JSONObject obj=jsonResponse.getJSONObject("root");
                            String success=obj.getString("success");

                            if(success.equals("false")) {
                                Log.e("ss", response);
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                dialog = builder.setMessage("계정을 다시 확인하세요.")
                                        .setNegativeButton("다시 시도",null)
                                        .create();
                                dialog.show();
                            }

                            else{
                                //JSONObject jsonResponse = new JSONObject(response);
                                Log.e("로그인확인", "login: "+jsonResponse);

                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);


//                                //dialog = builder.setMessage("로그인에 성공했습니다.")
//                                        //.setPositiveButton("확인",new DialogInterface.OnClickListener() {
//
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                finish();
//                                                Intent intent
//                                                        = new Intent(getApplicationContext(), LoginActivity.class);
//                                                startActivity(intent);
//                                            }
//                                        })
//                                        .create();
                                //dialog.show();
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                LoginActivity.this.startActivity(intent);
                                intent.putExtra("userID", userID);
                                //intent.putExtra("constName", constName);
                                LoginActivity.this.startActivity(intent);
                                Log.e("login id: ", userID);
                                finish();
                            }
                        }

                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                };
                LoginRequest loginRequest = new LoginRequest(userID, userPassword, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }

        });
    }
    @Override
    protected void onStop(){
        super.onStop();
        if(dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }
}