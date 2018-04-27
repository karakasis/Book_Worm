package com.example.xrhstos.bookapp.gallery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Pattern;

public class GalleryBackend extends AppCompatActivity {

  private static final String SEARCH_ONLINE_QUERY_KEY = "QUERY_ONLINE";
  private static final String SEARCH_ONLINE_PAGE_KEY = "PAGE_ONLINE";
  private static final String SEARCH_ONLINE_LANG_QUERY_KEY = "LANG_QUERY_ONLINE";
  private static final String FIRST_VISIBLE_KEY = "LAST_VISIBLE";
  private static final String LOADING_KEY = "LOADING";
  private static final String GLM_KEY = "GLM";

  private static final String TAB_KEY = "TAB";
  private static final String SORT_ITEM_KEY = "SORT_ITEM";
  private static final String SORT_ORDER_KEY = "SORT_ORDER";
  private static final String SORT_TRIGGER_KEY = "SORT_TRIGGER";

  private static final String QUERY_KEY = "QUERY";
  private static final String QUERY_STRING_KEY = "QUERY_STRING";

  public static boolean loadingData;
  public static String query;
  public static String langQuery;
  private static int currentPage;

  private PreviewController2 previewController;
  private int firstVisibleItem;
  private Parcelable glmState;

  private ArrayList<Book> myDataset;

  private View includedView;
  private View noBooksView;
  private BottomNavigationBar bottomNavigationBar;
  private int currentTab;
  private int lastVisibleCollection;
  private int lastVisibleWishlist;

  private MenuItem[] sortItems;
  private int itemId; //of selected sort item
  private boolean order; //of sorting false=ASC , true=DESC
  private boolean trigger; //sorting related
  private boolean searchQuery;
  private String searchQueryString;

  private void SavePreferences(){
    SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putInt(TAB_KEY,currentTab);
    editor.putString(SEARCH_ONLINE_QUERY_KEY,query);
    editor.putString(SEARCH_ONLINE_LANG_QUERY_KEY,langQuery);
    editor.putInt(SEARCH_ONLINE_PAGE_KEY,currentPage);
    firstVisibleItem = previewController.getFirstVisibleItem();
    editor.putInt(FIRST_VISIBLE_KEY, firstVisibleItem);

    editor.putBoolean(LOADING_KEY,loadingData);

    editor.putInt(SORT_ITEM_KEY,itemId);

    editor.putBoolean(SORT_ORDER_KEY,order);
    editor.putBoolean(SORT_TRIGGER_KEY,trigger);

    editor.putBoolean(QUERY_KEY,searchQuery);
    editor.putString(QUERY_STRING_KEY,searchQueryString);
    editor.apply();   // I missed to save the data to preference here,.
  }

  private void LoadPreferences(){
    SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
    currentTab = sharedPreferences.getInt(TAB_KEY,0);
    firstVisibleItem = sharedPreferences.getInt(FIRST_VISIBLE_KEY,0);

    loadingData = sharedPreferences.getBoolean(LOADING_KEY,false);

    itemId = sharedPreferences.getInt(SORT_ITEM_KEY,-1);
    order = sharedPreferences.getBoolean(SORT_ORDER_KEY,false);
    trigger = sharedPreferences.getBoolean(SORT_TRIGGER_KEY,false);

    searchQuery = sharedPreferences.getBoolean(QUERY_KEY,false);
    searchQueryString = sharedPreferences.getString(QUERY_STRING_KEY,"");
  }

  @Override
  public boolean onSupportNavigateUp() {
    onBackPressed();
    return true;
  }

