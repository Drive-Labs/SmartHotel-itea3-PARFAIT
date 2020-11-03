package com.mehdikerkar.moveup;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mehdikerkar.moveup.database.MyDBHandler;
import com.mehdikerkar.moveup.database.Reservation;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SettingsActivity extends AppCompatActivity {

    private MyDBHandler myDBHandler;
    private Button btnAdd, btnLoad;
    EditText etKeyIn, edNChIn, edKeyIn, edDDIn, edDFIn;
    TextView textView;
    String str = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActionBar actionBar = this.getSupportActionBar();

        edKeyIn = (EditText) findViewById(R.id.editTextKeyIn);
        edDDIn = (EditText) findViewById(R.id.editTextDateDIn);
        edDFIn = (EditText) findViewById(R.id.editTextDateFIn);
        edNChIn = (EditText) findViewById(R.id.editTextNumChIn);
        textView = (TextView) findViewById(R.id.textViewDisplay);

        btnAdd = (Button) findViewById(R.id.buttonAdd);
        btnLoad = (Button) findViewById(R.id.buttonLoad);

        myDBHandler = new MyDBHandler(this);





        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = edKeyIn.getText().toString();
                String dd = edDDIn.getText().toString();
                String df = edDFIn.getText().toString();
                String nc = edNChIn.getText().toString();

                Reservation reservation = null;
                if(edKeyIn.length() !=0 && edDDIn.length() !=0 && edDFIn.length() !=0 && edNChIn.length() !=0){
                    String md5Key = new String(md5(key));
                    reservation = new Reservation(nc,"1", md5Key, dd, df);
                    addData(reservation);
                }else{
                    toastMessage("Field Empty");
                }

            }
        });

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            loadData();
            }
        });
    }

    public void addData(Reservation reservation){
        boolean insertData = myDBHandler.addHandler(reservation);

        if (insertData){ toastMessage("Data Successfully Inserted"); }
        else{ toastMessage("Something went wrong"); }
    }

    public void loadData(){
        String loadedData = myDBHandler.loadHandler();
        str = str + loadedData;
        textView.setText(loadedData);
        if (loadedData == "") {
            toastMessage("DataBase Empty");
        }
        System.out.println("STR: "+str);
    }

    private void  toastMessage (String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    public byte[] md5(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString().getBytes();
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "".getBytes();
    }

}
