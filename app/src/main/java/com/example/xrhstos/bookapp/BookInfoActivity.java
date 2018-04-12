package com.example.xrhstos.bookapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by Xrhstos on 4/10/2018.
 */

public class BookInfoActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.book_info);

    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    String title = extras.getString("title");
    String author = extras.getString("author");
    final String url = extras.getString("url");

    final ImageView iv = (ImageView) findViewById(R.id.bookImage);
    Picasso.with(this)
        .load(String.valueOf(url))
        .into(new Target() {
          @Override
          public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from) {
                /* Save the bitmap or do something with it here */

            //Set it in the ImageView
            if(url.equals("https://s.gr-assets.com/assets/nophoto/book/111x148-bcc042a9c91a29c1d680899eff700a03.png")){
              iv.setImageResource(R.drawable.placeholder_book);
            }else{
              iv.setImageBitmap(bitmap);
            }
          }

          @Override
          public void onPrepareLoad(Drawable placeHolderDrawable) {}

          @Override
          public void onBitmapFailed(Drawable errorDrawable) {

            System.out.println("Failed to load : " + url);
          }
        });

    TextView tv = (TextView) findViewById(R.id.bookTitle);
    tv.setText(title);
    TextView tv2 = (TextView) findViewById(R.id.bookPublisher);
    tv2.setText(author);

  }

}
