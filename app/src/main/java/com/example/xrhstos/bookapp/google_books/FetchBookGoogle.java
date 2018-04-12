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
package com.example.xrhstos.bookapp.google_books;

import android.net.Uri;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.xrhstos.bookapp.MainMenu;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * AsyncTask implementation that opens a network connection and
 * query's the Book Service API.
 */
public class FetchBookGoogle extends AsyncTask<String,Void,String> {

  private ArrayList<String[]> collection;
  private MainMenu parent;

  // Constructor providing a reference to the views in MainActivity
  public FetchBookGoogle(MainMenu c) {
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
    collection = new ArrayList<>();

    // Set up variables for the try block that need to be closed in the finally block.
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String bookJSONString = null;

    // Attempt to query the Books API.
    try {
      // Base URI for the Books API.
      final String BOOK_BASE_URL =  "https://www.googleapis.com/books/v1/volumes?";

      final String QUERY_PARAM = "q"; // Parameter for the search string.
      final String MAX_RESULTS = "maxResults"; // Parameter that limits search results.
      final String PRINT_TYPE = "printType"; // Parameter to filter by print type.

      // Build up your query URI, limiting results to 10 items and printed books.
      Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
          .appendQueryParameter(QUERY_PARAM, queryString)
          .appendQueryParameter(MAX_RESULTS, "40")
          .appendQueryParameter(PRINT_TYPE, "books")
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

      reader = new BufferedReader(new InputStreamReader(inputStream));

      String line;
      while ((line = reader.readLine()) != null) {
        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
        // but it does make debugging a *lot* easier if you print out the completed buffer for debugging.
        builder.append(line + "\n");
        System.out.println(line);
      }

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
    try {
      // Convert the response into a JSON object.
      JSONObject jsonObject = new JSONObject(s);
      // Get the JSONArray of book items.
      JSONArray itemsArray = jsonObject.getJSONArray("items");

      // Initialize iterator and results fields.
      int i = 0;
      String title = null;
      String authors = null;

      // Look for results in the items array, exiting when both the title and author
      // are found or when all items have been checked.
      while (i < itemsArray.length() || (authors == null && title == null)) {
        // Get the current item information.
        JSONObject book = itemsArray.getJSONObject(i);
        JSONObject volumeInfo = book.getJSONObject("volumeInfo");

        // Try to get the author and title from the current item,
        // catch if either field is empty and move on.
        try {
          title = volumeInfo.getString("title");
          authors = volumeInfo.getString("authors");
          collection.add(new String[]{String.valueOf(i),title,authors,"url"});
        } catch (Exception e){
          e.printStackTrace();
        }

        // Move to the next item.
        i++;
      }
      parent.update(collection);

    } catch (Exception e){

      e.printStackTrace();
    }
  }
}
