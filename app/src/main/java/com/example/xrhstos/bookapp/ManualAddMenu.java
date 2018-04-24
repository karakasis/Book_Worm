package com.example.xrhstos.bookapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import info.hoang8f.widget.FButton;

/**
 * Created by Xrhstos on 4/24/2018.
 */

public class ManualAddMenu extends AppCompatActivity {

  private ManualAddMenu menu;
  private int currentIndexInflated = -1;
  private FButton barBut;
  private FButton isbnBut;
  private FButton manBut;
  private LinearLayout ll;

  private View adder;

  private TranslateAnimation l2r;

  @Override
  public void onBackPressed(){
    if(currentIndexInflated == -1){
      super.onBackPressed();
    }else{
      adder =  getLayoutInflater().inflate(R.layout.manual_add, null);
      final ObjectAnimator left = ObjectAnimator.ofFloat(adder, "translationX", -1500.0f);
      left.setInterpolator(new AccelerateInterpolator());
      left.setDuration(500);

      left.start();
      left.addListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
          super.onAnimationEnd(animation);
          currentIndexInflated = -1;
        }

        @Override
        public void onAnimationStart(Animator animation) {
          super.onAnimationStart(animation);
          ll.removeViewAt(1);
          ll.addView(manBut,1);
          manBut.startAnimation(l2r);
        }
      });
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.manual_add_menu);

    menu = this;

    ll = findViewById(R.id.framesContainer);
    ll.setOrientation(LinearLayout.VERTICAL);

    LayoutParams layoutParams = new LayoutParams(
        LayoutParams.MATCH_PARENT, 0, 1f);

    layoutParams.setMargins(8, 8, 8, 2);
    barBut = new FButton(this);
    isbnBut = new FButton(this);
    manBut = new FButton(this);
    adder =  getLayoutInflater().inflate(R.layout.manual_add, null);

    barBut.setButtonColor(this.getResources().getColor(R.color.fbutton_color_beige));
    barBut.setAlpha(0.7f);
    barBut.setText("Scan Barcode");
    if (VERSION.SDK_INT >= VERSION_CODES.M) {
      barBut.setTextAppearance(R.style.fragsButtons);
    }else{
      barBut.setTextAppearance(this,R.style.fragsButtons);
    }
    barBut.setShadowEnabled(true);
    barBut.setShadowHeight(5);
    barBut.setCornerRadius(5);
    barBut.setLayoutParams(layoutParams);


    isbnBut.setButtonColor(this.getResources().getColor(R.color.fbutton_color_beige));
    isbnBut.setAlpha(0.7f);
    isbnBut.setText("Add by isbn");
    if (VERSION.SDK_INT >= VERSION_CODES.M) {
      isbnBut.setTextAppearance(R.style.fragsButtons);
    }else{
      isbnBut.setTextAppearance(this,R.style.fragsButtons);
    }
    isbnBut.setShadowEnabled(true);
    isbnBut.setShadowHeight(5);
    isbnBut.setCornerRadius(5);
    isbnBut.setLayoutParams(layoutParams);

    manBut.setButtonColor(this.getResources().getColor(R.color.fbutton_color_pomegranate));
    manBut.setAlpha(0.7f);
    manBut.setText("Add manually");
    if (VERSION.SDK_INT >= VERSION_CODES.M) {
      manBut.setTextAppearance(R.style.fragsButtons);
    }else{
      manBut.setTextAppearance(this,R.style.fragsButtons);
    }
    manBut.setShadowEnabled(true);
    manBut.setShadowHeight(5);
    manBut.setCornerRadius(5);
    manBut.setLayoutParams(layoutParams);

    ll.addView(barBut,0);
    ll.addView(manBut,1);
    ll.addView(isbnBut,2);


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


    barBut.startAnimation(l2r);
    manBut.startAnimation(r2l);
    isbnBut.startAnimation(l2r);

    barBut.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        //v.startAnimation(c2r);
        right.start();
        up.start();
        down.start();
        down.addListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            IntentIntegrator integrator = new IntentIntegrator(menu);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
            integrator.setPrompt("Scan a barcode");
            integrator.setCameraId(0);  // Use a specific camera of the device
            integrator.setBeepEnabled(true);
            integrator.setBarcodeImageEnabled(true);
            integrator.setOrientationLocked(false);
            integrator.initiateScan();
          }
        });
      }
    });
    isbnBut.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        //v.startAnimation(c2r);
        right.start();
        up.start();
        down.start();
        down.addListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {



          }
        });
      }
    });
    manBut.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
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
      }
    });
  }
/*
  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    if(requestCode==251585180){

    }else{
      IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
      if (scanResult != null) {
        // handle scan result
        System.out.println(scanResult.toString());

        showLoading();

        StringRequest grISBN = VolleyNetworking.getInstance(this).goodReadsRequestByISBN(scanResult.getContents());
        VolleyNetworking.getInstance(this).addToRequestQueue(grISBN);
        JsonObjectRequest gISBN = VolleyNetworking.getInstance(this).googleRequestByISBN(scanResult.getContents());
        VolleyNetworking.getInstance(this).addToRequestQueue(gISBN);
      }else{
        notifier.setText("Could not find book from QR");
      }
      // else continue with any other code you need in the method
    }

  }
*/
}
