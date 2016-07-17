package com.example.myapplication;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Moshe Malka on 22/05/2016.
 */
public class SensorDataRequest_Temperature  extends StringRequest {

    // path to .PHP files that handel getting the data from the remote database.
    static final String TEMPERATURE_TABLE_REQUEST_URL = "http://babykeeper.netai.net/getTemperatureTableValues.php";

    // a Map object with String-type key and value is used to send our http request with certain parameters.
    Map<String,String> params;

    /*
        SensorDataRequest main constructor
     */
    public SensorDataRequest_Temperature(String raspberry_product_key,
                                         String timeRange,
                                         Response.Listener<String> listener) {
        super(Method.POST, TEMPERATURE_TABLE_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("raspberry_product_key",raspberry_product_key);
        params.put("timeRange",timeRange);
    }

    /*
        a function used to get all the parameters from our initiated object.
     */
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
