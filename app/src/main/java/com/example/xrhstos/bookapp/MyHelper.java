package com.example.xrhstos.bookapp;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;



/**
 * Created by Dimitris Koufounakis on 28/3/2018.
 */

public class MyHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "book_db.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "books";

    private static final String BOOK_TITLE = "_bookTitle";
    private static final String PUBLISHER = "publisher";
    private static final String BOOK_RATING = "rating";


    public MyHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(" CREATE TABLE " + TABLE_NAME + " ( " + BOOK_TITLE + " TEXT, " + PUBLISHER + " TEXT, " + BOOK_RATING +
                " REAL, PRIMARY KEY(" + BOOK_TITLE +","+PUBLISHER+") );");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    /**
     *  This method adds a row to the database
     * @param bookTitle
     * @param publisher
     * @param rating
     * @return
     */
    public boolean addRecord(String bookTitle, String publisher, float rating){

        boolean result = false;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(BOOK_TITLE,bookTitle);
        values.put(PUBLISHER,publisher);
        values.put(BOOK_RATING,rating);

        try{//Try inserting into database.
            sqLiteDatabase.insertOrThrow(TABLE_NAME,null, values);
            result = true;
        }catch (SQLiteConstraintException alreadyInserted){
            //Row is already inserted
        }
        return result;

    }

    /**
     * This method deletes a row from the database
     * @param bookTitle
     * @param publisher
     * @return
     */
    public boolean deleteRecord(String bookTitle, String publisher){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        int numOfRowsDeleted = 0;

        try{
            numOfRowsDeleted = sqLiteDatabase.delete(TABLE_NAME, BOOK_TITLE + "='" + bookTitle +"'"+
                    " AND "+ PUBLISHER + "='" + publisher+"'", null);

        }catch (Exception noSuchColumn){
            //No such column
            numOfRowsDeleted = 0;
        }

        if(numOfRowsDeleted > 0){//If 1 row at least deleted return true
            return true;
        }else{
            return false;
        }


    }

    public Cursor findRecord(String bootTitle, String publisher){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String q = "SELECT * FROM books WHERE " + BOOK_TITLE + " ='" + bootTitle + "'" + " AND " + PUBLISHER + "='" + publisher + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(q,null);
        return cursor;
    }

    public boolean updateRecord(String bookTitle, String publisher, float rating){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        boolean result = false;
        ContentValues values = new ContentValues();
        values.put(BOOK_TITLE,bookTitle);
        values.put(PUBLISHER,publisher);
        values.put(BOOK_RATING,rating);
        String q = "SELECT * FROM books WHERE " + BOOK_TITLE + " ='" + bookTitle + "'" + " AND " + PUBLISHER + "='" + publisher + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(q,null);
        if(cursor.moveToFirst()){
            sqLiteDatabase.update(TABLE_NAME, values, q, null);
            result = true;
        }
        cursor.close();
        //sqLiteDatabase.close();
        return result;
    }
}
