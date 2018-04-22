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

public class DatabaseHelper extends SQLiteOpenHelper {

    //DATABASE NAME,TABLES' NAMES
    private static final String DATABASE_NAME = "book_db.db";
    private static final int DATABASE_VERSION = 1;
    private static final String BOOKS_TABLE = "books";//<--TABLE
    private static final String BOOK_CATEGORIES_TABLE = "categories";//<--TABLE
    private static final String IMAGE_TABLE = "imageTable";//<--TABLE

    //BOOK_TABLE ATTRIBUTES
    private static final String BOOK_TITLE = "_bookTitle";
    private static final String PUBLISHER = "publisher";
    private static final String BOOK_RATING = "rating";

    private static final String BOOK_ID = "bookId";
    private static final String GOOGLE_ID = "googleId";
    private static final String ISBN = "isbn";
    private static final String BOOK_COVER_URL = "bookCoverUrl";
    private static final String DESCRIPTION = "description";
    private static final String CALLBACK_URL = "callBackUrl";
    private static final String PREVIEW_URL = "previewUrl";
    private static final String BUY_URL = "buyUrl";
    private static final String PAGE_COUNT = "pageCount";
    private static final String PUBLISHED_DATE = "publishedDate";
    private static final String IS_BOOK_COLLECTION = "isBookCollection";
    private static final String IS_BOOK_READ = "isBookRead";
    private static final String IS_BOOK_IN_WHISHLIST = "isBookInWishList";



    //IMAGE TABLE ATTRIBUTES
    //private static final String GOOGLE_ID = "googleId";     //Already declared above
    private static final String IMAGE_DATA = "image_data";

    //CATEGORIES TABLE ATTRIBUTES
    //private static final String GOOGLE_ID = "googleId";     //Already declared above
    private static final String CATEGORY = "category";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //BOOKS TABLE CREATE
        sqLiteDatabase.execSQL(" CREATE TABLE " + BOOKS_TABLE + " ( " + BOOK_TITLE + " TEXT, " + PUBLISHER + " TEXT, " + BOOK_RATING + " REAL," +
            BOOK_ID + " INT," + GOOGLE_ID +" INT," + ISBN + " INT," + BOOK_COVER_URL + " TEXT," + DESCRIPTION + " TEXT," +
            CALLBACK_URL + " TEXT," + PREVIEW_URL + " TEXT," + BUY_URL + " TEXT," + PAGE_COUNT + " INT," + PUBLISHED_DATE + " TEXT,"
            + IS_BOOK_COLLECTION + " INT," + IS_BOOK_READ + " INT," + IS_BOOK_IN_WHISHLIST  + " INT," + IMAGE_DATA + " BLOB," + " PRIMARY KEY(" + GOOGLE_ID +") );");



        //CATEGORIES TABLE CREATE
        sqLiteDatabase.execSQL(" CREATE TABLE " + BOOK_CATEGORIES_TABLE + " ( " + GOOGLE_ID + " INT,"   + CATEGORY +
            " TEXT, PRIMARY KEY("+ GOOGLE_ID +"," + CATEGORY +"));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BOOKS_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BOOK_CATEGORIES_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + IMAGE_TABLE);
        onCreate(sqLiteDatabase);
    }

    /**
     *  This method adds a row to the database
     * @param book
     * @return
     */
    public boolean addRecord(Book book){

        boolean resultBooksTable = false;
        boolean resultImageTable = false;
        boolean resultCategoriesTable = false;

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues booksTableValues = new ContentValues();

        //BOOK TABLE VALUES   \\\\------///
        booksTableValues.put(BOOK_TITLE,book.getBookTitle());
        //booksTableValues.put(PUBLISHER,book.getAuthor());
        booksTableValues.put(BOOK_RATING,book.getPersonalRating());
        booksTableValues.put(BOOK_ID,book.getId());
        booksTableValues.put(GOOGLE_ID,book.getGoogleID());
        booksTableValues.put(ISBN,book.getISBN13());
        booksTableValues.put(BOOK_COVER_URL,book.getBookCoverURL());
        booksTableValues.put(DESCRIPTION,book.getDescription());
        booksTableValues.put(CALLBACK_URL,book.getCallbackURL());
        booksTableValues.put(PREVIEW_URL,book.getPreviewURL());
        booksTableValues.put(BUY_URL,book.getBuyURL());
        booksTableValues.put(PAGE_COUNT,book.getPageCount());
        booksTableValues.put(PUBLISHED_DATE,book.getPublishedDate());
        booksTableValues.put(IS_BOOK_COLLECTION,book.isBookInCollection());
        booksTableValues.put(IS_BOOK_READ,book.isBookRead());
        booksTableValues.put(IS_BOOK_IN_WHISHLIST,book.isBookInWishlist());
        booksTableValues.put(IMAGE_DATA,book.getByteArray());

        //Categories Table

        try{//Try inserting into BOOKS_TABLE

            sqLiteDatabase.insertOrThrow(BOOKS_TABLE,null, booksTableValues);
            resultBooksTable = true;
        }catch (SQLiteConstraintException alreadyInserted){
            //Row is already inserted
        }

        //CATEGORY Table values
        ContentValues CategoriesTableValues = new ContentValues();
        CategoriesTableValues.put(GOOGLE_ID,book.getGoogleID());


        int i = 0;
        if (book.getCategories().length >= 0) try {//Try inserting into CATEGORIES_TABLE

            for (i = 0; i < book.getCategories().length; i++) {


                CategoriesTableValues.put(CATEGORY, book.getCategories()[i]);
                sqLiteDatabase.insertOrThrow(BOOK_CATEGORIES_TABLE, null, CategoriesTableValues);
            }

            resultCategoriesTable = true;

        } catch (SQLiteConstraintException alreadyInserted) {
            //Row is already inserted
        }


        return resultBooksTable && resultImageTable && resultCategoriesTable;

    }

