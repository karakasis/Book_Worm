package com.example.xrhstos.bookapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.Surface;


/**
 * Thanks to https://stackoverflow.com/a/16599549/9301923
 */

@SuppressWarnings("deprecation")
@SuppressLint("NewApi")
public class LockActivityOrientation{
  public static void lockActivityOrientation(Activity activity) {
    Display display = activity.getWindowManager().getDefaultDisplay();
    int rotation = display.getRotation();
    int height;
    int width;
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
      height = display.getHeight();
      width = display.getWidth();
    } else {
      Point size = new Point();
      display.getSize(size);
      height = size.y;
      width = size.x;
    }
    switch (rotation) {
      case Surface.ROTATION_90:
        if (width > height) {
          activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
          activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        }
        break;
      case Surface.ROTATION_180:
        if (height > width) {
          activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
        } else {
          activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        }
        break;
      case Surface.ROTATION_270:
        if (width > height) {
          activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
        } else {
          activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        break;
      default:
        if (height > width) {
          activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
          activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
      case Surface.ROTATION_0:
        break;
    }
  }
}
