package com.example.xrhstos.bookapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class GalleryBackend extends AppCompatActivity {
    private RecyclerView galleryRecycler;
    private RecyclerView.Adapter gAdapter;
    private RecyclerView.LayoutManager gLayoutManager;
    private ArrayList<Book> myDataset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        galleryRecycler = (RecyclerView) findViewById(R.id.gallery_recycler);

        gLayoutManager = new LinearLayoutManager(this);
        galleryRecycler.setLayoutManager(gLayoutManager);

        gAdapter = new GalleryAdapter(myDataset);
        galleryRecycler.setAdapter(gAdapter);



    }
}
