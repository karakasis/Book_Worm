package com.example.xrhstos.bookapp.main_menu;

import static com.example.xrhstos.bookapp.LockActivityOrientation.lockActivityOrientation;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.xrhstos.bookapp.Book;
import com.example.xrhstos.bookapp.BookInfoActivity;
import com.example.xrhstos.bookapp.Bookshelf;
import com.example.xrhstos.bookapp.Collection;
import com.example.xrhstos.bookapp.Database;
import com.example.xrhstos.bookapp.ManualAddMenu;
import com.example.xrhstos.bookapp.MyApp;
import com.example.xrhstos.bookapp.Ping;
import com.example.xrhstos.bookapp.R;
import com.example.xrhstos.bookapp.VolleyNetworking;
import com.example.xrhstos.bookapp.gallery.GalleryBackend;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Xrhstos on 4/11/2018.
 */

public class MainMenu extends AppCompatActivity{

  public static boolean loadingData;
  public static String query;
  public static String langQuery;
  private static int currentPage;

  private int MIN_BOUND_API = 20;

  private static final String SEARCH_ONLINE_QUERY_KEY = "QUERY_ONLINE";
  private static final String SEARCH_ONLINE_PAGE_KEY = "PAGE_ONLINE";
  private static final String SEARCH_ONLINE_LANG_QUERY_KEY = "LANG_QUERY_ONLINE";
  private static final String FIRST_VISIBLE_KEY = "LAST_VISIBLE";
  private static final String GLM_KEY = "GLM";
  private static final String GOOGLE_ON_KEY = "GOOGLE";
  private static final String GOODREADS_ON_KEY = "GOODREADS";
  private static final String IS_API_ERROR_KEY = "IS_API_ERROR";
  private static final String IS_API_ERROR_STRING_KEY = "IS_API_ERROR_STRING";
  private static final String IS_API_ERROR_REF_KEY = "IS_API_ERROR_REF";
  private static final String IS_API_ERROR_TYPE_KEY = "IS_API_ERROR_TYPE";
  private static final String FLIPPER_KEY = "FLIPPER";

  private boolean googleON;
  private boolean goodreadsON;

  private SearchView searchView;
  private View footer;
  private ViewFlipper vFlipper;
  private boolean isLoading;
  private boolean isError;
  private String error;
  private boolean refreshable;
  private int errorType;
  private Snackbar snackbar;

  private PreviewController previewController;
  private int firstVisibleItem;
  private Parcelable glmState;

  private Ping ping;

