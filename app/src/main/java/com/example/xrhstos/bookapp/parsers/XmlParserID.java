package com.example.xrhstos.bookapp.parsers;

import com.example.xrhstos.bookapp.Book;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Xrhstos on 4/10/2018.
 */

public class XmlParserID {

  public static ArrayList<String> xmlToListOfStrings;

  public static Book parse(String[] tags, String parent, Book fetchedBook){

    int tagAmount = tags.length;
    // "isbn","publication_year","publication_month"
            //    ,"publication_day","description","average_rating","num_pages","url"
    int counterLock = 0;
    int[] tagSkipper = new int[tagAmount];
    String ISBN = "";
    String publication_year = "";
    String publication_month = "";
    String publication_day = "";
    String description = "";
    String average_rating = "";
    String num_pages = "";
    String url = "";

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
        fetchedBook.setISBN(Integer.valueOf(ISBN));
        fetchedBook.setPageCount(Integer.valueOf(num_pages));
        fetchedBook.setPreviewURL(url);
        fetchedBook.setDescription(description);
        fetchedBook.setAverageRating(Float.valueOf(average_rating));
        fetchedBook.setPublishedDate(publication_day+"/"+publication_month+"/"+publication_year);
        fetchedBook.setBuyURL("https://www.goodreads.com/book_link/follow/8036?book_id="+fetchedBook.getId());
        break;
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
            str = XmlParserID.removeTags(str,tags[i]);

            // "isbn","publication_year","publication_month"
            //    ,"publication_day","description","average_rating","num_pages","url"
            if(tags[i].equals("isbn")) {
              ISBN = removeCDATA(str);
            }else if(tags[i].equals("publication_year")){
              publication_year = str;
            }else if(tags[i].equals("publication_month")){
              publication_month = str;
            }else if(tags[i].equals("publication_day")){
              publication_day = str;
            }else if(tags[i].equals("description")){
              description = removeCDATA(str);
            }else if(tags[i].equals("average_rating")){
              average_rating = str;
            }else if(tags[i].equals("num_pages")){
              num_pages = removeCDATA(str);
            }else if(tags[i].equals("url")){
              url = removeCDATA(str);
            }

            System.out.println(str);
            counterLock++;

            break;
          }

        }
      }

    }

    return fetchedBook;
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
        }else{
          str += actualString.charAt(c);
        }
      }

    }

    return str;
  }

//<isbn><![CDATA[0060853980]]></isbn>
  private static String removeCDATA (String s) {
    s = s.trim();
    if (s.startsWith("<![CDATA[")) {
      s = s.substring(9);
      int i = s.indexOf("]]>");
      if (i == -1) throw new IllegalStateException("argument starts with <![CDATA[ but cannot find pairing ]]>");
      s = s.substring(0, i);
    }
    return s;
  }

  public static void stringToList(String source){
    String[] lines = source.split("\\r?\\n");
    xmlToListOfStrings = new ArrayList<>();
    Collections.addAll(xmlToListOfStrings, lines);
  }

}