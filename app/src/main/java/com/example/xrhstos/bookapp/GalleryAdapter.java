package com.example.xrhstos.bookapp;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by philip on 20/4/2018.
 */

public class GalleryAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<Book> myBooks;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView bookView;
        public ViewHolder(ImageView v) {
            super(v);
            bookView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public GalleryAdapter(ArrayList<Book> myDataset) {
        myBooks = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        ImageView v = (ImageView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_image, parent, false);
        //...
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.bookView.setImageBitmap(myBooks[position].getBookCover());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return myBooks.size();
    }

}
