package com.mehdikerkar.moveup;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.GregorianCalendar;

public class Server
{

    private static boolean exec = true;
    private static final int PORT = 1024;
    private static final GregorianCalendar gc = new GregorianCalendar();

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void main(String[] args)
    {
        Socket s = null;
        PrintWriter pw;
        ServerSocket server;
        InetAddress addr;

        try {
            server = new ServerSocket(PORT);
            addr = InetAddress.getLocalHost();
            System.out.println("----------- SERVER IP:" + addr.getHostAddress() + " PORT " + PORT + " -----------");
            System.out.println("Connexion waitting");

            int cont = 0;

            while (exec)
            {
                try (Socket socket = server.accept()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String str = in.readLine();
                    System.out.println(str);
                    cont++;
                    System.out.println(cont + ": " + str + " - Ip: " + socket.getInetAddress().getHostAddress()
                            + " as " + gc.get(GregorianCalendar.HOUR_OF_DAY)
                            + ":" + gc.get(GregorianCalendar.MINUTE) );
/*
                    s = new Socket("10.130.12.142", PORT);
                    pw = new PrintWriter(new OutputStreamWriter(s.getOutputStream()), true);
                    pw.println(str);*/
                }
            }
        } catch (IOException e) {
            System.err.println("Server -> Error: " + e.getMessage());
            exec = false;
        }
    }


}
