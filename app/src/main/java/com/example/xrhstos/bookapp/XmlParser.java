package com.example.xrhstos.bookapp;

import java.util.ArrayList;

/**
 * Created by Xrhstos on 4/10/2018.
 */

public class XmlParser {

  public static ArrayList<String> xmlToListOfStrings;

  public static ArrayList<String[]> parse(String[] tags, String parent){

    int tagAmount = tags.length;

    int counterLock = 0;
    int[] tagSkipper = new int[tagAmount];
    ArrayList<String[]> collection = new ArrayList<>();

    boolean lookForParent = false;

    boolean inBookData = false;

    for(String str : xmlToListOfStrings){

      str = str.trim();
      if(!inBookData && str.startsWith("<" + parent + ">")){
        inBookData = true;
        collection.add(new String[tags.length]);
        continue;
      }else if(!inBookData){
        continue;
      }

      if(counterLock == tagAmount){
         lookForParent = true;
         counterLock = 0;
         tagSkipper = new int[tagAmount];
      }
      if(lookForParent){
          if(str.startsWith("</" + parent + ">")){
            lookForParent = false;
            inBookData = false;
          }
      }else{
        for(int i=0; i<tagAmount; i++){
          /*
          for(int tagger = 0; tagger<tagAmount; tagger++){
            if(tagSkipper[tagger] == i + 1){
              break tagBreaker;
            }
          }
          */
          if(tagSkipper[i] !=0){
            continue;
          }
          if(str.startsWith("<" + tags[i])){
            tagSkipper[i] = 1;
            str = XmlParser.removeTags(str,tags[i]);
            collection.get(collection.size()-1)[i] = str;
            System.out.println(str);
            counterLock++;

            break;
          }

        }
      }

    }

    return collection;






/*

    char[] ca = source.toCharArray();
    String title = "";
    String author = "";
    String bookCoverURL = "";
   // ArrayList<String[]> collection = new ArrayList<>();


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
    */
  }

  private static String removeTags(String actualString, String tag){

    String str = "";
    int externalCounter = 0;

    firstloop:
    for(int c=0; c<actualString.length(); c++){

      if(actualString.charAt(c) == '<'){
        for(int i=c; i<actualString.length();i++){
          if(actualString.charAt(i) == '>'){
            externalCounter = i + 1;
            break firstloop;
          }
        }
      }

    }

    for(int c=externalCounter; c<actualString.length(); c++){

      if(actualString.charAt(c) != '<'){
        str += actualString.charAt(c);
      }else{
        if(actualString.charAt(c+1) == '/' && actualString.charAt(c+2) == tag.charAt(0)){
          break;
        }
      }

    }

    return str;
  }

}