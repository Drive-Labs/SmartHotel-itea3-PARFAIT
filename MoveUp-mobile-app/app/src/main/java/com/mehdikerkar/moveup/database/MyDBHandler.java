package com.mehdikerkar.moveup.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mehdikerkar.moveup.common.logger.Log;

import java.util.Date;

public class MyDBHandler extends SQLiteOpenHelper {

    static MyDBHandler myDBHandler;
    //information of database
    private static String TAG = "DatabaseHelper";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "bookingDB.db";
    public static final String TABLE_NAME = "Reservation";
    public static final String COLUMN_ID = "Res_Id";
    public static final String COLUMN_NUMERO_CH = "NumChambre";
    public static final String COLUMN_NUMERO_CL = "NumClient";
    public static final String COLUMN_KEY = "Key";
    public static final String COLUMN_DATE_D = "DateD";
    public static final String COLUMN_DATE_F = "DateF";

    //initialize the database

    public MyDBHandler(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    public static synchronized MyDBHandler getInstance(Context context){
        if(myDBHandler == null){
            myDBHandler = new MyDBHandler(context);
        }
        return myDBHandler;
    }

    @Override

    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = String.format("CREATE TABLE %s(%s INTEGER PRIMARYKEY, %s VARCHAR(20), %s VARCHAR(20), %s VARCHAR(30) , %s DATE, %s DATE)", TABLE_NAME, COLUMN_ID, COLUMN_NUMERO_CH, COLUMN_NUMERO_CL, COLUMN_KEY, COLUMN_DATE_D, COLUMN_DATE_F);
        db.execSQL(CREATE_TABLE);
        Log.println(1,"DataBase","DB created!");
    }

    @Override

    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public int getLastId () {
        int lastId = 0;
        SQLiteDatabase db = this.getWritableDatabase();

        String query = String.format("SELECT * " +
                "    FROM    %s" +
                "    WHERE   %s = (SELECT MAX(%s)  FROM %s);", TABLE_NAME, COLUMN_ID, COLUMN_ID, TABLE_NAME);



        Cursor cursor = db.rawQuery(query, null);
        if (cursor == null)
            System.out.println("Nigea2 " + cursor.toString());
        if (cursor != null && cursor.moveToFirst()) {
            cursor.moveToFirst();
            try {
                lastId = cursor.getInt(0);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Nigea");
            }

        }
        cursor.close();
        db.close();
        return lastId;
    }

    public Cursor viewData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    public String loadHandler() {
        String result = "";
        String query = String.format("Select * FROM %s", TABLE_NAME);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            int result_0 = cursor.getInt(0);
            String result_1 = cursor.getString(1);
            String result_2 = cursor.getString(2);
            String result_3 = cursor.getString(3);
            String result_4 = cursor.getString(4);
            String result_5 = cursor.getString(5);

            result += String.valueOf(result_0) + " " + result_1 + " " + result_2 + " " + result_3 +
                    " " + result_4 + " " + result_5 + System.getProperty("line.separator");
        }

        cursor.close();
        db.close();
        return result;
    }

    public Reservation loadHandleLastIn() {
        Reservation reservation = null;
        Date dated = null, datef = null;
        //String query = String.format("Select * FROM %s WHERE", TABLE_NAME);
        String query2 = String.format("SELECT * FROM %s ORDER BY %s DESC LIMIT 1", TABLE_NAME, COLUMN_ID);
        String query = String.format("SELECT * " +
                "    FROM    %s" +
                "    WHERE   %s = (SELECT MAX(%s)  FROM %s);", TABLE_NAME,COLUMN_ID,COLUMN_ID,TABLE_NAME);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if ( cursor.moveToLast()){
                String string = cursor.getString(1);
                String string_2 = cursor.getString(2);
                String string_3 = cursor.getString(3);
                String result_4 = cursor.getString(4);
                String result_5 = cursor.getString(5);

                reservation = new Reservation(string, string_2, string_3, result_4, result_5);

        }else {
            Log.d(TAG, "Query is null");
        }


        cursor.close();
        db.close();
        return reservation;
    }


    public void ClearAllHandler()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
        db.close();
    }

    public ReservationList loadHandlerList() {
        ReservationList reservationList = null;
        Date dated = null, datef = null;
        String query = String.format("Select * FROM %s", TABLE_NAME);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            int result_0 = cursor.getInt(0);
            Reservation reservation = new Reservation(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5));
            reservationList.List.add(0,reservation);
        }

        cursor.close();
        db.close();
        return reservationList;
    }


    public boolean addHandler(Reservation reservation) {
        ContentValues values = new ContentValues();

        values.put(COLUMN_ID, reservation.getRes_id());
        values.put(COLUMN_NUMERO_CH, reservation.getNumChambre());
        values.put(COLUMN_NUMERO_CL, reservation.getNumClient());
        values.put(COLUMN_KEY, reservation.getKey());
        values.put(COLUMN_DATE_D,  reservation.getDated() );
        values.put(COLUMN_DATE_F, reservation.getDatef() );

        SQLiteDatabase db = this.getWritableDatabase();
        Log.d(TAG,"add Reservation");
        long result = db.insert(TABLE_NAME, null, values);
        db.close();

        if (result == -1){return false;}
        else {return true;}
    }

    public Reservation findHandler(String Res_Id) {
        String query = "Select * FROM " + TABLE_NAME + "WHERE" + COLUMN_ID + " = " + "'" + Res_Id + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Reservation reservation = new Reservation();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            reservation.setRes_id(Integer.parseInt(cursor.getString(0)));
            reservation.setNumChambre(cursor.getString(1));
            reservation.setNumClient(cursor.getString(2));
            reservation.setKey(cursor.getString(3));
            reservation.setDated(cursor.getString(4));
            reservation.setDatef(cursor.getString(5));

            cursor.close();
        } else {
            reservation = null;
        }

        db.close();
        return reservation;
    }

    public boolean deleteHandler(int Res_Id) {
        boolean result = false;
        String query = "Select * FROM" + TABLE_NAME + "WHERE" + COLUMN_ID + "= '" + String.valueOf(Res_Id) + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Reservation reservation = new Reservation();

        if (cursor.moveToFirst()) {
            reservation.setRes_id(Integer.parseInt(cursor.getString(0)));
            db.delete(TABLE_NAME, COLUMN_ID + "=?",
                    new String[] {
                            String.valueOf(reservation.getRes_id())
            });
            cursor.close();
            result = true;

        }

        db.close();
        return result;

    }

    public boolean updateHandler(int Res_Id,String numChambre, String numClient, String key, Date dateD, Date dateF) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(COLUMN_ID, Res_Id);
        args.put(COLUMN_NUMERO_CH, numChambre);
        args.put(COLUMN_NUMERO_CL, numClient);
        args.put(COLUMN_KEY, key);
        args.put(COLUMN_DATE_D, dateD.toString());
        args.put(COLUMN_DATE_F, dateF.toString());

        return db.update(TABLE_NAME, args, COLUMN_ID + "=" + Res_Id, null) > 0;

    }



}

