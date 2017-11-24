package com.lqz.liuqinzhi.first;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class SocketTCPService extends Service {

    private static final String TAG = "SocketTCPService";

    private boolean isServiceDestroyed = false;
    private String[] msgs = {"你好呀,哈哈", "请问你叫什么名字啊?", "今天北京天气真不错呀,sky",
            "你知道吗?我可是可以和多个人同时聊天的哦", "给你讲个笑话吧:据说爱笑的人运气都不会太差, 不知道真假."};

    public SocketTCPService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new TCPServer()).start();
        Log.e(TAG, "SocketTCPService create");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceDestroyed = true;
    }

    private class TCPServer implements Runnable {

        @Override
        public void run() {

            ServerSocket serverSocket = null;

            try {

                serverSocket = new ServerSocket(30000);
            } catch (IOException e) {
                Log.e(TAG, "establish tcp server failed, port: 30000");
                e.printStackTrace();
                return;
            }

            while (!isServiceDestroyed) {
                //接受客户端请求
                try {
                    Log.e(TAG, "wait accepting...");
                    final Socket socket = serverSocket.accept();
                    Log.e(TAG, "accept");

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                responseClient(socket);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        private void responseClient(Socket client) throws IOException {

            //用于接受客户端信息
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            //用于向客户端发送消息
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
            out.println("欢迎来到聊天室!");
            while (!isServiceDestroyed) {
                String str = in.readLine();
                Log.e(TAG, "msg from client: "+str);
                if (TextUtils.isEmpty(str)) {
                    //客户端断开连接
                    break;
                }
                int i = new Random().nextInt(msgs.length);
                out.println(msgs[i]);
                Log.e(TAG, "server send msg: "+msgs[i]);
            }

            Log.e(TAG, "client quit.");
            in.close();
            out.close();
            client.close();
        }
    }


}
