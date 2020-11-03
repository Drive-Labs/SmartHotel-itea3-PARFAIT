package com.mehdikerkar.moveup;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mehdikerkar.moveup.cardemulation.LockActivity;
import com.mehdikerkar.moveup.database.Reservation;
import com.mehdikerkar.moveup.database.ReservationList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainLockActivity extends AppCompatActivity {

    Reservation resList[];
    ArrayList<Reservation> reservationList = new ArrayList<Reservation>();
    ReservationList reservationListFromDBMainnActivity = new ReservationList();

    ReservationsAdapter reservationsAdapter;

    ListView listView = (ListView) findViewById(R.id.Key_listView);

    MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_lock);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Construct the data source
        if (reservationList == null){
            reservationList = new ArrayList<Reservation>();
        }
        // Create the adapter to convert the array to views
        //ArrayList<Reservation> arrayOfRess = new ArrayList<Reservation>();
        ReservationsAdapter adapter = new ReservationsAdapter(this, reservationList);

        // Attach the adapter to a ListView
        listView.setAdapter(adapter);

        resList = new Reservation[]{};


        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.parse("9/10/2015");
/*
            Reservation nRes1 = new Reservation("00001","00001","xxxxxxxxx1",sdf.parse("9/10/2015"), sdf.parse("10/10/2015"));
            Reservation nRes2 = new Reservation("00002","00001","xxxxxxxxx2",sdf.parse("04/07/19"), sdf.parse("07/07/19"));
            Reservation nRes3 = new Reservation("00003","00001","xxxxxxxxx3",sdf.parse("15/07/19"), sdf.parse("21/07/19"));
            Reservation nRes4 = new Reservation("00004","00001","xxxxxxxxx4",sdf.parse("28/07/19"), sdf.parse("30/07/19"));
            //nRes1, nRes2, nRes3, nRes4};
            reservationList.add(nRes1);
            reservationList.add(nRes2);
            reservationList.add(nRes3);
            */
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("reservation List Size"+reservationList.size());

        System.out.println(mainActivity.getMyDBHandler());
        reservationListFromDBMainnActivity = mainActivity.getMyDBHandler().loadHandlerList();

        for(int i=0 ; i < resList.length ; i++){
            resList[i] = reservationListFromDBMainnActivity.List.get(i);
            adapter.insert(resList[i], 0);
            //adapter.insert(reservationListFromDBMainnActivity.List.get(i), 0);
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View view, int i, long l) {
                Toast.makeText(MainLockActivity.this, "myPos "+i, Toast.LENGTH_LONG).show();
                Intent newIntent = new Intent(getApplicationContext(), LockActivity.class);
                //newIntent.putExtra("com.mehdikerkar.moveup.KEY", resList[i].getKey());
                startActivity(newIntent);
            }
                });
    }
        public class ReservationsAdapter extends ArrayAdapter<Reservation> {
            public ReservationsAdapter(Context context, ArrayList<Reservation> ress) {
                super(context, 0, ress);
            }

            @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
                Reservation res = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, parent, false);
            }
            // Lookup view for data population
            TextView tvNumCh = (TextView) convertView.findViewById(R.id.textViewNumCh);
            TextView tvDated = (TextView) convertView.findViewById(R.id.textViewDateDDisplay);
            TextView tvDatef = (TextView) convertView.findViewById(R.id.textViewDateF);
            ImageView ivAccess = (ImageView) convertView.findViewById(R.id.imageViewAccess);

            // Populate the data into the template view using the data object
            SimpleDateFormat form = new SimpleDateFormat("dd/MM/yyyy");
            tvNumCh.setText(res.getNumChambre());
            tvDated.setText(form.format(res.getDated()));
            tvDatef.setText(form.format(res.getDatef()));
            Date c = Calendar.getInstance().getTime();

            if (c.toString().compareTo(res.getDated().toString()) < 0  && c.toString().compareTo(res.getDatef().toString()) > 0) {
                System.out.println("normally the current date is bettween others");
               ivAccess.setImageResource(R.drawable.image_allow);
            }else{
                ivAccess.setImageResource(R.drawable.image_deny);
            }

            // Return the completed view to render on screen

            return convertView;

            }
    }

    public void viewData(){
        MainActivity mainActivity = new MainActivity();
        Cursor cursor = mainActivity.getMyDBHandler().viewData();

        if(cursor.getCount() == 0){
            Toast.makeText(this, "No data to show", Toast.LENGTH_SHORT).show();
        }else{
            while (cursor.moveToNext()){
                int result_0 = cursor.getInt(0);
                String result_1 = cursor.getString(1);
                String result_2 = cursor.getString(2);
                String result_3 = cursor.getString(3);
                String dated= cursor.getString(4);
                String datef = cursor.getString(5);

                Reservation reservation = new Reservation(result_1,result_2,result_3,dated,datef);
                reservationList.add(reservation);
            }

            //reservationsAdapter = new ArrayAdapter<Reservation>(this,reservationList);
        }
    }

    public ArrayList<Reservation> getReservationList() {
        return reservationList;
    }

    public void addOnList( Reservation reservation) {
        this.reservationList.add(reservation);
    }
}
