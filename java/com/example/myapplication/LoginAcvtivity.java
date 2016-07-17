package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.github.ybq.android.spinkit.style.FadingCircle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/*
    Login activity to log-in our users.
 */
public class LoginAcvtivity extends AppCompatActivity {
    // main UI objects deceleration.
    EditText emailEditText, passwordEditText;
    Button loginButton;
    TextView registerTextView,forgotPasswordTextView;
    //  SharedPreferences object decelerations.
    UserSessionManager session;
    HashMap<String,String> userDetails;

    ProgressBar progressBar ;
    FadingCircle fadingCircle;

    Intent i;
    Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_acvtivity_layout);

        //  binding the UI objects to the activity xml.
        emailEditText = (EditText)findViewById(R.id.emailLoginEditText);
        passwordEditText = (EditText)findViewById(R.id.passwordLoginEditText);
        loginButton = (Button) findViewById(R.id.loginButton);
        registerTextView = (TextView) findViewById(R.id.registerNowTextView);
        forgotPasswordTextView = (TextView) findViewById(R.id.forgotPasswordTextView);

        // progress bar variables decelerations.
        progressBar = (ProgressBar)findViewById(R.id.spin_kit);
        fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);
        fadingCircle.stop();

        //haveNetworkConnection();

        // setting the session object with the class context.
        session = new UserSessionManager(getApplicationContext());
        userDetails = session.getUserDetails();
        Log.d("user session(LA)",userDetails.toString());

        // if the user returned from the Register activity - add the raspberry pi product key
        // to his user session object.
        getIntentFromRegisterActivity();

        // setting onClick listener to our registerTextView , to go to the Register Activity.
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginAcvtivity.this, RegisterActivity.class);
                LoginAcvtivity.this.startActivity(registerIntent);
            }
        });
        /*
        setting onClick listener to our forgotPasswordTextView , to go to the Register Activity.
        it also ask's the user to enter his email address if not entered.
        */
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    // get the email address as a local variable.
                    final String email = emailEditText.getText().toString();
                    // if the email address entered is equal to blank space,
                    //  open up an alert dialog and ask the user to enter the email.
                    if (email.equals("") && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginAcvtivity.this);
                        builder.setTitle("No Email Entered")
                                .setMessage("Please enter your email to restore password")
                                .setNegativeButton("OK", null)
                                .create()
                                .show();
                    }
                    // if the email was entered and it is valid - check with the database if it exists.
                    else
                    {
                        // initiating a response listener.
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try{
                                    Log.i("Response:",response);
                                    // start fading circle progress bar animation
                                    startProgressBarAnimation();

                                    // initiating a json object to parse data.
                                    JSONObject jsonResponse = new JSONObject(response);
                                    // getting the value from the key 'success'.
                                    boolean success = jsonResponse.getBoolean("success");
                                    if (success){

                                        String password = jsonResponse.getString("password");
                                        // sending an email to the user with his password.
                                        sendEmail(email,"A Message From BabyKeeper",
                                                "Hello " +email+"\n"+
                                                        "This is a Massage from BabyKeeper !" +"\n"+
                                                        "you have requested to retrieve your password." +"\n"+
                                                        "your password is : "+password);


                                        // hide and stop the fading circle progress bar animation
                                        stopProgressBarAnimation();

                                        // show an alert dialog to tell user password was
                                        // sent to his email.
                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginAcvtivity.this);
                                        builder.setTitle("Password Recovery")
                                                .setMessage("Your password has been sent to : "+ email)
                                                .setNegativeButton("OK", null)
                                                .create()
                                                .show();
                                    }
                                    else{
                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginAcvtivity.this);
                                        builder.setTitle("Password Recovery")
                                                .setMessage("The Email Address You Entered Is Not Registered In Our Database. \n" +
                                                        "Email Entered : "+ email)
                                                .setNegativeButton("OK", null)
                                                .create()
                                                .show();
                                    }
                                }catch (JSONException je){
                                    je.printStackTrace();
                                }
                            }
                        };
                        // initiating a ForgotPasswordRequest object with the appropriate parameters.
                        ForgotPasswordRequest passwordRequest = new ForgotPasswordRequest(email,responseListener);
                        // creating a RequestQueue object and providing it with the context of this activity.
                        RequestQueue queue = Volley.newRequestQueue(LoginAcvtivity.this);
                        // adding our ForgotPasswordRequest object to our RequestQueue object.
                        queue.add(passwordRequest);
                    }
                }
            }
        });
        /*
            setting an onClick listener to our main login button.
         */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start fading circle progress bar animation
                startProgressBarAnimation();

                // get the email and password from our TextView's to handle locally.
                final String email = emailEditText.getText().toString();
                final String password = passwordEditText.getText().toString();
                if (isValidEmail(email)) {
                    if ( ! password.equals("") || ! password.equals(" ")){
                        // initiating a response listener
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    Log.d("Response:", response);
                                    //  initiating a json object to parse data.
                                    JSONObject jsonResponse = new JSONObject(response);
                                    // boolean object to hold a value that will tell us if the action preformed is successful.
                                    boolean success = jsonResponse.getBoolean("success");

                                    if (success) {
                                        // if the action succeeded - get all the parameters from our json object,
                                        // and add them to the user session manager object.
                                        // go to the Main Mqtt And Video Activity.

                                        session.createUserLoginSession(
                                                jsonResponse.getString("email"),
                                                jsonResponse.getString("password"),
                                                jsonResponse.getString("raspberry_product_key"));

                                        //this sets the Topic of the listener for mqtt messages to the product key of the user's raspberry pi
                                        //MqttConfig.setTopicHead(userDetails.get("raspberry_product_key").substring(24));

                                        // hide and stop the fading circle progress bar animation
                                        stopProgressBarAnimation();

                                        goToMainActivity();

                                    } else {
                                        // hide and stop the fading circle progress bar animation
                                        stopProgressBarAnimation();

                                        // if the action failed - show an alert dialog to the user.
                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginAcvtivity.this);
                                        builder.setMessage("Login Failed")
                                                .setNegativeButton("Retry", null)
                                                .create()
                                                .show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            /*
                                inner method to take user to the main activity.
                             */
                            private void goToMainActivity() {
                                Intent i = new Intent(getApplicationContext(), MainMqttAndVideoActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                // Add new Flag to start new Activity
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                LoginAcvtivity.this.startActivity(i);
                                //finish();
                            }
                        };

                        // initiating the LoginRequest object with the appropriate parameters.
                        LoginRequest loginRequest = new LoginRequest(email, password, responseListener);
                        // creating a RequestQueue object and providing it with the context of this activity.
                        RequestQueue queue = Volley.newRequestQueue(LoginAcvtivity.this);
                        // adding our LoginRequest object to our RequestQueue object.
                        queue.add(loginRequest);
                    }
                    else
                    {
                        // hide and stop the fading circle progress bar animation
                        stopProgressBarAnimation();

                        passwordEditText.setError("Invalid Password !");
                        passwordEditText.requestFocus();
                    }
                }
                else
                {
                    // hide and stop the fading circle progress bar animation
                    stopProgressBarAnimation();

                    emailEditText.setError("Invalid Email !");
                    emailEditText.requestFocus();
                }
            }
        });
    }

    private void stopProgressBarAnimation() {
        if((progressBar != null) && (fadingCircle != null)) {
            // hide and stop the fading circle progress bar animation
            progressBar.setVisibility(View.INVISIBLE);
            fadingCircle.stop();
            progressBar.clearAnimation();
        }
    }

    private void startProgressBarAnimation() {
        if((progressBar != null) && (fadingCircle != null)) {
            // start fading circle progress bar animation
            progressBar.setVisibility(View.VISIBLE);
            fadingCircle.start();
        }
    }

    /*
        getting an intent from the Register activity and getting all the bundled values
        and putting the email and password in the EditText's.
     */
    private void getIntentFromRegisterActivity() {
        i = getIntent();
        b = i.getExtras();
        if (b != null && !b.isEmpty()){
            userDetails.clear();
            userDetails.put("email",i.getStringExtra("email"));
            userDetails.put("password",i.getStringExtra("password"));
            userDetails.put("raspberry_product_key",i.getStringExtra("raspberry_product_key"));

            emailEditText.setText(i.getStringExtra("email"));
            passwordEditText.setText(i.getStringExtra("password"));

            Toast.makeText(getApplicationContext(),"New User Registered !",Toast.LENGTH_LONG).show();
        }
    }

    /*
       sends an email to the user using the SendMail class.
     */
    private void sendEmail(String email, String subject , String message) {
        //Creating SendMail object
        SendMail sm = new SendMail(this, email, subject, message);

        //Executing 'sendmail' to send email
        sm.execute();
    }

    /*
        validating email address.
     */
    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    /*
        closing our application when pressing the back button
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        this.finish();
        System.exit(0);
        Log.i("onBackPressed","Context:"+this.getClass().getSimpleName());
    }

    /*
      when called, onDestroy will clear our user session object.
   */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.logoutUser();
    }

    /*
        checks to see if the device is connected to the 3G OR WiFi network.
    private void haveNetworkConnection()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (null != ni && ni.isConnectedOrConnecting()) {
            switch(ni.getTypeName()) {
                case "WIFI":
                    if (ni.isConnected())
                        Toast.makeText(getApplicationContext(),"Connected:"+ni.getTypeName(),Toast.LENGTH_LONG).show();
                    break;
                case "MOBILE":
                    if (ni.isConnected())
                        Toast.makeText(getApplicationContext(),"Connected:"+ni.getTypeName(),Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    }
    */
}