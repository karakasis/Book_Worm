package com.example.xrhstos.bookapp;

import android.content.Context;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by Xrhstos on 4/20/2018.
 */

public class Collection {

  private ArrayList<Book> books;
  private HashMap<String,Book> bookMap;
  private Book currentBookLoaded;
  private boolean isBookLoaded;
  private static Collection mInstance;
  private boolean sqlFetched;


  private ArrayList<Book> booksWishlist;
  private HashMap<String,Book> bookWishlistMap;
  private Book currentBookWishlistLoaded;
  private boolean isBookWishlistLoaded;

  private Collection() {

    books = new ArrayList<>();
    bookMap = new HashMap<>();
    isBookLoaded = false;
    sqlFetched = false;

    booksWishlist = new ArrayList<>();
    bookWishlistMap = new HashMap<>();
  }

  public static synchronized Collection getInstance() {
    if (mInstance == null ) {
      mInstance = new Collection();
    }
    return mInstance;
  }

  public Book matchBook(String key){
    isBookLoaded = false;
    if(bookMap.containsKey(key)){
      currentBookLoaded = bookMap.get(key);
      isBookLoaded = true;
      return currentBookLoaded;
    }else{
      return null;
    }
  }

  public void addBook(Book newBook){
    //books.add(newBook);
    bookMap.put(newBook.getKey(),newBook);
    currentBookLoaded = newBook;
    isBookLoaded = true;
  }

  public Book removeBook(){
    if(isBookLoaded){
      String key = currentBookLoaded.getKey();
      isBookLoaded = false;
      return bookMap.remove(key);
    }else{
      return null;
    }
  }

  //returns the list of the book objects
  public ArrayList<Book> getBooks(){
    books = new ArrayList<>();
    books.addAll(bookMap.values());
    return new ArrayList<>(books);//mporei na exei error itan i retur

  }

  //returns the book in the asked position -- not working probably
  public Book getSingleBook(int position){
    return books.get(position);
  }

  public void fetchBooksFromDB(ArrayList<Book> fetched){
    for(Book b : fetched){
      bookMap.put(b.getKey(),b);
    }
  }

  public boolean isSqlFetched(){
    boolean reply = sqlFetched;
    sqlFetched = true;
    return reply;
  }

  public Book matchBookWishlist(String key){
    isBookWishlistLoaded = false;
    if(bookWishlistMap.containsKey(key)){
      currentBookLoaded = bookWishlistMap.get(key);
      isBookWishlistLoaded = true;
      return currentBookWishlistLoaded;
    }else{
      return null;
    }
  }

  public void addBookWishlist(Book newBook){
    //books.add(newBook);
    bookWishlistMap.put(newBook.getKey(),newBook);
    currentBookWishlistLoaded = newBook;
    isBookWishlistLoaded = true;
  }

  public Book removeBookWishlist(){
    if(isBookWishlistLoaded){
      String key = currentBookWishlistLoaded.getKey();
      isBookLoaded = false;
      return bookMap.remove(key);
    }else{
      return null;
    }
  }

}