  @Override
  public void onPause() {
    SavePreferences();
    super.onPause();
  }
  //TODO find a way to display either all books, collection only or wishlist only
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_gallery);

    MyApp app = (MyApp) getApplication();
    app.galleryBackend = this;

    Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar2);
    setSupportActionBar(myToolbar);
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);

    ViewStub vs = (ViewStub) findViewById(R.id.no_books_stub);
    noBooksView = vs.inflate();
    noBooksView.setVisibility(View.INVISIBLE);

    includedView = findViewById(R.id.previewGridGallery);

    if (savedInstanceState != null) {
      currentTab = savedInstanceState.getInt(TAB_KEY);
      query = savedInstanceState.getString(SEARCH_ONLINE_QUERY_KEY);
      langQuery = savedInstanceState.getString(SEARCH_ONLINE_LANG_QUERY_KEY);
      currentPage = savedInstanceState.getInt(SEARCH_ONLINE_PAGE_KEY);
      //bookshelf = (Bookshelf) savedInstanceState.getSerializable(BOOKSHELF_KEY);
      firstVisibleItem = savedInstanceState.getInt(FIRST_VISIBLE_KEY);

      loadingData = savedInstanceState.getBoolean(LOADING_KEY);

      itemId = savedInstanceState.getInt(SORT_ITEM_KEY);
      order = savedInstanceState.getBoolean(SORT_ORDER_KEY);
      trigger = savedInstanceState.getBoolean(SORT_TRIGGER_KEY);
      searchQuery = savedInstanceState.getBoolean(QUERY_KEY);
      searchQueryString = savedInstanceState.getString(QUERY_STRING_KEY);

    }else{

      currentTab = 0;
      currentPage = 1;
      loadingData = false;

      query = "";
      langQuery = "";

      lastVisibleCollection = 0;
      lastVisibleWishlist = 0;
      itemId = -1;
      order = false;
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

    //bottomNavigationBar.selectTab(currentTab,false);
    //handleTabs();


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

    bundle.putInt(SORT_ITEM_KEY,itemId);

    bundle.putBoolean(SORT_ORDER_KEY,order);
    bundle.putBoolean(SORT_TRIGGER_KEY,trigger);

    bundle.putBoolean(QUERY_KEY,searchQuery);
    bundle.putString(QUERY_STRING_KEY,searchQueryString);

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
    LoadPreferences();
    bottomNavigationBar.selectTab(currentTab,false);
    handleTabs();
    if(itemId!=-1 && sortItems!=null){ // custom select sort method
      customCheck(sortItems[itemId]);

    }
  }



  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_collection, menu);

    final MenuItem searchMenuItem = menu.findItem(R.id.action_search2);

    final SearchView searchView = (SearchView) searchMenuItem.getActionView();
    searchView.setQueryHint("Enter book title or author...");
    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

      @Override
      public boolean onQueryTextSubmit(String query) {

        if(!query.equals("")){
          searchBooks(query);
        }else{
          searchMenuItem.collapseActionView();
          searchView.setIconified(true);
          searchView.clearFocus();
        }

        return false;
      }

      @Override
      public boolean onQueryTextChange(String queryPiece) {

        return false;
      }
    });

    searchView.setOnCloseListener(new OnCloseListener() {
      @Override
      public boolean onClose() {
        searchQuery = false;
        searchQueryString = "";
        searchMenuItem.collapseActionView();
        searchView.setIconified(true);
        searchView.clearFocus();
        return false;
      }
    });

    sortItems = new MenuItem[4];
    sortItems[0] = menu.findItem(R.id.menuSortRating);
    sortItems[1] = menu.findItem(R.id.menuSortAvgRating);
    sortItems[2] = menu.findItem(R.id.menuSortTitle);
    sortItems[3] = menu.findItem(R.id.menuSortAuthor);
    //sortItems[4] = menu.findItem(R.id.menuSortNewest);

    if(itemId!=-1){ // custom select sort method
      customCheck(sortItems[itemId]);

    }

    return super.onCreateOptionsMenu(menu);
  }


  private void handleTabs(){
    if(currentTab == 0){
      showGallery();
    }else if(currentTab == 1){
      showWishlist();
    }
  }

  private void showGallery(){
    myDataset = Collection.getInstance().getBooks();
    if(myDataset.isEmpty()){
      includedView.setVisibility(View.INVISIBLE);
      AppCompatTextView tv = noBooksView.findViewById(R.id.error_text);
      ImageView iv = noBooksView.findViewById(R.id.error_image);
      iv.setImageResource(R.drawable.books_logo);
      tv.setText(getString(R.string.NoBooksCollection));
      noBooksView.setVisibility(View.VISIBLE);
    }else{
      showBooksUI(myDataset);
      previewController.scrollToVisibleItem(lastVisibleCollection);
    }
  }

  private void showWishlist(){
    myDataset = Collection.getInstance().getBooksWishlist();
    if(myDataset.isEmpty()){
      includedView.setVisibility(View.INVISIBLE);
      AppCompatTextView tv = noBooksView.findViewById(R.id.error_text);
      ImageView iv = noBooksView.findViewById(R.id.error_image);
      iv.setImageResource(R.drawable.books_logo);
      tv.setText(getString(R.string.NoBooksWishlist));
      noBooksView.setVisibility(View.VISIBLE);
    }else {
      showBooksUI(myDataset);
      previewController.scrollToVisibleItem(lastVisibleWishlist);
      if (glmState != null) {
        previewController.getLayoutManager().onRestoreInstanceState(glmState);
      }
    }
  }

  private void showBooksUI(ArrayList<Book> data){
    //look for search query first
    if(searchQuery){
      data = startSearch(searchQueryString,data);
    }
    //sort first
    if(itemId!=-1){ // if sorting is not disabled
      data = new ArrayList<>(sortingAlgorithms(data));
    }
    noBooksView.setVisibility(View.INVISIBLE);

    previewController = new PreviewController2(
        (RecyclerView) includedView.findViewById(R.id.grid_view), this);

    previewController.setData(data);
    includedView.setVisibility(View.VISIBLE);
  }

  //to theloume, na mpei anazitisi analoga me title ktl sto gallery.
  private void searchBooks(String query) {
    searchQuery = true;
    searchQueryString = query;
    handleTabs();
  }

  private ArrayList<Book> startSearch(String query,ArrayList<Book> myDataset) {
    ArrayList<Book> matched = new ArrayList<>();
    String dummy1,dummyp;
    String dummy2[];

    if(query.contains(" ")){
      String[] parts = query.split(Pattern.quote(" "));
      for(int i = 0;i<parts.length;i++){
        for(int j = 0;j<myDataset.size();j++){
          dummyp = parts[i].toUpperCase();
          dummy1 = myDataset.get(j).getBookTitle().toUpperCase();
          dummy2 = myDataset.get(j).getAuthor();
          //to getAuthor einai pinakas, to if elenxei mexri kai dio authors.
          if(dummyp == dummy1){
            matched.add(myDataset.get(j));
          }
          else{
            for(int k = 0;k<dummy2.length;k++){
              if(parts[i]==dummy2[k]){
                matched.add(myDataset.get(j));
              }
            }
          }
        }
      }



    }
    else{
      for(int i = 0;i<myDataset.size();i++){
        //omoiws
        if(query == myDataset.get(i).getBookTitle() || query == myDataset.get(i).getAuthor()[0] || query == myDataset.get(i).getAuthor()[1]){
          matched.add(myDataset.get(i));
        }
      }

    }
    return matched;


  }

  public void sortBooks(final MenuItem item) {

    if(itemId == translateItemToID(item)){ //trigger ASC-DESC
      trigger = true;
      order = !order;
    }else{
      trigger = false;
      order = false;
    }
    itemId = translateItemToID(item);
    //itemId = item.getItemId();
    if (!myDataset.isEmpty()) {
      showBooksUI(myDataset); // will take the data set and sort it and display the sorted
      customCheck(item); // will check the sort tab, if rotation the check will be done
      //on resume or in createoptionsmenu
    }

  }

  private ArrayList<Book> sortingAlgorithms(ArrayList<Book> myDataset){
    switch (itemId) {
      case 0:
        if(trigger){
          if(!order){ //asc
            Collections.sort(myDataset, new Comparator<Book>() {
              @Override
              public int compare(Book lhs, Book rhs) {

                return Float.valueOf(lhs.getPersonalRating())
                    .compareTo(rhs.getPersonalRating());
              }
            });

            //item.setTitle(getString(R.string.Sort_Rating_ASC));
          }else{ //des
            Collections.sort(myDataset, new Comparator<Book>() {
              @Override
              public int compare(Book lhs, Book rhs) {

                return Float.valueOf(rhs.getPersonalRating())
                    .compareTo(lhs.getPersonalRating());
              }
            });

            //item.setTitle(getString(R.string.Sort_Rating_DESC));
          }
        }else{
          Collections.sort(myDataset, new Comparator<Book>() {
            @Override
            public int compare(Book lhs, Book rhs) {

              return Float.valueOf(lhs.getPersonalRating())
                  .compareTo(rhs.getPersonalRating());
            }
          });

          //item.setTitle(getString(R.string.Sort_Rating_ASC));
        }

        break;
      case 1:
        if(trigger){
          if(!order){ //asc
            Collections.sort(myDataset, new Comparator<Book>() {
              @Override
              public int compare(Book lhs, Book rhs) {

                return Float.valueOf(lhs.getAverageRating())
                    .compareTo(rhs.getAverageRating());
              }
            });

            //item.setTitle(getString(R.string.Sort_AvgRating_ASC));
          }else{ //des
            Collections.sort(myDataset, new Comparator<Book>() {
              @Override
              public int compare(Book lhs, Book rhs) {

                return Float.valueOf(rhs.getAverageRating())
                    .compareTo(lhs.getAverageRating());
              }
            });

            //item.setTitle(getString(R.string.Sort_AvgRating_DESC));
          }
        }else{
          Collections.sort(myDataset, new Comparator<Book>() {
            @Override
            public int compare(Book lhs, Book rhs) {

              return Float.valueOf(lhs.getAverageRating())
                  .compareTo(rhs.getAverageRating());
            }
          });

          //item.setTitle(getString(R.string.Sort_AvgRating_ASC));
        }
        break;
      case 2:
        if(trigger){
          if(!order){ //asc
            Collections.sort(myDataset, new Comparator<Book>() {
              public int compare(Book obj1, Book obj2) {
                // ##       Ascending order
                return obj1.getBookTitle().compareToIgnoreCase(obj2.getBookTitle());
              }

            });

            //item.setTitle(getString(R.string.Sort_Title_ASC));
          }else{ //des
            Collections.sort(myDataset, new Comparator<Book>() {
              public int compare(Book obj1, Book obj2) {
                // ##       Ascending order
                return obj2.getBookTitle().compareToIgnoreCase(obj1.getBookTitle());
              }

            });

            //item.setTitle(getString(R.string.Sort_Title_DESC));
          }
        }else{
          Collections.sort(myDataset, new Comparator<Book>() {
            public int compare(Book obj1, Book obj2) {
              // ##       Ascending order
              return obj1.getBookTitle().compareToIgnoreCase(obj2.getBookTitle());
            }

          });

          //item.setTitle(getString(R.string.Sort_Title_ASC));
        }
        break;
      case 3:

        if(trigger){
          if(!order){ //asc
            Collections.sort(myDataset, new Comparator<Book>() {
              public int compare(Book obj1, Book obj2) {
                // ##       Ascending order
                return obj1.getAuthor()[0].compareToIgnoreCase(obj2.getAuthor()[0]);
              }

            });

            //item.setTitle(getString(R.string.Sort_Author_ASC));
          }else{ //des
            Collections.sort(myDataset, new Comparator<Book>() {
              public int compare(Book obj1, Book obj2) {
                // ##       Ascending order
                return obj2.getAuthor()[0].compareToIgnoreCase(obj1.getAuthor()[0]);
              }

            });

            //item.setTitle(getString(R.string.Sort_Author_DESC));
          }
        }else{
          Collections.sort(myDataset, new Comparator<Book>() {
            public int compare(Book obj1, Book obj2) {
              // ##       Ascending order
              return obj1.getAuthor()[0].compareToIgnoreCase(obj2.getAuthor()[0]);
            }

          });

          //item.setTitle(getString(R.string.Sort_Author_ASC));
        }
        break;


    }

    return myDataset;
  }

  private void customCheck(MenuItem item){
    for(MenuItem mi : sortItems){
      if(item == mi){
        SpannableString s;
        if(itemId == 0){
          if(order) // false = asc , true = desc
          {
            s = new SpannableString(getString(R.string.Sort_Rating_DESC));
          }else{
            s = new SpannableString(getString(R.string.Sort_Rating_ASC));
          }

        }else if(itemId == 1){
          if(order) // false = asc , true = desc
          {
            s = new SpannableString(getString(R.string.Sort_AvgRating_DESC));
          }else{
            s = new SpannableString(getString(R.string.Sort_AvgRating_ASC));
          }
        }else if(itemId == 2){
          if(order) // false = asc , true = desc
          {
            s = new SpannableString(getString(R.string.Sort_Title_DESC));
          }else{
            s = new SpannableString(getString(R.string.Sort_Title_ASC));
          }
        }else{
          if(order) // false = asc , true = desc
          {
            s = new SpannableString(getString(R.string.Sort_Author_DESC));
          }else{
            s = new SpannableString(getString(R.string.Sort_Author_ASC));
          }
        }
        s.setSpan(new ForegroundColorSpan(
            getResources().getColor(R.color.fbutton_color_belize_hole))
            , 0, s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.BOLD),0,s.length(),0);
        mi.setTitle(s);
      }else{
        SpannableString s;
        if(translateItemToID(mi) == 0){
          s = new SpannableString(getString(R.string.Sort_Rating));
        }else if(translateItemToID(mi) == 1){
          s = new SpannableString(getString(R.string.Sort_AvgRating));
        }else if(translateItemToID(mi) == 2){
          s = new SpannableString(getString(R.string.Sort_Title));
        }else{
          s = new SpannableString(getString(R.string.Sort_Author));
        }
        s.setSpan(new ForegroundColorSpan(
            getResources().getColor(R.color.blackButtonShadow)), 0, s.length(), 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL),0,s.length(),0);
        mi.setTitle(s);
      }
    }
  }

  private int translateItemToID(MenuItem item){
    int itemId;
    if(item == sortItems[0]){
      itemId = 0;
    }else if(item == sortItems[1]){
      itemId = 1;
    }else if(item == sortItems[2]){
      itemId = 2;
    }else{
      itemId = 3;
    }
    return itemId;
  }

  public void bookClick(int position){
    // Parceable implementation
    Intent intent = new Intent(this, BookInfoActivity.class);
    //this will pass the book object itself so any changes will be made to the Book
    //class as well
    //if(currentTab == 0){
      intent.putExtra("gallery", myDataset.get(position));
    //}
    /*
    else if( currentTab == 1){
      intent.putExtra("gallery", Collection.getInstance()
          .getSingleBookWishlist(position));
    }
    */
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
