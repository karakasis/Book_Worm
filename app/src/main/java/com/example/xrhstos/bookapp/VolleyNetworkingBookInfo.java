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



  }


