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

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * https://github.com/google-developer-training/android-fundamentals/tree/master/WhoWroteIt
 *
 * AsyncTask implementation that opens a network connection and
 * query's the Book Service API.
 */
public class FetchBook extends AsyncTask<String,Void,String>{

  // Variables for the search input field, and results TextViews
  @SuppressLint("StaticFieldLeak")
  private EditText mBookInput;
  @SuppressLint("StaticFieldLeak")
  private TextView mTitleText;
  @SuppressLint("StaticFieldLeak")
  private TextView mAuthorText;

  private ArrayList<String[]> collection;
  private LinearLayout mCollectionLayout;
  private MainMenu parent;

  // Class name for Log tag
  private static final String LOG_TAG = FetchBook.class.getSimpleName();

  // Constructor providing a reference to the views in BookSearch
  public FetchBook(MainMenu c) {
    //this.mTitleText = titleText;
    //this.mAuthorText = authorText;
    parent = c;
  }


  /**
   * Makes the Books API call off of the UI thread.
   *
   * @param params String array containing the search data.
   * @return Returns the JSON string from the Books API or
   *         null if the connection failed.
   */
  @Override
  protected String doInBackground(String... params) {

    // Get the search string
    String queryString = params[0];


    // Set up variables for the try block that need to be closed in the finally block.
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String bookJSONString = null;

    // Attempt to query the Books API.
    try {
      // Base URI for the Books API.
      //final String BOOK_BASE_URL =  "https://www.googleapis.com/books/v1/volumes?";

      final String BOOK_BASE_URL = "https://www.goodreads.com/search/index.xml";
      final String QUERY_PARAM = "q"; // The query text to match against book title, author,
      // and ISBN fields. Supports boolean operators and phrase searching.
      final String QUERY_PAGE = "page"; //Which page to return (default 1, optional)
      final String DEVELOPER_KEY = "key"; //dev key required
      final String SEARCH_FIELD = "search(field)"; //Field to search, one of 'title', 'author', or 'all' (default is 'all')
      final String MAX_RESULTS = "maxResults"; // Parameter that limits search results.
      final String PRINT_TYPE = "printType"; // Parameter to filter by print type.

      // Build up your query URI, limiting results to 50 items and printed books.
            /*
            //GOOGLE API URI
            Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, queryString)
                .appendQueryParameter(MAX_RESULTS, "20")
                .appendQueryParameter(PRINT_TYPE, "books")
                .build();
            */

      Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
          .appendQueryParameter(QUERY_PARAM, queryString)
          .appendQueryParameter(QUERY_PAGE, "1")
          .appendQueryParameter(DEVELOPER_KEY, "Y2yc0wb3LDtDVSGwlCSJDg")
          .appendQueryParameter(SEARCH_FIELD, "all")
          .build();

      URL requestURL = new URL(builtURI.toString());

      // Open the network connection.
      urlConnection = (HttpURLConnection) requestURL.openConnection();
      urlConnection.setRequestMethod("GET");
      urlConnection.connect();

      // Get the InputStream.
      InputStream inputStream = urlConnection.getInputStream();


      // Read the response string into a StringBuilder.
      StringBuilder builder = new StringBuilder();
      ArrayList<String> xmlToList = new ArrayList<>();

      reader = new BufferedReader(new InputStreamReader(inputStream));

      String line;
      while ((line = reader.readLine()) != null) {
        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
        // but it does make debugging a *lot* easier if you print out the completed buffer for debugging.
        builder.append(line + "\n");
        xmlToList.add(line);
        //System.out.println(line);
      }

      XmlParser.xmlToListOfStrings = new ArrayList<>(xmlToList);

      if (builder.length() == 0) {
        // Stream was empty.  No point in parsing.
        // return null;
        return null;
      }
      bookJSONString = builder.toString();

      // Catch errors.
    } catch (IOException e) {
      e.printStackTrace();

      // Close the connections.
    } finally {
      if (urlConnection != null) {
        urlConnection.disconnect();
      }
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          e.printStackTrace();
        }

      }
    }

    // Return the raw response.
    return bookJSONString;

  }

  /**
   * Handles the results on the UI thread. Gets the information from
   * the JSON and updates the Views.
   *
   * @param s Result from the doInBackground method containing the raw JSON response,
   *          or null if it failed.
   */
  @Override
  protected void onPostExecute(String s) {
    super.onPostExecute(s);
    collection  = XmlParser.parse(new String[]{"id type","title","name","image_url"}, "work");
    parent.update(collection);
    /*
    try {

      // Initialize iterator and results fields.
      int i = 0;
      String title = "";
      String authors = "";

      //Clear layout

      mCollectionLayout.removeAllViewsInLayout();

      // Look for results in the items array, exiting when both the title and author
      // are found or when all items have been checked.
      for(String[] arr : collection){
        // If both are found, display the result.
        title = arr[0];
        authors = arr[1];
        if (title != null && authors != null){


          TextView tv = new TextView(parent);
          tv.setText(title);
          tv.setTextAppearance(parent,R.style.bookResult);
          tv.setId(i*2 );
          tv.setLayoutParams(new LayoutParams(
              LayoutParams.MATCH_PARENT,
              LayoutParams.WRAP_CONTENT));
          mCollectionLayout.addView(tv);

          TextView tv2 = new TextView(parent);
          tv2.setText(authors);
          tv2.setTextAppearance(parent,R.style.bookResult);
          tv2.setId(i  + 1);
          tv2.setLayoutParams(new LayoutParams(
              LayoutParams.MATCH_PARENT,
              LayoutParams.WRAP_CONTENT));
          mCollectionLayout.addView(tv2);
        }
        i++;
      }

      mBookInput.setText("");

    } catch (Exception e){
      e.printStackTrace();
    }
    */
  }
}