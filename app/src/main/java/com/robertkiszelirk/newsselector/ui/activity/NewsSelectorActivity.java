package com.robertkiszelirk.newsselector.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.robertkiszelirk.newsselector.R;
import com.robertkiszelirk.newsselector.data.AnalyticsApplication;
import com.robertkiszelirk.newsselector.data.Constants;
import com.robertkiszelirk.newsselector.ui.fragment.SaveToReadFragment;
import com.robertkiszelirk.newsselector.ui.fragment.SearchFragment;
import com.robertkiszelirk.newsselector.ui.fragment.TopHeadlinesFragment;
import com.robertkiszelirk.newsselector.ui.widget.NewsSelectorWidget;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsSelectorActivity extends AppCompatActivity {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.nav_view)
    NavigationView navigationDrawerView;

    @BindView(R.id.fragment_container)
    FrameLayout fragmentContainer;

    @BindView(R.id.activity_news_selector_no_internet_connection_text_view)
    AppCompatTextView noInternetConnection;

    TopHeadlinesFragment topHeadlinesFragment;

    Fragment fragment;

    Class fragmentClass;

    SharedPreferences sharedPreferences;

    SharedPreferences.Editor sharedPrefEditor;

    Boolean refreshView = true;

    int alertDialogWidth = 1000;

    int alertDialogHeight = 1400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_selector);

        ButterKnife.bind(this);

        GoogleAnalytics.getInstance(this).setLocalDispatchPeriod(10);
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        Tracker tracker = application.getDefaultTracker();
        tracker.setScreenName("Main Screen");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());

        if(checkInternetConnection()) {
            noInternetConnection.setVisibility(View.GONE);
            fragmentContainer.setVisibility(View.VISIBLE);
            // Handle start from widget
            if(getIntent().getExtras() != null && getIntent().getExtras().getString(Constants.ARTICLE_URL) != null){
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));
                builder.setShowTitle(true);
                builder.setCloseButtonIcon(BitmapFactory.decodeResource(getResources(), R.drawable.arrow_back_white_24dp));
                customTabsIntent.launchUrl(this, Uri.parse(getIntent().getExtras().getString(Constants.ARTICLE_URL)));
            }
            // Handle configuration change
            if (savedInstanceState == null) {
                topHeadlinesFragment = new TopHeadlinesFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, topHeadlinesFragment).commit();
            } else {

                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory(Constants.TRACKER_CATEGORY)
                        .setAction(Constants.TRACKER_ACTION)
                        .build());

                refreshView = false;
            }
        }else{
            noInternetConnection.setVisibility(View.VISIBLE);
            fragmentContainer.setVisibility(View.GONE);
        }

        // Setup navigation drawer
        setupDrawerContent(navigationDrawerView);

        // Get shared preferences
        sharedPreferences = getSharedPreferences(getString(R.string.shared_pref_name),Context.MODE_PRIVATE);

        // Set shared preferences for saving
        sharedPrefEditor = sharedPreferences.edit();

        // Check if it has saved default value
        if(!sharedPreferences.contains(getString(R.string.shared_pref_country))) {

            // If not create one
            sharedPrefEditor.putString(getString(R.string.shared_pref_country), getString(R.string.shared_pref_country_name_default));
            sharedPrefEditor.putString(getString(R.string.shared_pref_country_id), getString(R.string.shared_pref_country_id_default_value));
            for(int i = 0; i < getResources().getStringArray(R.array.categories).length; i++){
                sharedPrefEditor.putBoolean(getResources().getStringArray(R.array.categories)[i], true);
            }
            sharedPrefEditor.apply();
        }
    }

    // Set up navigation view
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // Respond to navigation item selection
                    @Override
                    public boolean onNavigationItemSelected(@Nullable MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return false;
                    }
                });
    }

    // Handle navigation item selection
    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on navigation item clicked
        fragment = null;
        fragmentClass = null;
        switch(menuItem.getItemId()) {
            // Loads top headlines view
            case R.id.navigation_drawer_top_headlines:
                if(checkInternetConnection()){
                    fragmentContainer.setVisibility(View.VISIBLE);
                    noInternetConnection.setVisibility(View.GONE);
                    fragmentClass = TopHeadlinesFragment.class;
                    refreshView = true;
                }else{
                    fragmentContainer.setVisibility(View.GONE);
                    noInternetConnection.setVisibility(View.VISIBLE);
                }
                break;
            // Start alert dialog to select one country for top headlines
            case R.id.navigation_drawer_top_headlines_select_country:
                AlertDialog.Builder countryBuilder = new AlertDialog.Builder(this);
                countryBuilder.setTitle(R.string.news_selector_activity_alert_dialog_select_country)
                        .setItems(R.array.countries, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                noInternetConnection.setVisibility(View.GONE);
                                fragmentContainer.setVisibility(View.VISIBLE);
                                // Save selection to shared preferences
                                sharedPrefEditor.putString(getResources().getString(R.string.shared_pref_country), getResources().getStringArray(R.array.countries)[which]);
                                sharedPrefEditor.putString(getResources().getString(R.string.shared_pref_country_id), getResources().getStringArray(R.array.countries_id)[which]);
                                sharedPrefEditor.apply();
                                // Update widget
                                Intent intent = new Intent(getApplicationContext(), NewsSelectorWidget.class);
                                intent.setAction(Constants.WIDGET_REFRESH);
                                sendBroadcast(intent);
                                if(checkInternetConnection()) {
                                    // Change fragment
                                    fragmentClass = TopHeadlinesFragment.class;
                                    setFragment();
                                }else{
                                    // Handle no network state
                                    noInternetConnection.setVisibility(View.VISIBLE);
                                    fragmentContainer.setVisibility(View.GONE);
                                }
                            }
                        });
                // Create Alert Dialog
                AlertDialog countryAlertDialog = countryBuilder.create();
                countryAlertDialog.show();
                if(countryAlertDialog.getWindow() != null) {
                    countryAlertDialog.getWindow().setLayout(alertDialogWidth, alertDialogHeight);
                }
                refreshView = false;
                break;
            // Start alert dialog to select category for top headlines
            case R.id.navigation_drawer_top_headlines_select_category:

                // Create list to save check/uncheck category
                final boolean[] checkedItems = new boolean[getResources().getStringArray(R.array.categories).length];

                // Get values from shared preferences
                for(int i = 0; i < getResources().getStringArray(R.array.categories).length;i++){
                    checkedItems[i] = sharedPreferences.getBoolean(getResources().getStringArray(R.array.categories)[i],true);
                }

                // Create Alert Dialog to select category
                AlertDialog.Builder categoryBuilder = new AlertDialog.Builder(this);
                categoryBuilder.setTitle(R.string.news_selector_activity_alert_dialog_select_category)
                        .setMultiChoiceItems(R.array.categories_text, checkedItems,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which,
                                                        boolean isChecked) {
                                        // If the user checked the item, add it to the selected items
                                        checkedItems[which] = isChecked;
                                    }
                                })
                        // Set the action buttons
                        .setPositiveButton(R.string.news_selector_activity_alert_dialog_select_category_positive_action_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // Save categories selection
                                for(int i = 0; i < getResources().getStringArray(R.array.categories).length;i++){
                                    sharedPrefEditor.putBoolean(getResources().getStringArray(R.array.categories)[i], checkedItems[i]);
                                    sharedPrefEditor.apply();
                                }
                                if(checkInternetConnection()){
                                    fragmentContainer.setVisibility(View.VISIBLE);
                                    noInternetConnection.setVisibility(View.GONE);
                                    fragmentClass = TopHeadlinesFragment.class;
                                    setFragment();
                                }else{
                                    fragmentContainer.setVisibility(View.GONE);
                                    noInternetConnection.setVisibility(View.VISIBLE);
                                }
                            }
                        })
                        .setNegativeButton(R.string.news_selector_activity_alert_dialog_select_category_negative_action_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog categoryAlertDialog = categoryBuilder.create();
                categoryAlertDialog.show();
                if(categoryAlertDialog.getWindow() != null) {
                    categoryAlertDialog.getWindow().setLayout(alertDialogWidth, alertDialogHeight);
                }
                refreshView = false;
                break;
            // Loads search view
            case R.id.navigation_drawer_search:
                if(checkInternetConnection()){
                    fragmentContainer.setVisibility(View.VISIBLE);
                    noInternetConnection.setVisibility(View.GONE);
                    fragmentClass = SearchFragment.class;
                    refreshView = true;
                }else{
                    fragmentContainer.setVisibility(View.GONE);
                    noInternetConnection.setVisibility(View.VISIBLE);
                }
                break;

            // Loads save to read view
            case R.id.navigation_drawer_save_to_read:
                if(checkInternetConnection()){
                    fragmentContainer.setVisibility(View.VISIBLE);
                    noInternetConnection.setVisibility(View.GONE);
                    fragmentClass = SaveToReadFragment.class;
                    refreshView = true;
                }else{
                    fragmentContainer.setVisibility(View.GONE);
                    noInternetConnection.setVisibility(View.VISIBLE);
                }
                break;
        }

        // Refresh view if it is changed
        if((refreshView)&&(checkInternetConnection())) {
            setFragment();
        }

        // Close Navigation Drawer
        drawerLayout.closeDrawers();

    }

    // Set up fragments
    private void setFragment() {

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();


    }

    // Opens navigation Drawer
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    // Check internet connection
    private boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
