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
  private LinearLayout layout;
  private LayoutParams params;
  private BookInfoActivity parent;

  private ImageButton add;
  private ImageButton remove;
  private ImageButton wishAdd;
  private ImageButton wishRemove;
  private FButton read;
  private FButton notRead;

  public Buttons(Context ctx, LinearLayout layout1){
    mCtx = ctx;
    this.layout = layout1;
    params = new LinearLayout.LayoutParams
        (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,1F);
    parent = (BookInfoActivity) ctx;

    add = new ImageButton(mCtx);
    add.setImageResource(R.drawable.ic_add_green_800_24dp);
    add.setBackgroundColor( mCtx.getResources().getColor(R.color.fbutton_color_transparent));
    add.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        int index = layout.indexOfChild(v);
        layout.removeView(v);
        layout.addView(remove,index);

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
        int index = layout.indexOfChild(v);
        layout.removeView(v);
        layout.addView(add,index);
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
        int index = layout.indexOfChild(v);
        layout.removeView(v);
        layout.addView(wishRemove,index);
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
        int index = layout.indexOfChild(v);
        layout.removeView(v);
        layout.addView(wishAdd,index);
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
        int index = layout.indexOfChild(v);
        layout.removeView(v);
        layout.addView(notRead,index);
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
        int index = layout.indexOfChild(v);
        layout.removeView(v);
        layout.addView(read,index);
        parent.removeReadBook();
      }
    });
    notRead.setLayoutParams(params);

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
    layout.addView(add);
    return add;
  }

  public ImageButton remove(){
    layout.addView(remove);
    return remove;
  }

  public ImageButton wishAdd(){
    layout.addView(wishAdd);
    return wishAdd;
  }

  public ImageButton wishRemove(){
    layout.addView(wishRemove);
    return wishRemove;
  }

  /*
  <info.hoang8f.widget.FButton
    android:id="@+id/read_no"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Mark as read"
    fbutton:buttonColor="@color/fbutton_color_belize_hole"
    fbutton:shadowColor="#01579b"
    fbutton:shadowEnabled="true"
    fbutton:shadowHeight="5dp"
    fbutton:cornerRadius="5dp"
    />
   */
  public FButton markRead(){
    layout.addView(read);
    return read;
  }

  public FButton markNotRead(){
    layout.addView(notRead);
    return notRead;
  }

  public void swapToWish(boolean isInWishlist,boolean isRead){
    int index;
    if(isRead){
      index = layout.indexOfChild(notRead);
    }else{
      index = layout.indexOfChild(read);
    }
    if(isInWishlist){
      layout.removeViewAt(index);
      layout.addView(wishRemove,index);
    }else{
      layout.removeViewAt(index);
      layout.addView(wishAdd,index);
    }
  }

  public void swapToRead(boolean isRead,boolean isInWishlist){
    int index;
    if(isInWishlist){
      index = layout.indexOfChild(wishRemove);
    }else{
      index = layout.indexOfChild(wishAdd);
    }
    if(isRead){
      layout.removeViewAt(index);
      layout.addView(notRead,index);
    }else{
      layout.removeViewAt(index);
      layout.addView(read,index);
    }
  }


}
