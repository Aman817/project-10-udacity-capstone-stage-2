package com.robertkiszelirk.newsselector.data.getdata;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.robertkiszelirk.newsselector.data.HandleUrls;
import com.robertkiszelirk.newsselector.data.ParseJsonData;
import com.robertkiszelirk.newsselector.data.model.Article;
import com.robertkiszelirk.newsselector.data.model.GetJsonData;

import org.json.JSONException;

import java.util.ArrayList;

public class DownloadArticlesSearch extends AsyncTaskLoader<ArrayList<Article>> {

    private String searchText;
    private String language;

    public DownloadArticlesSearch(Context context, String searchText, String language) {
        super(context);
        this.searchText = searchText;
        this.language = language;
    }

    @Override
    public ArrayList<Article> loadInBackground() {

        ArrayList<Article> articleList = new ArrayList<>();

        String jsonData = GetJsonData.getJson(HandleUrls.createSearchListUrl(searchText, language));

        try {
            if(jsonData != null) {
                articleList = ParseJsonData.jsonParseForArticlesList(jsonData);
            }else{
                articleList = null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return articleList;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
