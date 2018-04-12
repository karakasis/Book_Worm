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
import org.w3c.dom.Text;

public class ImageAdapter extends BaseAdapter  {

    private final LayoutInflater layoutInflator;
    private Context mContext;
    private final PreviewController parentController;
    private ArrayList<String[]> data;

    // Keep all Images in array
    public Integer[] mThumbIds = {
            R.drawable.george_orwell , R.drawable.arktikos_kiklos
    };
    private NewHolder holder;

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

        URL url = null;
        try {
            url = new URL(data.get(position)[2]);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (convertView == null) {
            // get layout from xml file
            LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.grid_inflater, null);

            holder = new NewHolder();

            holder.titleView = (TextView) convertView
                .findViewById(R.id.bookTitleTag);
            holder.authorView = (TextView) convertView
                .findViewById(R.id.bookPublisherTag);
            holder.bookView = (ImageView) convertView
                .findViewById(R.id.bookImageTag);

            //imageView.setLayoutParams(new GridView.LayoutParams(imageView.getWidth(), imageView.getHeight()));

            convertView.setTag(holder);

        }else{
            holder = (NewHolder) convertView.getTag();
        }

        setImage(holder.bookView, data.get(position)[2],position);
        holder.titleView.setText(data.get(position)[0]);
        holder.authorView.setText(data.get(position)[1]);
        holder.bookView.setClipToOutline(true);
        holder.bookView.setScaleType(ScaleType.FIT_CENTER);
        return convertView;
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


