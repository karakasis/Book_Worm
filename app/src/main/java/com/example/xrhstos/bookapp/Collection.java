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
  private String currentQuery ;
  private static Collection mInstance;

  private Collection() {

    books = new ArrayList<>();
    bookMap = new HashMap<>();
    currentQuery = "";
    isBookLoaded = false;
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
    ArrayList<Book> retur = new ArrayList<>();
    retur.addAll(bookMap.values());
    return new ArrayList<>(retur);
  }

  //returns the book in the asked position -- not working probably
  public Book getSingleBook(int position){
    return books.get(position);
  }


}
