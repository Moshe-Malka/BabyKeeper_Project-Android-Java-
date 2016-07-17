package com.example.myapplication;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/*
    this class is responsible for handling the http request and response from the RegisterActivity.
 */
public class RegisterRequest extends StringRequest {
    // path to .PHP file that handel's the login logic and handel's the remote database.
    private static final String REGISTER_REQUEST_URL = "http://babykeeper.netai.net/Register.php";
    // a Map object with String-type key and value is used to send our http request with certain parameters.
    Map<String,String> params;
    /*
            RegisterRequest main constructor
     */
    public RegisterRequest(String email, String password, String raspberry_product_key ,Response.Listener<String> listener)
    {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("email",email);
        params.put("password",password);
        params.put("raspberry_product_key",raspberry_product_key);
    }

    /*
        a function used to get all the parameters from our initiated object.
     */
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
