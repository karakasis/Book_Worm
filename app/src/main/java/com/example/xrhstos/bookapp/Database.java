package com.example.xrhstos.bookapp;

import java.util.ArrayList;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Dimitris Koufounakis on 28/3/2018.
 */

public class Database extends SQLiteOpenHelper {

    //DATABASE NAME,TABLES' NAMES
    private static final String DATABASE_NAME = "book_db.db";
    private static final int DATABASE_VERSION = 1;
    private static final String BOOKS_TABLE = "books";//<--TABLE
    private static final String BOOK_CATEGORIES_TABLE = "categories";//<--TABLE
    private static final String AUTHORS_TABLE = "authorsTable";//<--TABLE

    //BOOK_TABLE ATTRIBUTES
    private static final String BOOK_TITLE = "_bookTitle";
    private static final String PERSONAL_RATING = "_personalRating";
    private static final String AVERAGE_RATING = "_averageRating";
    private static final String IMAGE_DATA = "imageData";
    private static final String BOOK_ID = "bookId";
    private static final String GOOGLE_ID = "googleId";
    private static final String ISBN10 = "isbn10";
    private static final String ISBN13 = "isbn13";
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



    //AUTHORS TABLE ATTRIBUTES
    //private static final String GOOGLE_ID = "googleId";     //Already declared above
    private static final String AUTHOR = "author";

    //CATEGORIES TABLE ATTRIBUTES
    //private static final String GOOGLE_ID = "googleId";     //Already declared above
    private static final String CATEGORY = "category";

    private static Database mInstance;

    public static synchronized Database getInstance(Context c) {
        if (mInstance == null ) {
            mInstance = new Database(c);
        }
        return mInstance;
    }


