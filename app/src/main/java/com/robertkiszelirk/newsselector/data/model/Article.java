package com.robertkiszelirk.newsselector.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Article implements Parcelable {

    private final String articleSourceId;
    private final String articleSourceName;
    private final String articleAuthor;
    private final String articleTitle;
    private final String articleDescription;
    private final String articleUrl;
    private final String articleImageUrl;
    private final String articlePublishedDate;

    public Article(
            String articleSourceId,
            String articleSourceName,
            String articleAuthor,
            String articleTitle,
            String articleDescription,
            String articleUrl,
            String articleImageUrl,
            String articlePublishedDate
    ){
        this.articleSourceId = articleSourceId;
        this.articleSourceName =articleSourceName;
        this.articleAuthor = articleAuthor;
        this.articleTitle = articleTitle;
        this.articleDescription = articleDescription;
        this.articleUrl = articleUrl;
        this.articleImageUrl = articleImageUrl;
        this.articlePublishedDate = articlePublishedDate;
    }

    public String getArticleSourceId() {
        return articleSourceId;
    }

    public String getArticleSourceName() {
        return articleSourceName;
    }

    public String getArticleAuthor() {
        return articleAuthor;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public String getArticleDescription() {
        return articleDescription;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public String   getArticleImageUrl() {
        return articleImageUrl;
    }

    public String getArticlePublishedDate() {
        return articlePublishedDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.articleSourceId);
        dest.writeString(this.articleSourceName);
        dest.writeString(this.articleAuthor);
        dest.writeString(this.articleTitle);
        dest.writeString(this.articleDescription);
        dest.writeString(this.articleUrl);
        dest.writeString(this.articleImageUrl);
        dest.writeString(this.articlePublishedDate);
    }

    protected Article(Parcel in) {
        this.articleSourceId = in.readString();
        this.articleSourceName = in.readString();
        this.articleAuthor = in.readString();
        this.articleTitle = in.readString();
        this.articleDescription = in.readString();
        this.articleUrl =  in.readString();
        this.articleImageUrl =  in.readString();
        this.articlePublishedDate = in.readString();
    }

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
