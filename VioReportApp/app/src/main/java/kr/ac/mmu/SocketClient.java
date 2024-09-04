package kr.ac.mmu;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class SocketClient extends Thread {
    private static final int SERVER_PORT = 8080; // Change this to the actual server port
    private static final String SERVER_IP = "172.20.10.3"; // Change this to the actual server IP addres
    private String messages;
    private Handler handler;

    public SocketClient(Handler handler, String messages){
        this.handler = handler;
        this.messages = messages;
    }

    @Override
    public void run() {
        try {
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            if (socket.isConnected()) {
                Log.d("SocketThread", "Socket connected.");
            } else {
                Log.d("SocketThread", "Socket not connected.");
            }
            OutputStream outputStream = socket.getOutputStream();

            outputStream.write(messages.getBytes());
            Log.d("SocketThread", messages);


            // 데이터 수신
            InputStream inputStream = socket.getInputStream();
            byte[] buffer = new byte[1024];
            int readBytes;
            StringBuilder stringBuilder = new StringBuilder();
            while ((readBytes = inputStream.read(buffer)) != -1) {
                stringBuilder.append(new String(buffer, 0, readBytes));
            }
            String receivedMessage = stringBuilder.toString();

            // 소켓 종료
            socket.close();

            // 수신한 데이터 처리
            Log.d("SocketThread", "Received message: " + receivedMessage);

            if (handler != null) {
                Message msg = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("result", receivedMessage);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
