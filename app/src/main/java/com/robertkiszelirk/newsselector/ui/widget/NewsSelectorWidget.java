package com.robertkiszelirk.newsselector.ui.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.PowerManager;
import android.view.View;
import android.widget.RemoteViews;

import com.robertkiszelirk.newsselector.R;
import com.robertkiszelirk.newsselector.data.WidgetGetArticleData;
import com.robertkiszelirk.newsselector.datatoui.WidgetUpdateService;
import com.robertkiszelirk.newsselector.ui.activity.NewsSelectorActivity;

public class NewsSelectorWidget extends AppWidgetProvider{

    private Boolean refreshView = true;

    private String refresh="refresh";

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context.getApplicationContext(), NewsSelectorWidget.class));

        final String action = intent.getAction();

        if (action != null && action.equals(WidgetGetArticleData.UPDATE_ARTICLES_LIST)) {

            refreshView = false;
            onUpdate(context, appWidgetManager, appWidgetIds);
        }

        if(action!=null&&refresh.equals(action)){

            onUpdate(context,appWidgetManager,appWidgetIds);
        }

        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(
            Context context,
            AppWidgetManager appWidgetManager,
            int[] appWidgetIds) {

        this.context = context;

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm != null && pm.isInteractive()) {

            for (int appWidgetId : appWidgetIds) {

                RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget_news_selector);

                // Set title
                SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_pref_name),Context.MODE_PRIVATE);
                rv.setTextViewText(R.id.widget_news_country, sharedPreferences.getString(context.getString(R.string.shared_pref_country),context.getString(R.string.widget_title_default_title)));
                // Set refresh button
                rv.setOnClickPendingIntent(R.id.widget_refresh_button, getPendingSelfIntent(context, refresh));

                if(checkInternetConnection()) {
                    rv.setViewVisibility(R.id.widget_article_list,View.VISIBLE);
                    rv.setViewVisibility(R.id.widget_article_list_empty_view,View.GONE);
                    Intent intent = new Intent(context, WidgetUpdateService.class);
                    // Add the app widget ID to the intent extras.
                    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                    intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
                    // Set up the RemoteViews object to use a RemoteViews adapter.
                    rv.setRemoteAdapter(R.id.widget_article_list, intent);
                    // The empty view is displayed when the collection has no items.
                    rv.setEmptyView(R.id.widget_article_list, R.id.widget_article_list_empty_view);
                    // template to handle the click listener for each item
                    Intent clickIntentTemplate = new Intent(context, NewsSelectorActivity.class);
                    PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                            .addNextIntentWithParentStack(clickIntentTemplate)
                            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    rv.setPendingIntentTemplate(R.id.widget_article_list, clickPendingIntentTemplate);
                    // Update list
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_article_list);
                }else{
                    rv.setViewVisibility(R.id.widget_article_list,View.GONE);
                    rv.setViewVisibility(R.id.widget_article_list_empty_view,View.VISIBLE);
                    rv.setTextViewText(R.id.widget_article_list_empty_view,context.getResources().getString(R.string.no_internet_connection_text_view_text));
                }

                appWidgetManager.updateAppWidget(appWidgetId, rv);

                refreshView = true;
            }
        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    // Check internet connection
    private boolean checkInternetConnection() {
        if(context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
