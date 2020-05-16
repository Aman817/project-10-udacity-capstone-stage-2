package com.robertkiszelirk.newsselector.data.getdata;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;

import com.robertkiszelirk.newsselector.data.HandleUrls;
import com.robertkiszelirk.newsselector.data.ParseJsonData;
import com.robertkiszelirk.newsselector.data.model.Article;
import com.robertkiszelirk.newsselector.data.model.GetJsonData;
import org.json.JSONException;
import java.util.ArrayList;

public class DownloadArticlesTopHeadlines extends AsyncTaskLoader<ArrayList<Article>> {

    private String country;
    private String category;

    public DownloadArticlesTopHeadlines(Context context, String country, String category) {
        super(context);
        this.country = country;
        this.category = category;
    }

    @Override
    public ArrayList<Article> loadInBackground() {

        ArrayList<Article> articleList = new ArrayList<>();

        String jsonData = GetJsonData.getJson(HandleUrls.createTopHeadlinesListUrl(country,category));

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
