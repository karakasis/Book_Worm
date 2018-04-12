package com.example.xrhstos.bookapp;

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



    public Bookshelf(){
        books = new ArrayList<>();
    }

    public void setBooks(ArrayList<String[]> b){
        stringBooks = new ArrayList<>(b);
        createBooks();
    }

    //Creates the array of Book objetcs.
    private void createBooks(){
        books = new ArrayList<>();
        for (int i=0;i<stringBooks.size();i++){
            books.add(new Book(Integer.parseInt(stringBooks.get(i)[0]),stringBooks.get(i)[1],stringBooks.get(i)[2],stringBooks.get(i)[3],""));
        }
    }

    //returns the list of the book objects
    public ArrayList<Book> getBooks(){
        return books;
    }

    //returns the book in the asked position
    public Book getSingleBook(int position){
        return books.get(position);
    }

    public ArrayList<String[]> getStringBooks(){
        return stringBooks;
    }
}
