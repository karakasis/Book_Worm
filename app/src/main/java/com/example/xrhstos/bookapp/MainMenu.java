package com.example.xrhstos.bookapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.util.TimingLogger;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Xrhstos on 4/11/2018.
 */

public class MainMenu extends AppCompatActivity{

  private static final String BOOKSHELF_KEY = "BOOKSHELF";
  private static final String SEARCH_ONLINE_QUERY_KEY = "QUERY_ONLINE";
  private static final String SEARCH_ONLINE_PAGE_KEY = "PAGE_ONLINE";
  private static final String FIRST_VISIBLE_KEY = "LAST_VISIBLE";
  private static final String GLM_KEY = "GLM";

  public static Bookshelf bookshelf;

  public static boolean loadingData = false;
  public static String query;
  public static String langQuery;
  private static int currentPage = 1;
  public TextView notifier;
  public TimingLogger tLogger;

  private View footer;

  private PreviewController previewController;
  private int firstVisibleItem;
  private Parcelable glmState;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_menu);


    footer = (View) findViewById(R.id.logo_container);

    Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
    setSupportActionBar(myToolbar);

    notifier = (TextView) findViewById(R.id.resultNotify);

    if(previewController == null){
      previewController = new PreviewController(
          (RecyclerView) findViewById(R.id.grid_view),this);
    }

    if (savedInstanceState != null) {
      query = savedInstanceState.getString(SEARCH_ONLINE_QUERY_KEY);
      currentPage = savedInstanceState.getInt(SEARCH_ONLINE_PAGE_KEY);
      //bookshelf = (Bookshelf) savedInstanceState.getSerializable(BOOKSHELF_KEY);
      firstVisibleItem = savedInstanceState.getInt(FIRST_VISIBLE_KEY);
    }else{
      bookshelf = new Bookshelf();

    }

  }

  @Override
  protected void onSaveInstanceState(Bundle bundle) {
    super.onSaveInstanceState(bundle);
    //bundle.putSerializable(BOOKSHELF_KEY, bookshelf);
    bundle.putString(SEARCH_ONLINE_QUERY_KEY,query);
    bundle.putInt(SEARCH_ONLINE_PAGE_KEY,currentPage);
    firstVisibleItem = previewController.getFirstVisibleItem();
    bundle.putInt(FIRST_VISIBLE_KEY, firstVisibleItem);

    // Save list state
    glmState = previewController.getLayoutManager().onSaveInstanceState();
    bundle.putParcelable(GLM_KEY, glmState);
  }

  @Override
  protected void onRestoreInstanceState(Bundle bundle) {
    super.onRestoreInstanceState(bundle);

    // Retrieve list state and list/item positions
    if(bundle != null)
      glmState = bundle.getParcelable(GLM_KEY);
  }

  @Override
  protected void onResume() {
    super.onResume();

    if (glmState != null) {
      rotateUpdate();
      previewController.getLayoutManager().onRestoreInstanceState(glmState);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu, menu);

    final MenuItem searchMenuItem = menu.findItem(R.id.action_search);

    final SearchView searchView = (SearchView) searchMenuItem.getActionView();
    searchView.setQueryHint("Dan Brown :el");
    searchView.setOnQueryTextListener(new OnQueryTextListener() {

      @Override
      public boolean onQueryTextSubmit(String query) {

        currentPage = 1;
        langQuery = "";
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

  public void searchBooks(String query) {
    this.query = query;

    System.out.println("Current page: " + getPage());
    footer.setVisibility(View.VISIBLE);
    //requestGoodReads();
    requestGoogle();
  }

  private void requestGoodReads(){

    StringRequest stringRequest;
    stringRequest = VolleyNetworking.getInstance(this).goodReadsRequest(query);
    if(stringRequest==null){
      requestGoogle();
    }else{
      VolleyNetworking.getInstance(this).addToRequestQueue(stringRequest);
      ImageView logo = (ImageView) findViewById(R.id.logo);
      logo.setImageResource(R.drawable.goodreads_logo);
    }
  }

  private void requestGoogle(){

    Pattern pattern = Pattern.compile(":\\w(\\D*)$");
    Matcher matcher = pattern.matcher(query);
    if (matcher.find())
    {
      System.out.println(matcher.group(0));
      langQuery = matcher.group(0).replace(":","");
      query = query.replace(matcher.group(0),"");
    }

    JsonObjectRequest jsonObjectRequest;
    jsonObjectRequest = VolleyNetworking.getInstance(this).googleRequest(query);
    VolleyNetworking.getInstance(this).addToRequestQueue(jsonObjectRequest);


    ImageView logo = (ImageView) findViewById(R.id.logo);
    logo.setImageResource(R.drawable.google_logo);

  }

  public void bookClick(int position){

    // Parceable implementation
    Intent intent = new Intent(this, BookInfoActivity.class);
    intent.putExtra("bookObject", bookshelf.getSingleBook(position));
    startActivity(intent);

  }

  public void update(ArrayList<String[]> bookData){
    bookshelf.addBooks(bookData,this);

    if(MainMenu.loadingData){
      informAdapter(bookshelf.getNewBooksFetchedAmount(),bookshelf.fetchExtraBooksOnly());
    }
    else{
      updateAdapter(bookshelf.getBooks());
    }
    if(tLogger!=null)
    tLogger.addSplit("printing images");
  }

  private void rotateUpdate(){
    //previewController = new PreviewController(
        //(RecyclerView) findViewById(R.id.grid_view),this);
    updateAdapter(bookshelf.getBooks());
    //previewController.setData(bookshelf.getBooks());
    previewController.scrollToVisibleItem(firstVisibleItem);
  }

  public void requestMoreResults(){
    currentPage++;
    searchBooks(query);
  }

  public void updateAdapter(ArrayList<Book> data){
    previewController = new PreviewController(
        (RecyclerView) findViewById(R.id.grid_view),this);
    previewController.setData(data);
  }

  public void informAdapter(int value, ArrayList<Book> newData){
    previewController.acceptResponseFromMainThread(value,newData);
  }

  public static int getPage(){
    return currentPage;
  }

  public static String getQuery(){
    return MainMenu.query;
  }
}

