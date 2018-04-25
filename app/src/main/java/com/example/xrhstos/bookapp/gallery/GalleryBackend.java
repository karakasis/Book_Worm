package com.example.xrhstos.bookapp.gallery;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.example.xrhstos.bookapp.Book;
import com.example.xrhstos.bookapp.BookInfoActivity;
import com.example.xrhstos.bookapp.Collection;
import com.example.xrhstos.bookapp.Database;
import com.example.xrhstos.bookapp.MyApp;
import com.example.xrhstos.bookapp.R;
import java.util.ArrayList;

public class GalleryBackend extends AppCompatActivity {

  private static final String BOOKSHELF_KEY = "BOOKSHELF";
  private static final String SEARCH_ONLINE_QUERY_KEY = "QUERY_ONLINE";
  private static final String SEARCH_ONLINE_PAGE_KEY = "PAGE_ONLINE";
  private static final String SEARCH_ONLINE_LANG_QUERY_KEY = "LANG_QUERY_ONLINE";
  private static final String FIRST_VISIBLE_KEY = "LAST_VISIBLE";
  private static final String LOADING_KEY = "LOADING";
  private static final String GLM_KEY = "GLM";
  private static final String GOOGLE_ON_KEY = "GOOGLE";
  private static final String GOODREADS_ON_KEY = "GOODREADS";

  private boolean googleON;
  private boolean goodreadsON;
  public static boolean loadingData;
  public static String query;
  public static String langQuery;
  private static int currentPage;
  public TextView notifier;

  private View footer;

  private PreviewController2 previewController;
  private int firstVisibleItem;
  private Parcelable glmState;


  private RecyclerView galleryRecycler;
  private RecyclerView.Adapter gAdapter;
  private RecyclerView.LayoutManager gLayoutManager;
  private ArrayList<Book> myDataset;

  private View includedView;



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gallery);

    MyApp app = (MyApp) getApplication();
    app.galleryBackend = this;

    myDataset = Collection.getInstance().getBooks();
    includedView = findViewById(R.id.previewGridGallery);

    //footer = (View) findViewById(R.id.logo_container);

    //Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
    //setSupportActionBar(myToolbar);

    //notifier = (TextView) findViewById(R.id.resultNotify);

    if(previewController == null){
      previewController = new PreviewController2(
          (RecyclerView) includedView.findViewById(R.id.grid_view),this);
    }

    previewController.setData(myDataset);

    if (savedInstanceState != null) {
      query = savedInstanceState.getString(SEARCH_ONLINE_QUERY_KEY);
      langQuery = savedInstanceState.getString(SEARCH_ONLINE_LANG_QUERY_KEY);
      currentPage = savedInstanceState.getInt(SEARCH_ONLINE_PAGE_KEY);
      //bookshelf = (Bookshelf) savedInstanceState.getSerializable(BOOKSHELF_KEY);
      firstVisibleItem = savedInstanceState.getInt(FIRST_VISIBLE_KEY);

      loadingData = savedInstanceState.getBoolean(LOADING_KEY);

    }else{

      currentPage = 1;
      loadingData = false;

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


/*
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu, menu);

    final MenuItem searchMenuItem = menu.findItem(R.id.action_search);

    final SearchView searchView = (SearchView) searchMenuItem.getActionView();
    searchView.setQueryHint("Dan Brown :el");
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

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
  */

  //to theloume, na mpei anazitisi analoga me title ktl sto gallery.
  private void searchBooks(String query) {
  }

  //ipefthino gia to scroll
  public void requestMoreResults(){

  }

  public void bookClick(int position){
    // Parceable implementation
    Intent intent = new Intent(this, BookInfoActivity.class);
    //this will pass the book object itself so any changes will be made to the Book
    //class as well
    intent.putExtra("gallery", Collection.getInstance().getSingleBook(position));
    startActivity(intent);
  }

  private void rotateUpdate() {
    //previewController = new PreviewController(
    //(RecyclerView) findViewById(R.id.grid_view),this);
    updateAdapter(Collection.getInstance().getBooks());
    //previewController.setData(bookshelf.getBooks());
    previewController.scrollToVisibleItem(firstVisibleItem);
  }

  public void updateAdapter(ArrayList<Book> data){
    previewController = new PreviewController2(
        (RecyclerView) includedView.findViewById(R.id.grid_view),this);
    previewController.setData(data);
  }
}
