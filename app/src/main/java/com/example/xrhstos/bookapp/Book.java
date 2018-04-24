package com.example.xrhstos.bookapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Picasso.LoadedFrom;
import com.squareup.picasso.Target;
import java.io.ByteArrayOutputStream;

/**
 * Created by Xrhstos on 4/11/2018.
 */

public class Book implements Parcelable {

  private String id;
  private String googleID;
  private String ISBN13;
  private String ISBN10;


  private String bookTitle;
  private String[] authors;
  private String bookCoverURL;
  private String description;
  private float personalRating;
  private float averageRating;

  //extra google stuff -start
  private String callbackURL;
  private String previewURL;
  private String buyURL;

  private String[] categories;
  private int pageCount;
  private String publishedDate; //might need string for date
  //extra google stuff -end

  private boolean isBookInCollection;
  private boolean isBookRead;
  private boolean isBookInWishlist;

  private Bitmap bookCover;
  private byte[] byteArray;

  public float getAverageRating() {
    return averageRating;
  }

  public void setAverageRating(float averageRating) {
    this.averageRating = averageRating;
  }

  public String getISBN10() {
    return ISBN10;
  }

  public void setISBN10(String ISBN10) {
    this.ISBN10 = ISBN10;
  }


  // convert from bitmap to byte array
  public void setByteArrayFromBitmap() {
    if(bookCover!=null){
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      bookCover.compress(Bitmap.CompressFormat.PNG, 0, stream);
      byteArray = stream.toByteArray();
      System.out.println("Bitmap loaded and saved");
    }else{
      /*
      int placeholder = R.drawable.placeholder;
      Drawable myIcon = MyApp.getContext().getResources().getDrawable( placeholder );
      bookCover = ((BitmapDrawable)myIcon).getBitmap();
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      bookCover.compress(Bitmap.CompressFormat.JPEG, 100, stream);
      byteArray = stream.toByteArray();
      */
      byteArray = null;
      System.out.println("Bitmap saved - loaded placeholder");
    }
  }

  // convert from byte array to bitmap
  public Bitmap getImage(byte[] image) {
    return BitmapFactory.decodeByteArray(image, 0, image.length);
  }

  public void setBitmapFromByteArray(byte[] byteArray){
    this.bookCover = getImage(byteArray);
  }
  public byte[] getByteArray(){
    if(byteArray==null){
      setByteArrayFromBitmap();
    }
    return byteArray;
  }

  public Book(){ // for sql
    pageCount = -1;
  }

  public Book(String id, String title, String[] authors, String url){
    this.id = id;
    bookTitle = title;
    this.authors = authors;
    bookCoverURL = url;
    pageCount = -1;
  }

  public String getKey(){
    if(googleID == null){
      return id;
    }else{
      return googleID;
    }
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {

    dest.writeString(bookTitle);
    dest.writeStringArray(authors);
    dest.writeString(bookCoverURL);
    dest.writeString(description);
    dest.writeString(ISBN10);
    dest.writeString(ISBN13);


    //extra google stuff-start
    dest.writeString(googleID);
    dest.writeString(callbackURL);
    dest.writeString(previewURL);
    dest.writeString(buyURL);

    dest.writeStringArray(categories);
    dest.writeInt(pageCount);
    dest.writeString(publishedDate);
    //extra google stuff-end


    dest.writeString(id);
    dest.writeFloat(personalRating);
    dest.writeFloat(averageRating);

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
    authors = in.createStringArray();
    bookCoverURL = in.readString();
    description = in.readString();
    ISBN10 = in.readString();
    ISBN13 = in.readString();

    //extra google stuff-start
    googleID = in.readString();
    callbackURL = in.readString();
    previewURL = in.readString();
    buyURL = in.readString();

    categories = in.createStringArray();
    pageCount = in.readInt();
    publishedDate = in.readString();
    //extra google stuff-end


    id = in.readString();
    personalRating = in.readFloat();
    averageRating = in.readFloat();

    isBookInCollection = in.readByte() != 0;
    isBookRead = in.readByte() != 0;
    isBookInWishlist = in.readByte() != 0;


    bookCover = (Bitmap)in.readParcelable(getClass().getClassLoader());
  }

  /*
  public void requestBookCover(MainMenu context){
    ImageRequest ir = VolleyNetworking.getInstance(context).bitmapRequest(bookCoverURL,this);
    VolleyNetworking.getInstance(context).addToRequestQueue(ir);
  }

  public void responseBookCover(Bitmap response){
    bookCover = response;

    MyApp.getInstance().mainMenu.bitmapRequestCount++;
  }
*/

  public void setBookCover(Bitmap response){
    bookCover = response;
  }

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

  public float getPersonalRating() {
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

  public String[] getAuthor() {
    return authors;
  }

  public void setAuthor(String[] authors) {
    this.authors = authors;
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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getISBN13() {
    return ISBN13;
  }

  public void setISBN13(String ISBN13) {
    this.ISBN13 = ISBN13;
  }

  public String getGoogleID() {
    return googleID;
  }

  public void setGoogleID(String googleID) {
    this.googleID = googleID;
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

  public String getMarketURI(){
    if(googleID==null){

      return null;
    }else if(id==null){
      String market;
      market = buyURL.replace("https://play.google.com/store/books/","");
      return market;
    }else{
      return null;
    }

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

  public String getPublishedDate() {
    return publishedDate;
  }

  public void setPublishedDate(String publishedDate) {
    this.publishedDate = publishedDate;
  }

  public String[] getCategories() {
    return categories;
  }

  public void setCategories(String[] categories) {
    this.categories = categories;
  }
}
