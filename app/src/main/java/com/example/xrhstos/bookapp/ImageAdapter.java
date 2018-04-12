package com.example.xrhstos.bookapp;

/**
 * Created by philip on 10/4/2018.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter  {

    private final LayoutInflater layoutInflator;
    private Context mContext;
    private final PreviewController parentController;
    private ArrayList<String[]> data;

    // Keep all Images in array
    public Integer[] mThumbIds = {
            R.drawable.george_orwell , R.drawable.arktikos_kiklos
    };

    // Constructor
    public ImageAdapter(PreviewController par, Context c, ArrayList<String[]> data){
        parentController = par;
        mContext = c;
        this.data = data;
        layoutInflator = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
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

        ImageView imageView = null;

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        URL url = null;
        try {
            url = new URL(data.get(position)[2]);
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
            titleTag.setText(data.get(position)[0]);  // using dummy data for now

            TextView authorTag = (TextView) gridView
                    .findViewById(R.id.bookPublisherTag);

            // set values into views
            authorTag.setText(data.get(position)[1]);  // using dummy data for now

            imageView = (ImageView) gridView
                    .findViewById(R.id.bookImageTag);

            setImage(imageView, data.get(position)[2],position);

            imageView.setClipToOutline(true);
            imageView.setScaleType(ScaleType.FIT_CENTER);
            //imageView.setLayoutParams(new GridView.LayoutParams(imageView.getWidth(), imageView.getHeight()));


        }else {
            gridView = (View) convertView;
        }
        return gridView;
    }

    private void setImage(final ImageView container, final String url,final int position){
        Picasso.with(mContext)
            .load(String.valueOf(url))
            .into(new Target() {
                @Override
                public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from) {
                /* Save the bitmap or do something with it here */

                    //Set it in the ImageView
                    if(url.equals("https://s.gr-assets.com/assets/nophoto/book/111x148-bcc042a9c91a29c1d680899eff700a03.png")){
                        //container.setImageResource(R.drawable.placeholder_book);
                        container.setBackgroundResource(R.drawable.placeholder_book);
                    }else{
                        container.setImageBitmap(bitmap);
                    }
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {}

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                    System.out.println("Failed loading " + url);
                }
            });

        container.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                parentController.getParent().bookClick(position);
            }

        });
    }

}


