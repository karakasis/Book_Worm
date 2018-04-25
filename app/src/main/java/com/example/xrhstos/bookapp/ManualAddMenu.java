package com.example.xrhstos.bookapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewStub;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.csot.fliplayout.lib.FlipLayout;
import com.example.xrhstos.bookapp.grid.GridAdapter;
import com.example.xrhstos.bookapp.grid.GridAdapter3;
import com.example.xrhstos.bookapp.grid.WrapContentGridLayoutManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import info.hoang8f.widget.FButton;
import java.util.ArrayList;

/**
 * Created by Xrhstos on 4/24/2018.
 */

public class ManualAddMenu extends AppCompatActivity {

  private ManualAddMenu menu;
  private int currentIndexInflated = -1;

  private FButton barBut;
  private FButton isbnBut;
  private FButton manBut;

  FlipLayout fliplayout1;
  FlipLayout fliplayout2;
  FlipLayout fliplayout3;

  //VIEW1
  private int scannerCounter = 0;
  private ArrayList<Book> booksFromScanner;
  //VIEW2
  EditText bookTitle, publisher;
  RatingBar ratingBar;
  float ratingValue;
  private FButton addBook;
  //VIEW3
  EditText isbnText;
  private FButton searchByIsbn;

  //COMBINED VIEWS
  private RecyclerView recyclerView1;
  private RecyclerView recyclerView2;
  private RecyclerView recyclerView3;
  private WrapContentGridLayoutManager glm1;
  private WrapContentGridLayoutManager glm2;
  private WrapContentGridLayoutManager glm3;
  private GridAdapter3 ga1;
  private GridAdapter3 ga2;
  private GridAdapter3 ga3;