    /**
     * This method deletes a row from the database
     * @param book
     * @return
     */
    public boolean deleteRecord(Book book){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        int numOfRowsDeleted = 0;

        try{//Delete everything for this book from categories table
            numOfRowsDeleted = sqLiteDatabase.delete(BOOK_CATEGORIES_TABLE, GOOGLE_ID + "='" + book.getGoogleID() +"'", null);

        }catch (Exception noSuchColumn){
            //No such column
            numOfRowsDeleted = 0;
        }

        try{
            numOfRowsDeleted = sqLiteDatabase.delete(BOOKS_TABLE, GOOGLE_ID + "='" + book.getGoogleID() +"'", null);

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

    /** Joins the two tables for a certain google_ID (book) and returns a cursor with as many
     * lines as the categories. All lines are the same except from the category column.
     *
     * @param book
     * @return
     */
    public Cursor findRecord(Book book){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String q = "SELECT * FROM " + BOOKS_TABLE + ", " + BOOK_CATEGORIES_TABLE
            + " WHERE "
            + BOOKS_TABLE + "." + GOOGLE_ID + "='" + book.getGoogleID()
            + "'" + " AND "
            + BOOKS_TABLE + "." + GOOGLE_ID + " ='" + BOOK_CATEGORIES_TABLE + "." + GOOGLE_ID + "'";//INNER JOIN

        Cursor cursor = sqLiteDatabase.rawQuery(q,null);
        return cursor;
    }

    /**
     *   UPDATE PERSONAL RATING
     * @param book
     * @return
     */
    public boolean updateRecord(Book book){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        boolean result = false;
        ContentValues booksTableValues = new ContentValues();
        //BOOK TABLE VALUES   \\\\------///
        booksTableValues.put(BOOK_TITLE,book.getBookTitle());
        //booksTableValues.put(PUBLISHER,book.getAuthor());
        booksTableValues.put(BOOK_RATING,book.getPersonalRating());
        booksTableValues.put(BOOK_ID,book.getId());
        booksTableValues.put(GOOGLE_ID,book.getGoogleID());
        booksTableValues.put(ISBN,book.getISBN13());
        booksTableValues.put(BOOK_COVER_URL,book.getBookCoverURL());
        booksTableValues.put(DESCRIPTION,book.getDescription());
        booksTableValues.put(CALLBACK_URL,book.getCallbackURL());
        booksTableValues.put(PREVIEW_URL,book.getPreviewURL());
        booksTableValues.put(BUY_URL,book.getBuyURL());
        booksTableValues.put(PAGE_COUNT,book.getPageCount());
        booksTableValues.put(PUBLISHED_DATE,book.getPublishedDate());
        booksTableValues.put(IS_BOOK_COLLECTION,book.isBookInCollection());
        booksTableValues.put(IS_BOOK_READ,book.isBookRead());
        booksTableValues.put(IS_BOOK_IN_WHISHLIST,book.isBookInWishlist());
        booksTableValues.put(IMAGE_DATA,book.getByteArray());


        String q = "SELECT * FROM " + BOOKS_TABLE +" WHERE " + GOOGLE_ID + " ='" + book.getGoogleID() + "'";
        Cursor cursor = sqLiteDatabase.rawQuery(q,null);
        if(cursor.moveToFirst()){
            sqLiteDatabase.update(BOOKS_TABLE, booksTableValues, q, null);
            result = true;
        }
        cursor.close();
        //sqLiteDatabase.close();
        return result;
    }
}
