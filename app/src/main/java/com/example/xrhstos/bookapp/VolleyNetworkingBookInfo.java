package com.example.xrhstos.bookapp;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView.ScaleType;
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
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.xrhstos.bookapp.parsers.JsonIDParser;
import com.example.xrhstos.bookapp.parsers.JsonParser;
import com.example.xrhstos.bookapp.parsers.XmlParser;
import com.example.xrhstos.bookapp.parsers.XmlParserID;
import com.example.xrhstos.bookapp.parsers.XmlParserISBN;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import org.json.JSONObject;

/**
 * Created by Xrhstos on 4/13/2018.
 */

public class VolleyNetworkingBookInfo {
  private static VolleyNetworkingBookInfo mInstance;
  private RequestQueue mRequestQueue;
  private static BookInfoActivity mCtx;

  private String goodreadskey = "Y2yc0wb3LDtDVSGwlCSJDg";

  private VolleyNetworkingBookInfo(Context context) {
    mCtx = (BookInfoActivity) context;
    mRequestQueue = getRequestQueue();

  }

  public static synchronized VolleyNetworkingBookInfo getInstance(Context context) {
    if (mInstance == null) {
      mInstance = new VolleyNetworkingBookInfo(context);
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

  public StringRequest goodReadsRequestByID(final String queryIDString, final Book book){

    //String requestURL = "https://www.goodreads.com/book/show?format=json&key="+goodreadskey+"&id="+queryIDString;
    String requestURL = "https://www.goodreads.com/book/show/"+queryIDString+".xml?key="+goodreadskey;
    return new StringRequest(Request.Method.GET, requestURL,
        new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {

            BookInfoActivity bia = mCtx;
            XmlParserID.stringToList(response);

            bia.update(XmlParserID.parse(new String[]{"isbn13","isbn","publication_year","publication_month"
                ,"publication_day","description","average_rating","num_pages","url"}, "book", book));


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
      }
    });


  }

  public JsonObjectRequest googleRequestByID(final String queryIDString, final Book book){

    String requestURL =  "https://www.googleapis.com/books/v1/volumes/" + queryIDString;

    return new JsonObjectRequest(Request.Method.GET, requestURL,
        null
        , new Response.Listener<JSONObject>() {

      @Override
      public void onResponse(JSONObject response) {

        BookInfoActivity bia = mCtx;
        bia.update(JsonIDParser.parse(response,book));

        System.out.println("Source : GoogleID");
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
      }
    });


  }


}