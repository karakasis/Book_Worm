package com.example.xrhstos.bookapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.xrhstos.bookapp.scanner.IntentIntegrator;
import com.example.xrhstos.bookapp.scanner.IntentResult;
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
  private static final String SEARCH_ONLINE_LANG_QUERY_KEY = "LANG_QUERY_ONLINE";
  private static final String FIRST_VISIBLE_KEY = "LAST_VISIBLE";
  private static final String LOADING_KEY = "LOADING";
  private static final String GLM_KEY = "GLM";
  private static final String GOOGLE_ON_KEY = "GOOGLE";
  private static final String GOODREADS_ON_KEY = "GOODREADS";

  public static Bookshelf bookshelf;

  private boolean googleON;
  private boolean goodreadsON;
  public static boolean loadingData;
  public static String query;
  public static String langQuery;
  private static int currentPage;
  public TextView notifier;

  private View footer;
  private View loading;
  private View grid;

  private PreviewController previewController;
  private int firstVisibleItem;
  private Parcelable glmState;
  public int bitmapRequestCount;
  public int bitmapMaxCount;

  private DatabaseHelper myDb;
  private Ping ping;

  private int scannerCounter = 0;
  private ArrayList<Book> booksFromScanner;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_menu);

    ping = new Ping();
    myDb = new DatabaseHelper(this);

    MyApp app = (MyApp) getApplication();
    app.mainMenu = this;

    footer = (View) findViewById(R.id.logo_container);

    Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
    setSupportActionBar(myToolbar);

    notifier = (TextView) findViewById(R.id.resultNotify);
    ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
    stub.setLayoutResource(R.layout.loading);
    loading = stub.inflate();
    loading.setVisibility(View.GONE);

    ViewStub stub1 = (ViewStub) findViewById(R.id.layout_stub_grid);
    grid = stub1.inflate();
    grid.setVisibility(View.INVISIBLE);
    if(previewController == null){
      previewController = new PreviewController(
          (RecyclerView) grid.findViewById(R.id.grid_view),this);
    }



    if (savedInstanceState != null) {
      query = savedInstanceState.getString(SEARCH_ONLINE_QUERY_KEY);
      langQuery = savedInstanceState.getString(SEARCH_ONLINE_LANG_QUERY_KEY);
      currentPage = savedInstanceState.getInt(SEARCH_ONLINE_PAGE_KEY);
      //bookshelf = (Bookshelf) savedInstanceState.getSerializable(BOOKSHELF_KEY);
      firstVisibleItem = savedInstanceState.getInt(FIRST_VISIBLE_KEY);
      googleON = savedInstanceState.getBoolean(GOOGLE_ON_KEY);
      goodreadsON = savedInstanceState.getBoolean(GOODREADS_ON_KEY);
      loadingData = savedInstanceState.getBoolean(LOADING_KEY);
      if(googleON){
        ImageView logo = (ImageView) findViewById(R.id.logo);
        logo.setImageResource(R.drawable.google_logo);
      }else if(goodreadsON){
        ImageView logo = (ImageView) findViewById(R.id.logo);
        logo.setImageResource(R.drawable.goodreads_logo);
      }
    }else{
      //bookshelf = new Bookshelf();
      currentPage = 1;
      loadingData = false;
      googleON = false;
      query = "";
      langQuery = "";
    }

  }

  @Override
  protected void onSaveInstanceState(Bundle bundle) {
    super.onSaveInstanceState(bundle);
    //bundle.putSerializable(BOOKSHELF_KEY, bookshelf);
    bundle.putString(SEARCH_ONLINE_QUERY_KEY,query);
    bundle.putString(SEARCH_ONLINE_LANG_QUERY_KEY,langQuery);
    bundle.putInt(SEARCH_ONLINE_PAGE_KEY,currentPage);
    firstVisibleItem = previewController.getFirstVisibleItem();
    bundle.putInt(FIRST_VISIBLE_KEY, firstVisibleItem);

    bundle.putBoolean(LOADING_KEY,loadingData);
    bundle.putBoolean(GOODREADS_ON_KEY,goodreadsON);
    bundle.putBoolean(GOOGLE_ON_KEY,googleON);

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
        if(query.equals("")){
          footer.setVisibility(View.GONE);
        }
        if (searchMenuItem != null) {
          searchMenuItem.collapseActionView();
        }
        searchView.setIconified(true);
        searchView.clearFocus();

        grid.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);
        searchBooks(query);

        return false;
      }

      @Override
      public boolean onQueryTextChange(String queryPiece) {

        //ping.executeCommand();
        return false;
      }
    });

    return super.onCreateOptionsMenu(menu);
  }

  public void searchBooks(String query) {
    MainMenu.query = query;

    Pattern pattern = Pattern.compile(":\\w(\\D*)$");
    Matcher matcher = pattern.matcher(query);
    if (matcher.find())
    {
      System.out.println(matcher.group(0));
      langQuery = matcher.group(0).replace(":","");
      MainMenu.query = query.replace(matcher.group(0),"");
    }


    System.out.println("Current page: " + getPage());
    footer.setVisibility(View.VISIBLE);
    if(langQuery.isEmpty() || langQuery.equals("en")){
      requestGoodReads();
    }else{
      requestGoogle();
    }

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
      goodreadsON = true;
      googleON = false;
    }
  }

  private void requestGoogle(){

    JsonObjectRequest jsonObjectRequest;
    jsonObjectRequest = VolleyNetworking.getInstance(this).googleRequest(query);
    VolleyNetworking.getInstance(this).addToRequestQueue(jsonObjectRequest);


    ImageView logo = (ImageView) findViewById(R.id.logo);
    logo.setImageResource(R.drawable.google_logo);
    goodreadsON = false;
    googleON = true;

  }

  public void bookClick(int position){
    VolleyNetworking.getInstance(this).getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
      @Override
      public boolean apply(Request<?> request) {
        return true;
      }
    });
    // Parceable implementation
    Intent intent = new Intent(this, BookInfoActivity.class);
    //this will pass the book object itself so any changes will be made to the Book
    //class as well
    intent.putExtra("bookObjectPos", position);
    startActivity(intent);

  }

  public void addBookManual(MenuItem item){

    IntentIntegrator integrator = new IntentIntegrator(this);
    integrator.initiateScan();

  }

  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
    if (scanResult != null) {
      // handle scan result
      System.out.println(scanResult.toString());
      StringRequest grISBN = VolleyNetworking.getInstance(this).goodReadsRequestByISBN(scanResult.getContents());
      VolleyNetworking.getInstance(this).addToRequestQueue(grISBN);
      JsonObjectRequest gISBN = VolleyNetworking.getInstance(this).googleRequestByISBN(scanResult.getContents());
      VolleyNetworking.getInstance(this).addToRequestQueue(gISBN);
    }
    // else continue with any other code you need in the method
  }

  public void updateByISBN(ArrayList<Book> bookData){
    if(scannerCounter == 0){
      booksFromScanner = new ArrayList<>();
    }
    if(bookData!=null){
      booksFromScanner.addAll(bookData);
    }
    scannerCounter++;

    if(scannerCounter==2){
      scannerCounter = 0;
      Bookshelf.getInstance().addBooksByISBN(booksFromScanner);
      updateAdapter(Bookshelf.getInstance().getBooks());
    }
  }

  public void update(ArrayList<Book> bookData){
    Bookshelf.getInstance().addBooks(bookData,this);

    if(MainMenu.loadingData){
      informAdapter(Bookshelf.getInstance().getNewBooksFetchedAmount(),Bookshelf.getInstance().fetchExtraBooksOnly());
    }
    else{
      updateAdapter(Bookshelf.getInstance().getBooks());
    }

  }

  public void startUI(){
    System.out.println(bitmapRequestCount);
    if(!(bitmapRequestCount<bitmapMaxCount)){
      if(MainMenu.loadingData){
        informAdapter(Bookshelf.getInstance().getNewBooksFetchedAmount()
            ,Bookshelf.getInstance().fetchExtraBooksOnly());
      }
      else{
        updateAdapter(Bookshelf.getInstance().getBooks());
      }
    }
  }

  private void rotateUpdate(){
    //previewController = new PreviewController(
        //(RecyclerView) findViewById(R.id.grid_view),this);
    updateAdapter(Bookshelf.getInstance().getBooks());
    //previewController.setData(Bookshelf.getInstance().getBooks());
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

  public String getAPI(){
    if(googleON){
      return "Google";
    }else if(goodreadsON){
      return "Goodreads";
    }else{
      return "no_api";
    }
  }

  public void showGrid(){
    loading.setVisibility(View.GONE);
    grid.setVisibility(View.VISIBLE);
  }
}

