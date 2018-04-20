package com.example.xrhstos.bookapp;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by philip on 12/4/2018.
 *
 * Creates a book object of class Book every time a search(for every book in the results) happens and distributes
 *    these objects where they are needed.
 */

public class Bookshelf {

    private ArrayList<String[]> stringBooks;
    private ArrayList<Book> books;
    private String currentQuery ;
    private int newBooksFetchedAmount;


    public Bookshelf(){
        books = new ArrayList<>();
        currentQuery = "";
    }

    //Creates the array of Book objetcs.
/*
    public void addBooks(ArrayList<String[]> stringBooks){
        this.stringBooks = new ArrayList<>(stringBooks);
        if(!currentQuery.equals(MainMenu.query)){
            currentQuery = MainMenu.query;
            books = new ArrayList<>();
        }
        newBooksFetchedAmount = stringBooks.size();
        for (int i=0;i<stringBooks.size();i++){
            books.add(new Book(Integer.parseInt(stringBooks.get(i)[0]),stringBooks.get(i)[1],stringBooks.get(i)[2],stringBooks.get(i)[3],""));
        }
    }
*/
    public void addBooks(ArrayList<String[]> stringBooks, MainMenu context){
        this.stringBooks = new ArrayList<>(stringBooks);
        if(!currentQuery.equals(MainMenu.query)){
            currentQuery = MainMenu.query;
            books = new ArrayList<>();
        }
        newBooksFetchedAmount = 0;
        for (int i=0;i<stringBooks.size();i++){
            if(!stringBooks.get(i)[3].equals("https://s.gr-assets.com/assets/nophoto/book/111x148-bcc042a9c91a29c1d680899eff700a03.png")){
                //books.add(new Book(Integer.parseInt(stringBooks.get(i)[0]),stringBooks.get(i)[1],stringBooks.get(i)[2],stringBooks.get(i)[3],""));
                books.add(new Book(i,stringBooks.get(i)[1],stringBooks.get(i)[2],stringBooks.get(i)[3],""));
                books.get(books.size()-1).setGoogleID(stringBooks.get(i)[0]);
                books.get(books.size()-1).requestBookCover(context);
                newBooksFetchedAmount++;
            }
          }
    }

    public ArrayList<Book> fetchExtraBooksOnly(){
        ArrayList<Book> booksExtra = new ArrayList<>();
        for(int i=books.size()-stringBooks.size(); i<books.size();i++){
            booksExtra.add(books.get(i));
        }
        return new ArrayList<>(booksExtra);
    }

    public int getNewBooksFetchedAmount(){
        return newBooksFetchedAmount;
    }

    //returns the list of the book objects
    public ArrayList<Book> getBooks(){
        return new ArrayList<>(books);
    }

    //returns the book in the asked position
    public Book getSingleBook(int position){
        Book b = books.get(position);
        return b;
    }

    public ArrayList<String[]> getStringBooks(){
        return stringBooks;
    }
}
