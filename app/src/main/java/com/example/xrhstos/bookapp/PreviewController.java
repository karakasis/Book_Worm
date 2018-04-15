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
import com.example.xrhstos.bookapp.grid.EndlessScrollListener;
import com.example.xrhstos.bookapp.grid.GridAdapter;
import java.util.ArrayList;


/**
 * Created by philip on 10/4/2018.
 */

public class PreviewController{

    private final MainMenu parent;
    private GridAdapter gridAdapter;
    private RecyclerView recyclerView;
    private GridLayoutManager glm;
    private EndlessScrollListener esl;


    public PreviewController(RecyclerView rv, final MainMenu parent){

        this.parent = parent;
        recyclerView = rv;

         glm = new GridLayoutManager(
            parent,
            calculateNoOfColumns(parent)
        );
        recyclerView.setLayoutManager(glm);

        esl =new EndlessScrollListener(glm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                loadNextDataFromApi(page);
            }
        };

        recyclerView.addOnScrollListener(esl);

    }

    public void setData(ArrayList<Book> books){

        if(gridAdapter!=null){
            gridAdapter.getDataSet().clear();
            gridAdapter.notifyDataSetChanged();
            esl.resetState();
        }
        gridAdapter = new GridAdapter(this,parent,books);

        recyclerView.setAdapter(gridAdapter);
    }

    private void loadNextDataFromApi(int page) {
        MainMenu.loadingData = true;
        parent.requestMoreResults();
    }

    public void acceptResponseFromMainThread(int newDataSize){
        final int curSize = gridAdapter.getItemCount() - newDataSize;
        final int finSize = gridAdapter.getItemCount();

        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                gridAdapter.notifyItemRangeInserted(curSize, finSize);
                MainMenu.loadingData = false;
            }
        });
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