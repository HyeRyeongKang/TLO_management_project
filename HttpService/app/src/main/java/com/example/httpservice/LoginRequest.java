package com.example.httpservice;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public class LoginRequest extends StringRequest {

    final static private String URL = "http://165.246.234.168/~ryeong/UserLogin.php";
    private Map<String, String> parameters;

    public LoginRequest(String userID, String userPassword, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userPassword", userPassword);

        Log.e("request", "request check: "+userID);
    }

    @Override
    public Map<String, String> getParams(){return parameters;}
}
