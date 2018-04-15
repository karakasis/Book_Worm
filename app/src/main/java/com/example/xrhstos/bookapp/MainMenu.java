package com.example.xrhstos.bookapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.util.TimingLogger;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import java.util.ArrayList;

/**
 * Created by Xrhstos on 4/11/2018.
 */

public class MainMenu extends AppCompatActivity{

  public static Bookshelf bs;

  public static boolean loadingData = false;
  public static String query;
  private static int currentPage = 1;
  public TextView notifier;
  public TimingLogger tLogger;

  private View footer;

  private static PreviewController previewController;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_menu);

    Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
    setSupportActionBar(myToolbar);

    notifier = (TextView) findViewById(R.id.resultNotify);

    if(MainMenu.previewController == null){
      MainMenu.previewController = new PreviewController(
          (RecyclerView) findViewById(R.id.grid_view),this);
    }
    if(bs==null){
      bs = new Bookshelf(this);
    }else{
      rotateUpdate();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu, menu);

    footer =(View) findViewById(R.id.logo_container);

    final MenuItem searchMenuItem = menu.findItem(R.id.action_search);

    final SearchView searchView = (SearchView) searchMenuItem.getActionView();
    searchView.setOnQueryTextListener(new OnQueryTextListener() {

      @Override
      public boolean onQueryTextSubmit(String query) {

        currentPage = 1;

        tLogger = new TimingLogger("ExecutionTime","Search books clicked");
        if(query.equals("")){
          footer.setVisibility(View.GONE);
        }
        if (searchMenuItem != null) {
          searchMenuItem.collapseActionView();
        }
        searchView.setIconified(true);
        searchView.clearFocus();

        searchBooks(query);

        return false;
      }

      @Override
      public boolean onQueryTextChange(String queryPiece) {

        return false;
      }
    });

    return super.onCreateOptionsMenu(menu);
  }

  public void searchBooks(final String query) {
    MainMenu.query = query;
    System.out.println("Current page: " + getPage());
    footer.setVisibility(View.VISIBLE);
    requestGoodReads(query);
    //requestGoogle(query);
  }

  private void requestGoodReads(final String queryString){

    StringRequest stringRequest;
    stringRequest = VolleyNetworking.getInstance(this).goodReadsRequest(queryString);
    if(stringRequest==null){
      requestGoogle(queryString);
    }else{
      VolleyNetworking.getInstance(this).addToRequestQueue(stringRequest);
      ImageView logo = (ImageView) findViewById(R.id.logo);
      logo.setImageResource(R.drawable.goodreads_logo);
    }
  }

  private void requestGoogle(final String queryString){


    JsonObjectRequest jsonObjectRequest;
    jsonObjectRequest = VolleyNetworking.getInstance(this).googleRequest(queryString);
    VolleyNetworking.getInstance(this).addToRequestQueue(jsonObjectRequest);


    ImageView logo = (ImageView) findViewById(R.id.logo);
    logo.setImageResource(R.drawable.google_logo);

  }

  public void bookClick(int position){

    // Parceable implementation
    Intent intent = new Intent(this, BookInfoActivity.class);
    intent.putExtra("bookObject", bs.getSingleBook(position));
    startActivity(intent);

  }

  public void update(ArrayList<String[]> bookData){
    bs.addBooks(bookData);

    if(MainMenu.loadingData){
      informAdapter(bs.getNewBooksFetchedAmount());
    }
    else{
      updateAdapter(bs.getBooks());
    }
    if(tLogger!=null)
    tLogger.addSplit("printing images");
  }

  private void rotateUpdate(){
    MainMenu.previewController.setData(bs.getBooks());
  }

  public void requestMoreResults(){
    currentPage++;
    searchBooks(query);
  }

  public void updateAdapter(ArrayList<Book> data){
    MainMenu.previewController.setData(data);
  }

  public void informAdapter(int value){
    MainMenu.previewController.acceptResponseFromMainThread(value);
  }

  public static int getPage(){
    return currentPage;
  }


}

