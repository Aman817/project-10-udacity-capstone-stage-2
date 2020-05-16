package com.robertkiszelirk.newsselector.ui.fragment;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.robertkiszelirk.newsselector.R;
import com.robertkiszelirk.newsselector.data.Constants;
import com.robertkiszelirk.newsselector.data.getdata.DownloadArticlesTopHeadlines;
import com.robertkiszelirk.newsselector.data.model.Article;
import com.robertkiszelirk.newsselector.datatoui.ArticlesToRecyclerView;

import java.util.ArrayList;

public class CategoriesFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<ArrayList<Article>>{

    private RecyclerView articlesRecyclerView;

    private TextView noDataText;

    private ArrayList<Article> articleArrayList;

    public CategoriesFragment(){}

    // Create new fragment and pass data
    public CategoriesFragment newCategoriesFragment(String country, String category){

        CategoriesFragment categoriesFragment = new CategoriesFragment();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.ARTICLE_COUNTRY,country);
        bundle.putString(Constants.ARTICLE_CATEGORY,category);

        categoriesFragment.setArguments(bundle);

        return categoriesFragment;
    }

    // Create fragment view
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View categoriesView = inflater.inflate(R.layout.fragment_categories, container, false);

        articlesRecyclerView = categoriesView.findViewById(R.id.categories_article_recycler_view);

        noDataText = categoriesView.findViewById(R.id.categories_article_no_data);

        AppCompatTextView articleNoInternetConnection = categoriesView.findViewById(R.id.categories_article_no_internet_connection);

        if(checkInternetConnection()) {

            articleNoInternetConnection.setVisibility(View.GONE);
            // Handle configuration change
            if (savedInstanceState != null) {

                articleArrayList = savedInstanceState.getParcelableArrayList(Constants.ARTICLE_LIST);

                if(articleArrayList != null) {
                    ArticlesToRecyclerView.LoadArticlesListToRecyclerView(
                            false,
                            articleArrayList,
                            articlesRecyclerView,
                            getContext()
                    );
                }else{
                    getLoaderManager().initLoader(0, null, this);
                }

            }
        }else{
            TopHeadlinesFragment.swipeProgressGone();

            articlesRecyclerView.setVisibility(View.GONE);
        }

        AdView adView = categoriesView.findViewById(R.id.adView);
        // Create an ad request.
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        adView.loadAd(adRequest);

        return categoriesView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Initiate loader at first start
        if((savedInstanceState == null)&&(checkInternetConnection())) {
            getLoaderManager().initLoader(0, null, this);
        }
    }

    @NonNull
    @Override
    public Loader<ArrayList<Article>> onCreateLoader(int id, Bundle args) {
        // Download articles from network
        String country;
        String category;

        if(getArguments() != null){
            country = getArguments().getString(Constants.ARTICLE_COUNTRY);
            category = getArguments().getString(Constants.ARTICLE_CATEGORY);
        }else{
            country = Constants.ARTICLE_BASE_COUNTRY;
            category = Constants.ARTICLE_BASE_CATEGORY;
        }
        return new DownloadArticlesTopHeadlines(getContext(),country,category);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Article>> loader, ArrayList<Article> data) {

        TopHeadlinesFragment.swipeProgressGone();

        if(data != null) {
            articlesRecyclerView.setVisibility(View.VISIBLE);
            noDataText.setVisibility(View.GONE);
            articleArrayList = data;
            ArticlesToRecyclerView.LoadArticlesListToRecyclerView(
                    false,
                    data,
                    articlesRecyclerView,
                    getContext());
        }else{
            articlesRecyclerView.setVisibility(View.GONE);
            noDataText.setVisibility(View.VISIBLE);
        }
        getLoaderManager().destroyLoader(0);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Article>> loader) {

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putParcelableArrayList(Constants.ARTICLE_LIST,articleArrayList);
        super.onSaveInstanceState(outState);
    }

    // Check internet connection
    private boolean checkInternetConnection() {
        if(getActivity() != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
