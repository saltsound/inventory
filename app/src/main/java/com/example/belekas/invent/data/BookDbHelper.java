package com.example.belekas.invent.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.belekas.invent.data.BookContract.BooksEntry;

public class BookDbHelper extends SQLiteOpenHelper{

    /** Name of the database file */
    private static final String DATABASE_NAME = "shelf.db";

    /**
     * Database version.
     */
    private static final int DATABASE_VERSION = 1;

    public BookDbHelper(Context context){ super (context, DATABASE_NAME, null, DATABASE_VERSION);}

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statements for database
        String SQL_CREATE_BOOKS_TABLE =  "CREATE TABLE " + BooksEntry.TABLE_NAME + " ("
                + BooksEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BooksEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL, "
                + BooksEntry.COLUMN_BOOK_PRICE + " INTEGER NOT NULL DEFAULT 0, "
                + BooksEntry.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + BooksEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + BooksEntry.COLUMN_SUPPLIER_PHONE + " TEXT NOT NULL);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    // if needed upgrades database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
