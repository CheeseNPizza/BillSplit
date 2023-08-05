package com.example.assignment_billsplit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Constants for the database name and version
    private static final String DATABASE_NAME = "bill_split";
    private static final int DATABASE_VERSION = 1;

    // Constructor for the DatabaseHelper class
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the database table when the database is first created
        String SQL_CREATE_PERSONS_TABLE = "CREATE TABLE " + BillContract.PersonEntry.TABLE_NAME + " (" +
                BillContract.PersonEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BillContract.PersonEntry.COLUMN_TIME + " TEXT, " +
                BillContract.PersonEntry.COLUMN_DETAIL + " TEXT);";

        db.execSQL(SQL_CREATE_PERSONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    // Method to insert a new record into the database
    public long insertRecord(String text) {
        SQLiteDatabase db = this.getWritableDatabase();

        Calendar calendar = Calendar.getInstance();

        // Get the current time
        String time = getTime();

        // Create a ContentValues object to store the data to be inserted
        ContentValues values = new ContentValues();
        values.put(BillContract.PersonEntry.COLUMN_TIME, time);
        values.put(BillContract.PersonEntry.COLUMN_DETAIL, text);

        // Insert the values into the database
        long newRowId = db.insert(BillContract.PersonEntry.TABLE_NAME, null, values);

        // Close the database connection
        db.close();

        // Return the ID of the newly inserted row
        return newRowId;
    }

    // Method to get the current time in a formatted string
    public String getTime()
    {
        Calendar calendar = Calendar.getInstance();

        // Extract year, month, day, hour, minute, and second
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Months are 0-based
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        // Get AM or PM designation
        String amPm = (hour >= 12) ? "PM" : "AM";
        // Convert hour to 12-hour format
        hour = (hour % 12 == 0) ? 12 : hour % 12;

        // Create a formatted time string
        String time = day + "/" + month + "/" + year + " " + hour + ":" + minute + ":" + second + " " + amPm;

        return time;
    }

    // Method to retrieve all details from the database
    public List<String> getAllDetails() {
        List<String> detailsList = new ArrayList<>();

        // Get a readable database
        SQLiteDatabase db = this.getReadableDatabase();

        // Define the columns to retrieve
        String[] projection = {BillContract.PersonEntry.COLUMN_DETAIL};

        // Query the database for details
        Cursor cursor = db.query(
                BillContract.PersonEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        // Iterate through the cursor to retrieve details and add them to the list
        while (cursor.moveToNext()) {
            String detail = cursor.getString(cursor.getColumnIndexOrThrow(BillContract.PersonEntry.COLUMN_DETAIL));
            detailsList.add(detail);
        }

        // Close the cursor
        cursor.close();

        // Return the list of details
        return detailsList;
    }
}

