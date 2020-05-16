package com.robertkiszelirk.newsselector.datatoui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.robertkiszelirk.newsselector.R;
import com.robertkiszelirk.newsselector.data.getdata.DownloadArticlesTopHeadlines;
import com.robertkiszelirk.newsselector.data.model.Article;

import java.util.ArrayList;


public class WidgetUpdateService extends RemoteViewsService{

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new GridRemoteViewsFactory(this.getApplicationContext());
    }


    private class GridRemoteViewsFactory implements RemoteViewsFactory {

        private ArrayList<Article> articleArrayList;
        private Context context;


        GridRemoteViewsFactory(Context context) {
            this.context = context;
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            if(checkInternetConnection()) {
                SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_pref_name),Context.MODE_PRIVATE);
                articleArrayList = new DownloadArticlesTopHeadlines(
                        getApplicationContext(),
                        sharedPreferences.getString(context.getResources().getString(R.string.shared_pref_country_id),"us"),
                        "general")
                                .loadInBackground();
                if(articleArrayList == null){
                    articleArrayList = new ArrayList<>();
                    articleArrayList.add(new Article(
                            null,
                            context.getResources().getString(R.string.no_data_text),
                            null,
                            context.getResources().getString(R.string.no_data_text),
                            null,
                            null,
                            null,
                            null
                    ));
                }
            }
         }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return articleArrayList.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {

            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_list_item);
            rv.setTextViewText(R.id.widget_article_title, articleArrayList.get(position).getArticleTitle());
            rv.setTextViewText(R.id.widget_article_source, articleArrayList.get(position).getArticleSourceName());
            try {
                Bitmap bitmap = Glide.with(context)
                        .asBitmap()
                        .load(articleArrayList.get(position).getArticleImageUrl())
                        .submit(512, 512)
                        .get();

                rv.setImageViewBitmap(R.id.widget_article_image, bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent intent = new Intent();
            intent.putExtra("articleUrl", articleArrayList.get(position).getArticleUrl());
            rv.setOnClickFillInIntent(R.id.widget_article_list_item, intent);
            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }

    // Check internet connection
    private boolean checkInternetConnection() {
        if(this.getApplicationContext() != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = null;
            if (connectivityManager != null) {
                activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            }
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }else{
            return false;
        }
    }
}
