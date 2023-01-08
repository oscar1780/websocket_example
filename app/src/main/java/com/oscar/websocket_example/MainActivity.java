package com.oscar.websocket_example;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.oscar.websocket.XTRWebsocket;
import com.oscar.websocket.XTRWebsocketRecvDataCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity implements XTRWebsocketRecvDataCallback {

    private XTRWebsocket xtrWebsocket = null;
    private String websocketUrl = "ws://192.168.0.101:8000/websocket";

    // 机器人状态请求消息
//    private String xtrStatusRequest = "{\"op\":\"subscribe\",\"topic\":\"/xtrobot_interface/xtrobot_status\",\"type\":\"xtr_database/xtrobot_status\"}";

    private String xtrStatusRequest = "{\n" +
            "\t\"topic\": \"/xtrobot_interface/xtrobot_status\",\n" +
            "\t\"msg\": {\n" +
            "\t\t\"batteryStatus\": [50.9, 3.0, 77.0, 9.0],\n" +
            "\t\t\"robotMode\": \"standby\",\n" +
            "\t\t\"detectState\": \"\",\n" +
            "\t\t\"taskId\": \"\",\n" +
            "\t\t\"detectUuid\": \"\",\n" +
            "\t\t\"pauseState\": 0,\n" +
            "\t\t\"chargeState\": 0,\n" +
            "\t\t\"taskState\": \"\",\n" +
            "\t\t\"mapId\": \"default_map\",\n" +
            "\t\t\"ptzId\": -1,\n" +
            "\t\t\"sensorData\": [0.0, 12.0, 9.2, 0.0],\n" +
            "\t\t\"dateTime\": \"2022-12-07 14:17:04\",\n" +
            "\t\t\"robotId\": \"ROBOT_002\",\n" +
            "\t\t\"robotVelocity\": 0.0,\n" +
            "\t\t\"taskUuid\": \"\",\n" +
            "\t\t\"errorCode\": \"\",\n" +
            "\t\t\"timestamp\": 1670393824,\n" +
            "\t\t\"pointId\": -1,\n" +
            "\t\t\"peripheralDistance\": [45, 308, 369, 44, 3, 3],\n" +
            "\t\t\"rbotPositionHeading\": [0.0, 0.0, -0.0],\n" +
            "\t\t\"taskCount\": 0\n" +
            "\t},\n" +
            "\t\"op\": \"publish\"\n" +
            "}";

    // 调试信息头 TAG
    private String TAG = "MainActivity";

    // 机器人信息显示框
    private TextView robotInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        robotInfoTextView = findViewById(R.id.textView);

        // 初始化并打开websocket
        xtrWebsocket = new XTRWebsocket();
        xtrWebsocket.initWebsocket(websocketUrl, this);

        new Thread(() -> {
            while (true) {
                try {
//                    String currentTime = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(new Date());
//                        xtrWebsocket.sendData(currentTime);
//                        Log.d("Thread",currentTime);
//                        xtrWebsocket.sendData(currentTime + "/" + xtrStatusRequest);
                    xtrWebsocket.sendData(xtrStatusRequest);
                    // 10 hz
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 关闭websocket
        if(null != xtrWebsocket) {
            xtrWebsocket.closeWebsocket();
        }

    }

    // websocket接收数据回调函数
    @Override
    public void onRecDataCallback(String _data) {
        JSONObject obj = JSON.parseObject(_data);
        String msgString = obj.getString("msg");
        JSONObject msgObj = JSON.parseObject(msgString);

        // 机器人模式
        String robotMode = msgObj.getString("robotMode");

        // 电池状态信息
        JSONArray batteryStatus = msgObj.getJSONArray("batteryStatus");
        double bat1 = batteryStatus.getDoubleValue(0);
        double bat2 = batteryStatus.getDoubleValue(1);
        double bat3 = batteryStatus.getDoubleValue(2);
        double bat4 = batteryStatus.getDoubleValue(3);

        // 传感器数据
        JSONArray sensorData = msgObj.getJSONArray("sensorData");
        double sen1 = sensorData.getDoubleValue(0);
        double sen2 = sensorData.getDoubleValue(1);
        double sen3 = sensorData.getDoubleValue(2);
        double sen4 = sensorData.getDoubleValue(3);

        // 机器人速度
        Double robotVelocity = msgObj.getDoubleValue("robotVelocity");

        // 避障传感器数据
        JSONArray peripheralDistance = msgObj.getJSONArray("peripheralDistance");
        int perp1 = peripheralDistance.getIntValue(0);
        int perp2 = peripheralDistance.getIntValue(1);
        int perp3 = peripheralDistance.getIntValue(2);
        int perp4 = peripheralDistance.getIntValue(3);
        int perp5 = peripheralDistance.getIntValue(4);
        int perp6 = peripheralDistance.getIntValue(5);

        // 机器人位置姿态数据
        JSONArray rbotPositionHeading = msgObj.getJSONArray("rbotPositionHeading");
        double rbph1 = rbotPositionHeading.getDoubleValue(0);
        double rbph2 = rbotPositionHeading.getDoubleValue(1);
        double rbph3 = rbotPositionHeading.getDoubleValue(2);

        // 机器人充电状态
        int chargeState = msgObj.getIntValue("chargeState");

//        Log.d(TAG,"*************************************");
//        Log.d(TAG,robotMode);
//        Log.d(TAG,String.valueOf(robotVelocity));
//        Log.d(TAG,String.valueOf(bat1) + " " + String.valueOf(bat2) + " " + String.valueOf(bat3) + " " + String.valueOf(bat4));
//        Log.d(TAG,String.valueOf(sen1) + " " + String.valueOf(sen2) + " " + String.valueOf(sen3) + " " + String.valueOf(sen4));
//        Log.d(TAG,String.valueOf(perp1) + " " + String.valueOf(perp2) + " " + String.valueOf(perp3) + " " + String.valueOf(perp4) + " " + String.valueOf(perp5) + " " + String.valueOf(perp6));
//        Log.d(TAG,String.valueOf(rbph1) + " " + String.valueOf(rbph2) + " " + String.valueOf(rbph3));
//        Log.d(TAG,String.valueOf(chargeState));

//        Log.d(TAG, String.valueOf(surfaceView.getWidth()));
//        Log.d(TAG, String.valueOf(surfaceView.getHeight()));

        // 在UI线程中更新界面
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String robotInfo = String.format("模式:%s\n速度:%.2f\n电量1:%.2f电量2:%.2f电量3:%.2f电量4:%.2f",
                                    robotMode,robotVelocity,bat1,bat2,bat3,bat4);
                robotInfoTextView.setText(robotInfo);
            }
        });
    }
}