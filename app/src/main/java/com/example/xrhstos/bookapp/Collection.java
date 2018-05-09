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

    //check wishlist
    if(currentBookLoaded.isBookInWishlist()){ //< duplicate in wishlist map
      if(matchBookWishlist(currentBookLoaded.getKey())!=null){
        removeBookWishlist();
      }
    }
  }

  public Book removeBook(){
    if(isBookLoaded){
      String key = currentBookLoaded.getKey();
      isBookLoaded = false;

      //check wishlist
      if(currentBookLoaded.isBookInWishlist()){ //< removing book from collection but passing it to the other map
        addBookWishlist(currentBookLoaded);
      }
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

  public boolean isSqlFetched(){
    boolean reply = sqlFetched;
    sqlFetched = true;
    return reply;
  }

  public Book matchBookWishlist(String key){
    isBookWishlistLoaded = false;
    if(bookWishlistMap.containsKey(key)){
      currentBookWishlistLoaded = bookWishlistMap.get(key);
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
      isBookWishlistLoaded = false;
      return bookWishlistMap.remove(key);
    }else{
      return null;
    }
  }

  //returns the list of the book objects only in wishlist
  public ArrayList<Book> getBooksWishlist(){
    booksWishlist = new ArrayList<>();
    booksWishlist.addAll(bookWishlistMap.values());
    return new ArrayList<>(booksWishlist);//mporei na exei error itan i retur

  }

  //returns the book in the asked position -- not working probably
  public Book getSingleBookWishlist(int position){
    return booksWishlist.get(position);
  }

  public void fetchBooksFromDB(ArrayList<Book> fetched){
    for(Book b : fetched){
      //check if isCollection or isWishlist item
      if(b.isBookInCollection()){
        bookMap.put(b.getKey(),b); //if its also in wishlist doesn't matter it gets overrided
      }else if(b.isBookInWishlist() && !b.isBookInCollection()) //<prob wont come to 2nd arg)
      {
        bookWishlistMap.put(b.getKey(),b);
      }

    }
  }

}
