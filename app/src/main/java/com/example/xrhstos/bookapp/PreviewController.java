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

public class PreviewController{

    private MainMenu parent;

    public PreviewController(GridView gv, MainMenu parent, ArrayList<String> urls){

        this.parent = parent;
        ImageAdapter ia = new ImageAdapter(this ,parent  ,urls);
        gv.setAdapter(ia);

    }

    public MainMenu getParent() {
        return parent;
    }
}