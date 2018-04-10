package com.example.xrhstos.bookapp;

/**
 * Created by philip on 10/4/2018.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter  {
    private Context mContext;

    private ArrayList<String> urls;

    // Keep all Images in array
    public Integer[] mThumbIds = {
            R.drawable.george_orwell , R.drawable.arktikos_kiklos
    };

    // Constructor
    public ImageAdapter(Context c, ArrayList<String> urls){

        mContext = c;
        this.urls = urls;
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
        //System.out.println(position);
        final ImageView imageView = new ImageView(mContext);
        URL url = null;
        try {
            url = new URL(urls.get(position));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Picasso.with(mContext)
            .load(String.valueOf(url))
            .into(new Target() {
                @Override
                public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from) {
                /* Save the bitmap or do something with it here */

                    //Set it in the ImageView
                    imageView.setImageBitmap(bitmap);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {}

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                    System.out.println("Failed" + position);
                }
            });
        //Load bitmaps
        /*


        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        //imageView.setImageResource(mThumbIds[position]);
        imageView.setScaleType(ScaleType.FIT_CENTER);
        imageView.setLayoutParams(new GridView.LayoutParams(400, 550));
        return imageView;
    }

}


