package com.example.xrhstos.bookapp;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Xrhstos on 4/10/2018.
 */

public class xmlMyClass {

  public static ArrayList<String[]> parse(String source){

    char[] ca = source.toCharArray();
    String title = "";
    String author = "";
    String bookCoverURL = "";
    ArrayList<String[]> collection = new ArrayList<>();
    int counterLock = 0;


    for(int i=0; i<ca.length; i++){

      if(ca[i] == '<'){
        if(ca[i+1] == 't'){
          if(ca[i+2] == 'i'){
            if(ca[i+3] == 't'){

              int counter = i+7;
              counterLock++;

              do{
                title += ca[counter];
                counter++;

              }while(ca[counter] != '<');

              System.out.println(title);


            }
          }
        }
      }

      if(ca[i] == '<'){
        if(ca[i+1] == 'n'){
          if(ca[i+2] == 'a'){

            int counter = i+6;
            counterLock++;

            do{
              author += ca[counter];
              counter++;

            }while(ca[counter] != '<');


            System.out.println(author);
          }
        }
      }

      if(ca[i] == '<'){
        if(ca[i+1] == 'i'){
          if(ca[i+2] == 'm'){

            int counter = i+11;
            counterLock++;

            do{
              bookCoverURL += ca[counter];
              counter++;

            }while(ca[counter] != '<');


            System.out.println(bookCoverURL);
          }
        }
      }

      if(counterLock == 3 ){
        collection.add(new String[]{new String(title),new String(author), new String(bookCoverURL)});
        title = "";
        author = "";
        bookCoverURL = "";
        counterLock = 0;
      }


    }
    return  collection;
  }

}