package com.example.xrhstos.bookapp;

//import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import java.util.ArrayList;


/**
 * Created by philip on 10/4/2018.
 */

public class Fragment1 extends Fragment {

    private ArrayList<String> urls;

    public Fragment1(){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        /*
        Bundle bundle = getArguments();
        if (bundle != null){
            urls = bundle.getStringArrayList("urls");
        }
        */
        return inflater.inflate(R.layout.fragment_fragment1,parent,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        if(MainActivity.cover_url!=null)
        urls = new ArrayList<>(MainMenu.cover_url);
       // super.onViewCreated(savedInstanceState);
        //setContentView(R.layout.fragment_fragment1);

        GridView gridView = (GridView) view.findViewById(R.id.grid_view);
        //ImageAdapter ia = new ImageAdapter(this, getActivity(),urls);
        //gridView.setAdapter(ia);


        // Instance of ImageAdapter Class
        //gridView.setAdapter(new ImageAdapter(this));
    }

    public interface ActivityCommunicator{
        public void passDataToActivity(String someValue);
    }


}
