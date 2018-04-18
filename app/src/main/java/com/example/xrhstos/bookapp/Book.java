package com.example.xrhstos.bookapp;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.volley.toolbox.ImageRequest;
import com.example.xrhstos.bookapp.transformation.RoundCorners;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.io.Serializable;

/**
 * Created by Xrhstos on 4/11/2018.
 */

public class Book implements Parcelable {

  private int id;
  private String googleID;
  private int ISBN;


  private String bookTitle;
  private String author;
  private String bookCoverURL;
  private String description;
  private int personalRating;

  //extra google stuff -start
  private String callbackURL;
  private String previewURL;
  private String buyURL;

  private String[] categories;
  private int pageCount;
  private int publishedDate; //might need string for date
  //extra google stuff -end

  private boolean isBookInCollection;
  private boolean isBookRead;
  private boolean isBookInWishlist;

  private Bitmap bookCover;

  public Book(int id, String title, String author, String url , String desc){
    this.id = id;
    bookTitle = title;
    this.author = author;
    bookCoverURL = url;
    description = desc;
  }


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {

    dest.writeString(bookTitle);
    dest.writeString(author);
    dest.writeString(bookCoverURL);
    dest.writeString(description);


    //extra google stuff-start
    dest.writeString(googleID);
    dest.writeString(callbackURL);
    dest.writeString(previewURL);
    dest.writeString(buyURL);

    dest.writeStringArray(categories);
    dest.writeInt(pageCount);
    dest.writeInt(publishedDate);
    //extra google stuff-end


    dest.writeInt(id);
    dest.writeInt(personalRating);

    dest.writeByte((byte) (isBookInCollection ? 1 : 0));
    dest.writeByte((byte) (isBookRead ? 1 : 0));
    dest.writeByte((byte) (isBookInWishlist ? 1 : 0));

    dest.writeParcelable(bookCover,flags);

  }

  public static final Parcelable.Creator<Book> CREATOR
      = new Parcelable.Creator<Book>() {
    public Book createFromParcel(Parcel in) {
      return new Book(in);
    }

    public Book[] newArray(int size) {
      return new Book[size];
    }
  };

  public Book(Parcel in) {
    bookTitle = in.readString();
    author = in.readString();
    bookCoverURL = in.readString();
    description = in.readString();


    //extra google stuff-start
    googleID = in.readString();
    callbackURL = in.readString();
    previewURL = in.readString();
    buyURL = in.readString();

    categories = in.createStringArray();
    pageCount = in.readInt();
    publishedDate = in.readInt();
    //extra google stuff-end


    id = in.readInt();
    personalRating = in.readInt();

    isBookInCollection = in.readByte() != 0;
    isBookRead = in.readByte() != 0;
    isBookInWishlist = in.readByte() != 0;


    bookCover = (Bitmap)in.readParcelable(getClass().getClassLoader());
  }

  public void requestBookCover(MainMenu context){
    ImageRequest ir = VolleyNetworking.getInstance(context).bitmapRequest(bookCoverURL,this);
    VolleyNetworking.getInstance(context).addToRequestQueue(ir);
  }

  public void responseBookCover(Bitmap response){
    bookCover = response;
  }

  /* Picasso request
  public void setBookCover(MainMenu context){
    Picasso.with(context)
        .setLoggingEnabled(true)
        .load(bookCoverURL)
        .transform(new RoundCorners(5,5))
        .into(new Target() {
          @Override
          public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from) {
            bookCover = bitmap;
          }

          @Override
          public void onPrepareLoad(Drawable placeHolderDrawable) {}

          @Override
          public void onBitmapFailed(Drawable errorDrawable) {

            System.out.println("Failed loading " + id);
          }
        });
  }
*/
  public Bitmap getBookCover(){
    return bookCover;
  }

  public String getBookCoverURL() {
    return bookCoverURL;
  }

  public void setBookCoverURL(String bookCoverURL) {
    this.bookCoverURL = bookCoverURL;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public int getPersonalRating() {
    return personalRating;
  }

  public void setPersonalRating(int personalRating) {
    this.personalRating = personalRating;
  }

  public String getBookTitle() {
    return bookTitle;
  }

  public void setBookTitle(String bookTitle) {
    this.bookTitle = bookTitle;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public boolean isBookInCollection() {
    return isBookInCollection;
  }

  public void setBookInCollection(boolean bookInCollection) {
    isBookInCollection = bookInCollection;
  }

  public boolean isBookRead() {
    return isBookRead;
  }

  public void setBookRead(boolean bookRead) {
    isBookRead = bookRead;
  }

  public boolean isBookInWishlist() {
    return isBookInWishlist;
  }

  public void setBookInWishlist(boolean bookInWishlist) {
    isBookInWishlist = bookInWishlist;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getISBN() {
    return ISBN;
  }

  public void setISBN(int ISBN) {
    this.ISBN = ISBN;
  }

  public String getCallbackURL() {
    return callbackURL;
  }

  public void setCallbackURL(String callbackURL) {
    this.callbackURL = callbackURL;
  }

  public String getPreviewURL() {
    return previewURL;
  }

  public void setPreviewURL(String previewURL) {
    this.previewURL = previewURL;
  }

  public String getBuyURL() {
    return buyURL;
  }

  public void setBuyURL(String buyURL) {
    this.buyURL = buyURL;
  }

  public int getPageCount() {
    return pageCount;
  }

  public void setPageCount(int pageCount) {
    this.pageCount = pageCount;
  }

  public int getPublishedDate() {
    return publishedDate;
  }

  public void setPublishedDate(int publishedDate) {
    this.publishedDate = publishedDate;
  }

  public String[] getCategories() {
    return categories;
  }

  public void setCategories(String[] categories) {
    this.categories = categories;
  }
}
