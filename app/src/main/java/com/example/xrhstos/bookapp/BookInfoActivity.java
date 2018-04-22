package com.example.xrhstos.bookapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import info.hoang8f.widget.FButton;
import java.util.ArrayList;

/**
 * Created by Xrhstos on 4/10/2018.
 */

public class BookInfoActivity extends AppCompatActivity {

  private Book currentBook;
  private Buttons buttonsController;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    //Intent intent = getIntent();
    Bundle data = getIntent().getExtras();
    currentBook = (Book) data.getParcelable("bookObject");

    String googleID = currentBook.getGoogleID();
    String id = currentBook.getId();

    Book matchedBook= Collection.getInstance().matchBook(currentBook.getKey());
    if(matchedBook != null){
      System.out.println("found ok");
      currentBook = matchedBook;
    }

    if(currentBook.getDescription() == null){//hot fix not to ask for api if api is already inputted
      if(googleID != null){
        JsonObjectRequest jor = VolleyNetworking.getInstance(this).googleRequestByID(googleID,currentBook);
        VolleyNetworking.getInstance(this).addToRequestQueue(jor);
      }else if(id != null){
        StringRequest stringRequest = VolleyNetworking.getInstance(this).goodReadsRequestByID(id,currentBook);
        VolleyNetworking.getInstance(this).addToRequestQueue(stringRequest);
      }
    }else{ //go to update anyways
      update(currentBook);
    }

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
    buttonsController.swapToRead(currentBook.isBookRead());
  }

  public void removeBook(){
    currentBook.setBookInCollection(false);
    Collection.getInstance().removeBook();
    //update sql here
    buttonsController.swapToWish(currentBook.isBookInWishlist());
  }

  public void update(Book data){
    currentBook = data;
    setContentView(R.layout.book_info);
    String title = currentBook.getBookTitle();
    String[] author = currentBook.getAuthor();

    //start ui elements
    ImageButton iv = (ImageButton) findViewById(R.id.bookImage);
    iv.setImageBitmap(currentBook.getBookCover());

    AppCompatTextView tv = (AppCompatTextView) findViewById(R.id.bookTitle);
    tv.setText(title);

    AppCompatTextView tv2 = (AppCompatTextView) findViewById(R.id.bookPublisher);
    tv2.setText("by ");
    for(int i=0; i<currentBook.getAuthor().length; i++){
      tv2.setText(tv2.getText()+currentBook.getAuthor()[i]+", ");
    }
    int l = tv2.getText().length();
    tv2.setText(tv2.getText().toString().substring(0,l-2));

    RatingBar rb2 = findViewById(R.id.ratingBar2);
    rb2.setRating(currentBook.getAverageRating());
    RatingBar rb1 = findViewById(R.id.ratingBar1);
    rb1.setRating(currentBook.getPersonalRating());
    rb1.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
      @Override
      public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser){
        ratingBar.setRating(rating);

      }
    });

    TextView tvDesc = findViewById(R.id.description);
    tvDesc.setText(currentBook.getDescription());

    LinearLayout info = findViewById(R.id.moreInfo);
    if(!currentBook.getPublishedDate().isEmpty()){
      TextView tvPublishedDate = new TextView(this);
      tvPublishedDate.setText("Published: "+currentBook.getPublishedDate());
      info.addView(tvPublishedDate);
    }
    if(currentBook.getCategories()!=null){
      String[] catStr = currentBook.getCategories();
      int cat = catStr.length;
      TextView tvCatTitle = new TextView(this);
      tvCatTitle.setText("Genres: ");
      info.addView(tvCatTitle);
      TextView[] tvcats = new TextView[cat];
      for(int i=0; i<cat; i++){
        tvcats[i] = new TextView(this);
        tvcats[i].setText(catStr[i]);
        info.addView(tvcats[i]);
        if(i==3){
          break;
        }
      }
    }
    if(currentBook.getISBN()!=-1){
      TextView tvISBN = new TextView(this);
      tvISBN.setText("ISBN: "+String.valueOf(currentBook.getISBN()));
      info.addView(tvISBN);
    }
    if(currentBook.getPageCount()!=-1){
      TextView tvPagecount = new TextView(this);
      tvPagecount.setText("Pages: "+String.valueOf(currentBook.getPageCount()));
      info.addView(tvPagecount);
    }


    createButtons();
  }

  public void buyBook(View view){
    try {
      startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://"+currentBook.getMarketURI())));
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

}
