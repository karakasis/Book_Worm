package com.example.xrhstos.bookapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.volley.toolbox.JsonObjectRequest;
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
    setContentView(R.layout.book_info);

    //Intent intent = getIntent();
    Bundle data = getIntent().getExtras();
    currentBook = (Book) data.getParcelable("bookObject");
    //Book extras = (Book)intent.getSerializableExtra("bookObject");
    String title = currentBook.getBookTitle();
    String[] author = currentBook.getAuthor();
    String googleID = currentBook.getGoogleID();

    Book matchedBook= Collection.getInstance().matchBook(currentBook.getKey());
    if(matchedBook != null){
      System.out.println("found ok");
      currentBook = matchedBook;
    }





    ImageView iv = (ImageView) findViewById(R.id.bookImage);
    iv.setImageBitmap(currentBook.getBookCover());

    TextView tv = (TextView) findViewById(R.id.bookTitle);
    tv.setText(title);
    TextView tv2 = (TextView) findViewById(R.id.bookPublisher);
    tv2.setText(author);

    createButtons();
    JsonObjectRequest jor = VolleyNetworking.getInstance(this).googleRequestByID(googleID,currentBook);
    VolleyNetworking.getInstance(this).addToRequestQueue(jor);

  }

  private void createButtons(){
    buttonsController = new Buttons(this, (LinearLayout) findViewById(R.id.buttonsLayout));

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
    buttonsController.swapToRead(currentBook.isBookRead(),currentBook.isBookInWishlist());
  }

  public void removeBook(){
    currentBook.setBookInCollection(false);
    Collection.getInstance().removeBook();
    //update sql here
    buttonsController.swapToWish(currentBook.isBookInWishlist(),currentBook.isBookRead());
  }

  public void update(Book data){
    currentBook = data;
  }

}
