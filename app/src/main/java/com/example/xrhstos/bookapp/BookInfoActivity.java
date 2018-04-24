package com.example.xrhstos.bookapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;
import info.hoang8f.widget.FButton;

/**
 * Created by Xrhstos on 4/10/2018.
 */

public class BookInfoActivity extends AppCompatActivity {

  private Book currentBook;
  private Buttons buttonsController;

  private View loading;
  private View info;

  private boolean isLoading;

  @Override
  public void onBackPressed(){
    VolleyNetworking.getInstance(this).getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
      @Override
      public boolean apply(Request<?> request) {
        return true;
      }
    });
    //for now hot fix to prevent sql lag?
   if(Database.getInstance(MyApp.getContext()).isBookSaved(currentBook))
      Database.getInstance(MyApp.getContext()).updateRecord(currentBook);
    super.onBackPressed();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.info_menu);

    MyApp.getInstance().bookInfoActivity=this;

    ViewStub stub = (ViewStub) findViewById(R.id.layout_stub_load);
    stub.setLayoutResource(R.layout.loading);
    loading = stub.inflate();
    loading.setVisibility(View.VISIBLE);

    ViewStub stub1 = (ViewStub) findViewById(R.id.layout_stub_info);
    info = stub1.inflate();
    info.setVisibility(View.GONE);



    if(savedInstanceState!=null){
      isLoading = savedInstanceState.getBoolean("IS_LOADING");
      if(isLoading){
        showLoading();
      }else{
        showInfo();
      }
    }else{
      isLoading = true;
    }



    //Intent intent = getIntent();
    Bundle data = getIntent().getExtras();
    if(data.containsKey("gallery")){
      currentBook = (Book) data.getParcelable("gallery");
    }else{
      int position = data.getInt("bookObjectPos");
      currentBook = Bookshelf.getInstance().getSingleBook(position);
    }


    String googleID = currentBook.getGoogleID();
    String id = currentBook.getId();

    Book matchedBook = Collection.getInstance().matchBook(currentBook.getKey());
    if(matchedBook != null){
      System.out.println("found ok");
      currentBook = matchedBook;
    }

    if(currentBook.getDescription() == null){//hot fix not to ask for api if api is already inputted
      showLoading();
      if(googleID != null){
        JsonObjectRequest jor = VolleyNetworking.getInstance(MyApp.getContext()).googleRequestByID(googleID,currentBook);
        VolleyNetworking.getInstance(MyApp.getContext()).addToRequestQueue(jor);
      }else if(id != null){
        StringRequest stringRequest = VolleyNetworking.getInstance(MyApp.getContext()).goodReadsRequestByID(id,currentBook);
        VolleyNetworking.getInstance(MyApp.getContext()).addToRequestQueue(stringRequest);
      }
    }else{ //go to update anyways
      update(currentBook);
    }

  }

  @Override
  protected void onSaveInstanceState(Bundle bundle) {
    super.onSaveInstanceState(bundle);
    bundle.putBoolean("IS_LOADING",isLoading);
  }

  private void createButtons(){
    buttonsController = new Buttons(this
        , (LinearLayout) findViewById(R.id.addCollection)
        , (LinearLayout) findViewById(R.id.addWishlist_read));

    if(currentBook.isBookInCollection()){ // <remove from collection
      ImageButton removeButton = buttonsController.remove();

      if(currentBook.isBookRead()){ // <remove read tag
        FButton bookNotRead = buttonsController.markNotRead();

      }else{ // <apply read tag
        FButton bookRead = buttonsController.markRead();

      }
    }else{ // <add to collection
      ImageButton addButton = buttonsController.add();

      if(currentBook.isBookInWishlist()){ // <remove from wishlist
        ImageButton wishlistButtonNo = buttonsController.wishRemove();

      }else{ // <add to wishlist
        ImageButton wishlistButtonYes = buttonsController.wishAdd();

      }
    }

  }

  public void addReadBook() {
    currentBook.setBookRead(true);
  }

  public void removeReadBook() {
    currentBook.setBookRead(false);
  }

  public void wishlistRemoveBook() {
    currentBook.setBookInWishlist(false);
  }

  public void wishlistAddBook() {
    currentBook.setBookInWishlist(true);
  }

  public void addBook(){
    currentBook.setBookInCollection(true);
    Collection.getInstance().addBook(currentBook);
    //update sql here
    Database.getInstance(MyApp.getContext()).addRecord(currentBook);
    buttonsController.swapToRead(currentBook.isBookRead());
  }

  public void removeBook(){
    currentBook.setBookInCollection(false);
    Collection.getInstance().removeBook();
    //update sql here
   Database.getInstance(MyApp.getContext()).deleteRecord(currentBook);
    buttonsController.swapToWish(currentBook.isBookInWishlist());
  }

  public void update(Book data){
    currentBook = data;

    if(data.getBookCover() == null){
      Picasso.with(this).load(data.getBookCoverURL()).into(new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, LoadedFrom from) {
          currentBook.setBookCover(bitmap);
          startUI();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
      });
    }else{
      startUI();
    }


  }

  private void startUI(){
    showInfo();
    //setContentView(R.layout.book_info);

    String title = currentBook.getBookTitle();
    String[] author = currentBook.getAuthor();

    //start ui elements
    ImageButton iv = (ImageButton) info.findViewById(R.id.bookImage);
    iv.setImageBitmap(currentBook.getBookCover());

    AppCompatTextView tv = (AppCompatTextView) info.findViewById(R.id.bookTitle);
    tv.setText(title);

    AppCompatTextView tv2 = (AppCompatTextView) info.findViewById(R.id.bookPublisher);
    tv2.setText("by ");
    for(int i=0; i<currentBook.getAuthor().length; i++){
      tv2.setText(tv2.getText()+currentBook.getAuthor()[i]+", ");
    }
    int l = tv2.getText().length();
    tv2.setText(tv2.getText().toString().substring(0,l-2));

    RatingBar rb2 = info.findViewById(R.id.ratingBar2);
    rb2.setRating(currentBook.getAverageRating());
    RatingBar rb1 = info.findViewById(R.id.ratingBar1);
    rb1.setRating(currentBook.getPersonalRating());
    rb1.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
      @Override
      public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser){
        ratingBar.setRating(rating);

      }
    });

    TextView tvDesc = info.findViewById(R.id.description);
    tvDesc.setText(Html.fromHtml(currentBook.getDescription()));

    LinearLayout infoLL = info.findViewById(R.id.moreInfo);
    if(currentBook.getPublishedDate()!=null){
      TextView tvPublishedDate = new TextView(this);
      tvPublishedDate.setText("Published: "+currentBook.getPublishedDate());
      infoLL.addView(tvPublishedDate);
    }
    if(currentBook.getCategories()!=null){
      String[] catStr = currentBook.getCategories();
      int cat = catStr.length;
      TextView tvCatTitle = new TextView(this);
      tvCatTitle.setText("Genres: ");
      infoLL.addView(tvCatTitle);
      TextView[] tvcats = new TextView[cat];
      for(int i=0; i<cat; i++){
        tvcats[i] = new TextView(this);
        tvcats[i].setText(catStr[i]);
        infoLL.addView(tvcats[i]);
        if(i==3){
          break;
        }
      }
    }
    if(currentBook.getISBN13()!= null){
      TextView tvISBN13 = new TextView(this);
      tvISBN13.setText("ISBN_13: "+currentBook.getISBN13());
      infoLL.addView(tvISBN13);
    }
    if(currentBook.getISBN10()!= null){
      TextView tvISBN10 = new TextView(this);
      tvISBN10.setText("ISBN_10: "+currentBook.getISBN10());
      infoLL.addView(tvISBN10);
    }
    if(currentBook.getPageCount()!=-1){
      TextView tvPagecount = new TextView(this);
      tvPagecount.setText("Pages: "+String.valueOf(currentBook.getPageCount()));
      infoLL.addView(tvPagecount);
    }


    createButtons();
  }

  public void buyBook(View view){
    try {
      if(currentBook.getMarketURI() == null){
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(currentBook.getBuyURL())));
      }else{
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://"+currentBook.getMarketURI())));

      }
     } catch (android.content.ActivityNotFoundException anfe) {
      startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(currentBook.getBuyURL())));
    }
  }

  public void previewCallback(View view){
    try {
      startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(currentBook.getPreviewURL())));
    } catch (android.content.ActivityNotFoundException anfe) {
      System.out.println("cant open url");
    }
  }

  public void showInfo(){
    isLoading = false;
    info.setVisibility(View.VISIBLE);
    loading.setVisibility(View.GONE);
  }

  public void showLoading(){
    isLoading = true;
    info.setVisibility(View.INVISIBLE);
    loading.setVisibility(View.VISIBLE);
  }

}
