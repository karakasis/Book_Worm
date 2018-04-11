package com.example.xrhstos.bookapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

/**
 * Created by Xrhstos on 4/11/2018.
 */

public class MainMenu extends AppCompatActivity implements Fragment1.ActivityCommunicator{

  public static ArrayList<String> cover_url ;
  public static ArrayList<String[]> books ;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_menu);
    SearchView sv = (SearchView) findViewById(R.id.searchview);

    sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        searchBooks(query);
        return true;
      }

      @Override
      public boolean onQueryTextChange(String s) {
        return false;
      }

    });

        /*
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("urls",cover_url);
        // set Fragmentclass Arguments
        Fragment1 fragobj = new Fragment1();
        fragobj.setArguments(bundle);

*/


  }

  public void searchBooks(String query) {
    // Get the search string from the input field.
    String queryString = query;

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
      new FetchBook(this).execute(queryString);

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

  public void bookClick(int position){
    Intent intent = new Intent(this, Fragment2.class);
    intent.putExtra("title", books.get(position)[1]);
    intent.putExtra("author", books.get(position)[2]);
    intent.putExtra("url", books.get(position)[3]);
    startActivity(intent);
  }

  public void update(ArrayList<String[]> bookData){
    books = new ArrayList<>(bookData);
    ArrayList<String> urls = new ArrayList<>();
    for(String[] arr : bookData){
      urls.add(arr[3]);
    }

    GridView gridView = (GridView) findViewById(R.id.grid_view);
    ImageAdapter ia = new ImageAdapter(this , this,urls);
    gridView.setAdapter(ia);

    /*
    Intent intent = new Intent(this, MainMenu.class);
    intent.putStringArrayListExtra("cover_urls", urls);
    startActivity(intent);
    */
  }

    /*
    public void onPhilosopherClick(String p) {
        View quoteFragment = findViewById(R.id.fragment2);
        boolean mDualPane = quoteFragment != null && quoteFragment.getVisibility() == View.VISIBLE;
        if (mDualPane) {
            Fragment2 f = (Fragment2) getSupportFragmentManager().findFragmentById(R.id.fragment2);
            f.showQuote(p);
        } else {
            Intent intent = new Intent(this, QuoteActivity.class);
            intent.putExtra("philospher", p);
            startActivity(intent);
        }

    }
    */

  public void passDataToActivity(String s){

  }
}