    private Database(Context context) {
        super(context, DATABASE_NAME, null, 1);
        //TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //BOOKS TABLE CREATE
        sqLiteDatabase.execSQL(" CREATE TABLE " + BOOKS_TABLE + " ( " + BOOK_TITLE + " TEXT, " + PERSONAL_RATING + " REAL," + AVERAGE_RATING + " REAL," +
            BOOK_ID + " TEXT," + GOOGLE_ID +" TEXT," + ISBN13 + " TEXT," + ISBN10 + " TEXT," + BOOK_COVER_URL + " TEXT," + DESCRIPTION + " TEXT," +
            CALLBACK_URL + " TEXT," + PREVIEW_URL + " TEXT," + BUY_URL + " TEXT," + PAGE_COUNT + " INT," + PUBLISHED_DATE + " TEXT,"
            + IS_BOOK_COLLECTION + " INT," + IS_BOOK_READ + " INT," + IS_BOOK_IN_WHISHLIST  + " INT," + IMAGE_DATA + " BLOB," + " PRIMARY KEY(" + GOOGLE_ID +") );");
        //No boolean type in SQLite, INT used instead.

        //CATEGORIES TABLE CREATE
        sqLiteDatabase.execSQL(" CREATE TABLE " + AUTHORS_TABLE + " ( " + GOOGLE_ID + " TEXT,"   + CATEGORY +
            " TEXT, PRIMARY KEY("+ GOOGLE_ID +"," + CATEGORY +"));");

        //AUTHORS TABLE CREATE
        sqLiteDatabase.execSQL(" CREATE TABLE " + BOOK_CATEGORIES_TABLE + " ( " + GOOGLE_ID + " TEXT,"   + AUTHOR +
            " TEXT, PRIMARY KEY("+ GOOGLE_ID +"," + AUTHOR +"));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BOOKS_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + BOOK_CATEGORIES_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AUTHORS_TABLE);
        onCreate(sqLiteDatabase);
    }

    /**
     *  This method adds a Book to the database
     * @param book
     * @return
     */
    public boolean addRecord(Book book){

        boolean resultBooksTable = false;
        boolean resultAuthorsTable = false;
        boolean resultCategoriesTable = false;

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues booksTableValues = new ContentValues();

        //BOOK TABLE VALUES   \\\\------///
        booksTableValues.put(BOOK_TITLE,book.getBookTitle());
        booksTableValues.put(AVERAGE_RATING,book.getAverageRating());
        booksTableValues.put(PERSONAL_RATING,book.getPersonalRating());
        booksTableValues.put(BOOK_ID,book.getId());
        booksTableValues.put(GOOGLE_ID,book.getGoogleID());
        booksTableValues.put(ISBN13,book.getISBN13());
        booksTableValues.put(ISBN10,book.getISBN10());
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

        //Inserting into BOOKS_TABLE

        try{//Try inserting into BOOKS_TABLE

            sqLiteDatabase.insertOrThrow(BOOKS_TABLE,null, booksTableValues);
            resultBooksTable = true;
        }catch (SQLiteConstraintException alreadyInserted){
            //Row is already inserted
        }

        //CATEGORY Table values
        ContentValues CategoriesTableValues = new ContentValues();
        CategoriesTableValues.put(GOOGLE_ID,book.getGoogleID());

        //AUTHORS_TABLE values
        ContentValues authorsTableValues = new ContentValues();
        authorsTableValues.put(GOOGLE_ID,book.getGoogleID());

        //Insert categories into BOOK_CATEGORIES_TABLE
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

        //Insert authors into AUTHORS_TABLE
        i = 0;
        if (book.getAuthor().length >= 0) try {//Try inserting into CATEGORIES_TABLE

            for (i = 0; i < book.getAuthor().length; i++) {


                CategoriesTableValues.put(AUTHOR, book.getAuthor()[i]);
                sqLiteDatabase.insertOrThrow(AUTHORS_TABLE, null, authorsTableValues);
            }

            resultAuthorsTable = true;

        } catch (SQLiteConstraintException alreadyInserted) {
            //Row is already inserted
        }


        return resultBooksTable && resultAuthorsTable && resultCategoriesTable;

    }

    /**
     * Packs all books from database to a bookList and returns the list
     * @return
     */
    public ArrayList<Book> getSavedBooksList(){

        ArrayList<Book> savedBooksList = new ArrayList<Book>();

        Book book;// = new Book(0,null, null,null,null);//First declaration so it's not undeclared.



        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        //Query
        String q = "SELECT * FROM " + BOOKS_TABLE;//The entire table
        Cursor cursor = sqLiteDatabase.rawQuery(q,null);

        /////->One iteration for each book

        try{
            //Index of each collumn
            int titleIndex = cursor.getColumnIndex(BOOK_TITLE);
            int averageRatingIndex = cursor.getColumnIndex(AVERAGE_RATING);
            int personalRatingIndex = cursor.getColumnIndex(PERSONAL_RATING);
            int IdIndex = cursor.getColumnIndex(BOOK_ID);
            int googleIdIndex = cursor.getColumnIndex(GOOGLE_ID);
            int isbn13Index = cursor.getColumnIndex(ISBN13);
            int isbn10Index = cursor.getColumnIndex(ISBN10);
            int urlIndex = cursor.getColumnIndex(BOOK_COVER_URL);
            int descriptionIndex = cursor.getColumnIndex(DESCRIPTION);
            int callbackUrlIndex = cursor.getColumnIndex(CALLBACK_URL);
            int previewUrlIndex = cursor.getColumnIndex(PREVIEW_URL);
            int buyUrlIndex = cursor.getColumnIndex(BUY_URL);
            int pageCountIndex = cursor.getColumnIndex(PAGE_COUNT);
            int dateIndex = cursor.getColumnIndex(PUBLISHED_DATE);
            int isBookCollectionIndex = cursor.getColumnIndex(IS_BOOK_COLLECTION);
            int isBookReadIndex = cursor.getColumnIndex(IS_BOOK_READ);
            int isBookInWishlistIndex = cursor.getColumnIndex(IS_BOOK_IN_WHISHLIST);
            int bookCoverIndex = cursor.getColumnIndex(IMAGE_DATA);



            cursor.moveToPosition(-1);

            while(cursor.moveToNext()){


                book = new Book();
                book.setId(cursor.getString(IdIndex));
                book.setAverageRating(averageRatingIndex);
                book.setPersonalRating(personalRatingIndex);
                book.setBookTitle(cursor.getString(titleIndex));
                book.setBookCoverURL(cursor.getString(urlIndex));
                book.setDescription(cursor.getString(descriptionIndex));
                book.setGoogleID(cursor.getString(googleIdIndex));
                book.setCallbackURL(cursor.getString(callbackUrlIndex));
                book.setPreviewURL(cursor.getString(previewUrlIndex));
                book.setBuyURL(cursor.getString(buyUrlIndex));
                book.setPageCount(cursor.getInt(pageCountIndex));
                book.setPublishedDate(cursor.getString(dateIndex));
                book.setISBN13(cursor.getString(isbn13Index));
                book.setISBN10(cursor.getString(isbn10Index));
                book.setBookInCollection(1 == cursor.getInt(isBookCollectionIndex));//1==int var (converts int to boolean)
                book.setBookRead(1 == cursor.getInt(isBookReadIndex));//SQLite doesn't support boolean.
                book.setBookInWishlist(1 == cursor.getInt(isBookInWishlistIndex));
                book.setBitmapFromByteArray(cursor.getBlob(bookCoverIndex));

                //Query of categories of the current book
                String q1 = "SELECT * FROM " + BOOK_CATEGORIES_TABLE
                    +" WHERE " + BOOK_CATEGORIES_TABLE + "." + GOOGLE_ID + "='" + book.getGoogleID() + "'";//The entire table
                Cursor cursorCategories = sqLiteDatabase.rawQuery(q1,null);

                //Query of authors of the current book
                String q2 = "SELECT * FROM " + AUTHORS_TABLE
                    +" WHERE " + AUTHORS_TABLE + "." + GOOGLE_ID + "='" + book.getGoogleID() + "'";//The entire table
                Cursor cursorAuthors = sqLiteDatabase.rawQuery(q2,null);

                int categoryIndex = cursorCategories.getColumnIndex(CATEGORY);

                int authorIndex = cursorAuthors.getColumnIndex(AUTHOR);

                String[] categories = new String[cursorCategories.getCount()];

                String[] authors = new String[cursorAuthors.getCount()];

                cursorCategories.moveToPosition(-1);
                cursorAuthors.moveToPosition(-1);

                //Create String[] of categories
                while(cursorCategories.moveToNext()) {
                    categories[cursorCategories.getPosition()] = cursorCategories.getString(categoryIndex);

                }
                book.setCategories(categories);

                //Create String[] of authors
                while(cursorAuthors.moveToNext()) {
                    authors[cursorAuthors.getPosition()] = cursorAuthors.getString(authorIndex);

                }
                book.setAuthor(authors);

                //Add book to list
                savedBooksList.add(book);
            }
        }catch (Exception outOfBounds){

        }
        finally {
            cursor.close();
        }




        return savedBooksList;
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
            numOfRowsDeleted += sqLiteDatabase.delete(BOOK_CATEGORIES_TABLE, GOOGLE_ID + "='" + book.getGoogleID() +"'", null);

        }catch (Exception noSuchColumn){
            //No such column
            //numOfRowsDeleted = 0;
        }

        try{
            numOfRowsDeleted += sqLiteDatabase.delete(BOOKS_TABLE, GOOGLE_ID + "='" + book.getGoogleID() +"'", null);

        }catch (Exception noSuchColumn){
            //No such column
            //numOfRowsDeleted = 0;
        }

        try{//Delete everything for this book from authors table
            numOfRowsDeleted += sqLiteDatabase.delete(AUTHORS_TABLE, GOOGLE_ID + "='" + book.getGoogleID() +"'", null);

        }catch (Exception noSuchColumn){
            //No such column
            //numOfRowsDeleted = 0;
        }

        if(numOfRowsDeleted > 0){//If 1 row at least deleted return true
            return true;
        }else{
            return false;
        }


    }

