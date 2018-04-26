package com.example.xrhstos.bookapp;

import android.app.Application;
import android.content.Context;
import cn.gavinliu.android.lib.scale.config.ScaleConfig;
import com.example.xrhstos.bookapp.gallery.GalleryBackend;
import com.example.xrhstos.bookapp.main_menu.MainMenu;

/**
 * Created by Xrhstos on 4/19/2018.
 */

public class MyApp extends Application {
  private static MyApp instance;
  public MainMenu mainMenu;
  public GalleryBackend galleryBackend;
  public BookInfoActivity bookInfoActivity;
  public ManualAddMenu manualAddMenu;

  public static MyApp getInstance() {
    return instance;
  }

  public static Context getContext(){
    return instance;
    // or return instance.getApplicationContext();
  }

  @Override
  public void onCreate() {
    ScaleConfig.create(this,
        1080, // Design Width
        1920, // Design Height
        3,    // Design Density
        this.getResources().getDisplayMetrics().scaledDensity,    // Design FontScale
        ScaleConfig.DIMENS_UNIT_DP);
    instance = this;
    super.onCreate();
  }
}
