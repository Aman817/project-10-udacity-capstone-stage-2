package com.robertkiszelirk.newsselector.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.robertkiszelirk.newsselector.R;
import com.robertkiszelirk.newsselector.data.Constants;
import com.robertkiszelirk.newsselector.data.getdata.DownloadArticlesSearch;
import com.robertkiszelirk.newsselector.data.model.Article;
import com.robertkiszelirk.newsselector.datatoui.ArticlesToRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<ArrayList<Article>>{

    @BindView(R.id.search_recycler_view)
    RecyclerView searchRecyclerView;

    @BindView(R.id.search_no_internet_connection)
    AppCompatTextView searchNoInternetConnection;

    @BindView(R.id.search_no_data)
    AppCompatTextView searchNoData;

    @BindView(R.id.search_toolbar)
    Toolbar toolbar;

    private ArrayList<Article> articleArrayList;

    private String searchText;

    private String selectedLanguage;

    SharedPreferences sharedPreferences;

    SharedPreferences.Editor sharedPrefEditor;

    public SearchFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.fragment_search, container, false);

        ButterKnife.bind(this,mainView);

        toolbar = mainView.findViewById(R.id.search_toolbar);
        toolbar.setTitle(R.string.search_article_title);

        if ((getActivity()) != null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        }

        ActionBar actionbar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        // Get saved language from shared preferences
        if(getContext() != null) {
            sharedPreferences = getContext().getSharedPreferences(getString(R.string.shared_pref_name), Context.MODE_PRIVATE);

            sharedPrefEditor = sharedPreferences.edit();

            if (!sharedPreferences.contains(Constants.ARTICLE_SEARCH_LANGUAGE)) {
                sharedPrefEditor.putString(Constants.ARTICLE_SEARCH_LANGUAGE, Constants.ARTICLE_BASE_SEARCH_LANGUAGE);
                sharedPrefEditor.apply();
                selectedLanguage = Constants.ARTICLE_BASE_SEARCH_LANGUAGE;
            } else {
                selectedLanguage = sharedPreferences.getString(Constants.ARTICLE_SEARCH_LANGUAGE, Constants.ARTICLE_BASE_SEARCH_LANGUAGE);
            }
        }else{
            selectedLanguage = Constants.ARTICLE_BASE_SEARCH_LANGUAGE;
        }

        // Set spinner to select language
        final Spinner languageSelector = mainView.findViewById(R.id.search_select_language);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.languages));
        languageSelector.setAdapter(adapter);
        languageSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLanguage = getResources().getStringArray(R.array.languages_id)[position];
                sharedPrefEditor.putString(Constants.ARTICLE_SEARCH_LANGUAGE,selectedLanguage);
                sharedPrefEditor.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Get id of language
        List<String> languageIdList = Arrays.asList(getResources().getStringArray(R.array.languages_id));
        languageSelector.setSelection(languageIdList.indexOf(selectedLanguage));

        // Handle configuration change
        if(savedInstanceState != null){
            if(checkInternetConnection()) {
                searchRecyclerView.setVisibility(View.VISIBLE);
                searchNoInternetConnection.setVisibility(View.GONE);

                searchText = savedInstanceState.getString(Constants.ARTICLE_SEARCH_TEXT);
                articleArrayList = savedInstanceState.getParcelableArrayList(Constants.ARTICLE_LIST);

                if(searchText != null && articleArrayList != null) {
                    toolbar.setTitle(searchText);
                    searchRecyclerView.setVisibility(View.VISIBLE);
                    searchNoData.setVisibility(View.GONE);
                    // Add data to RecyclerView
                    ArticlesToRecyclerView.LoadArticlesListToRecyclerView(
                            false,
                            articleArrayList,
                            searchRecyclerView,
                            getContext()
                    );
                }else{
                    searchRecyclerView.setVisibility(View.GONE);
                    searchNoData.setVisibility(View.VISIBLE);
                }
            }else{
                searchRecyclerView.setVisibility(View.GONE);
                searchNoInternetConnection.setVisibility(View.VISIBLE);
            }
        }

        return mainView;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        inflater.inflate( R.menu.search_fragment_toolbar_icons, menu);

        // Set search view
        final MenuItem myActionMenuItem = menu.findItem( R.id.search_article);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                    searchView.setIconified(true);
                }

                if(checkInternetConnection()) {
                    searchRecyclerView.setVisibility(View.VISIBLE);
                    searchNoInternetConnection.setVisibility(View.GONE);
                    // Handle search text
                    toolbar.setTitle(query);
                    searchText = query;
                    startSearch();
                }else{
                    searchRecyclerView.setVisibility(View.GONE);
                    searchNoInternetConnection.setVisibility(View.VISIBLE);
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });
    }

    public void startSearch(){
        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @NonNull
    @Override
    public Loader<ArrayList<Article>> onCreateLoader(int id, @Nullable Bundle args) {
        return new DownloadArticlesSearch(
                getContext(),
                searchText,
                selectedLanguage);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<Article>> loader, ArrayList<Article> data) {

        if(data != null) {
            searchRecyclerView.setVisibility(View.VISIBLE);
            searchNoData.setVisibility(View.GONE);
            articleArrayList = data;
            // Add data to RecyclerView
            ArticlesToRecyclerView.LoadArticlesListToRecyclerView(
                    false,
                    data,
                    searchRecyclerView,
                    getContext()
            );
        }else{
            searchRecyclerView.setVisibility(View.GONE);
            searchNoData.setVisibility(View.VISIBLE);
        }

        getLoaderManager().destroyLoader(1);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<Article>> loader) {

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        outState.putString(Constants.ARTICLE_SEARCH_TEXT,searchText);
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
