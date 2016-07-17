package com.example.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class SensorDataActivity extends AppCompatActivity {
    UserSessionManager session;
    HashMap<String, String> userDetails;
    static final String TEMP_RASP_ID = "91e17061-9586-4182-b606-dac81a8b1586";
    Button logout;
    Spinner mainSpinner;
    ArrayAdapter<CharSequence> adapter;
    public boolean isSpinnerTouched;

    SensorDataRequest_Temperature sensorDataRequest_temperature;
    SensorDataRequest_Humidity sensorDataRequest_humidity;
    SensorDataRequest_Sound sensorDataRequest_sound;

    Calendar calendar;
    Locale current;
    // format => 2016-05-17 22:10:22

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_data_activity_layout);

        current = getResources().getConfiguration().locale;

        logout = (Button) findViewById(R.id.logoutButton);
        mainSpinner = (Spinner) findViewById(R.id.mainSpinner);
        initSpinnerAdapter();

        session = new UserSessionManager(getApplicationContext());
       /* if(! session.checkLogin()){
            finish();
        }*/
        userDetails = session.getUserDetails();
        Log.d("user session", userDetails.toString());

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.logoutUser();
            }
        });
    }


    private void initSpinnerAdapter() {
        adapter = ArrayAdapter.createFromResource(this, R.array.spinner_opts,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mainSpinner.setAdapter(adapter);

        isSpinnerTouched = false;

        mainSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isSpinnerTouched = true;
                return false;
            }
        });
        mainSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isSpinnerTouched) {
                    Log.i("SpinnerSelection", parent.getItemAtPosition(position).toString());
                    // getting spinner options as a string array from our resources.
                    String[] spinnerOpts = getResources().getStringArray(R.array.spinner_opts);
                    String currentTime;
                    if (parent.getItemAtPosition(position).equals(spinnerOpts[0])) {
                        // 24-hour

                        // calculating current and time range
                        currentTime = getCurrentDateTime();
                        Calendar tmpCal = Calendar.getInstance(current);
                        tmpCal.setTimeZone(TimeZone.getDefault());
                        tmpCal.add(Calendar.DAY_OF_MONTH, -1);
                        String timeRange = tmpCal.get(Calendar.YEAR) + "-" +
                                tmpCal.get(Calendar.MONTH) + "-" +
                                tmpCal.get(Calendar.DAY_OF_MONTH)
                                + " " +
                                "00:00:00";
                        Log.i("day_original", currentTime);
                        Log.i("day_minus", timeRange);
                        // adding the response listeners and executing them.
                        addResponseListener_temperature(timeRange);
                        addResponseListener_humidity(timeRange);
                        addResponseListener_sound(timeRange);
                        queueInit();


                    } else if (parent.getItemAtPosition(position).equals(spinnerOpts[1])) {
                        // week

                        // calculating current and time range
                        currentTime = getCurrentDateTime();
                        Calendar tmpCal = Calendar.getInstance(current);
                        tmpCal.setTimeZone(TimeZone.getDefault());
                        tmpCal.add(Calendar.MONTH, 1);
                        tmpCal.add(Calendar.DAY_OF_MONTH, -7);
                        String timeRange = tmpCal.get(Calendar.YEAR) + "-" +
                                tmpCal.get(Calendar.MONTH) + "-" +
                                tmpCal.get(Calendar.DAY_OF_MONTH)
                                + " " +
                                "00:00:00";
                        Log.i("week_original", currentTime);
                        Log.i("week_minus", timeRange);
                        // adding the response listeners and executing them.
                        addResponseListener_temperature(timeRange);
                        addResponseListener_humidity(timeRange);
                        addResponseListener_sound(timeRange);
                        queueInit();

                    } else {
                        // month

                        // calculating current and time range
                        currentTime = getCurrentDateTime();
                        Calendar tmpCal = Calendar.getInstance(current);
                        tmpCal.setTimeZone(TimeZone.getDefault());
                        //tmpCal.add(Calendar.MONTH,-1);
                        String timeRange = tmpCal.get(Calendar.YEAR) + "-" +
                                tmpCal.get(Calendar.MONTH) + "-" +
                                tmpCal.get(Calendar.DAY_OF_MONTH)
                                + " " +
                                "00:00:00";
                        Log.i("month_original", currentTime);
                        Log.i("month_minus", timeRange);
                        // adding the response listeners and executing them.
                        addResponseListener_temperature(timeRange);
                        addResponseListener_humidity(timeRange);
                        addResponseListener_sound(timeRange);
                        queueInit();

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /*
        Response Listener for the Sound table Request.
     */
    private void addResponseListener_sound(String timeRange) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response != null) {
                        Log.d("response_sound", response);
                        JSONArray json = new JSONArray(response);

                        ArrayList<String> sound_dates = new ArrayList<>();
                        ArrayList<String> sound_data = new ArrayList<>();
                        for (int i = 0; i < json.length(); i++) {
                            JSONArray j = new JSONArray(json.get(i).toString());
                            sound_dates.add(i,j.getString(0).substring(0,10));
                            sound_data.add(i,j.getString(1));
                        }
                        Log.i("output_data", sound_data.toString());
                        Log.i("output_dates", sound_dates.toString());

                        // draw sound lineChart
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        // initiating the SensorDataRequest object with the appropriate parameters.
        sensorDataRequest_sound = new SensorDataRequest_Sound(
                //userDetails.get("raspberry_product_key"),
                TEMP_RASP_ID,
                timeRange,
                responseListener);

    }

    /*
        Response Listener for the Humidity table Request.
     */
    private void addResponseListener_humidity(String timeRange) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response != null) {
                        Log.d("response_humidity", response);
                        JSONArray json = new JSONArray(response);

                        ArrayList<String> humidity_dates = new ArrayList<>();
                        ArrayList<String> humidity_data = new ArrayList<>();
                        for (int i = 0; i < json.length(); i++) {
                            JSONArray j = new JSONArray(json.get(i).toString());
                            humidity_dates.add(i,j.getString(0).substring(0,10));
                            humidity_data.add(i,j.getString(1));
                            }
                        Log.i("output_data", humidity_data.toString());
                        Log.i("output_dates", humidity_dates.toString());


                        // draw humidity lineChart
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        // initiating the SensorDataRequest object with the appropriate parameters.
        sensorDataRequest_humidity = new SensorDataRequest_Humidity(
                //userDetails.get("raspberry_product_key"),
                TEMP_RASP_ID,
                timeRange,
                responseListener);
    }

    /*
        Response Listener for the Temperature table Request.
    */
    private void addResponseListener_temperature(String timeRange) {
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response != null) {
                        Log.d("response_temperature", response);
                        JSONArray json = new JSONArray(response);

                        ArrayList<String> temperature_dates = new ArrayList<>();
                        ArrayList<String> temperature_data = new ArrayList<>();
                        for (int i = 0; i < json.length(); i++) {
                            JSONArray j = new JSONArray(json.get(i).toString());
                            temperature_dates.add(i,j.getString(0).substring(0,10));
                            temperature_data.add(i,j.getString(1));
                        }
                        Log.i("output_data", temperature_data.toString());
                        Log.i("output_dates", temperature_dates.toString());

                        // draw temperature lineChart
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        // initiating the SensorDataRequest object with the appropriate parameters.
        sensorDataRequest_temperature = new SensorDataRequest_Temperature(
                //userDetails.get("raspberry_product_key"),
                TEMP_RASP_ID,
                timeRange,
                responseListener);
    }

    /*
        initiates the RequestQueue object with the activity context and
        adds all the table's request's to the queue.
     */
    private void queueInit() {
        // creating a RequestQueue object and providing it with the context of this activity.
        RequestQueue queue = Volley.newRequestQueue(SensorDataActivity.this);
        // adding our SensorDataRequest object to our RequestQueue object.
        queue.add(sensorDataRequest_temperature);
        queue.add(sensorDataRequest_humidity);
        queue.add(sensorDataRequest_sound);

    }

    /*
        going back to the main activity when the back button was pressed.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i("onBackPressed", "Context:" + this.getClass().getSimpleName());
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /*
        when called, onDestroy will clear our user session object.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.logoutUser();
    }


    public String getCurrentDateTime() {
        String dummyTime = "00:00:00";
        calendar = Calendar.getInstance(current);
        calendar.setTimeZone(TimeZone.getDefault());
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        return year + "-" + month + "-" + day + " " + dummyTime;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}

