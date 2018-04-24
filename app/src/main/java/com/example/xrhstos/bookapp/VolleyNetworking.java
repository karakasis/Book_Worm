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
import com.example.xrhstos.bookapp.main_menu.MainMenu;
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

public class VolleyNetworking {

  private static VolleyNetworking mInstance;
  private RequestQueue mRequestQueue;
  private static Context mCtx;

  private String goodreadskey = "Y2yc0wb3LDtDVSGwlCSJDg";

  private VolleyNetworking(Context context) {
    mCtx = context;
    mRequestQueue = getRequestQueue();

  }

  public static synchronized VolleyNetworking getInstance(Context context) {
    if (mInstance == null || !mCtx.getClass().equals(context.getClass())) {
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

  public StringRequest goodReadsRequest(final String queryString) {

    //final String BOOK_BASE_URL = "https://www.goodreads.com/search/index.xml";
    final String BOOK_BASE_URL = "https://www.goodreads.com/search/index.xml";
    final String QUERY_PARAM = "q"; // The query text to match against book title, author,
    // and ISBN fields. Supports boolean operators and phrase searching.
    final String QUERY_PAGE = "page"; //Which page to return (default 1, optional)
    final String DEVELOPER_KEY = "key"; //dev key required
    final String SEARCH_FIELD = "search(field)"; //Field to search, one of 'title', 'author', or 'all' (default is 'all')

    Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
        .appendQueryParameter(QUERY_PARAM, queryString)
        .appendQueryParameter(QUERY_PAGE, String.valueOf(MainMenu.getPage()))
        .appendQueryParameter(DEVELOPER_KEY, goodreadskey)
        .appendQueryParameter(SEARCH_FIELD, "all")
        .build();
    String requestURL = builtURI.toString();

    return new StringRequest(Request.Method.GET, requestURL,
        new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {

            MainMenu mm = (MainMenu) mCtx;
            XmlParser.stringToList(response);

            mm.update(
                XmlParser.parse(new String[]{"id", "title", "name", "image_url"}, "best_book"));

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
        System.out.println(message);
      }
    });
  }


  public StringRequest goodReadsRequestByISBN(final String queryISBNString) {

    String requestURL =
        "https://www.goodreads.com/book/isbn/" + queryISBNString + "?key=" + goodreadskey;
    return new StringRequest(Request.Method.GET, requestURL,
        new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {

            MainMenu mm = (MainMenu) mCtx;
            XmlParserISBN.stringToList(response);
            Book book = XmlParserISBN.parse(
                new String[]{"id", "title", "image_url", "name", "isbn13", "isbn",
                    "publication_year", "publication_month"
                    , "publication_day", "description", "average_rating", "num_pages", "url"},
                "book");

            ArrayList<Book> res = new ArrayList<>();
            res.add(book);
            if (book == null) {
              mm.updateByISBN(null);
            } else {
              mm.updateByISBN(res);
            }

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
        mm.updateByISBN(null);

        System.out.println(message);
      }
    });


  }

  public JsonObjectRequest googleRequest(final String queryString) {

    final String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";

    final String QUERY_PARAM = "q"; // Parameter for the search string.
    final String MAX_RESULTS = "maxResults"; // Parameter that limits search results.
    final String PRINT_TYPE = "printType"; // Parameter to filter by print type.
    final String INDEX = "startIndex";
    final String LANG = "langRestrict";
    final String ORDER = "orderBy";
    final String PROJECTION = "projection";

    // Build up your query URI, limiting results to 10 items and printed books.
    Uri builtURI;
    if (MainMenu.langQuery.isEmpty()) {
      builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
          .appendQueryParameter(QUERY_PARAM, queryString)
          .appendQueryParameter(MAX_RESULTS, "40")
          .appendQueryParameter(INDEX, String.valueOf(40 * (MainMenu.getPage() - 1)))
          .appendQueryParameter(PRINT_TYPE, "books")
          .appendQueryParameter(ORDER, "relevance")
          .appendQueryParameter(PROJECTION, "full")
          .build();
    } else {
      builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
          .appendQueryParameter(QUERY_PARAM, queryString)
          .appendQueryParameter(MAX_RESULTS, "40")
          .appendQueryParameter(INDEX, String.valueOf(40 * (MainMenu.getPage() - 1)))
          .appendQueryParameter(LANG, MainMenu.langQuery)
          .appendQueryParameter(PRINT_TYPE, "books")
          .appendQueryParameter("orderBy", "relevance")
          .build();
    }

    String requestURL = builtURI.toString();

    return new JsonObjectRequest(Request.Method.GET, requestURL,
        null
        , new Response.Listener<JSONObject>() {

      @Override
      public void onResponse(JSONObject response) {

        MainMenu mm = (MainMenu) mCtx;
        mm.update(JsonParser.parse(response, false));

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
        System.out.println(message);
      }
    });


  }