  @Override
  public void onBackPressed(){
    if(currentIndexInflated == -1){
      super.onBackPressed();
    }else{
      if(currentIndexInflated == 0){
        fliplayout1.showChild(0,true);
      }else if(currentIndexInflated == 1){
        fliplayout2.showChild(0,true);
      }else if(currentIndexInflated == 2){
        fliplayout3.showChild(0,true);
      }
      currentIndexInflated = -1;
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.manual);
    menu = this;
    MyApp.getInstance().manualAddMenu = this;

    fliplayout1 = findViewById(R.id.fliplayout1);
    barBut = findViewById(R.id.barcodeButton);
    barBut.setButtonColor(this.getResources().getColor(R.color.fbutton_color_beige));

    fliplayout2 = findViewById(R.id.fliplayout2);
    manBut = findViewById(R.id.manualButton);
    manBut.setButtonColor(this.getResources().getColor(R.color.fbutton_color_pomegranate));

    fliplayout3 = findViewById(R.id.fliplayout3);
    isbnBut = findViewById(R.id.isbnButton);
    isbnBut.setButtonColor(this.getResources().getColor(R.color.fbutton_color_beige));


    bookTitle = (EditText) findViewById(R.id.book);//bookTitle editText
    publisher = (EditText) findViewById(R.id.bookPublisher);//publisher editText
    ratingBar = (RatingBar) findViewById(R.id.ratingBar);
    ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){
      @Override
      public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser){
        ratingValue = rating;
        ratingBar.setRating(rating);

      }
    });
    addBook = findViewById(R.id.addManualButtonYES);

    isbnText = (EditText) findViewById(R.id.isbnEditText);//bookTitle editText
    searchByIsbn = findViewById(R.id.findIsbnButtonYES);

    //recyclers:

    View view1 = findViewById(R.id.r_view_1);
    recyclerView1 = view1.findViewById(R.id.grid_view);

    glm1 = new WrapContentGridLayoutManager(
        this,
        calculateNoOfColumns(this)
    );
    recyclerView1.setLayoutManager(glm1);

    View view2 = findViewById(R.id.r_view_2);
    recyclerView2 = view2.findViewById(R.id.grid_view);

    glm2 = new WrapContentGridLayoutManager(
        this,
        calculateNoOfColumns(this)
    );
    recyclerView2.setLayoutManager(glm2);

    View view3 = findViewById(R.id.r_view_3);
    recyclerView3 = view3.findViewById(R.id.grid_view);

    glm3 = new WrapContentGridLayoutManager(
        this,
        calculateNoOfColumns(this)
    );
    recyclerView3.setLayoutManager(glm3);



    barBut.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        //v.startAnimation(c2r);
        /*
        right.start();
        up.start();
        down.start();
        down.addListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);

          }
        });
        */
        //fliplayout1.showChild(1,true);

        IntentIntegrator integrator = new IntentIntegrator(menu);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("Scan a barcode");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();

        //fliplayout1.showNextChild(); <
        if(currentIndexInflated == 1){
          fliplayout2.showChild(0,true);
          fliplayout3.showChild(0,false);
        }else if(currentIndexInflated == 2){
          fliplayout2.showChild(0,false);
          fliplayout3.showChild(0,true);
        }
        currentIndexInflated = 0;
      }
    });
    isbnBut.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        //v.startAnimation(c2r);
        /*
        right.start();
        up.start();
        down.start();
        down.addListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {



          }
        });
*/
        //fliplayout3.showChild(1,true);
        fliplayout3.showNextChild();
        if(currentIndexInflated == 0){
          fliplayout1.showChild(0,true);
          fliplayout2.showChild(0,false);
        }else if(currentIndexInflated == 1){
          fliplayout1.showChild(0,false);
          fliplayout2.showChild(0,true);
        }
        currentIndexInflated = 2;
      }
    });
    manBut.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        /*
        right.start();
        right.addListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);

            currentIndexInflated = 1;
          }

          @Override
          public void onAnimationStart(Animator animation) {
            super.onAnimationStart(animation);
            View adder =  getLayoutInflater().inflate(R.layout.manual_add, null);
            ll.removeViewAt(1);
            ll.addView(adder,1);
            //adder.startAnimation(l2r);
          }
        });
        */
        //fliplayout2.showChild(1,true);
        fliplayout2.showNextChild();
        if(currentIndexInflated == 0){
          fliplayout1.showChild(0,true);
          fliplayout3.showChild(0,false);
        }else if(currentIndexInflated == 2){
          fliplayout1.showChild(0,false);
          fliplayout3.showChild(0,true);
        }
        currentIndexInflated = 1;
      }
    });

    addBook.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        addBookManually();
        fliplayout2.showNextChild();
      }
    });

    searchByIsbn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        addBookByISBN();
        //fliplayout3.showNextChild();
      }
    });
  }

  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
   IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
      if (scanResult != null && scanResult.getContents()!=null) {
        // handle scan result
        System.out.println(scanResult.toString());

        showLoading(); // swap with flipchild

        VolleyNetworking.getInstance(this).getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
          @Override
          public boolean apply(Request<?> request) {
            return true;
          }
        });

        StringRequest grISBN = VolleyNetworking.getInstance(MyApp.getContext()).goodReadsRequestByISBN(scanResult.getContents());
        VolleyNetworking.getInstance(this).addToRequestQueue(grISBN);
        JsonObjectRequest gISBN = VolleyNetworking.getInstance(MyApp.getContext()).googleRequestByISBN(scanResult.getContents());
        VolleyNetworking.getInstance(this).addToRequestQueue(gISBN);
      }else{
        //notifier.setText("Could not find book from QR");

      }
      // else continue with any other code you need in the method

  }

  public void updateByISBN(ArrayList<Book> bookData){
    if(scannerCounter == 0){
      booksFromScanner = new ArrayList<>();
    }
    if(bookData!=null){
      booksFromScanner.addAll(bookData);
    }
    scannerCounter++;

    if(scannerCounter==2){
      scannerCounter = 0;
      if(currentIndexInflated == 0){
        ga1 = new GridAdapter3(this,booksFromScanner);
        fliplayout1.showChild(2,false);
        recyclerView1.setAdapter(ga1);
      }else if(currentIndexInflated == 2){
        ga3 = new GridAdapter3(this,booksFromScanner);
        fliplayout3.showChild(3,false);
        recyclerView3.setAdapter(ga3);
      }

    }
  }

  private void addBookManually(){

    String title = bookTitle.getText().toString();
    String[] bookPublisher = new String[1];
    bookPublisher[0] = publisher.getText().toString();
    Book book = new Book(null,title,bookPublisher,null);

    int placeholder = R.drawable.placeholder;
    Drawable myIcon = getResources().getDrawable( placeholder );
    book.setBookCover(((BitmapDrawable)myIcon).getBitmap());

    ArrayList<Book> list = new ArrayList<>();
    list.add(book);
    ga2 = new GridAdapter3(this,list);
    recyclerView2.setAdapter(ga2);

  }

  private void addBookByISBN(){
    String isbn = isbnText.getText().toString();
    String replaced = isbn.replace("-", "");
    String replaced2 = replaced.replace(" ", "");
    showLoading(); // swap with flipchild

    VolleyNetworking.getInstance(this).getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
      @Override
      public boolean apply(Request<?> request) {
        return true;
      }
    });

    StringRequest grISBN = VolleyNetworking.getInstance(MyApp.getContext()).goodReadsRequestByISBN(replaced2);
    VolleyNetworking.getInstance(this).addToRequestQueue(grISBN);
    JsonObjectRequest gISBN = VolleyNetworking.getInstance(MyApp.getContext()).googleRequestByISBN(replaced2);
    VolleyNetworking.getInstance(this).addToRequestQueue(gISBN);


  }

  private static int calculateNoOfColumns(Context context) {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
    int noOfColumns = (int) (dpWidth / 120);
    return noOfColumns;
  }

  public void bookClick(int pos){
    if(currentIndexInflated == 0){

    }else if(currentIndexInflated == 1){
      Book b = ga2.getDataSet().get(pos);
      Intent intent = new Intent(this, BookInfoActivity.class);
      //this will pass the book object itself so any changes will be made to the Book
      //class as well
      intent.putExtra("manual", b);
      startActivity(intent);
    }else if(currentIndexInflated == 2){

    }
  }

  private void showLoading(){
    if(currentIndexInflated == 0){
      fliplayout1.showChild(1,false);
    }else if(currentIndexInflated == 2){
      fliplayout3.showChild(2,false);
    }
  }
}


    /*
    //button slide r2left
    final TranslateAnimation r2l = new TranslateAnimation(1500.0f, 0.0f, 0.0f,
        0.0f); // new TranslateAnimation(xFrom,xTo, yFrom,yTo)
    r2l.setDuration(1000); // animation duration
    r2l.setFillAfter(true);

    //button slide l2right
    l2r = new TranslateAnimation(-1500.0f, 0.0f, 0.0f,
        0.0f); // new TranslateAnimation(xFrom,xTo, yFrom,yTo)
    l2r.setDuration(1000); // animation duration
    l2r.setFillAfter(true);


    final ObjectAnimator right = ObjectAnimator.ofFloat(manBut, "translationX", 1500.0f);

    right.setInterpolator(new AccelerateInterpolator());
    right.setDuration(500);

    final ObjectAnimator up = ObjectAnimator.ofFloat(barBut, "translationY", -1500.0f);

    up.setInterpolator(new AccelerateInterpolator());
    up.setDuration(500);

    final ObjectAnimator down = ObjectAnimator.ofFloat(isbnBut, "translationY", 1500.0f);

    down.setInterpolator(new AccelerateInterpolator());
    down.setDuration(500);


    //barBut.startAnimation(l2r);
    //manBut.startAnimation(r2l);
    //isbnBut.startAnimation(l2r);
*/