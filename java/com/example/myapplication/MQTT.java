package com.example.myapplication;
/*
    MQTT class is responsible for starting and stoping an MQTT instance.
 */
public class MQTT {
    MainMqttAndVideoActivity activity;
    MQTTThread thread;
    /*
        main constructor
     */
    public MQTT(MainMqttAndVideoActivity activity) {
        this.activity = activity;
    }

    /*
    starts the service.
     */
    void start() {
        if (thread != null)
            return;

        thread = new MQTTThread(activity);
        thread.start();
    }

    /*
    stops the service.
     */
    void stop() {
        if (thread == null)
            return;

        thread.quit();
        thread = null;
    }
}