  private int currentFlippedView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_menu);
    System.out.println("OnCreate");
    ping = new Ping();


    if(!Collection.getInstance().isSqlFetched()) {
      Collection.getInstance().fetchBooksFromDB(Database.getInstance(this).getSavedBooksList());
    }

    VolleyNetworking.refresh(this);

    MyApp app = (MyApp) getApplication();
    app.mainMenu = this;

    footer = (View) findViewById(R.id.logo_container);

    Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
    setSupportActionBar(myToolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);

    vFlipper = findViewById(R.id.container);
    vFlipper.setAutoStart(false);

    if (savedInstanceState != null) {
      query = savedInstanceState.getString(SEARCH_ONLINE_QUERY_KEY);
      langQuery = savedInstanceState.getString(SEARCH_ONLINE_LANG_QUERY_KEY);
      currentPage = savedInstanceState.getInt(SEARCH_ONLINE_PAGE_KEY);
      //bookshelf = (Bookshelf) savedInstanceState.getSerializable(BOOKSHELF_KEY);
      firstVisibleItem = savedInstanceState.getInt(FIRST_VISIBLE_KEY);
      googleON = savedInstanceState.getBoolean(GOOGLE_ON_KEY);
      goodreadsON = savedInstanceState.getBoolean(GOODREADS_ON_KEY);

      isError = savedInstanceState.getBoolean(IS_API_ERROR_KEY);

      error = savedInstanceState.getString(IS_API_ERROR_STRING_KEY);
      refreshable = savedInstanceState.getBoolean(IS_API_ERROR_REF_KEY);
      errorType = savedInstanceState.getInt(IS_API_ERROR_TYPE_KEY);

      currentFlippedView = savedInstanceState.getInt(FLIPPER_KEY);
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
      isLoading = false;
      isError = false;
      error = "";
      errorType = -1;
      refreshable = false;
      currentFlippedView = 0;
    }

    if(previewController == null){
      previewController = new PreviewController(
          (RecyclerView) vFlipper.getChildAt(0).findViewById(R.id.grid_view),this);
    }
    if(!Bookshelf.getInstance().getBooks().isEmpty()){
      updateAdapter(Bookshelf.getInstance().getBooks());
      previewController.scrollToVisibleItem(firstVisibleItem);
    }

    if(isError){
      errorHandling(error,refreshable,errorType);
    }else{
      showGrid();
    }

    if(Bookshelf.getInstance().getBooks().isEmpty()){
      flipViews(2); // error page
      AppCompatTextView tv = vFlipper.getChildAt(2).findViewById(R.id.error_text);
      ImageView iv = vFlipper.getChildAt(2).findViewById(R.id.error_image);
      iv.setImageResource(R.drawable.books_logo);
      tv.setText(getString(R.string.instructions));
    }

  }

  @Override
  protected void onSaveInstanceState(Bundle bundle) {
    super.onSaveInstanceState(bundle);
    System.out.println("onSaveInstanceState");
    //bundle.putSerializable(BOOKSHELF_KEY, bookshelf);
    bundle.putString(SEARCH_ONLINE_QUERY_KEY,query);
    bundle.putString(SEARCH_ONLINE_LANG_QUERY_KEY,langQuery);
    bundle.putInt(SEARCH_ONLINE_PAGE_KEY,currentPage);
    firstVisibleItem = previewController.getFirstVisibleItem();
    bundle.putInt(FIRST_VISIBLE_KEY, firstVisibleItem);


    bundle.putBoolean(GOODREADS_ON_KEY,goodreadsON);
    bundle.putBoolean(GOOGLE_ON_KEY,googleON);

    bundle.putBoolean(IS_API_ERROR_KEY,isError);

    bundle.putString(IS_API_ERROR_STRING_KEY,error);
    bundle.putBoolean(IS_API_ERROR_REF_KEY,refreshable);
    bundle.putInt(IS_API_ERROR_TYPE_KEY,errorType);

    bundle.putInt(FLIPPER_KEY,currentFlippedView);

    // Save list state
    glmState = previewController.getLayoutManager().onSaveInstanceState();
    bundle.putParcelable(GLM_KEY, glmState);
  }

  @Override
  protected void onRestoreInstanceState(Bundle bundle) {
    super.onRestoreInstanceState(bundle);

    System.out.println("onRestoreInstanceState");
    // Retrieve list state and list/item positions
    if(bundle != null)
      glmState = bundle.getParcelable(GLM_KEY);
  }

  @Override
  protected void onResume() {
    super.onResume();

    System.out.println("onResume");
    System.out.println(isError);
    if (glmState != null) {
      //rotateUpdate();
      previewController.getLayoutManager().onRestoreInstanceState(glmState);
    }
    if(currentFlippedView==0){
      if(googleON){
        ImageView logo = (ImageView) findViewById(R.id.logo);
        logo.setImageResource(R.drawable.google_logo);
      }else if (goodreadsON){
        ImageView logo = (ImageView) findViewById(R.id.logo);
        logo.setImageResource(R.drawable.goodreads_logo);
      }else{
        footer.setVisibility(View.INVISIBLE);
      }
    }

  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu, menu);

    System.out.println("onCreateOptionsMenu");
    final MenuItem searchMenuItem = menu.findItem(R.id.action_search);

    searchView = (SearchView) searchMenuItem.getActionView();
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

        showLoading();
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
      goodreadsON = true;
      googleON = false;
    }
  }

  private void requestGoogle(){

    JsonObjectRequest jsonObjectRequest;
    jsonObjectRequest = VolleyNetworking.getInstance(this).googleRequest(query);

    VolleyNetworking.getInstance(this).addToRequestQueue(jsonObjectRequest);
    goodreadsON = false;
    googleON = true;

  }

  public void toGallery(MenuItem item){
    Intent intent = new Intent(this,GalleryBackend.class);
    startActivity(intent);
  }

  public void addBookManual(MenuItem item){
    Intent intent = new Intent(this, ManualAddMenu.class);
    startActivity(intent);
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

  public void update(ArrayList<Book> bookData){

    Bookshelf.getInstance().addBooks(bookData, this);
    if((Bookshelf.getInstance().getNewBooksFetchedAmount() < MIN_BOUND_API)
        && (bookData.size() != 0)){ // << bookdata size can be used for other purposes as well
      requestMoreResults();
    }else{
      startUI();
    }
    if(bookData.size() == 0){
      snackbar = Snackbar.make(findViewById(R.id.main),getString(R.string.end_results),Snackbar.LENGTH_LONG);
      View sbView = snackbar.getView();
      TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
      textView.setTextColor(getResources().getColor(R.color.fbutton_color_wet_asphalt));
      sbView.setBackgroundColor(getResources().getColor(R.color.logoBackgroundColor));
      snackbar.show();
    }

  }

  public void startUI(){
    if(MainMenu.loadingData){
      informAdapter(Bookshelf.getInstance().fetchExtraBooksOnly());
    }
    else{
      updateAdapter(Bookshelf.getInstance().getBooks());
    }
  }

  public void requestMoreResults(){
    currentPage++;
    searchBooks(query);
  }

  public void updateAdapter(ArrayList<Book> data){
    previewController = new PreviewController(
        (RecyclerView) vFlipper.getChildAt(0).findViewById(R.id.grid_view),this);
    previewController.setData(data);
  }

  public void informAdapter(ArrayList<Book> newData){
    previewController.acceptResponseFromMainThread(newData);
  }

  public static int getPage(){
    return currentPage;
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
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    isError = false;
    isLoading = false;
    footer.setVisibility(View.VISIBLE);
    if(googleON){
      ImageView logo = (ImageView) findViewById(R.id.logo);
      logo.setImageResource(R.drawable.google_logo);
    }else if (goodreadsON){
      ImageView logo = (ImageView) findViewById(R.id.logo);
      logo.setImageResource(R.drawable.goodreads_logo);
    }else{
      footer.setVisibility(View.INVISIBLE);
    }
    flipViews(0);
    /*
    loading.setVisibility(View.INVISIBLE);
    grid.setVisibility(View.VISIBLE);
    errors.setVisibility(View.GONE);
    */
  }

  public void showLoading(){
    lockActivityOrientation(this);
    isError = false;
    isLoading = true;
    if(snackbar!=null){
      snackbar.dismiss();
    }
    footer.setVisibility(View.INVISIBLE);
    flipViews(1);
    /*
    grid.setVisibility(View.INVISIBLE);
    loading.setVisibility(View.VISIBLE);
    errors.setVisibility(View.GONE);
    */
  }

  public void showError(){
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    isError = true;
    isLoading = false;
    loadingData = false;
    footer.setVisibility(View.INVISIBLE);
    flipViews(2);
    /*
    errors.setVisibility(View.VISIBLE);
    grid.setVisibility(View.INVISIBLE);
    loading.setVisibility(View.GONE);
    */
  }

  public void flipViews(int index){
    vFlipper.setDisplayedChild(index);
    currentFlippedView = index;
  }

  public void errorHandling(String error,boolean refreshable,int errorType){
    this.error = error;
    this.refreshable = refreshable;
    this.errorType = errorType;
    showError();
    if(refreshable){
      snackbar = Snackbar.make(findViewById(R.id.main),error,Snackbar.LENGTH_INDEFINITE)
          .setAction(R.string.RefreshPage, new OnClickListener() {
            @Override
            public void onClick(View view) {
              searchView.setQuery(query,true);
            }
          })
          .setActionTextColor(getResources().getColor(R.color.poweredColor));
      View sbView = snackbar.getView();
      TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
      textView.setTextColor(getResources().getColor(R.color.fbutton_color_wet_asphalt));
      sbView.setBackgroundColor(getResources().getColor(R.color.logoBackgroundColor));
      snackbar.show();

    }else{
      snackbar = Snackbar.make(findViewById(R.id.main),error,Snackbar.LENGTH_LONG);
      View sbView = snackbar.getView();
      TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
      textView.setTextColor(getResources().getColor(R.color.fbutton_color_wet_asphalt));
      sbView.setBackgroundColor(getResources().getColor(R.color.logoBackgroundColor));
      snackbar.show();
    }
    ImageView eView = (ImageView) vFlipper.getChildAt(2).findViewById(R.id.error_image);
    if(errorType == 0){
      eView.setImageResource(R.drawable.no_server);
    }else if(errorType == 1){
      eView.setImageResource(R.drawable.no_internet);
    }

  }

}

