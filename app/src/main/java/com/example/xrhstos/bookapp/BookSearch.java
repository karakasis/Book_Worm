/*
 * Copyright (C) 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.xrhstos.bookapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.w3c.dom.Text;

/**
 * https://github.com/google-developer-training/android-fundamentals/tree/master/WhoWroteIt
 *
 * The WhoWroteIt app query's the Book Search API for Books based
 * on a user's search.
 */
public class BookSearch extends AppCompatActivity {

  // Variables for the search input field, and results TextViews.
  private EditText mBookInput;
  //private TextView mTitleText;
  //private TextView mAuthorText;
  private LinearLayout mCollectionLayout;


  /**
   * Initializes the activity.
   *
   * @param savedInstanceState The current state data
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.search_layout);

    // Initialize all the view variables.
    mBookInput = (EditText)findViewById(R.id.bookInput);
    //mTitleText = (TextView)findViewById(R.id.titleText);
    //mAuthorText = (TextView)findViewById(R.id.authorText);
    mCollectionLayout = findViewById(R.id.collectionLayout);
  }

  /**
   * Gets called when the user pushes the "Search Books" button
   *
   * @param view The view (Button) that was clicked.
   */
  public void searchBooks(View view) {
    // Get the search string from the input field.
    String queryString = mBookInput.getText().toString();

    // Hide the keyboard when the button is pushed.
    InputMethodManager inputManager = (InputMethodManager)
        getSystemService(Context.INPUT_METHOD_SERVICE);
    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
        InputMethodManager.HIDE_NOT_ALWAYS);

    // Check the status of the network connection.
    ConnectivityManager connMgr = (ConnectivityManager)
        getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

    // If the network is active and the search field is not empty, start a FetchBook AsyncTask.
    if (networkInfo != null && networkInfo.isConnected() && queryString.length()!=0) {
      TextView notifier = (TextView) findViewById(R.id.resultNotify);
      notifier.setText("Results: ");
      new FetchBook(this, mCollectionLayout , mBookInput).execute(queryString);

    }
    // Otherwise update the TextView to tell the user there is no connection or no search term.
    else {
      if (queryString.length() == 0) {
        TextView notifier = (TextView) findViewById(R.id.resultNotify);
        notifier.setText(R.string.no_search_term);
      } else {
        TextView notifier = (TextView) findViewById(R.id.resultNotify);
        notifier.setText(R.string.no_network);
      }
    }
  }

  public void update(ArrayList<String[]> bookData){
    ArrayList<String> urls = new ArrayList<>();
    for(String[] arr : bookData){
      urls.add(arr[3]);
    }
    Intent intent = new Intent(this, MainActivity.class);
    intent.putStringArrayListExtra("cover_urls", urls);
    startActivity(intent);
  }
}