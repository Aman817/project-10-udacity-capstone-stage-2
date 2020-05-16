package com.robertkiszelirk.newsselector.datatoui;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.robertkiszelirk.newsselector.data.databasedata.ArticlesContract;
import com.robertkiszelirk.newsselector.data.model.Article;

import java.util.ArrayList;

public class HandleSavedArticles {

    private final Context context;

    public HandleSavedArticles(Context context){
        this.context = context;
    }

    void saveArticleToDatabase(
            String title,
            String sourceId,
            String sourceName,
            String author,
            String description,
            String url,
            String imageUrl,
            String published){

        ContentValues contentValues = new ContentValues();
        contentValues.put(ArticlesContract.ArticlesEntry.COLUMN_ARTICLE_TITLE,title);
        contentValues.put(ArticlesContract.ArticlesEntry.COLUMN_ARTICLE_SOURCE_ID,sourceId);
        contentValues.put(ArticlesContract.ArticlesEntry.COLUMN_ARTICLE_SOURCE_NAME,sourceName);
        contentValues.put(ArticlesContract.ArticlesEntry.COLUMN_ARTICLE_SOURCE_AUTHOR,author);
        contentValues.put(ArticlesContract.ArticlesEntry.COLUMN_ARTICLE_SOURCE_DESCRIPTION,description);
        contentValues.put(ArticlesContract.ArticlesEntry.COLUMN_ARTICLE_URL,url);
        contentValues.put(ArticlesContract.ArticlesEntry.COLUMN_ARTICLE_IMAGE_URL,imageUrl);
        contentValues.put(ArticlesContract.ArticlesEntry.COLUMN_ARTICLE_PUBLISHED,published);
        context.getContentResolver().insert(ArticlesContract.ArticlesEntry.CONTENT_URI,contentValues);

    }

    void deleteFavoriteFromDatabase(String articleTitle){

        String selection = ArticlesContract.ArticlesEntry.COLUMN_ARTICLE_TITLE + "=?";
        String[] selectionArgs = {articleTitle};
        context.getContentResolver().delete(ArticlesContract.ArticlesEntry.CONTENT_URI, selection, selectionArgs);

    }

    boolean checkIfArticleIsSaved(String articleTitle){
        Cursor cursor = context.getContentResolver().query(
                ArticlesContract.ArticlesEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                if(articleTitle.equals(
                        cursor.getString(
                                cursor.getColumnIndex(
                                        ArticlesContract.ArticlesEntry.COLUMN_ARTICLE_TITLE)))) {
                    return true;
                }
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return false;
    }

    public ArrayList<Article> getArticles(){

        ArrayList<Article> articlesList = new ArrayList<>();

        Cursor cursor = context.getContentResolver().query(
                ArticlesContract.ArticlesEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        if (cursor != null) {
            while (cursor.moveToNext()) {

                articlesList
                        .add(new Article(
                                cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticlesEntry.COLUMN_ARTICLE_SOURCE_ID)),
                                cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticlesEntry.COLUMN_ARTICLE_SOURCE_NAME)),
                                cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticlesEntry.COLUMN_ARTICLE_SOURCE_AUTHOR)),
                                cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticlesEntry.COLUMN_ARTICLE_TITLE)),
                                cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticlesEntry.COLUMN_ARTICLE_SOURCE_DESCRIPTION)),
                                cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticlesEntry.COLUMN_ARTICLE_URL)),
                                cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticlesEntry.COLUMN_ARTICLE_IMAGE_URL)),
                                cursor.getString(cursor.getColumnIndex(ArticlesContract.ArticlesEntry.COLUMN_ARTICLE_PUBLISHED))
                        ));

            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return articlesList;
    }

}
