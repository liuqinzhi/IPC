package com.lqz.liuqinzhi.first;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServer {

    public static void main(String[] args) throws IOException {

        //创建一个ServerSocket,用来监听客户端的socket请求
        ServerSocket serverSocket = new ServerSocket(30000);
        //采用循环不断接受客户端的请求,服务端也将产生一个对应的socket
        while(true) {

            Socket socket = serverSocket.accept();
            OutputStream os = socket.getOutputStream();
            os.write("您好,您收到了服务器的信念祝福!".getBytes("UTF-8"));
            os.close();
            socket.close();
        }
    }

}

