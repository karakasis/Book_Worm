package com.example.xrhstos.bookapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.util.Log;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

/**
 * Created by Xrhstos on 4/22/2018.
 */

public class Ping {

  public boolean executeCommand(){
    System.out.println("executeCommand");
    Runtime runtime = Runtime.getRuntime();
    try
    {
      Process  mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
      int mExitValue = mIpAddrProcess.waitFor();
      System.out.println(" mExitValue "+mExitValue);
      if(mExitValue==0){
        return true;
      }else{
        return false;
      }
    }
    catch (InterruptedException ignore)
    {
      ignore.printStackTrace();
      System.out.println(" Exception:"+ignore);
    }
    catch (IOException e)
    {
      e.printStackTrace();
      System.out.println(" Exception:"+e);
    }
    return false;
  }
}
