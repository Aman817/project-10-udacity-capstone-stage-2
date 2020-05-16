package com.robertkiszelirk.newsselector.data.databasedata;

import android.net.Uri;
import android.provider.BaseColumns;

public class ArticlesContract {

    // Authority to access content provider
    static final String AUTHORITY = "com.robertkiszelirk.newsselector";

    // The base content URI
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "favorites" directory
    static final String PATH_ARTICLES = "articles";

    // The name of the database
    static final String DATABASE_NAME = "articles.db";

    // The version of the database
    static final int DATABASE_VERSION = 1;

    /* ArticlesEntry is an inner class that defines the contents of the favorites table */
    public static final class ArticlesEntry implements BaseColumns {

        // ArticlesEntry content URI
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTICLES).build();

        // Articles table name
        static final String TABLE_NAME = "articles";

        // Articles column names
        public static final String COLUMN_ARTICLE_TITLE = "articleTitle";
        public static final String COLUMN_ARTICLE_SOURCE_ID = "articleSourceId";
        public static final String COLUMN_ARTICLE_SOURCE_NAME = "articleSourceName";
        public static final String COLUMN_ARTICLE_SOURCE_AUTHOR = "articleAuthor";
        public static final String COLUMN_ARTICLE_SOURCE_DESCRIPTION = "articleDescription";
        public static final String COLUMN_ARTICLE_URL = "url";
        public static final String COLUMN_ARTICLE_IMAGE_URL = "imageUrl";
        public static final String COLUMN_ARTICLE_PUBLISHED = "published";

        // Create articles table
        static final String CREATE_TABLE_ARTICLES =
                "CREATE TABLE " + ArticlesEntry.TABLE_NAME + "(" +
                        ArticlesEntry.COLUMN_ARTICLE_TITLE + " TEXT," +
                        ArticlesEntry.COLUMN_ARTICLE_SOURCE_ID + " TEXT," +
                        ArticlesEntry.COLUMN_ARTICLE_SOURCE_NAME + " TEXT," +
                        ArticlesEntry.COLUMN_ARTICLE_SOURCE_AUTHOR + " TEXT," +
                        ArticlesEntry.COLUMN_ARTICLE_SOURCE_DESCRIPTION + " TEXT," +
                        ArticlesEntry.COLUMN_ARTICLE_URL + " TEXT," +
                        ArticlesEntry.COLUMN_ARTICLE_IMAGE_URL + " TEXT," +
                        ArticlesEntry.COLUMN_ARTICLE_PUBLISHED + " TEXT);";

        static final String DELETE_TABLE_ARTICLES =
                "DROP TABLE IF EXISTS " + ArticlesEntry.TABLE_NAME;
    }
}
