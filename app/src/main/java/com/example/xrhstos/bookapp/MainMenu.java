package com.example.xrhstos.bookapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TimingLogger;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import java.util.ArrayList;

/**
 * Created by Xrhstos on 4/11/2018.
 */

public class MainMenu extends AppCompatActivity{

  //public static ArrayList<String[]> books ;
  public static Bookshelf bs;
  private RequestQueue queue;

  public TextView notifier;
  public TimingLogger tLogger;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_menu);

    notifier = (TextView) findViewById(R.id.resultNotify);

    final SearchView sv = (SearchView) findViewById(R.id.searchview);

    sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {

        tLogger = new TimingLogger("ExecutionTime","Search books clicked");


        // Hide the keyboard when the button is pushed.
        InputMethodManager inputManager = (InputMethodManager)
            getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
            InputMethodManager.HIDE_NOT_ALWAYS);


        searchBooks(query);
        sv.setIconified(true);
        return true;
      }

      @Override
      public boolean onQueryTextChange(String queryPiece) {
        tLogger = new TimingLogger("ExecutionTime","Search books typed");
        searchBooks(queryPiece);
        return false;
      }

    });

    if(bs==null){
      bs = new Bookshelf();
    }else{
      update(bs.getStringBooks());
    }
  }

  public void searchBooks(final String query) {

    requestGoodReads(query);

  }

  private void requestGoodReads(final String queryString){

    StringRequest stringRequest;
    stringRequest = VolleyNetworking.getInstance(this).goodReadsRequest(queryString);
    if(stringRequest==null){
      requestGoogle(queryString);
    }else{
      VolleyNetworking.getInstance(this).addToRequestQueue(stringRequest);
    }
  }

  private void requestGoogle(final String queryString){


    JsonObjectRequest jsonObjectRequest;
    jsonObjectRequest = VolleyNetworking.getInstance(this).googleRequest(queryString);
    VolleyNetworking.getInstance(this).addToRequestQueue(jsonObjectRequest);

  }

  public void bookClick(int position){
    Intent intent = new Intent(this, BookInfoActivity.class);
    //intent.putExtra("title", books.get(position)[1]);
    intent.putExtra("bookObject", bs.getSingleBook(position));
    //intent.putExtra("author", books.get(position)[2]);
    //intent.putExtra("url", books.get(position)[3]);
    startActivity(intent);
  }

  public void update(ArrayList<String[]> bookData){
    bs.setBooks(bookData);

    PreviewController pc = new PreviewController(
        (GridView) findViewById(R.id.grid_view),
        this, bs.getBooks());

    tLogger.addSplit("printing images");
  }



}

