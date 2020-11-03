package com.mehdikerkar.moveup;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mehdikerkar.moveup.cardemulation.LockActivity;
import com.mehdikerkar.moveup.database.MyDBHandler;
import com.mehdikerkar.moveup.database.Reservation;

import general_utils.Base64BasicEncryption;
import general_utils.EndPoint;
import socket_utils.TCPSender;

import static java.lang.System.out;

public class MainActivity extends AppCompatActivity {

    MyDBHandler myDBHandler;
    SQLiteDatabase db;
    TextView StatutDBtextView;
    Button AccKeyBtn;
    Button AddResBtn;
    Button ViewServerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDBHandler = new MyDBHandler(this);//.getInstance(this);

        //REMETTRE L' ID AU DERNIER !!!!! POUR PAS AJOUTER EN RECOMMENCANT de 1
        Reservation reservation = new Reservation();
        reservation.toString();
        if (myDBHandler.getLastId() != 0)
            reservation.setRes_id(myDBHandler.getLastId());


        AccKeyBtn = (Button) findViewById(R.id.AccessKeyBtn);
        AddResBtn =(Button) findViewById(R.id.AddResBtn);
 //       ViewServerBtn = (Button)  findViewById(R.id.ViewServerBtn);
//        StatutDBtextView = (TextView) findViewById(R.id.StatutDBtextView);

        AddResBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                Intent newIntent = new Intent(getApplicationContext(), ClientAndroid.class);
                startActivity(newIntent);

                }
        });

        AccKeyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent secondIntent = new Intent(getApplicationContext(), LockActivity.class);
                //secondIntent.putExtra("com.exemple.learntutorialapp.SOMETHING", "HELLO ALGERIA !");
                startActivity(secondIntent);
            }
        });
      /* ViewServerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent MainLockIntent = new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(MainLockIntent);
            }
        });*/
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.setting_menu, menu);
        return true;
    }

    public MyDBHandler getMyDBHandler() {
        return myDBHandler;
    }

    public void setMyDBHandler(MyDBHandler myDBHandler) {
        this.myDBHandler = myDBHandler;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.action_clearDB) {
            myDBHandler.ClearAllHandler();
            Reservation reservation = new Reservation();
            reservation.setRes_id(0);
            Toast.makeText(this,"DB Cleared", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.action_AddRes) {
            Reservation nRes = new Reservation("09000","09000", "MEHDI-DRIVE_LAB", "2222-10-15", "2222-10-24");
            boolean b = addData(nRes);
            if ( b = true ){
                Toast.makeText(this,"Reservation Added", Toast.LENGTH_SHORT).show();
            }else Toast.makeText(this,"Reservation Not Added", Toast.LENGTH_SHORT).show();

            return true;
        }

        if (id == R.id.action_sendClr) {
            try{
                EndPoint endPoint = new  EndPoint("192.168.43.147",1009);
                String str = "clr d1 d2 0 1";  //MD5.getMd5("password");
                //Fct + dates + Numero Chambre + Numero Client
                String b64 = Base64BasicEncryption.encodeB64(str);
                out.println("etape 2" + b64);
                TCPSender.send(endPoint, b64);

            } catch (Exception e) {
                Toast.makeText(this, "Could not connect->" + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                out.println(e.getMessage());
            }
            return true;
        }

        if (id == R.id.action_sendItr) {
            try{
                EndPoint endPoint = new  EndPoint("192.168.43.147",1009);
                String str = "itr d1 d2 0 1";  //MD5.getMd5("password");
                //Fct + dates + Numero Chambre + Numero Client
                String b64 = Base64BasicEncryption.encodeB64(str);
                out.println("etape 2" + b64);
                TCPSender.send(endPoint, b64);

            } catch (Exception e) {
                Toast.makeText(this, "Could not connect->" + e.getMessage(),
                        Toast.LENGTH_LONG).show();
                out.println(e.getMessage());
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean addData(Reservation reservation){
        boolean insertData = myDBHandler.addHandler(reservation);

        if (insertData){
            toastMessage("Data Successfully Inserted");
            return true;
        }
        else{
            toastMessage("Something went wrong");
            return false;
        }
    }

    private void  toastMessage (String message){
        // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        out.println(message);
    }




}
