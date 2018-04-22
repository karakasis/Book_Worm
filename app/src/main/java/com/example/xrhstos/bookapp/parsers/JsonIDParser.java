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
public class JsonIDParser {

  public static Book parse(JSONObject jsonObject, Book fetchedBook) {

      int ISBN = -1;
      String description;
      String callbackURL;
      String previewURL;
      String buyURL;

      double averageRating;

      String[] categories;
      int pageCount;
      String publishedDate; //might need string for date

    JSONObject volumeInfo = null;
    try {
      volumeInfo = jsonObject.getJSONObject("volumeInfo");
    } catch (JSONException e) {
      e.printStackTrace();
    }
    JSONObject saleInfo = null;
    try {
      saleInfo = jsonObject.getJSONObject("saleInfo");
    } catch (JSONException e) {
      e.printStackTrace();
    }

    try {
            JSONArray isbn = volumeInfo.getJSONArray("industryIdentifiers");
            for(int j=0; j<isbn.length(); j++){
              if(isbn.getJSONObject(j).getString("type").equals("ISBN_10")){
                ISBN = isbn.getJSONObject(j).getInt("identifier");
              }
            }
            fetchedBook.setISBN(ISBN);
            System.out.println(ISBN);
        } catch (JSONException e) {
          e.printStackTrace();
        }
        try{
          description = volumeInfo.getString("description");
          fetchedBook.setDescription(description);
          System.out.println(description);
        } catch (JSONException e) {
          e.printStackTrace();
        }
        try{
          averageRating = volumeInfo.getDouble("averageRating");
          fetchedBook.setAverageRating((float) averageRating);
          System.out.println(averageRating);
        } catch (JSONException e) {
          e.printStackTrace();
        }
        try{
          callbackURL = jsonObject.getString("selfLink");
          fetchedBook.setCallbackURL(callbackURL);
          System.out.println(callbackURL);
        } catch (JSONException e) {
          e.printStackTrace();
        }
        try{
          previewURL = volumeInfo.getString("previewLink");
          fetchedBook.setPreviewURL(previewURL);
          System.out.println(previewURL);
        } catch (JSONException e) {
          e.printStackTrace();
        }
        try{
          if(saleInfo.getString("saleability").equals("FOR_SALE")){
            buyURL = saleInfo.getString("buyLink");
            fetchedBook.setBuyURL(buyURL);
            System.out.println(buyURL);
          }
        } catch (JSONException e) {
          e.printStackTrace();
        }
        try{
          JSONArray cat = volumeInfo.getJSONArray("categories");
          categories = new String[cat.length()];
          for(int j=0; j<cat.length(); j++){
            categories[j] = cat.getString(j);
            System.out.println(categories[j]);
          }
          fetchedBook.setCategories(categories);
        } catch (JSONException e) {
          e.printStackTrace();
        }
        try{
          pageCount = volumeInfo.getInt("pageCount");
          fetchedBook.setPageCount(pageCount);
          System.out.println(pageCount);
        } catch (JSONException e) {
          e.printStackTrace();
        }
        try{
          publishedDate = volumeInfo.getString("publishedDate");
          fetchedBook.setPublishedDate(publishedDate);
          System.out.println(publishedDate);
        } catch (JSONException e) {
          e.printStackTrace();
        }


    return fetchedBook;
  }
}
