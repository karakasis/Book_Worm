package com.example.xrhstos.bookapp;

import android.content.Context;
import android.media.Image;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import info.hoang8f.widget.FButton;

/**
 * Created by Xrhstos on 4/20/2018.
 */

public class Buttons {

  private Context mCtx;
  private LinearLayout layoutTop;
  private LinearLayout layoutBot;
  private LayoutParams params;
  private BookInfoActivity parent;

  private ImageButton add;
  private FButton addStr;
  private ImageButton remove;
  private FButton removeStr;
  private ImageButton wishAdd;
  private FButton wishAddStr;
  private ImageButton wishRemove;
  private FButton wishRemoveStr;
  private FButton read;
  private FButton notRead;

  public Buttons(Context ctx, LinearLayout layout1,LinearLayout layout2){
    mCtx = ctx;
    this.layoutTop = layout1;
    this.layoutBot = layout2;
    params = new LinearLayout.LayoutParams
        (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,1F);
    parent = (BookInfoActivity) ctx;

    add = new ImageButton(mCtx);
    add.setImageResource(R.drawable.ic_add_green_800_24dp);
    add.setBackgroundColor( mCtx.getResources().getColor(R.color.fbutton_color_transparent));
    add.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        layoutTop.removeAllViews();
        layoutTop.addView(removeStr);
        layoutTop.addView(remove);

        parent.addBook();
      }
    });
    add.setLayoutParams(params);

    remove = new ImageButton(mCtx);
    remove.setImageResource(R.drawable.ic_remove_red_400_24dp);
    remove.setBackgroundColor(mCtx.getResources().getColor(R.color.fbutton_color_transparent));
    remove.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        layoutTop.removeAllViews();
        layoutTop.addView(addStr);
        layoutTop.addView(add);

        parent.removeBook();
      }
    });
    remove.setLayoutParams(params);

    wishAdd = new ImageButton(mCtx);
    wishAdd.setImageResource(R.drawable.ic_favorite_border_red_400_24dp);
    wishAdd.setBackgroundColor(mCtx.getResources().getColor(R.color.fbutton_color_transparent));
    wishAdd.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        layoutBot.removeAllViews();
        layoutBot.addView(wishRemoveStr);
        layoutBot.addView(wishRemove);

        parent.wishlistAddBook();
      }
    });
    wishAdd.setLayoutParams(params);

    wishRemove = new ImageButton(mCtx);
    wishRemove.setImageResource(R.drawable.ic_favorite_red_400_24dp);
    wishRemove.setBackgroundColor(mCtx.getResources().getColor(R.color.fbutton_color_transparent));
    wishRemove.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        layoutBot.removeAllViews();
        layoutBot.addView(wishAddStr);
        layoutBot.addView(wishAdd);

        parent.wishlistRemoveBook();
      }
    });
    wishRemove.setLayoutParams(params);

    read = new FButton(mCtx);
    read.setText("Mark as read");
    read.setButtonColor(mCtx.getResources().getColor(R.color.fbutton_color_belize_hole));
    read.setShadowColor(mCtx.getResources().getColor(R.color.buttonShadow));
    read.setShadowEnabled(true);
    read.setShadowHeight(5);
    read.setCornerRadius(5);
    read.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        layoutBot.removeAllViews();
        layoutBot.addView(notRead);

        parent.addReadBook();
      }
    });
    read.setLayoutParams(params);

    notRead = new FButton(mCtx);
    notRead.setText("Mark as not read");
    notRead.setButtonColor(mCtx.getResources().getColor(R.color.fbutton_color_belize_hole));
    notRead.setShadowColor(mCtx.getResources().getColor(R.color.buttonShadow));
    notRead.setShadowEnabled(true);
    notRead.setShadowHeight(5);
    notRead.setCornerRadius(5);
    notRead.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {

        layoutBot.removeAllViews();
        layoutBot.addView(read);
        parent.removeReadBook();
      }
    });
    notRead.setLayoutParams(params);

    addStr = new FButton(mCtx);
    addStr.setText("Add to Collection");
    addStr.setButtonColor(mCtx.getResources().getColor(R.color.green800));
    addStr.setShadowColor(mCtx.getResources().getColor(R.color.green900));
    addStr.setShadowEnabled(true);
    addStr.setShadowHeight(5);
    addStr.setCornerRadius(5);
    addStr.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        layoutTop.removeAllViews();
        layoutTop.addView(removeStr);
        layoutTop.addView(remove);

        parent.addBook();
      }
    });
    addStr.setLayoutParams(params);

    removeStr = new FButton(mCtx);
    removeStr.setText("Remove from Collection");
    removeStr.setButtonColor(mCtx.getResources().getColor(R.color.red400));
    removeStr.setShadowColor(mCtx.getResources().getColor(R.color.red600));
    removeStr.setShadowEnabled(true);
    removeStr.setShadowHeight(5);
    removeStr.setCornerRadius(5);
    removeStr.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        layoutTop.removeAllViews();
        layoutTop.addView(addStr);
        layoutTop.addView(add);

        parent.removeBook();
      }
    });
    removeStr.setLayoutParams(params);

    wishAddStr = new FButton(mCtx);
    wishAddStr.setText("Add to Wishlist");
    wishAddStr.setButtonColor(mCtx.getResources().getColor(R.color.red400));
    wishAddStr.setShadowColor(mCtx.getResources().getColor(R.color.red600));
    wishAddStr.setShadowEnabled(true);
    wishAddStr.setShadowHeight(5);
    wishAddStr.setCornerRadius(5);
    wishAddStr.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        layoutBot.removeAllViews();
        layoutBot.addView(wishRemoveStr);
        layoutBot.addView(wishRemove);

        parent.wishlistAddBook();
      }
    });
    wishAddStr.setLayoutParams(params);

    wishRemoveStr = new FButton(mCtx);
    wishRemoveStr.setText("Remove from Wishlist");
    wishRemoveStr.setButtonColor(mCtx.getResources().getColor(R.color.red400));
    wishRemoveStr.setShadowColor(mCtx.getResources().getColor(R.color.red600));
    wishRemoveStr.setShadowEnabled(true);
    wishRemoveStr.setShadowHeight(5);
    wishRemoveStr.setCornerRadius(5);
    wishRemoveStr.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {

        layoutBot.removeAllViews();
        layoutBot.addView(wishAddStr);
        layoutBot.addView(wishAdd);


        parent.wishlistRemoveBook();
      }
    });
    wishRemoveStr.setLayoutParams(params);

  }
  /*
  <ImageButton
    android:id="@+id/add_yes"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:src="@drawable/ic_add_green_800_24dp"
    android:background="@color/fbutton_color_transparent"/>
   */

  public ImageButton add(){
    layoutTop.addView(addStr);
    layoutTop.addView(add);
    return add;
  }

  public ImageButton remove(){
    layoutTop.addView(removeStr);
    layoutTop.addView(remove);
    return remove;
  }

  public ImageButton wishAdd(){
    layoutBot.addView(wishAddStr);
    layoutBot.addView(wishAdd);
    return wishAdd;
  }

  public ImageButton wishRemove(){
    layoutBot.addView(wishRemoveStr);
    layoutBot.addView(wishRemove);
    return wishRemove;
  }

  public FButton markRead(){
    layoutBot.addView(read);
    return read;
  }

  public FButton markNotRead(){
    layoutBot.addView(notRead);
    return notRead;
  }

  public void swapToWish(boolean isInWishlist){
    if(isInWishlist){
      layoutBot.removeAllViews();
      layoutBot.addView(wishRemoveStr);
      layoutBot.addView(wishRemove);
    }else{
      layoutBot.removeAllViews();
      layoutBot.addView(wishAddStr);
      layoutBot.addView(wishAdd);
    }
  }

  public void swapToRead(boolean isRead){
    if(isRead){
      layoutBot.removeAllViews();
      layoutBot.addView(notRead);
    }else {
      layoutBot.removeAllViews();
      layoutBot.addView(read);
    }
  }


}
