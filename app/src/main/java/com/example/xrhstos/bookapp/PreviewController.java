package com.example.xrhstos.bookapp;

//import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import java.util.ArrayList;


/**
 * Created by philip on 10/4/2018.
 */

public class PreviewController{

    private MainMenu parent;
    private ArrayList<String[]> tau;  //list of arrays with tittle, author and url of each book

    public PreviewController(GridView gv, MainMenu parent, ArrayList<Book> books){
        tau = new ArrayList<>();
        //String[] dummy = new String[3];
        for (int i=0;i<books.size();i++){
            /*
            dummy[0] = books.get(i).getBookTitle();
            dummy[1] = books.get(i).getAuthor();
            dummy[2] = books.get(i).getBookCoverURL();
            tau.add(dummy);
            dummy = new String[3];
            */
            tau.add(new String[]{
               books.get(i).getBookTitle(),
                books.get(i).getAuthor(),
               books.get(i).getBookCoverURL(),
            });

        }
        this.parent = parent;
        ImageAdapter ia = new ImageAdapter(this ,parent  ,tau);
        gv.setAdapter(ia);

    }

    public MainMenu getParent() {
        return parent;
    }
}