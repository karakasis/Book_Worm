package com.example.xrhstos.bookapp;

/**
 * Created by philip on 10/4/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter  {

    private final LayoutInflater layoutInflator;
    private Context mContext;
    private final PreviewController parentController;
    private ArrayList<String> urls;

    // Keep all Images in array
    public Integer[] mThumbIds = {
            R.drawable.george_orwell , R.drawable.arktikos_kiklos
    };

    // Constructor
    public ImageAdapter(PreviewController par, Context c, ArrayList<String> urls){
        parentController = par;
        mContext = c;
        this.urls = urls;
        layoutInflator = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        URL url = null;
        try {
            url = new URL(urls.get(position));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (convertView == null) {
            // get layout from xml file
            gridView = inflater.inflate(R.layout.grid_inflater, null);


            // pull views
            TextView titleTag = (TextView) gridView
                    .findViewById(R.id.bookTitleTag);

            // set values into views
            titleTag.setText("test");  // using dummy data for now

            TextView authorTag = (TextView) gridView
                    .findViewById(R.id.bookPublisherTag);

            // set values into views
            authorTag.setText("test");  // using dummy data for now

            final ImageView imageView = (ImageView) gridView
                    .findViewById(R.id.bookImageTag);

            Picasso.with(mContext)
                    .load(String.valueOf(url))
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from) {
                /* Save the bitmap or do something with it here */

                            //Set it in the ImageView
                            if(urls.get(position).equals("https://s.gr-assets.com/assets/nophoto/book/111x148-bcc042a9c91a29c1d680899eff700a03.png")){
                                imageView.setImageResource(R.drawable.placeholder_book);
                            }else{
                                imageView.setImageBitmap(bitmap);
                            }
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {}

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                            System.out.println("Failed" + position);
                        }
                    });

            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    parentController.getParent().bookClick(position);
                }

            });

            imageView.setClipToOutline(true);
            imageView.setScaleType(ScaleType.FIT_CENTER);
            imageView.setLayoutParams(new GridView.LayoutParams(imageView.getWidth(), imageView.getHeight()));

            return imageView;

        }

        return null;


    }

}


