package com.example.xrhstos.bookapp.grid;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * Created by Xrhstos on 4/16/2018.
 */

public class WrapContentGridLayoutManager extends GridLayoutManager {

  public WrapContentGridLayoutManager(Context context,
      AttributeSet attrs,
      int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  public WrapContentGridLayoutManager(Context context, int spanCount) {
    super(context, spanCount);
  }

  public WrapContentGridLayoutManager(Context context, int spanCount, int orientation,
      boolean reverseLayout) {
    super(context, spanCount, orientation, reverseLayout);
  }

  //... constructor
  @Override
  public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
    try {
      super.onLayoutChildren(recycler, state);
    } catch (IndexOutOfBoundsException e) {
      Log.e("probe", "meet a IOOBE in RecyclerView");
      //MainMenu mainActivity = MyApp.getInstance().mainMenu;

    }
  }
}