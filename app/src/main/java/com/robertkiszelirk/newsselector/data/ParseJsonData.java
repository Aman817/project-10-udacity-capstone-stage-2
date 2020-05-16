package com.robertkiszelirk.newsselector.data;

import com.robertkiszelirk.newsselector.data.model.Article;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;


public class ParseJsonData {

    public static ArrayList<Article> jsonParseForArticlesList(String jsonString) throws JSONException {

        ArrayList<Article> articlesList = new ArrayList<>();

        JSONObject mainData = new JSONObject(jsonString);

        JSONArray articlesJsonArray = mainData.getJSONArray("articles");

        for( int i = 0; i < articlesJsonArray.length(); i++){

            JSONObject article = articlesJsonArray.getJSONObject(i);

            JSONObject articleSource = article.getJSONObject("source");

            articlesList.add(new Article(
                    (articleSource.getString("id") != null) ? articleSource.getString("id") : "No source ID.",
                    (articleSource.getString("name") != null) ? articleSource.getString("name") : "No source.",
                    (article.getString("author") != null) ? article.getString("author") : "No author.",
                    (article.getString("title") != null) ? article.getString("title") : "No title",
                    (article.getString("description") != null) ? article.getString("description") : "No description",
                    article.getString("url"),
                    article.getString("urlToImage"),
                    (article.getString("publishedAt") != null) ? article.getString("publishedAt") : "No time"

            ));
        }
        return articlesList;
    }

}
