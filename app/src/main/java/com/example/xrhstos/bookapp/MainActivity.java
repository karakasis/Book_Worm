package com.example.xrhstos.bookapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by philip on 10/4/2018.
 */

public class MainActivity extends AppCompatActivity implements Fragment1.ActivityCommunicator {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /*
    public void onPhilosopherClick(String p) {
        View quoteFragment = findViewById(R.id.fragment2);
        boolean mDualPane = quoteFragment != null && quoteFragment.getVisibility() == View.VISIBLE;
        if (mDualPane) {
            Fragment2 f = (Fragment2) getSupportFragmentManager().findFragmentById(R.id.fragment2);
            f.showQuote(p);
        } else {
            Intent intent = new Intent(this, QuoteActivity.class);
            intent.putExtra("philospher", p);
            startActivity(intent);
        }

    }
    */

    public void passDataToActivity(String s){

    }
}