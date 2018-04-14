package com.example.xrhstos.bookapp.grid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import com.example.xrhstos.bookapp.Book;
import com.example.xrhstos.bookapp.NewHolder;
import com.example.xrhstos.bookapp.PreviewController;
import com.example.xrhstos.bookapp.R;
import com.example.xrhstos.bookapp.transformation.RoundCorners;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Xrhstos on 4/13/2018.
 */

// Create the basic adapter extending from RecyclerView.Adapter
// Note that we specify the custom ViewHolder which gives us access to our views
public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

  private Context mContext;
  private final PreviewController parentController;
  private ArrayList<Book> data;

  // Pass in the contact array into the constructor
  public GridAdapter(PreviewController par, Context c, ArrayList<Book> data){
    parentController = par;
    mContext = c;
    this.data = data;
    setHasStableIds(true);
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(context);

    // Inflate the custom layout
    View gridInflater = inflater.inflate(R.layout.grid_inflater, parent, false);

    // Return a new holder instance
    ViewHolder viewHolder = new ViewHolder(gridInflater);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull GridAdapter.ViewHolder holder, int position) {
    // Get the data model based on position
    Book book = data.get(position);

      setImage(holder.bookView, book.getBookCoverURL(),position);
      //holder.titleView.setText(book.getBookTitle());
      //holder.authorView.setText(book.getAuthor());
    holder.titleView.setText(position+"");
    holder.authorView.setText(position+"");
      //holder.bookView.setClipToOutline(true);
      //holder.bookView.setScaleType(ScaleType.FIT_CENTER);

  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public int getItemViewType(int position) {
    return position;
  }

  @Override
  public int getItemCount() {
    return data.size();
  }

  // Provide a direct reference to each of the views within a data item
  // Used to cache the views within the item layout for fast access
  public class ViewHolder extends RecyclerView.ViewHolder {
    // Your holder should contain a member variable
    // for any view that will be set as you render a row
    public ImageView bookView;
    public TextView titleView;
    public TextView authorView;

    // We also create a constructor that accepts the entire item row
    // and does the view lookups to find each subview
    public ViewHolder(View itemView) {
      // Stores the itemView in a public final member variable that can be used
      // to access the context from any ViewHolder instance.
      super(itemView);

      bookView = (ImageView) itemView.findViewById(R.id.bookImageTag);
      titleView = (TextView) itemView.findViewById(R.id.bookTitleTag);
      authorView = (TextView) itemView.findViewById(R.id.bookPublisherTag);
    }
  }

  private void setImage(final ImageView container, final String url,final int position){
    Picasso.with(mContext)
        .load(String.valueOf(url))
        .transform(new RoundCorners(5,5))
        .into(new Target() {
          @Override
          public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from) {
                /* Save the bitmap or do something with it here */

            //Set it in the ImageView
            if(url.equals("https://s.gr-assets.com/assets/nophoto/book/111x148-bcc042a9c91a29c1d680899eff700a03.png")){
              container.setImageResource(R.drawable.placeholder);
              //container.setBackgroundResource(R.drawable.placeholder_book);
              //container.setImageBitmap(null);
              //container.setImageResource(R.drawable.book_shape);
            }else{
              container.setImageBitmap(bitmap);
            }
          }

          @Override
          public void onPrepareLoad(Drawable placeHolderDrawable) {}

          @Override
          public void onBitmapFailed(Drawable errorDrawable) {

            System.out.println("Failed loading " + url);
          }
        });

    container.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {
        parentController.getParent().bookClick(position);
      }

    });
  }
}
