package com.robertkiszelirk.newsselector.data;

import android.net.Uri;

import com.robertkiszelirk.newsselector.BuildConfig;

import java.net.MalformedURLException;
import java.net.URL;


public class HandleUrls {

    public static URL createTopHeadlinesListUrl(String country,String category){

        URL movieListUrl = null;

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("newsapi.org")
                .appendPath("v2")
                .appendPath("top-headlines")
                .appendQueryParameter("country",country)
                .appendQueryParameter("category",category)
                .appendQueryParameter("apiKey", BuildConfig.API_KEY);

        try{
            movieListUrl = new URL(builder.build().toString());
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return movieListUrl;
    }

    public static URL createSearchListUrl(String searchText,String language){

        URL movieListUrl = null;

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("newsapi.org")
                .appendPath("v2")
                .appendPath("everything")
                .appendQueryParameter("q",searchText)
                .appendQueryParameter("language",language)
                .appendQueryParameter("sortBy","publishedAt")
                .appendQueryParameter("pageSize","100")
                .appendQueryParameter("apiKey", BuildConfig.API_KEY);

        try{
            movieListUrl = new URL(builder.build().toString());
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return movieListUrl;
    }

}
