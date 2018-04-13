package com.example.xrhstos.bookapp;

//import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import com.example.xrhstos.bookapp.grid.GridAdapter;
import java.util.ArrayList;


/**
 * Created by philip on 10/4/2018.
 */

public class PreviewController{

    private final MainMenu parent;
    private ArrayList<String[]> tau;  //list of arrays with tittle, author and url of each book

    public PreviewController(RecyclerView gv, final MainMenu parent, ArrayList<Book> books){
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
        GridAdapter gridAdapter = new GridAdapter(this,parent,books);
        gv.setAdapter(gridAdapter);
        gv.setLayoutManager(new GridLayoutManager(
            parent,
            calculateNoOfColumns(parent)
        ));

        /*
        ImageAdapter ia = new ImageAdapter(this ,parent  ,tau);
        gv.setAdapter(ia);
        gv.setOnScrollListener(new AbsListView.OnScrollListener(){

            @Override
            public void onScroll(AbsListView view,
                int firstVisibleItem, int visibleItemCount,
                int totalItemCount) {
                //Algorithm to check if the last item is visible or not
                final int lastItem = firstVisibleItem + visibleItemCount;
                if(lastItem == totalItemCount){
                    // here you have reached end of list, load more data
                    parent.requestMoreResults();
                }
            }
            @Override
            public void onScrollStateChanged(AbsListView view,int scrollState) {
                //blank, not required in your case
            }
        });

*/


    }

    public MainMenu getParent() {
        return parent;
    }

    private static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / 120);
        return noOfColumns;
    }
}