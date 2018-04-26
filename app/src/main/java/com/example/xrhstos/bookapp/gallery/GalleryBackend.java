package com.example.xrhstos.bookapp.gallery;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.eightbitlab.bottomnavigationbar.BottomBarItem;
import com.eightbitlab.bottomnavigationbar.BottomNavigationBar;
import com.example.xrhstos.bookapp.Book;
import com.example.xrhstos.bookapp.BookInfoActivity;
import com.example.xrhstos.bookapp.Collection;
import com.example.xrhstos.bookapp.Database;
import com.example.xrhstos.bookapp.MyApp;
import com.example.xrhstos.bookapp.R;
import java.util.ArrayList;

public class GalleryBackend extends AppCompatActivity {

  private static final String SEARCH_ONLINE_QUERY_KEY = "QUERY_ONLINE";
  private static final String SEARCH_ONLINE_PAGE_KEY = "PAGE_ONLINE";
  private static final String SEARCH_ONLINE_LANG_QUERY_KEY = "LANG_QUERY_ONLINE";
  private static final String FIRST_VISIBLE_KEY = "LAST_VISIBLE";
  private static final String LOADING_KEY = "LOADING";
  private static final String GLM_KEY = "GLM";

  private static final String TAB_KEY = "TAB";

  public static boolean loadingData;
  public static String query;
  public static String langQuery;
  private static int currentPage;

  private PreviewController2 previewController;
  private int firstVisibleItem;
  private Parcelable glmState;

  private ArrayList<Book> myDataset;

  private View includedView;
  private BottomNavigationBar bottomNavigationBar;
  private int currentTab;


  //TODO find a way to display either all books, collection only or wishlist only
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gallery);

    MyApp app = (MyApp) getApplication();
    app.galleryBackend = this;

    if (savedInstanceState != null) {
      currentTab = savedInstanceState.getInt(TAB_KEY);
      query = savedInstanceState.getString(SEARCH_ONLINE_QUERY_KEY);
      langQuery = savedInstanceState.getString(SEARCH_ONLINE_LANG_QUERY_KEY);
      currentPage = savedInstanceState.getInt(SEARCH_ONLINE_PAGE_KEY);
      //bookshelf = (Bookshelf) savedInstanceState.getSerializable(BOOKSHELF_KEY);
      firstVisibleItem = savedInstanceState.getInt(FIRST_VISIBLE_KEY);

      loadingData = savedInstanceState.getBoolean(LOADING_KEY);

    }else{

      currentTab = 0;
      currentPage = 1;
      loadingData = false;

      query = "";
      langQuery = "";
    }

    BottomBarItem collectionTab = new BottomBarItem(R.drawable.ic_local_library_indigo_a200_24dp);
    BottomBarItem wishlistTab = new BottomBarItem(R.drawable.ic_favorite_red_400_24dp);
    bottomNavigationBar = findViewById(R.id.bottom_bar);
    bottomNavigationBar.addTab(collectionTab);
    bottomNavigationBar.addTab(wishlistTab);

    bottomNavigationBar.setOnSelectListener(new BottomNavigationBar.OnSelectListener() {
      @Override
      public void onSelect(int position) {
        System.out.println("onselect");
        currentTab = position;
        handleTabs();
      }
    });

    bottomNavigationBar.selectTab(currentTab,false);
    handleTabs();

  }

  @Override
  protected void onSaveInstanceState(Bundle bundle) {
    super.onSaveInstanceState(bundle);
    //bundle.putSerializable(BOOKSHELF_KEY, bookshelf);
    bundle.putInt(TAB_KEY,currentTab);
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
    bottomNavigationBar.selectTab(currentTab,false);
    handleTabs();
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

  private void handleTabs(){
    if(currentTab == 0){
      showGallery();
    }else if(currentTab == 1){
      showWishlist();
    }
  }

  private void showGallery(){
    myDataset = Collection.getInstance().getBooks();
    includedView = findViewById(R.id.previewGridGallery);

    //if(previewController == null){
    previewController = new PreviewController2(
        (RecyclerView) includedView.findViewById(R.id.grid_view),this);
    //}

    previewController.setData(myDataset);
  }

  private void showWishlist(){
    myDataset = Collection.getInstance().getBooksWishlist();
    includedView = findViewById(R.id.previewGridGallery);

    //if(previewController == null){
    previewController = new PreviewController2(
        (RecyclerView) includedView.findViewById(R.id.grid_view),this);
    //}

    previewController.setData(myDataset);
  }

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
    if(currentTab == 0){
      intent.putExtra("gallery", Collection.getInstance().getSingleBook(position));
    }else if( currentTab == 1){
      intent.putExtra("gallery", Collection.getInstance()
          .getSingleBookWishlist(position));
    }
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
