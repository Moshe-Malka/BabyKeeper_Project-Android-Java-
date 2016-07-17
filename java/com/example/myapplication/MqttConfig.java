package com.example.myapplication;
import java.util.UUID;

/**
 * Created by Moshe Malka on 25/04/2016.
 */
public class MqttConfig {

    final static String DEGREE_SIGN = "°";
    final static String PERCENT_SIGN = "%";

    final static String CLIENT_ID = "client_number_"+UUID.randomUUID().toString();

    final static String BROKER_ADDRESS = "test.mosquitto.org";
    //final static String BROKER_ADDRESS = "iot.eclipse.org";
    //final static String BROKER_ADDRESS = "broker.hivemq.com";

    final static int BROKER_PORT = 1883;
    final static String FULL_ADDRESS = "tcp://"+BROKER_ADDRESS+":"+BROKER_PORT;

    private static String TOPIC_HEAD = "babykeeper";
    static String TOPIC_TEMPERATURE = TOPIC_HEAD+"/temperature";
    static String TOPIC_HUMIDITY = TOPIC_HEAD+"/humidity";
    static String TOPIC_RASPBERRY_IP_ADDRESS = TOPIC_HEAD+"/Ras_Ip";
    static String TOPIC_SOUND = TOPIC_HEAD+"/sound";


    public static void setTopicHead(String topicHead){
        TOPIC_HEAD = topicHead;
    }
    public static String getTopicHead(){
        return TOPIC_HEAD ;
    }

    public static String getTopicsByIndex(int index){
        String head = MqttConfig.TOPIC_HEAD;
        String[] topics = new String[] {
                head +"/temperature",
                head +"/humidity",
                head +"/sound",
                head +"/Ras_Ip"};
        return topics[index];
    }


}
