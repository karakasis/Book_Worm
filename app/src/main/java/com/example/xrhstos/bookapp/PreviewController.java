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
    private GridAdapter gridAdapter;
    private RecyclerView recyclerView;


    public PreviewController(RecyclerView rv, final MainMenu parent){

        this.parent = parent;
        recyclerView = rv;



    }

    public void setData(ArrayList<Book> books){

        gridAdapter = new GridAdapter(this,parent,books);
        gridAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(gridAdapter);

        recyclerView.setLayoutManager(new GridLayoutManager(
            parent,
            calculateNoOfColumns(parent)
        ));

        recyclerView.invalidate();
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