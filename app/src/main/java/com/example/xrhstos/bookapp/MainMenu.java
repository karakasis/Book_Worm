package com.example.xrhstos.bookapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;
import com.example.xrhstos.bookapp.google_books.FetchBookGoogle;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Xrhstos on 4/11/2018.
 */

public class MainMenu extends AppCompatActivity{

  //public static ArrayList<String[]> books ;
  public static ArrayList<Book> books;
  public static Bookshelf bs;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_menu);
    final SearchView sv = (SearchView) findViewById(R.id.searchview);

    sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        searchBooks(query);
        sv.setIconified(true);
        return true;
      }

      @Override
      public boolean onQueryTextChange(String s) {
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
    // Get the search string from the input field.
    final String queryString = query;

    // Hide the keyboard when the button is pushed.
    InputMethodManager inputManager = (InputMethodManager)
        getSystemService(Context.INPUT_METHOD_SERVICE);
    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
        InputMethodManager.HIDE_NOT_ALWAYS);

    // Check the status of the network connection.
    ConnectivityManager connMgr = (ConnectivityManager)
        getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

    // If the network is active and the search field is not empty, start a FetchBook AsyncTask.
    if (networkInfo != null && networkInfo.isConnected() && queryString.length()!=0) {
      //TextView notifier = (TextView) findViewById(R.id.resultNotify);
      //notifier.setText("Results: ");
      new CheckConnection(new CheckConnection.AsynResponse() {
        @Override
        public void processFinish(Boolean isServerReachable) {
          if(isServerReachable){
            searchBooksOnline(queryString,true);
          }else{
            TextView notifier = (TextView) findViewById(R.id.resultNotify);
            notifier.setText("Server out of reach.");
            requestGoogle(queryString);
          }
        }
      },"https://www.goodreads.com/").execute();

    }
    // Otherwise update the TextView to tell the user there is no connection or no search term.
    else {
      if (queryString.length() == 0) {
        TextView notifier = (TextView) findViewById(R.id.resultNotify);
        notifier.setText(R.string.no_search_term);
      } else {
        TextView notifier = (TextView) findViewById(R.id.resultNotify);
        notifier.setText(R.string.no_network);
      }
    }
  }

  private void requestGoogle(final String queryString){
    new CheckConnection(new CheckConnection.AsynResponse() {
      @Override
      public void processFinish(Boolean isServerReachable) {
        if(isServerReachable){
          searchBooksOnline(queryString,false);
        }else{
          TextView notifier = (TextView) findViewById(R.id.resultNotify);
          notifier.setText("Server out of reach.");
        }
      }
    },"https://books.google.gr/").execute();
  }

  private void searchBooksOnline(String query,boolean isGoodReads){
    if(isGoodReads){
      System.out.println("Source : Goodreads");
      new FetchBook(this).execute(query);
    }else{
      System.out.println("Source : Google");
      new FetchBookGoogle(this).execute(query);
    }
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
    //books = new ArrayList<>(bookData);
    bs.setBooks(bookData);

    ArrayList<String> urls = new ArrayList<>();
    for(String[] arr : bookData){
      urls.add(arr[3]);
    }


    PreviewController pc = new PreviewController((GridView) findViewById(R.id.grid_view), this, bs.getBooks());
  }



}

