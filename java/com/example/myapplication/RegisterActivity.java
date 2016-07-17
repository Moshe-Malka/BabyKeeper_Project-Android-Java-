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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
/*
    activity used to get the user's email,password and product key and handle
    database insertion (if allowable) of a new user.
 */
public class RegisterActivity extends AppCompatActivity {
    // main UI objects deceleration.
    EditText emailEditText,passwordEditText,productKeyEditText;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity_layout);
        //  binding the UI objects to the activity xml.
        emailEditText = (EditText) findViewById(R.id.registerEmailEditText);
        passwordEditText = (EditText)findViewById(R.id.registerPasswordEditText);
        productKeyEditText = (EditText)findViewById(R.id.registerProductKeyEditText);
        registerButton = (Button)findViewById(R.id.registerButton);

        // initiating an onClick listener on the Register Button.
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // getting all our data into local-scope variables.
                final String email = emailEditText.getText().toString();
                final String password = passwordEditText.getText().toString();
                final String raspberry_product_key = productKeyEditText.getText().toString();

                // first - check if the email entered is a valid one.
                if (isValidEmail(email))
                {
                    // initiating a response listener to listen for any response from our request.
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                // json object to get information from our callback.
                                JSONObject jsonObject = new JSONObject(response);
                                Log.d("Json Data", jsonObject.toString());

                                // get the boolean check to see if the user entered a key that is present
                                // in our database.
                                boolean validProductKey = jsonObject.getBoolean("valid_key");

                                // getting two boolean variables , one for each insert statement.
                                boolean firstSuccess = jsonObject.getBoolean("first_insert_success");
                                boolean secondSuccess = jsonObject.getBoolean("second_insert_success");
                                // checking if they BOTH succeeded.

                                if(validProductKey)
                                {
                                    if (firstSuccess) {
                                        if (secondSuccess) {
                                            // if they did - go to the login activity and also send the product key,
                                            // email and password.
                                            Intent intent = new Intent(RegisterActivity.this, LoginAcvtivity.class);
                                            intent.putExtra("raspberry_product_key", raspberry_product_key);
                                            intent.putExtra("email", email);
                                            intent.putExtra("password", password);

                                            RegisterActivity.this.startActivity(intent);
                                        } else {
                                            // if it didn't - show alert dialog stating that there was an error
                                            // and add the reason of the error from our json object.
                                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                            builder.setTitle("Registration Failed")
                                                    .setMessage("Reason: /n" + jsonObject.getString("second_reason"))
                                                    .setNegativeButton("OK", null)
                                                    .create()
                                                    .show();
                                        }
                                    } else {
                                        // if it didn't - show alert dialog stating that there was an error
                                        // and add the reason of the error from our json object.
                                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                        builder.setTitle("Registration Failed")
                                                .setMessage("Reason: /n" + jsonObject.getString("first_reason"))
                                                .setNegativeButton("OK", null)
                                                .create()
                                                .show();
                                    }
                                }
                                else{
                                    // if the json object returned an answer that the key
                                    // entered by the user is not present in our DB - show an alert dialog.
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                    builder.setTitle("Registration Failed")
                                            .setMessage("Reason: /n" + "The Product Key You Entered" +
                                            " Is not A BabyKeeper"+"\u00a9"+
                                            "Valid Key.")
                                            .setNegativeButton("OK", null)
                                            .create()
                                            .show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                /*
                    1 - initiating a RegisterRequest object with the appropriate parameters.
                    2 - initiating a RequestQueue and setting it's context to this activity.
                    3 - adding the RegisterRequest object to our RequestQueue object.
                 */
                    RegisterRequest registerRequest = new RegisterRequest(email, password, raspberry_product_key, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                    queue.add(registerRequest);
                }
                else{
                    emailEditText.setError("Invalid Email !");
                    emailEditText.requestFocus();
                }
            }
        });
    }

    /*
        validating email address.
     */
    public static boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
