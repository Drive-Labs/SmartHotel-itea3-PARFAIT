package com.mehdikerkar.moveup.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.widget.Toast;

import com.mehdikerkar.moveup.common.logger.Log;

public class MyDBQuery {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "bookingDB.db";
    public static final String TABLE_NAME = "Reservation";
    public static final String COLUMN_ID = "Res_Id";
    public static final String COLUMN_NUMERO_CH = "NumChambre";
    public static final String COLUMN_NUMERO_CL = "NumClient";
    public static final String COLUMN_KEY = "Key";
    public static final String COLUMN_DATE_D = "DateD";
    public static final String COLUMN_DATE_F = "DateF";

    private Context context;

    public MyDBQuery(Context context){
        this.context = context;
        Log.println(1,"DBQuery","Conext");
    }

    public long insertReservation(Reservation reservation){

        long id = -1;
        MyDBHandler myDBHandler = MyDBHandler.getInstance(context);
        SQLiteDatabase sqLiteDatabase = myDBHandler.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NUMERO_CH,reservation.getNumChambre());
        contentValues.put(COLUMN_NUMERO_CL,reservation.getNumClient());
        contentValues.put(COLUMN_KEY,reservation.getKey());

        contentValues.put(COLUMN_DATE_D,reservation.getDated().toString());
        contentValues.put(COLUMN_DATE_F,reservation.getDatef().toString());



        try {
            id = sqLiteDatabase.insertOrThrow(TABLE_NAME, null, contentValues);
        } catch (SQLiteException e){
            Toast.makeText(context, "Operation failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            sqLiteDatabase.close();
        }

        return id;
    }
}
