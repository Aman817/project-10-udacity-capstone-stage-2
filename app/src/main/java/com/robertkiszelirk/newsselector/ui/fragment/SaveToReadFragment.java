package com.robertkiszelirk.newsselector.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.robertkiszelirk.newsselector.R;
import com.robertkiszelirk.newsselector.data.model.Article;
import com.robertkiszelirk.newsselector.datatoui.ArticlesToRecyclerView;
import com.robertkiszelirk.newsselector.datatoui.HandleSavedArticles;

import java.util.ArrayList;

public class SaveToReadFragment extends Fragment {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    @SuppressLint("StaticFieldLeak")
    private static RecyclerView savedArticlesRecyclerView;

    public SaveToReadFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.fragment_save_to_read, container, false);

        Toolbar toolbar = mainView.findViewById(R.id.save_to_read_toolbar);
        toolbar.setTitle(R.string.save_to_read_title);

        if (getActivity() != null) {
            ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        }

        ActionBar actionbar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        savedArticlesRecyclerView = mainView.findViewById(R.id.save_to_read_recycler_view);

        context = getContext();

        setSavedArticlesList();

        return mainView;
    }

    public static void setSavedArticlesList(){

        HandleSavedArticles handleSavedArticles = new HandleSavedArticles(context);

        ArrayList<Article> savedArticlesList = handleSavedArticles.getArticles();

        ArticlesToRecyclerView.LoadArticlesListToRecyclerView(
                true,
                savedArticlesList,
                savedArticlesRecyclerView,
                context
        );

    }
}
