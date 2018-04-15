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

    //Intent intent = getIntent();
    Bundle data = getIntent().getExtras();
    Book extras = (Book) data.getParcelable("bookObject");
    //Book extras = (Book)intent.getSerializableExtra("bookObject");
    String title = extras.getBookTitle();
    String author = extras.getAuthor();


    ImageView iv = (ImageView) findViewById(R.id.bookImage);
    iv.setImageBitmap(extras.getBookCover());


    TextView tv = (TextView) findViewById(R.id.bookTitle);
    tv.setText(title);
    TextView tv2 = (TextView) findViewById(R.id.bookPublisher);
    tv2.setText(author);

  }

}
