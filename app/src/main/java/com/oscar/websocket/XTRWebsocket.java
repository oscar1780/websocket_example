package com.oscar.websocket;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/// XTR 讯腾机器人缩写
/// WebSocket 通讯类
public class XTRWebsocket extends WebSocketListener {
    private String TAG = "XTRWebsocket";

    // websocket 地址
    private String websocketUrl = "";
    // client对象
    private OkHttpClient websocketClient = new OkHttpClient.Builder().readTimeout(0, TimeUnit.MILLISECONDS).build();
    // websocket接口
    private WebSocket mWebSocket = null;
    // websocket接收到的数据
    private String recvData = "";
    // websocket接收数据回调接口
    private XTRWebsocketRecvDataCallback xtrWebsocketRecvDataCallback;

    // 初始化websocket
    public void initWebsocket(String _websocketUrl, XTRWebsocketRecvDataCallback _xtrWebsocketRecvDataCallback) {
        websocketUrl = _websocketUrl;
        // 设置请求信息
        Request request = new Request.Builder().url(websocketUrl).build();
        // 初始化mWebSocket对象，并设置消息监听为本类
        mWebSocket = websocketClient.newWebSocket(request, this);
        // 接收数据接口初始化
        xtrWebsocketRecvDataCallback = _xtrWebsocketRecvDataCallback;
    }

    // 关闭websocket
    public void closeWebsocket() {
        // Trigger shutdown of the dispatcher's executor so this process can exit cleanly.
        websocketClient.dispatcher().executorService().shutdown();
    }

    // 发送数据
    public boolean sendData(String _data) {
        return mWebSocket.send(_data);
    }

    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        super.onOpen(webSocket, response);

        // websocket 发送数据
//        webSocket.send("Hello...");
//        webSocket.send("...World!");
//        webSocket.send(ByteString.decodeHex("deadbeef"));
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        super.onMessage(webSocket, text);
//        Log.d(TAG,"Received MSG " + text);
        recvData = text;
        // 将数据通过接口回调
        xtrWebsocketRecvDataCallback.onRecDataCallback(recvData);
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
        super.onMessage(webSocket, bytes);
    }

    @Override
    public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        super.onClosing(webSocket, code, reason);
        // 关闭websocket
        webSocket.close(1000,null);
    }

    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
        t.printStackTrace();
    }

    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        super.onClosed(webSocket, code, reason);
    }


}