    /** Returns true if book exists in the database.
     *  Returns false if it doesn't.
     *
     * @param book
     * @return
     */
    public boolean isBookSaved(Book book){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String q = "SELECT * FROM " + BOOKS_TABLE
            + " WHERE "
            + BOOKS_TABLE + "." + GOOGLE_ID + "='" + book.getGoogleID()
            + "'";

        Cursor cursor = sqLiteDatabase.rawQuery(q,null);

        return cursor.moveToFirst();

    }

    /**
     *   UPDATE PERSONAL RATING
     *   The only variable for update is PERSONAL_RATING
     *   The update takes place only on BOOKS_TABLE.
     *   Other tables don't have to be updated.
     * @param book
     * @return
     */
    public boolean updateRecord(Book book){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        boolean result = false;
        ContentValues booksTableValues = new ContentValues();
        //BOOK TABLE VALUES   \\\\------///
        booksTableValues.put(BOOK_TITLE,book.getBookTitle());
        booksTableValues.put(AVERAGE_RATING,book.getAverageRating());
        booksTableValues.put(PERSONAL_RATING,book.getPersonalRating());
        booksTableValues.put(BOOK_ID,book.getId());
        booksTableValues.put(GOOGLE_ID,book.getGoogleID());
        booksTableValues.put(ISBN13,book.getISBN13());
        booksTableValues.put(ISBN10,book.getISBN10());
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
