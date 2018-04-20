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

import com.example.xrhstos.bookapp.MainMenu;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * AsyncTask implementation that opens a network connection and
 * query's the Book Service API.
 */
public class JsonIDParser {

  private static ArrayList<String[]> collection;
  public static JSONObject jsonObject;

  public static ArrayList<String[]> parse(String[] tags) {
    collection = new ArrayList<>();
    try {

      // Get the JSONArray of book items.
      JSONArray itemsArray = jsonObject.getJSONArray("items");

      // Initialize iterator and results fields.
      int i = 0;
      String title = null;
      String authors = null;
      String url = null;
      String googleID = "";

      // Look for results in the items array, exiting when both the title and author
      // are found or when all items have been checked.
      while (i < itemsArray.length() || (authors == null && title == null)) {
        // Get the current item information.
        JSONObject book = itemsArray.getJSONObject(i);
        JSONObject volumeInfo = book.getJSONObject("volumeInfo");

        // Try to get the author and title from the current item,
        // catch if either field is empty and move on.
        try {
          if(!MainMenu.langQuery.isEmpty() && volumeInfo.getString("language").equals(MainMenu.langQuery)){
            System.out.println(collection.size());
            title = volumeInfo.getString("title");
            System.out.println(title);
            authors = volumeInfo.getString("authors");
            System.out.println(authors);
            url = volumeInfo.getJSONObject("imageLinks").getString("thumbnail");
            System.out.println(url);
            googleID = book.getString("id");
            System.out.println(googleID);
            //collection.add(new String[]{String.valueOf(i), title, authors, url});
            collection.add(new String[]{googleID, title, authors, url});
          }
          if(MainMenu.langQuery.isEmpty()){
            System.out.println(collection.size());
            title = volumeInfo.getString("title");
            System.out.println(title);
            authors = volumeInfo.getString("authors");
            System.out.println(authors);
            url = volumeInfo.getJSONObject("imageLinks").getString("thumbnail");
            System.out.println(url);
            //collection.add(new String[]{String.valueOf(i), title, authors, url});
          }
        } catch (Exception e) {
          e.printStackTrace();
        }

        // Move to the next item.
        i++;
      }

    } catch (Exception e) {

      e.printStackTrace();
    }

    return collection;
  }
}