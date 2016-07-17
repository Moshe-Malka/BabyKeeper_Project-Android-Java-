package com.example.myapplication;

/**
 * Created by Moshe Malka on 27/04/2016.
 */

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
/*
    a Thread class responsible for all the MQTT initiation and callback handling.
 */
public class MQTTThread extends Thread {

    // static variables to set time to sleep between connections, and our MQTT thread name.
    final static int TIME_TO_SLEEP = 1000;
    final static String MQTT_THREAD_NAME = "MQTTThread";
    // activity object , used to link this thread to the  MainMqttAndVideoActivity activity.
    MainMqttAndVideoActivity activity;
    // a break flag for the main thread loop.
    boolean break_flag;
    // main mqtt-related objects.
    MqttClient client;
    MemoryPersistence persistence = new MemoryPersistence();
    MqttConnectOptions opts = new MqttConnectOptions();

    /*
    main constructor
     */
    public MQTTThread(MainMqttAndVideoActivity activity) {
        this.activity = activity;
        setName(MQTT_THREAD_NAME);
    }
    /*
        a function to check if the user is connected.
     */
    boolean isConnect() {
        if (client == null)
            return false;
        if (!client.isConnected()) {
            disconnect();
            return false;
        }
        return true;
    }
    /*
        the main connect function - connecting to an mqtt broker , registering with
         different parameters and subscribing to different topics.
     */
    boolean connect() {
        if (client != null)
            return true;

        try {
            client = new MqttClient(MqttConfig.FULL_ADDRESS, MqttConfig.CLIENT_ID,
                    persistence);
            opts.setCleanSession(true);
            opts.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
            client.connect(opts);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    disconnect();
                    Log.i("MQTT Connection","Connection Lost");
                    connect();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.i("Message Arrived","Topic: "+topic+"   Message: "+new String(message.getPayload()));
                    activity.onReceiveMessage(topic, message.toString());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
            // the topics we wish to get messages from.
            //client.subscribe("#");
            /*client.subscribe(new String[] { MqttConfig.TOPIC_TEMPERATURE,
                    MqttConfig.TOPIC_HUMIDITY,
                    MqttConfig.TOPIC_RASPBERRY_IP_ADDRESS,
                    MqttConfig.TOPIC_SOUND});
            */
            Log.i("Client",client.getClientId());

            client.subscribe(MqttConfig.TOPIC_TEMPERATURE);
            client.subscribe(MqttConfig.TOPIC_HUMIDITY);
            client.subscribe(MqttConfig.TOPIC_RASPBERRY_IP_ADDRESS);
            client.subscribe(MqttConfig.TOPIC_SOUND);


            if (client.isConnected()) {
                Log.i("MQTT Connection", "Connected");
            }
            else{
                Log.i("MQTT Connection", "Not Connected");
            }
        } catch (MqttException e) {
            e.printStackTrace();
            client = null;
            return false;
        }
        return true;
    }

    /*
        function to disconnect the user from the connection.
     */
    void disconnect() {
        if (client != null) {
            try {
                client.disconnect();
                Log.i("Mqtt Thread","Disconnecting...");
            } catch (MqttException e) {
                e.printStackTrace();
            }
            client = null;
        }
    }

    /*
        main run method
     */
    @Override
    public void run() {
        while (!break_flag) {
            if (!isConnect()) {
                connect();
            }
            try {
                Thread.sleep(TIME_TO_SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
                if (e.getCause() != null) {
                    Log.e("InterruptedException", e.getCause().toString());
                }
            }
        }
        disconnect();
    }
    /*
        function to quit the main thread.
     */
    public void quit() {
        break_flag = true;
        disconnect();
        this.interrupt();

        try {
            this.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}