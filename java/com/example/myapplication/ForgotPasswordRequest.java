package com.example.myapplication;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Moshe Malka on 10/05/2016.
 */
/*
    StringRequest class to handle a case where the user forgot its password.
 */
public class ForgotPasswordRequest extends StringRequest {
    // path to php file that checks if user is in database, and if so - it replies the user's password.
    private static final String FORGOT_PASSWORD_REQUEST_URL = "http://babykeeper.netai.net/ForgotPassword.php";
    Map<String,String> params;

    public ForgotPasswordRequest(String email, Response.Listener<String> listener) {
        super(Method.POST, FORGOT_PASSWORD_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("email",email);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
