package com.example.xrhstos.bookapp.main_menu;

import android.content.Intent;
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
import com.bumptech.glide.Glide;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Xrhstos on 4/11/2018.
 */

public class MainMenu extends AppCompatActivity{

  private int MIN_BOUND_API = 20;

  private static final String BOOKSHELF_KEY = "BOOKSHELF";
  private static final String SEARCH_ONLINE_QUERY_KEY = "QUERY_ONLINE";
  private static final String SEARCH_ONLINE_PAGE_KEY = "PAGE_ONLINE";
  private static final String SEARCH_ONLINE_LANG_QUERY_KEY = "LANG_QUERY_ONLINE";
  private static final String FIRST_VISIBLE_KEY = "LAST_VISIBLE";
  private static final String LOADING_KEY = "LOADING";
  private static final String GLM_KEY = "GLM";
  private static final String GOOGLE_ON_KEY = "GOOGLE";
  private static final String GOODREADS_ON_KEY = "GOODREADS";
  private static final String IS_API_LOADING_KEY = "IS_API_LOADING";

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
  private boolean isLoading;

  private PreviewController previewController;
  private int firstVisibleItem;
  private Parcelable glmState;
  public int bitmapRequestCount;
  public int bitmapMaxCount;

  private Database myDb;
  private Ping ping;

  private int scannerCounter = 0;
  private ArrayList<Book> booksFromScanner;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_menu);

    ping = new Ping();


    if(Collection.getInstance().isEmpty()) {
      Collection.getInstance().fetchBooksFromDB(Database.getInstance(this).getSavedBooksList());
    }



    MyApp app = (MyApp) getApplication();
    app.mainMenu = this;

    footer = (View) findViewById(R.id.logo_container);

    Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
    setSupportActionBar(myToolbar);

    notifier = (TextView) findViewById(R.id.resultNotify);
    ViewStub stub = (ViewStub) findViewById(R.id.layout_stub);
    stub.setLayoutResource(R.layout.loading);
    loading = stub.inflate();
    /*
    Glide.with(this)
        .asGif().load(R.drawable.book_loading_ring)
        .into((ImageView)loading.findViewById(R.id.glide));
*/
    ViewStub stub1 = (ViewStub) findViewById(R.id.layout_stub_grid);
    grid = stub1.inflate();

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
      isLoading = savedInstanceState.getBoolean(IS_API_LOADING_KEY);
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
    }

    if(isLoading){
      showLoading();
    }else{
      showGrid();
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
    bundle.putBoolean(IS_API_LOADING_KEY,isLoading);

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

  public void toGallery(MenuItem item){
    Intent intent = new Intent(this,GalleryBackend.class);
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

  public void addBookManual(MenuItem item){
    /*
    IntentIntegrator integrator = new IntentIntegrator(this);
    integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
    integrator.setPrompt("Scan a barcode");
    integrator.setCameraId(0);  // Use a specific camera of the device
    integrator.setBeepEnabled(true);
    integrator.setBarcodeImageEnabled(true);
    integrator.setOrientationLocked(false);
    integrator.initiateScan();
    */
    Intent intent = new Intent(this, ManualAddMenu.class);
    //this will pass the book object itself so any changes will be made to the Book
    //class as well
    startActivity(intent);

  }

  /*
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
    if (scanResult != null) {
      // handle scan result
      System.out.println(scanResult.toString());

      showLoading();

      StringRequest grISBN = VolleyNetworking.getInstance(this).goodReadsRequestByISBN(scanResult.getContents());
      VolleyNetworking.getInstance(this).addToRequestQueue(grISBN);
      JsonObjectRequest gISBN = VolleyNetworking.getInstance(this).googleRequestByISBN(scanResult.getContents());
      VolleyNetworking.getInstance(this).addToRequestQueue(gISBN);
    }else{
      notifier.setText("Could not find book from QR");
    }
    // else continue with any other code you need in the method
  }
*/
  public void updateByISBN(ArrayList<Book> bookData){
    currentPage = 1; // ? maybe yes maybe not
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

    Bookshelf.getInstance().addBooks(bookData, this);
    if((Bookshelf.getInstance().getNewBooksFetchedAmount() < MIN_BOUND_API)
        && (bookData.size() != 0)){ // << bookdata size can be used for other purposes as well
      showLoading();
      requestMoreResults();
    }else{
      startUI();
    }

  }

  public void startUI(){
    showGrid();
    if(MainMenu.loadingData){
      informAdapter(Bookshelf.getInstance().getNewBooksFetchedAmount(),Bookshelf.getInstance().fetchExtraBooksOnly());
    }
    else{
      updateAdapter(Bookshelf.getInstance().getBooks());
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
    isLoading = false;
    loading.setVisibility(View.GONE);
    grid.setVisibility(View.VISIBLE);
  }

  public void showLoading(){
    isLoading = true;
    grid.setVisibility(View.INVISIBLE);
    loading.setVisibility(View.VISIBLE);
  }
}

