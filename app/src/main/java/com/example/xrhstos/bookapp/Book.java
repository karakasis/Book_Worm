package com.example.xrhstos.bookapp;

/**
 * Created by Xrhstos on 4/11/2018.
 */

public class Book {

  private int id;

  private String bookTitle;
  private String author;
  private String bookCoverURL;
  private String description;
  private int personalRating;

  private boolean isBookInCollection;
  private boolean isBookRead;
  private boolean isBookInWishlist;

  public Book(int id, String title, String author, String url , String desc){
    this.id = id;
    bookTitle = title;
    this.author = author;
    bookCoverURL = url;
    description = desc;
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
}
