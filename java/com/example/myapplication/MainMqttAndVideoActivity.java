package com.example.myapplication;
/**
 * Created by Moshe Malka on 20/04/2016
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.ClientCertRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import xyz.hanks.library.SmallBang;


public class MainMqttAndVideoActivity extends AppCompatActivity {
    // setting up main static variables.
    final static int SIZE_OF_FAB_SUB_BUTTONS = 180;
    final static int SIZE_OF_FAB_MAIN_BUTTON = 300;
    final static String FONT_NAME = "Dekar.otf";
    // setting UI components.
    TextView temperatureTextView, humidityTextView;
    ImageView icon_pic,icon_logout,icon_charts, babySmileImageView,babyCryImageView;
    WebView web;

    FloatingActionButton actionButton;
    SubActionButton.Builder itemBuilder;
    SubActionButton takePictureButton,logoutUserButton,chartsButton;

    Typeface myTypeFace;
    // setting objects that refer to the handling of MQTT messaging in this activity.
    MQTT mqtt;
    Handler mqttHandler;
    // setting the object that is responsible for the 'baby cried' animation.
    SmallBang mSmallBang;

    UserSessionManager session;
    HashMap<String,String> userDetails;

    // setting the Relative Layout object.
    private RelativeLayout myRelativeLayout;
    // setting the bitmap for our screenshot action.
    private Bitmap bmp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mqtt_and_video_activity_layout);
        web = (WebView)findViewById(R.id.webView);
        if (web != null) {
            web.getSettings().setLoadWithOverviewMode(true);
            web.getSettings().setJavaScriptEnabled(true);
            web.getSettings().setUseWideViewPort(true);
            web.setWebChromeClient(new WebChromeClient());
            web.loadUrl("https://10.0.0.7:8080/stream");
        }
        // setup an MQTT instance to communicate with the broker and get messages.
        mqtt = new MQTT(this);
        // setup the handler that will handle all incoming messages.
        mqttHandler = new Handler();
        Log.i("mqtt topic",MqttConfig.getTopicHead());
        //  initializing the UserSessionManager object.
        session = new UserSessionManager(getApplicationContext());

        userDetails = session.getUserDetails();
        Log.d("user session",userDetails.toString());

        // initiate configure and run my custom floating action button.
        mSmallBang = SmallBang.attach2Window(this);
        try{
            // initiate the babyCryImageView
            babyCryImageView = (ImageView)findViewById(R.id.babyScreamImageView);
            if (babyCryImageView != null) {
                babyCryImageView.setVisibility(View.INVISIBLE);
            }
            // initiate the custom FAB
            setupTextViews();
            initiateFAB();
            setupFABButtons();
            setupActionMenu();
        }catch (Exception e){
            e.printStackTrace();
            Log.e("error in onCreate "," FAB initiation Failed");
            Toast.makeText(MainMqttAndVideoActivity.this,"FAB initiation Failed",Toast.LENGTH_LONG).show();
        }
        myRelativeLayout = (RelativeLayout)findViewById(R.id.mainWindowRelativeLayout);

            /*      ******************  Uncomment after final testing   ******************
        if(session.checkLogin()){
            finish();
        }
        */
    }

    /*
        captures a screenshot of a given View object.
     */
    public static Bitmap captureScreenshot(View v) {
        Bitmap screenshot = null;
        try {
            if(v!=null) {
                screenshot = Bitmap.createBitmap(v.getMeasuredWidth(),v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(screenshot);
                v.draw(canvas);
            }
        }catch (Exception e){
            Log.d("Main MQTT & Video", "Screenshot capture Failed:" + e.getMessage());
        }
        return screenshot;
    }

    /*
        sets up the action menu with all the buttons we built earlier in the code.
     */
    private void setupActionMenu() {
        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(takePictureButton,SIZE_OF_FAB_SUB_BUTTONS,SIZE_OF_FAB_SUB_BUTTONS)
                .addSubActionView(logoutUserButton,SIZE_OF_FAB_SUB_BUTTONS,SIZE_OF_FAB_SUB_BUTTONS)
                .addSubActionView(chartsButton,SIZE_OF_FAB_SUB_BUTTONS,SIZE_OF_FAB_SUB_BUTTONS)
                .attachTo(actionButton)
                .build();
    }

    /*
        sets up a button for every icon that pops-out from the FAB,
         and sets onClickListener's on each of them.
     */
    private void setupFABButtons() {
        // initiate the itemBuilder with the activity context.
        itemBuilder = new SubActionButton.Builder(this);
        // set size and content view of four buttons,
        // set onClickListener to each of them.
        takePictureButton = itemBuilder.setContentView(icon_pic)
                .build();
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // take picture from main window containing live video stream.
                myRelativeLayout.post(new Runnable() {
                    public void run() {

                        //take screenshot
                        bmp = captureScreenshot(myRelativeLayout);

                        TakeScreenshot t = new TakeScreenshot();
                        t.SaveImage(getApplicationContext(),bmp);
                    }

                });
            }
        });

        logoutUserButton = itemBuilder.setContentView(icon_logout)
                .build();
        logoutUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.logoutUser();
                /*
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // Add new Flag to start new Activity
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MainMqttAndVideoActivity.this.startActivity(i);
                */
                //finish();
            }
        });

        chartsButton = itemBuilder.setContentView(icon_charts)
                .build();
        chartsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SensorDataActivity.class);
                MainMqttAndVideoActivity.this.startActivity(i);
                finish();
            }
        });
    }

    /*
        this function sets up the Custom Floating Action Button
        by initiating ImageView's objects and assigning them an appropriate icon.
     */
    private void initiateFAB() {
        // assigning an icon to an image view
        babySmileImageView = new ImageView(this);
        babySmileImageView.setImageResource(R.drawable.baby_smile);
        // sets up and builds the fab content view (the ImageView from above) ,background shape and size.
        actionButton = new FloatingActionButton.Builder(this)
                .setContentView(babySmileImageView)
                .setBackgroundDrawable(R.drawable.circle)
                .setLayoutParams(new FloatingActionButton.LayoutParams(SIZE_OF_FAB_MAIN_BUTTON,SIZE_OF_FAB_MAIN_BUTTON))
                .build();
        //setting up the icons that come out of the fab.
        icon_pic = new ImageView(this);
        icon_pic.setImageResource(R.drawable.picture_24);

        icon_charts = new ImageView(this);
        icon_charts.setImageResource(R.drawable.statistics_24);

        icon_logout = new ImageView(this);
        icon_logout.setImageResource(R.drawable.exit_24);
    }

    /*
        this function sets up all the TextView's and their font.
     */
    private void setupTextViews() {
        // custom font for the temperature and humidity readings.
        myTypeFace = Typeface.createFromAsset(getAssets(), FONT_NAME);

        temperatureTextView = (TextView) findViewById(R.id.temperatureTextView);
        humidityTextView = (TextView) findViewById(R.id.humidityTextView);

        temperatureTextView.setTypeface(myTypeFace);
        humidityTextView.setTypeface(myTypeFace);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mqtt.start();
        Log.i("Mqtt","Started / onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mqtt.stop();
        Log.i("Mqtt","Stoped / onPause");
    }

    /*
        receives messages from the MQTT thread and handles them according to their topic.
     */
    public void onReceiveMessage(String topic, final String s) {
        mqttHandler.post(new Runnable() {
                    String topic, message;

                    public Runnable setMessage(String topic, String message) {
                        this.topic = topic;
                        this.message = message;
                        return this;
                    }

                    @Override
                    public void run() {
                        // trim our message from unnecessary blank spaces.
                        String m = message.trim().toLowerCase();

                        // topic = "babykeeper/temperature"
                        if (topic.equals(MqttConfig.getTopicsByIndex(0)))
                        {
                            // set the temperature TextView with the Celsius sign.
                            temperatureTextView.setText(m + MqttConfig.DEGREE_SIGN);
                            // set the TextView color according to the value of the message.
                            setTemperatureColor(Double.parseDouble(m));
                        }
                        // topic = "babykeeper/humidity"
                        else if(topic.equals(MqttConfig.getTopicsByIndex(1)))
                        {
                            // set the humidity TextView with the percent sign.
                            humidityTextView.setText(m + MqttConfig.PERCENT_SIGN);
                        }
                        // topic = "babykeeper/sound"
                        else if(topic.equals(MqttConfig.getTopicsByIndex(2)))
                        {
                            // handle a message that says that the baby is crying/screaming.
                            if(Integer.valueOf(m) > 95)
                            {
                                handleBabySound();
                            }
                        }
                        // topic = "babykeeper/Ras_Ip"
                        else
                        {
                            // TODO : do something with raspberry address.
                        }
                        /*
                        // switch statement to handle different topics.
                        switch (topic) {

                            case MqttConfig.getTopicsByIndex(0):
                                // set the temperature TextView with the Celsius sign.
                                temperatureTextView.setText(m + MqttConfig.DEGREE_SIGN);
                                // set the TextView color according to the value of the message.
                                setTemperatureColor(Double.parseDouble(m));
                                break;

                            case MqttConfig.TOPIC_HUMIDITY:
                                // set the humidity TextView with the precent sign.
                                humidityTextView.setText(m + MqttConfig.PERCENT_SIGN);
                                break;

                            case MqttConfig.TOPIC_RASPBERRY_IP_ADDRESS:

                                break;

                            case MqttConfig.TOPIC_SOUND:
                                // handle a message that says that the baby is crying/screaming.
                                handleBabySound();
                                break;
                        }
                        */
                    }
                }.setMessage(topic, s)
        );
    }

    /*
        this function handles a message that says the baby has screamed/cried.
        it fills the screen with an animation of a crying baby for 5 seconds.
        * uses an external library.
    */
    private void handleBabySound() {
        babyCryImageView.setVisibility(View.VISIBLE);
        new CountDownTimer(6000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.i("Finishing Animation in:",Long.toString(millisUntilFinished /1000));
                mSmallBang.bang(findViewById(R.id.babyScreamImageView));
            }
            @Override
            public void onFinish() {
                babyCryImageView.setVisibility(View.INVISIBLE);
                cancel();
            }
        }.start();
    }

    /*
        this function sets a color parameter to the temperature TextView based on the given value.
     */
    public void setTemperatureColor(double value){
        if (value<=15.0){
            temperatureTextView.setTextColor(Color.parseColor(TemperatureColors.COLD.toString()));
        }
        else if(value>15.0 && value<20.0){
            temperatureTextView.setTextColor(Color.parseColor(TemperatureColors.NORMAL.toString()));
        }
        else if(value>20.0 && value <=23.0){
            temperatureTextView.setTextColor(Color.parseColor(TemperatureColors.WARM.toString()));
        }
        else if(value>23.0 && value<=26.0){
            temperatureTextView.setTextColor(Color.parseColor(TemperatureColors.HOT.toString()));
        }
        else if(value>26.0){
            temperatureTextView.setTextColor(Color.parseColor(TemperatureColors.TOO_HOT.toString()));
        }
    }

    /*
       going to the login activity when the back button was pressed.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        session.logoutUser();
        Log.i("onBackPressed","Context:"+this.getClass().getSimpleName());
    }

    /*
      when called, onDestroy will clear our user session object.
   */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        session.logoutUser();
        Log.i("MainMqttActivity","onDestroy");
    }
}