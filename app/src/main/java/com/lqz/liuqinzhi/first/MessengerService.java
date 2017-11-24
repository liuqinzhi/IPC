package com.lqz.liuqinzhi.first;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class MessengerService extends Service {

    private static final String TAG = "MessengerService";
    private static final int MSG_NUM = 0x110;

    //最好直接换成 HandlerThread 的形式
    private Messenger mMessenger = new Messenger(new Handler() {
        @Override
        public void handleMessage(Message msgFromClient) {

            Message msgToClient = Message.obtain(msgFromClient);
            switch (msgFromClient.what) {
                case MSG_NUM:
                    msgToClient.what = MSG_NUM;
                    try {
                        Thread.sleep(1000);
                        Log.e(TAG, "arg1 = " + msgFromClient.arg1 + ", arg2 = " + msgFromClient.arg2);
                        msgToClient.arg2 = msgFromClient.arg1 + msgFromClient.arg2;
                        msgToClient.replyTo.send(msgToClient);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
            }
            super.handleMessage(msgFromClient);
        }
    });

    public MessengerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }
}