  public JsonObjectRequest googleRequestByISBN(final String queryISBNString) {

    String requestURL = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + queryISBNString;

    return new JsonObjectRequest(Request.Method.GET, requestURL,
        null
        , new Response.Listener<JSONObject>() {

      @Override
      public void onResponse(JSONObject response) {

        MainMenu mm = (MainMenu) mCtx;
        mm.updateByISBN(JsonParser.parse(response, true));

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
        mm.updateByISBN(null);
        System.out.println(message);
      }
    });


  }

  public ImageRequest bitmapRequest(final String mImageURLString, final Book book) {
    return new ImageRequest(
        mImageURLString, // Image URL
        new Response.Listener<Bitmap>() { // Bitmap listener
          @Override
          public void onResponse(Bitmap response) {
            // Do something with response
            /*
            book.responseBookCover(response);
            MainMenu mm = (MainMenu) mCtx;
            mm.startUI();
            */

            // Save this downloaded bitmap to internal storage
            //Uri uri = saveImageToInternalStorage(response);

            // Display the internal storage saved image to image view
            //mImageViewInternal.setImageURI(uri);
          }
        }, 0, // Image width
        0, // Image height
        ScaleType.FIT_XY, // Image scale type
        Bitmap.Config.RGB_565,
        new Response.ErrorListener() { // Error listener
          @Override
          public void onErrorResponse(VolleyError error) {
            // Do something with error response
            //error.printStackTrace();
            System.out.println("Failed loading " + book.getId());
          }
        }
    );
  }

  // Custom method to save a bitmap into internal storage
  protected Uri saveImageToInternalStorage(Bitmap bitmap) {
    // Initialize ContextWrapper
    ContextWrapper wrapper = new ContextWrapper(mCtx);

    // Initializing a new file
    // The bellow line return a directory in internal storage
    File file = wrapper.getDir("Images", MODE_PRIVATE);

    // Create a file to save the image
    file = new File(file, "UniqueFileName" + ".jpg");

    try {
      // Initialize a new OutputStream
      OutputStream stream = null;

      // If the output file exists, it can be replaced or appended to it
      try {
        stream = new FileOutputStream(file);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }

      // Compress the bitmap
      bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

      // Flushes the stream
      try {
        stream.flush();
      } catch (IOException e) {
        e.printStackTrace();
      }

      // Closes the stream
      stream.close();

    } catch (IOException e) // Catch the exception
    {
      e.printStackTrace();
    }

    // Parse the gallery image url to uri
    // Return the saved image Uri
    return Uri.parse(file.getAbsolutePath());
  }

  public StringRequest goodReadsRequestByID(final String queryIDString, final Book book) {

    //String requestURL = "https://www.goodreads.com/book/show?format=json&key="+goodreadskey+"&id="+queryIDString;
    String requestURL =
        "https://www.goodreads.com/book/show/" + queryIDString + ".xml?key=" + goodreadskey;
    return new StringRequest(Request.Method.GET, requestURL,
        new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {

            //BookInfoActivity bia = (BookInfoActivity) mCtx;
            XmlParserID.stringToList(response);

            MyApp.getInstance().bookInfoActivity.update(XmlParserID
                .parse(new String[]{"isbn13", "isbn", "publication_year", "publication_month"
                        , "publication_day", "description", "average_rating", "num_pages", "url"},
                    "book", book));

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
        System.out.println(message);
      }
    });


  }

  public JsonObjectRequest googleRequestByID(final String queryIDString, final Book book) {

    String requestURL = "https://www.googleapis.com/books/v1/volumes/" + queryIDString;

    return new JsonObjectRequest(Request.Method.GET, requestURL,
        null
        , new Response.Listener<JSONObject>() {

      @Override
      public void onResponse(JSONObject response) {

        //BookInfoActivity bia = (BookInfoActivity) mCtx;
        MyApp.getInstance().bookInfoActivity.update(JsonIDParser.parse(response, book));

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
        System.out.println(message);
      }
    });


  }
}