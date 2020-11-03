package socket_utils;

import android.os.Handler;

import general_utils.EndPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


public class TCPReceiver {

    private ServerSocket serverSocket;
    public static final int SERVERPORT = 1009;
    private Socket clientSocket;
    private BufferedReader input;
    Handler updateConversationHandler;
    String read;

    public void receive(final EndPoint endPoint){

        updateConversationHandler = new Handler();
        String msg = "";
        new Thread(new Runnable(){
            public void run(){
                Socket socket = null;
                try {
                    serverSocket = new ServerSocket(SERVERPORT);
                } catch (IOException e) { e.printStackTrace(); }

                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        socket = serverSocket.accept();
                        try {
                            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            String read = input.readLine();
                            updateConversationHandler.post(new updateUIThread(read));

                        }catch (IOException e) { e.printStackTrace(); }

                    }catch (IOException e) { e.printStackTrace(); }
                }

            }
        }).start();
    }

    class updateUIThread implements Runnable {
        private String msg;
        public updateUIThread(String str) {
            this.msg = str;
        }
        @Override
        public void run() {
            System.out.println("log: "+ msg);
        }
    }

    public Handler getUpdateConversationHandler() {
        return updateConversationHandler;
    }

    /**
     *  Getters and Setters
     */
    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }
}
