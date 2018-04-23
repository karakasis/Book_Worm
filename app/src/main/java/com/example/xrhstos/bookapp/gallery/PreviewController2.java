package com.example.xrhstos.bookapp.gallery;

//import android.app.Fragment;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import com.example.xrhstos.bookapp.Book;
import com.example.xrhstos.bookapp.gallery.GalleryBackend;
import com.example.xrhstos.bookapp.grid.EndlessScrollListener;
import com.example.xrhstos.bookapp.grid.GridAdapter;
import com.example.xrhstos.bookapp.grid.GridAdapter2;
import com.example.xrhstos.bookapp.grid.WrapContentGridLayoutManager;
import com.example.xrhstos.bookapp.main_menu.MainMenu;
import java.util.ArrayList;


/**
 * Created by philip on 10/4/2018.
 */

public class PreviewController2{

  private final GalleryBackend parent;
  private static GridAdapter2 gridAdapter;
  private RecyclerView recyclerView;
  private WrapContentGridLayoutManager glm;
  private EndlessScrollListener esl;


  public PreviewController2(RecyclerView rv, final GalleryBackend parent){

    this.parent = parent;
    recyclerView = rv;

    glm = new WrapContentGridLayoutManager(
        parent,
        calculateNoOfColumns(parent)
    );
    recyclerView.setLayoutManager(glm);

    esl =new EndlessScrollListener(glm) {
      @Override
      public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
        // Triggered only when new data needs to be appended to the list
        loadNextDataFromApi(page);
      }
    };

    recyclerView.addOnScrollListener(esl);

  }

  public void setData(ArrayList<Book> books){

    if(gridAdapter!=null){
      final int oldSize = gridAdapter.getDataSet().size();
      gridAdapter.getDataSet().clear();
      gridAdapter.notifyItemRangeChanged(oldSize-1,0);
      //gridAdapter.notifyDataSetChanged();
      esl.resetState();
      recyclerView.scrollToPosition(0);

      gridAdapter.getDataSet().addAll(books);
      gridAdapter.notifyItemRangeChanged(0,books.size()-1);
      //gridAdapter.notifyDataSetChanged();
            /*
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    recyclerView.setAdapter(gridAdapter);
                    parent.showGrid();
                }
            }, 1500);
            */
      recyclerView.setAdapter(gridAdapter);

      recyclerView.scrollToPosition(0);
    }else{
      PreviewController2.gridAdapter = new GridAdapter2(this,parent,books);
      Handler handler = new Handler();
            /*
            handler.postDelayed(new Runnable() {
                public void run() {
                    recyclerView.setAdapter(gridAdapter);
                    parent.showGrid();
                }
            }, 1500);
            */
      recyclerView.setAdapter(gridAdapter);


    }


  }

  private void loadNextDataFromApi(int page) {
    MainMenu.loadingData = true;
    parent.requestMoreResults();
  }

  public void acceptResponseFromMainThread(int newDataSize , ArrayList<Book> newData){
    final int curSize = gridAdapter.getItemCount();
    gridAdapter.getDataSet().addAll(newData);
    final int finSize = gridAdapter.getItemCount();

    recyclerView.post(new Runnable() {
      @Override
      public void run() {
        gridAdapter.notifyItemRangeInserted(curSize, finSize);
        MainMenu.loadingData = false;
      }
    });
  }

  public GalleryBackend getParent() {
    return parent;
  }

  private static int calculateNoOfColumns(Context context) {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
    int noOfColumns = (int) (dpWidth / 120);
    return noOfColumns;
  }

  public int getFirstVisibleItem(){
    return glm.findFirstVisibleItemPosition();
  }

  public void scrollToVisibleItem(int firstVisible){

    gridAdapter.notifyItemRangeChanged(0,0);
    gridAdapter.notifyItemRangeChanged(0,gridAdapter.getItemCount());
    glm.scrollToPosition(firstVisible);
  }

  public WrapContentGridLayoutManager getLayoutManager(){
    return glm;
  }
}