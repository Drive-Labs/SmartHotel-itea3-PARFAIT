package socket_utils;

import general_utils.EndPoint;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class TCPSender {
    public static void send(final EndPoint endPoint, final String str){
        new Thread(new Runnable(){
            public void run(){
                Socket s = null;
                PrintWriter pw;
                try {
                    s = new Socket(endPoint.ipAddress, endPoint.port);
                            System.out.println("thread socket 1");
                    pw = new PrintWriter(new OutputStreamWriter(
                            s.getOutputStream()), true);
                    pw.println(str);
                            System.out.println("thread socket 3");
                    pw.close();
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
    /*
    public static void send(EndPoint endPoint, byte[] bytes){
        try {
            Socket s = new Socket(endPoint.ipAddress, endPoint.port);
            OutputStream outputStream = s.getOutputStream();
            outputStream.write(bytes);
            outputStream.close();
            s.close();
            //Socket client = new Socket("192.168.0.80", 80);
            try {
                s.setSoTimeout(100);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            //DataOutputStream dataOutputStream = new DataOutputStream(s.getOutputStream());
            //DataInputStream dataInputStream = new DataInputStream(s.getInputStream());
            //String messsage="bb";
            //dataOutputStream.writeUTF(messsage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
