package com.example.xrhstos.bookapp;

import com.example.xrhstos.bookapp.main_menu.MainMenu;
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
    private static Bookshelf mInstance;


    private Bookshelf() {

        books = new ArrayList<>();
        currentQuery = "";
    }

    public static synchronized Bookshelf getInstance() {
        if (mInstance == null ) {
            mInstance = new Bookshelf();
        }
        return mInstance;
    }

    public void addBooks(ArrayList<Book> data, MainMenu context){
        if(context.getAPI().equals("Google")){
            if(!currentQuery.equals(MainMenu.query)){
                currentQuery = MainMenu.query;
                books = new ArrayList<>(data);
            }
            books.addAll(data);
            /*
            newBooksFetchedAmount = 0;
            MyApp.getInstance().mainMenu.bitmapRequestCount = 0;
            for (int i=0;i<data.size();i++){
                books.get(books.size()-1).requestBookCover(context);
                MyApp.getInstance().mainMenu.bitmapRequestCount++;
                newBooksFetchedAmount++;
            }
            */
            newBooksFetchedAmount = data.size();
        }else{
            if(!currentQuery.equals(MainMenu.query)){
                currentQuery = MainMenu.query;
                books = new ArrayList<>();
            }
            newBooksFetchedAmount = 0;
            //MyApp.getInstance().mainMenu.bitmapRequestCount = 0;
            for (int i=0;i<data.size();i++){
                if(!data.get(i).getBookCoverURL().equals("https://s.gr-assets.com/assets/nophoto/book/111x148-bcc042a9c91a29c1d680899eff700a03.png")){
                    books.add(data.get(i));
                    //books.get(books.size()-1).requestBookCover(context);
                    newBooksFetchedAmount++;
                }
            }
        }
        //MyApp.getInstance().mainMenu.bitmapMaxCount = newBooksFetchedAmount;

    }

    public void addBooksByISBN(ArrayList<Book> data){
        currentQuery = "";
        books = new ArrayList<>(data);
        newBooksFetchedAmount = data.size();
    }


    public ArrayList<Book> fetchExtraBooksOnly(){
        ArrayList<Book> booksExtra = new ArrayList<>();
        for(int i=books.size()-newBooksFetchedAmount; i<books.size();i++){
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

    public boolean matchBook(Book book){
        String bookKey = book.getKey();
        for(Book b : books){
            if(b.getKey().equals(bookKey)){
                b = book;
                return true;
            }
        }
        return false;
    }

}
