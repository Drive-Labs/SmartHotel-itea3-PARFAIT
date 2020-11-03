package com.mehdikerkar.moveup;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mehdikerkar.moveup.database.MyDBHandler;
import com.mehdikerkar.moveup.database.Reservation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import general_utils.AESEncryptDecrypt;
import general_utils.Base64BasicEncryption;
import general_utils.EndPoint;
import socket_utils.TCPSender;

import static java.lang.System.out;


public class ClientAndroid extends Activity implements OnClickListener {
    private static boolean exec = true;
    private static final int PORT = 1024;
    private static final GregorianCalendar gc = new GregorianCalendar();

    MyDBHandler myDBHandler;
    private Button btnConectar;
    EditText dateD, dateF, numCh;
    String date_str, numCh_str;
    TextView log;
    EndPoint endPoint = new  EndPoint("192.168.43.147",1009);// //"10.130.12.142", 1009);
    private String str;
    Context context = this;
    Activity runningActivity;
    Reservation nRes;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_2);

        myDBHandler = new MyDBHandler(this);

        btnConectar = (Button) findViewById(R.id.btnConectar);
        dateD = (EditText) findViewById(R.id.sendDateDEditText);
        dateF = (EditText) findViewById(R.id.sendDateFEditText);
        numCh = (EditText) findViewById(R.id.numChEditText);
        log = (TextView) findViewById(R.id.receivedDataTextView);
        btnConectar.setOnClickListener(this);

        out.println("----------- SERVER IP:" + getIPAddress(true) + " PORT " + PORT + " -----------");
        log.setText("- SERVER IP " + getIPAddress(true) + " PORT " + PORT + " -");
        out.println("Connexion waitting");


    }

    public Reservation getnRes() {
        return nRes;
    }

    public void setnRes(Reservation nRes) {
        this.nRes = nRes;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        if (view == btnConectar) { // Clicked the Connect button
            try {
                // Attempts to initiate a connection to the Socket Server
                out.println("etape 1 "+ dateD.getText().toString()+dateF.getText().toString());
                //--------------------------------------------------------------########################
                // implementation of 2 dates ---> one with the right format
                // change from key to numero chambre
                date_str = dateD.getText().toString()+"/12:00:00 "+dateF.getText().toString()+"/12:00:00";
                numCh_str = numCh.getText().toString();
                //////////////////////////////nRes.setKey("martien");
                if (date_str == "" || numCh_str == "")
                    Toast.makeText(this, "Fields Error", Toast.LENGTH_LONG).show();
                else{
                    String str = "add "+date_str+" "+ numCh_str +" 1";  //MD5.getMd5("password");
                    //Fct + dates + Numero Chambre + Numero Client
                    String b64 = Base64BasicEncryption.encodeB64(str);
                    out.println("etape 2" + b64);
                    TCPSender.send(endPoint, b64);
                    out.println("etape 3");
                    dateD.setText("");
                    dateF.setText("");
                    numCh.setText("");
                    out.println("previous Server");
                    runningActivity= this;
                    // pour plusieur Keys
                    //mainLockActivity.reservationList.add(new Reservation("00001","00001","xxxxxxxxx1",sdf.parse("9/10/2020"), sdf.parse("10/10/2020")));

                    lunchServer();
                    out.println("after Server");
                    //log.setText(s.getStr());
                }

            } catch (Exception e) {
                Toast.makeText(this, "Could not connect->" + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                out.println(e.getMessage());
            }
        }
    }



    /**
     * Method to recover ip from device

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return null;
    }
     */

    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void lunchServer(){

        Thread thread = new Thread(new Runnable() {
            @SuppressLint("LongLogTag")
            public void run() {

                ServerSocket server;
                String  arrOfStr[];
                String dkey = null, d, str= null, screenText = "";
                String finalStr;
                String Nkey = null;

                try {
                    // FOR thread interption
                    if(Thread.currentThread().isInterrupted()) {return;}

                    server = new ServerSocket(PORT);
                    int cont = 0;
                    while (exec) {
                        try (Socket socket = server.accept()){

                            try { Thread.sleep( 1000);
                            } catch (InterruptedException e) { e.printStackTrace(); }
                            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            str = in.readLine();
                            cont++;
                            out.println(cont + ": " + str + " - Ip: " + socket.getInetAddress().getHostAddress()
                                    + " as " + gc.get(GregorianCalendar.YEAR) + "-" + gc.get(GregorianCalendar.MONTH)
                                    + "-" + gc.get(GregorianCalendar.DAY_OF_MONTH) + " " + gc.get(GregorianCalendar.HOUR)
                                    + ":" + gc.get(GregorianCalendar.MINUTE) + ":" + gc.get(GregorianCalendar.SECOND));


                            finalStr = str; //garde original

                            if(str!=null){
                                try{
                                    arrOfStr = str.split(" ", 4);
                                    d = arrOfStr[1];
                                    dkey = d.substring(1, d.length()-1);
                                }catch(Error e){
                                    e.printStackTrace();
                                }
                                out.println("la cle encrypté recu: "+dkey);
                                AESEncryptDecrypt aesEncryptDecrypt = new AESEncryptDecrypt();

                                try {
                                    Nkey = aesEncryptDecrypt.AESdecrypt(dkey);
                                } catch (InvalidAlgorithmParameterException e) {
                                    e.printStackTrace();
                                } catch (InvalidKeyException e) {
                                    e.printStackTrace();
                                } catch (BadPaddingException e) {
                                    e.printStackTrace();
                                } catch (IllegalBlockSizeException e) {
                                    e.printStackTrace();
                                } catch (NoSuchPaddingException e) {
                                    e.printStackTrace();
                                } catch (NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                }
                                out.println("la veritable cle: "+Nkey);
                            }else{
                                out.println("Socket VIDE");
                            }
                            //implementation function découppage de la socket ---> KEY
                            //et creation d'une Reservation
                            /*SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                            Reservation nRes = new Reservation("00001","00001","xxxxxxxxx1",sdf.parse("9/10/2020"), sdf.parse("10/10/2020"));
                            mainLockActivity.reservationList.add(nRes);*/
                            String  arrOfDates[] = date_str.split(" ");
                            String dated = arrOfDates[0].split("/",2)[0];
                            String datef = arrOfDates[1].split("/",2)[0];
                            out.println("dated:"+dated+" datef:"+datef);

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                            nRes = new Reservation(numCh_str,"00001", Nkey, dated, datef);
                            //mainActivity = new MainActivity();
                            addData(nRes);
                            //mainActivity.getDb().addHandler(nRes);

                            final String finalStr1 = finalStr;
                            /*
                            runningActivity.runOnUiThread(new Runnable() {
                                public void run() {
                                    log.append(finalStr1 +"\n");
                                    //log.setText(finalStr);
                                }
                            });*/
                            log.append(finalStr1 +"\n");
                            out.println("apres log");
                        } catch (IOException e) {
                            System.err.println("Server -> Error: " + e.getMessage());
                            exec = false;
                        }
                        try { Thread.sleep( 2*1000);
                        } catch (InterruptedException e) { e.printStackTrace(); }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });
        thread.start();
    }

    public void addData(Reservation reservation){
        boolean insertData = myDBHandler.addHandler(reservation);

        if (insertData){
            toastMessage("Data Successfully Inserted");
        }
        else{
            toastMessage("Something went wrong");
        }
    }

    private void  toastMessage (String message){
       // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        out.println(message);
    }
}