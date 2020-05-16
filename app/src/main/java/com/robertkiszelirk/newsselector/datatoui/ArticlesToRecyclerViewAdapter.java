package com.robertkiszelirk.newsselector.datatoui;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.robertkiszelirk.newsselector.R;
import com.robertkiszelirk.newsselector.data.model.Article;
import com.robertkiszelirk.newsselector.ui.fragment.SaveToReadFragment;

import java.util.ArrayList;

public class ArticlesToRecyclerViewAdapter extends RecyclerView.Adapter<ArticlesToRecyclerViewAdapter.ViewHolder> {

    private final Boolean inSavedArticleState;

    private final ArrayList<Article> articleList;

    private final Context context;

    ArticlesToRecyclerViewAdapter(
            Boolean inSavedArticleState,
            ArrayList<Article> articleList,
            Context context) {
        this.inSavedArticleState = inSavedArticleState;
        this.articleList = articleList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recyclerview_article, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ArticlesToRecyclerViewAdapter.ViewHolder holder, int position) {

        if(!articleList.get(position).getArticleImageUrl().equals("null")) {

            Glide.with(holder.itemView.getContext())
                    .load(articleList.get(position).getArticleImageUrl())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.articleImageProgressBar.setVisibility(View.GONE);
                            holder.articleImage.setVisibility(View.VISIBLE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.articleImageProgressBar.setVisibility(View.GONE);
                            holder.articleImage.setVisibility(View.VISIBLE);
                            return false;
                        }
                    })
                    .into(holder.articleImage);
        }else{
            holder.articleImageProgressBar.setVisibility(View.GONE);
            holder.articleImage.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.articleImage.setImageResource(R.drawable.image_not_available);
        }

        HandleSavedArticles handleSavedArticles = new HandleSavedArticles(context);

        if(handleSavedArticles.checkIfArticleIsSaved(articleList.get(position).getArticleTitle())) {
            holder.articleSave.setScaleType(ImageView.ScaleType.FIT_CENTER);
            holder.articleSave.setImageResource(R.drawable.baseline_delete_white_48dp);
        }else{
            holder.articleSave.setScaleType(ImageView.ScaleType.FIT_CENTER);
            holder.articleSave.setImageResource(R.drawable.baseline_save_white_48dp);
        }

        String articleData = articleList.get(position).getArticleSourceName();
        holder.articleSourcePublish.setText(articleData);
        holder.articleTitle.setText(articleList.get(position).getArticleTitle());

    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final CardView articleCardView;

        final ImageView articleImage;

        final ProgressBar articleImageProgressBar;

        final ImageButton articleSave;

        final TextView articleSourcePublish;
        final TextView articleTitle;

        ViewHolder(final View itemView) {
            super(itemView);

            articleCardView = itemView.findViewById(R.id.article_card_view);
            articleCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(checkInternetConnection()) {
                        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                        CustomTabsIntent customTabsIntent = builder.build();
                        builder.setToolbarColor(context.getResources().getColor(R.color.colorPrimary));
                        builder.setShowTitle(true);
                        builder.setCloseButtonIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.arrow_back_white_24dp));
                        customTabsIntent.launchUrl(context, Uri.parse(articleList.get(getAdapterPosition()).getArticleUrl()));
                    }else {
                        Toast.makeText(context,context.getResources().getString(R.string.no_internet_connection_text_view_text),Toast.LENGTH_SHORT).show();
                    }
                }
            });

            articleImage = itemView.findViewById(R.id.article_card_view_article_image);

            articleImageProgressBar = itemView.findViewById(R.id.article_card_view_article_image_progress_bar);

            articleSave = itemView.findViewById(R.id.article_card_view_article_save_to_read);

            articleSave.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            HandleSavedArticles handleSavedArticles = new HandleSavedArticles(context);

                            if(handleSavedArticles.checkIfArticleIsSaved(articleList.get(getAdapterPosition()).getArticleTitle())){

                                handleSavedArticles.deleteFavoriteFromDatabase(articleList.get(getAdapterPosition()).getArticleTitle());

                                if(inSavedArticleState){

                                    SaveToReadFragment.setSavedArticlesList();

                                }
                                articleSave.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                articleSave.setImageResource(R.drawable.baseline_save_white_48dp);
                            }else{

                                handleSavedArticles.saveArticleToDatabase(
                                        articleList.get(getAdapterPosition()).getArticleTitle(),
                                        articleList.get(getAdapterPosition()).getArticleSourceId(),
                                        articleList.get(getAdapterPosition()).getArticleSourceName(),
                                        articleList.get(getAdapterPosition()).getArticleAuthor(),
                                        articleList.get(getAdapterPosition()).getArticleDescription(),
                                        articleList.get(getAdapterPosition()).getArticleUrl(),
                                        articleList.get(getAdapterPosition()).getArticleImageUrl(),
                                        articleList.get(getAdapterPosition()).getArticlePublishedDate()
                                );
                                articleSave.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                articleSave.setImageResource(R.drawable.baseline_delete_white_48dp);
                            }
                        }
                    }
            );

            articleSourcePublish = itemView.findViewById(R.id.article_card_view_article_data);
            articleTitle = itemView.findViewById(R.id.article_card_view_article_title);

        }
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
