package com.example.xrhstos.bookapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Xrhstos on 4/12/2018.
 */

public class CheckConnection extends AsyncTask<Void,Void,Void>
{

  private boolean isOk = false;
  private String url;
  public interface AsynResponse {
    void processFinish(Boolean output);
  }

  AsynResponse asynResponse = null;

  public CheckConnection(AsynResponse asynResponse,String url) {
    this.asynResponse = asynResponse;
    this.url = url;
  }

  @Override
  protected Void doInBackground(Void... voids) {

      try {
        URL urlServer = new URL(url);
        HttpURLConnection urlConn = (HttpURLConnection) urlServer.openConnection();
        urlConn.setConnectTimeout(3000); //<- 3Seconds Timeout
        try {
          urlConn.connect();
        } catch (IOException e) {
          isOk = false;
        }
        if (urlConn.getResponseCode() == 200) {
          isOk = true;
        } else {
          isOk = false;
        }
      } catch (MalformedURLException e1) {
        isOk = false;
      } catch (IOException e) {
        isOk = false;
      }

    return null;
  }

  @Override
  protected void onPostExecute(Void aVoid) {
    super.onPostExecute(aVoid);
    asynResponse.processFinish(isOk);
  }
}


