package com.example.xrhstos.bookapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import android.database.Cursor;


/**
 * Main Activity
 */


public class DatabaseActivity extends AppCompatActivity {
    EditText bookTitle, publisher;
    RatingBar ratingBar;

    float ratingValue;
    private static final String TAG = "DatabaseHelper";

    DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database);
        bookTitle = (EditText) findViewById(R.id.book);//bookTitle editText
        publisher = (EditText) findViewById(R.id.bookPublisher);//publisher editText

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        //RatingBar listener
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser){
                ratingValue = rating;

            }
        });
        //rating = 5;

        myDb = new DatabaseHelper(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    /**
     *  This method adds a row to the database
     *  If row already exists catches exception and shows "ALREADY ADDED"
     * @param v
     */
    public void addEntry(View v){
        bookTitle = (EditText) findViewById(R.id.book);
        publisher = (EditText) findViewById(R.id.bookPublisher);



        myDb = new DatabaseHelper(this);
        String title = bookTitle.getText().toString();
        String bookPublisher = publisher.getText().toString();
        //ratingBar.getNumStars();
        boolean success = myDb.addRecord(title, bookPublisher, ratingValue);

        if(success){
            Toast.makeText(this, "ADDED", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "ALREADY INSERTED", Toast.LENGTH_SHORT).show();
        }
        bookTitle.setText("");
        publisher.setText("");
        //ratingBar.setRating(0);

    }

    /**
     * This method deletes the row given from EditText
     * If no rows deleted catches exception and shows "No Match Found"
     * @param v
     */
    public void deleteEntry(View v){
        bookTitle = (EditText) findViewById(R.id.book);
        publisher = (EditText) findViewById(R.id.bookPublisher);



        myDb = new DatabaseHelper(this);
        String title = bookTitle.getText().toString();
        String bookPublisher = publisher.getText().toString();
        boolean result = myDb.deleteRecord(title,bookPublisher);

        if(result){//If record deleted
            bookTitle.setText("");
            publisher.setText("");
            Toast.makeText(this, "Book Deleted", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "No match found", Toast.LENGTH_SHORT).show();
        }


    }

    public void findEntry(View v){
        bookTitle = (EditText) findViewById(R.id.book);
        publisher = (EditText) findViewById(R.id.bookPublisher);

        String title = bookTitle.getText().toString();
        String bookPublisher = publisher.getText().toString();

        Cursor cursor;
        cursor = myDb.findRecord(title,bookPublisher);

        if(cursor.moveToFirst()){
            cursor.moveToFirst();
            bookTitle.setText(cursor.getString(0));
            publisher.setText(cursor.getString(1));
            ratingBar.setRating(Float.parseFloat(cursor.getString(2)));

            Toast.makeText(this, "Book found", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "No match found", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    public void updateEntry(View v){
        bookTitle = (EditText) findViewById(R.id.book);
        publisher = (EditText) findViewById(R.id.bookPublisher);

        String title = bookTitle.getText().toString();
        String bookPublisher = publisher.getText().toString();

        myDb.updateRecord(title, bookPublisher, ratingValue);
    }






}
