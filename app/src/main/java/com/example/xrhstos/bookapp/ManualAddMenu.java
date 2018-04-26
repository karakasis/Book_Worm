package com.example.xrhstos.bookapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
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
  private int orientation;

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
  private boolean useSmallInflater = false;

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
    orientation = this.getResources().getConfiguration().orientation;
    if(orientation == Configuration.ORIENTATION_PORTRAIT){
      useSmallInflater = true;
    }else{
      useSmallInflater = false;
    }

    VolleyNetworking.refresh(MyApp.getContext());




    fliplayout1 = findViewById(R.id.fliplayout1);
    barBut = findViewById(R.id.barcodeButton);
    barBut.setButtonColor(this.getResources().getColor(R.color.fbutton_color_beige));

    fliplayout2 = findViewById(R.id.fliplayout2);
    manBut = findViewById(R.id.manualButton);
    manBut.setButtonColor(this.getResources().getColor(R.color.fbutton_color_pomegranate));

    fliplayout3 = findViewById(R.id.fliplayout3);
    isbnBut = findViewById(R.id.isbnButton);
    isbnBut.setButtonColor(this.getResources().getColor(R.color.fbutton_color_beige));

    if(savedInstanceState!=null){
      currentIndexInflated = savedInstanceState.getInt("INFLATED_INDEX");
      int child = savedInstanceState.getInt("VISIBLE_CHILD");
      if(currentIndexInflated == 0){
        fliplayout1.showChild(child,false);
      }else if(currentIndexInflated == 1){
        fliplayout2.showChild(child,false);
      }else if(currentIndexInflated == 2){
        fliplayout3.showChild(child,false);
      }
    }

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
        calculateNoOfColumns()
    );
    recyclerView1.setLayoutManager(glm1);

    View view2 = findViewById(R.id.r_view_2);
    recyclerView2 = view2.findViewById(R.id.grid_view);

    glm2 = new WrapContentGridLayoutManager(
        this,
        calculateNoOfColumns()
    );
    recyclerView2.setLayoutManager(glm2);

    View view3 = findViewById(R.id.r_view_3);
    recyclerView3 = view3.findViewById(R.id.grid_view);

    glm3 = new WrapContentGridLayoutManager(
        this,
        calculateNoOfColumns()
    );
    recyclerView3.setLayoutManager(glm3);



    barBut.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
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
        String title = bookTitle.getText().toString().trim();
        String author = publisher.getText().toString().trim();

        if(title.isEmpty())
        {
          //bookTitle.setHint("Enter book title");//it gives user to hint
          bookTitle.setError("Enter book title");//it gives user to info message
          if(author.isEmpty()){
            publisher.setHint("Enter book author");
            publisher.setError("Enter book author");
          }
        }else if(author.isEmpty()){
          publisher.setHint("Enter book author");
          publisher.setError("Enter book author");
          if(title.isEmpty()) {
            bookTitle.setHint("Enter book title");
            bookTitle.setError("Enter book title");
          }
        }else{
          addBookManually(title,author);
          //fliplayout2.showNextChild();
        }
      }
    });

    searchByIsbn.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        String isbn = isbnText.getText().toString();
        String replaced = isbn.replace("-", "");
        String replaced2 = replaced.replace(" ", "");
        if(replaced2.length() == 10 || replaced2.length() == 13){
          addBookByISBN(replaced2);
        }else{
          isbnText.setHint("Enter a valid ISBN");
          bookTitle.setError("Enter a valid ISBN");
        }

        //fliplayout3.showNextChild();
      }
    });
  }

  @Override
  protected void onSaveInstanceState(Bundle bundle) {
    super.onSaveInstanceState(bundle);
    bundle.putInt("INFLATED_INDEX",currentIndexInflated);
    if(currentIndexInflated == 0){
      bundle.putInt("VISIBLE_CHILD",fliplayout1.getVisibleChild());
    }else if(currentIndexInflated == 1){

      bundle.putInt("VISIBLE_CHILD",fliplayout2.getVisibleChild());
    }else if(currentIndexInflated == 2){

      bundle.putInt("VISIBLE_CHILD",fliplayout3.getVisibleChild());
    }
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
        //notifier.setText(R.string.couldNotQR);//Could not get info on QR

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
        ga1 = new GridAdapter3(this,booksFromScanner,useSmallInflater);
        fliplayout1.showChild(2,false);
        recyclerView1.setAdapter(ga1);
      }else if(currentIndexInflated == 2){
        ga3 = new GridAdapter3(this,booksFromScanner,useSmallInflater);
        fliplayout3.showChild(3,false);
        recyclerView3.setAdapter(ga3);
      }

    }
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
  }

  private void addBookManually(String title,String author){
    String[] bookPublisher = new String[1];
    bookPublisher[0] = author;
    Book book = new Book(title+"-"+bookPublisher[0],title,bookPublisher,null);

    //hotfix for bookinfoactivity to bypass api search
    book.setDescription("");
    //
    int placeholder = R.drawable.placeholder;
    Drawable myIcon = getResources().getDrawable( placeholder );
    book.setBookCover(((BitmapDrawable)myIcon).getBitmap());

    ArrayList<Book> list = new ArrayList<>();
    list.add(book);
    ga2 = new GridAdapter3(this,list,useSmallInflater);


    fliplayout2.showNextChild();
    recyclerView2.setAdapter(ga2);

    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

  }

  private void addBookByISBN(String isbn){
    showLoading(); // swap with flipchild

    VolleyNetworking.getInstance(this).getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
      @Override
      public boolean apply(Request<?> request) {
        return true;
      }
    });

    StringRequest grISBN = VolleyNetworking.getInstance(MyApp.getContext()).goodReadsRequestByISBN(isbn);
    VolleyNetworking.getInstance(this).addToRequestQueue(grISBN);
    JsonObjectRequest gISBN = VolleyNetworking.getInstance(MyApp.getContext()).googleRequestByISBN(isbn);
    VolleyNetworking.getInstance(this).addToRequestQueue(gISBN);


  }

  private int calculateNoOfColumns() {

    DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
    float dpWidth;
    int noOfColumns;
    if (useSmallInflater) {
      dpWidth = displayMetrics.widthPixels / displayMetrics.density;
      noOfColumns = (int) (dpWidth / 96); //small inflater
    } else {
      dpWidth = (displayMetrics.widthPixels/3) / displayMetrics.density; // divided by 3 to get
      noOfColumns = (int) (dpWidth / 120); //normal inflater       // actual size
    }
    return noOfColumns;
  }

  public void bookClick(int pos){
    if(currentIndexInflated == 0){
      Book b = ga1.getDataSet().get(pos);
      Intent intent = new Intent(this, BookInfoActivity.class);
      //this will pass the book object itself so any changes will be made to the Book
      //class as well
      intent.putExtra("manual", b);
      startActivity(intent);
    }else if(currentIndexInflated == 1){
      Book b = ga2.getDataSet().get(pos);
      Intent intent = new Intent(this, BookInfoActivity.class);
      //this will pass the book object itself so any changes will be made to the Book
      //class as well
      intent.putExtra("manual", b);
      startActivity(intent);
    }else if(currentIndexInflated == 2){
      Book b = ga3.getDataSet().get(pos);
      Intent intent = new Intent(this, BookInfoActivity.class);
      //this will pass the book object itself so any changes will be made to the Book
      //class as well
      intent.putExtra("manual", b);
      startActivity(intent);
    }
  }

  private void showLoading(){
    LockActivityOrientation.lockActivityOrientation(this);
    if(currentIndexInflated == 0){
      fliplayout1.showChild(1,false);
    }else if(currentIndexInflated == 2){
      fliplayout3.showChild(2,false);
    }
  }
}
