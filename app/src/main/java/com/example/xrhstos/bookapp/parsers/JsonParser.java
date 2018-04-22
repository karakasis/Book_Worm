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
package com.example.xrhstos.bookapp.parsers;

import android.util.JsonWriter;
import com.example.xrhstos.bookapp.Book;
import com.example.xrhstos.bookapp.MainMenu;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * AsyncTask implementation that opens a network connection and
 * query's the Book Service API.
 */
public class JsonParser {

  public static ArrayList<Book> parse(JSONObject jsonObject) {
    ArrayList<Book> collection = new ArrayList<>();
    try {

      // Get the JSONArray of book items.
      JSONArray itemsArray = jsonObject.getJSONArray("items");


    // Initialize iterator and results fields.
      int i = 0;
      String title = "";
      String[] authors;
      String url = "";
      String googleID = "";

      // Look for results in the items array, exiting when both the title and author
      // are found or when all items have been checked.
      while (i < itemsArray.length()) {
        // Get the current item information.
        JSONObject book = null;
        try {
          book = itemsArray.getJSONObject(i);
        } catch (JSONException e) {
          e.printStackTrace();
        }
        JSONObject volumeInfo = null;
        try {
          volumeInfo = book.getJSONObject("volumeInfo");
        } catch (JSONException e) {
          e.printStackTrace();
        }

        // Try to get the author and title from the current item,
        // catch if either field is empty and move on.
        try {
          if(!MainMenu.langQuery.isEmpty() && volumeInfo.getString("language").equals(MainMenu.langQuery)){
            System.out.println(collection.size());
            title = volumeInfo.getString("title");
            System.out.println(title);

            JSONArray aut = volumeInfo.getJSONArray("authors");
            authors = new String[aut.length()];
            for(int j=0; j<aut.length(); j++){
              authors[j] = aut.getString(j);
              System.out.println(authors[j]);
            }

            url = volumeInfo.getJSONObject("imageLinks").getString("thumbnail");
            System.out.println(url);
            googleID = book.getString("id");
            System.out.println(googleID);

            collection.add(new Book(null,title,authors,url));
            collection.get(collection.size()-1).setGoogleID(googleID);
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }

        // Move to the next item.
        i++;
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }
    return collection;

  }
}
