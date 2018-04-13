package com.example.xrhstos.bookapp;

import android.content.Context;
import android.net.Uri;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.xrhstos.bookapp.parsers.JsonParser;
import com.example.xrhstos.bookapp.parsers.XmlParser;
import org.json.JSONObject;

/**
 * Created by Xrhstos on 4/13/2018.
 */

public class VolleyNetworking {
  private static VolleyNetworking mInstance;
  private RequestQueue mRequestQueue;
  private static Context mCtx;

  private VolleyNetworking(Context context) {
    mCtx = context;
    mRequestQueue = getRequestQueue();

  }

  public static synchronized VolleyNetworking getInstance(Context context) {
    if (mInstance == null) {
      mInstance = new VolleyNetworking(context);
    }
    return mInstance;
  }

  public RequestQueue getRequestQueue() {
    if (mRequestQueue == null) {
      // getApplicationContext() is key, it keeps you from leaking the
      // Activity or BroadcastReceiver if someone passes one in.
      mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
    }
    return mRequestQueue;
  }

  public <T> void addToRequestQueue(Request<T> req) {
    getRequestQueue().add(req);
  }

  public StringRequest goodReadsRequest(final String queryString){

    //final String BOOK_BASE_URL = "https://www.goodreads.com/search/index.xml";
    final String BOOK_BASE_URL = "https://www.goodreads.com/search/index.xml";
    final String QUERY_PARAM = "q"; // The query text to match against book title, author,
    // and ISBN fields. Supports boolean operators and phrase searching.
    final String QUERY_PAGE = "page"; //Which page to return (default 1, optional)
    final String DEVELOPER_KEY = "key"; //dev key required
    final String SEARCH_FIELD = "search(field)"; //Field to search, one of 'title', 'author', or 'all' (default is 'all')


    Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
        .appendQueryParameter(QUERY_PARAM, queryString)
        .appendQueryParameter(QUERY_PAGE, "1")
        .appendQueryParameter(DEVELOPER_KEY, "Y2yc0wb3LDtDVSGwlCSJDg")
        .appendQueryParameter(SEARCH_FIELD, "all")
        .build();
    String requestURL = builtURI.toString();

    return new StringRequest(Request.Method.GET, requestURL,
        new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {

            MainMenu mm = (MainMenu) mCtx;
            mm.tLogger.addSplit("Response volley");
            XmlParser.stringToList(response);

            mm.tLogger.addSplit("xml To list");
            mm.update(XmlParser.parse(new String[]{"id type","title","name","image_url"}, "work"));

            mm.tLogger.addSplit("xml parsing");
            mm.tLogger.dumpToLog();

            System.out.println("Source : Goodreads");
          }
        }
      , new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError volleyError) {
        String message = "";
        if (volleyError instanceof NetworkError) {
          message = "Cannot connect to Internet...Please check your connection!";
        } else if (volleyError instanceof ServerError) {
          message = "The server could not be found. Please try again after some time!!";
        } else if (volleyError instanceof AuthFailureError) {
          message = "Cannot connect to Internet...Please check your connection!";
        } else if (volleyError instanceof ParseError) {
          message = "Parsing error! Please try again after some time!!";
        } else if (volleyError instanceof NoConnectionError) {
          message = "Cannot connect to Internet...Please check your connection!";
        } else if (volleyError instanceof TimeoutError) {
          message = "Connection TimeOut! Please check your internet connection.";
        }
        MainMenu mm = (MainMenu) mCtx;
        mm.notifier.setText(message);
      }
    });
  }


  public JsonObjectRequest googleRequest(final String queryString){

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

    String requestURL = builtURI.toString();

    return new JsonObjectRequest(Request.Method.GET, requestURL,
        null
        , new Response.Listener<JSONObject>() {

          @Override
          public void onResponse(JSONObject response) {

            MainMenu mm = (MainMenu) mCtx;
            mm.tLogger.addSplit("Response volley");
            JsonParser.jsonObject = response;
            JsonParser.parse(null,mm);
            mm.tLogger.addSplit("JSON Parse");
            mm.tLogger.dumpToLog();

            System.out.println("Source : Google");
          }
        }, new Response.ErrorListener() {

          @Override
          public void onErrorResponse(VolleyError volleyError) {
            String message = "";
            if (volleyError instanceof NetworkError) {
              message = "Cannot connect to Internet...Please check your connection!";
            } else if (volleyError instanceof ServerError) {
              message = "The server could not be found. Please try again after some time!!";
            } else if (volleyError instanceof AuthFailureError) {
              message = "Cannot connect to Internet...Please check your connection!";
            } else if (volleyError instanceof ParseError) {
              message = "Parsing error! Please try again after some time!!";
            } else if (volleyError instanceof NoConnectionError) {
              message = "Cannot connect to Internet...Please check your connection!";
            } else if (volleyError instanceof TimeoutError) {
              message = "Connection TimeOut! Please check your internet connection.";
            }
            MainMenu mm = (MainMenu) mCtx;
            mm.notifier.setText(message);
          }
        });


  }

}