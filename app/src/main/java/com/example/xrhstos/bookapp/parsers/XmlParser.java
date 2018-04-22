package com.example.xrhstos.bookapp.parsers;

import com.example.xrhstos.bookapp.Book;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Xrhstos on 4/10/2018.
 */

public class XmlParser {

  public static ArrayList<String> xmlToListOfStrings;

  public static ArrayList<Book> parse(String[] tags, String parent){

    int tagAmount = tags.length;

    int counterLock = 0;
    int[] tagSkipper = new int[tagAmount];
    ArrayList<Book> collection = new ArrayList<>();
    String title = "";
    String[] authors = new String[1];
    String url = "";
    String id = "";

    boolean lookForParent = false;

    boolean inBookData = false;

    for(String str : xmlToListOfStrings){
      //System.out.println(str);
      str = str.trim();
      if(!inBookData && str.startsWith("<" + parent)){
        inBookData = true;
        //collection.add(new Book);
        continue;
      }else if(!inBookData){
        continue;
      }

      if(counterLock == tagAmount){
         lookForParent = true;
         counterLock = 0;
         tagSkipper = new int[tagAmount];
        collection.add(new Book(id,title,authors,url));
        collection.get(collection.size()-1).setGoogleID(null);
         title = "";
        authors = new String[1];
        url = "";
        id = "";
      }
      if(lookForParent){
          if(str.startsWith("</" + parent + ">")){
            lookForParent = false;
            inBookData = false;
          }
      }else{
        for(int i=0; i<tagAmount; i++){

          if(tagSkipper[i] !=0){
            continue;
          }

          if(str.startsWith("<" + tags[i])){

            tagSkipper[i] = 1;
            str = XmlParser.removeTags(str,tags[i]);
            if(tags[i].equals("id")) {
              id = str;
            }else if(tags[i].equals("name")){
              authors[0] = str;
            }else if(tags[i].equals("title")){
              title = str;
            }else if(tags[i].equals("image_url")){
              url = str;
            }

            System.out.println(str);
            counterLock++;

            break;
          }

        }
      }

    }

    return collection;
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

  public static void stringToList(String source){
    String[] lines = source.split("\\r?\\n");
    xmlToListOfStrings = new ArrayList<>();
    Collections.addAll(xmlToListOfStrings, lines);
  }

}