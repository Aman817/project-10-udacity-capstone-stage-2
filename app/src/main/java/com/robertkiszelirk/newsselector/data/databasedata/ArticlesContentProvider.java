package com.robertkiszelirk.newsselector.data.databasedata;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ArticlesContentProvider extends ContentProvider {

    private final static int ARTICLES = 100;
    private final static int ARTICLE_WITH_ID = 101;
    private final static UriMatcher uriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /*
          All paths added to the UriMatcher have a corresponding int.
          For each kind of uri you may want to access, add the corresponding match with addURI.
          The two calls below add matches for the article directory and a single item by ID.
         */
        uriMatcher.addURI(ArticlesContract.AUTHORITY, ArticlesContract.PATH_ARTICLES, ARTICLES);
        uriMatcher.addURI(ArticlesContract.AUTHORITY, ArticlesContract.PATH_ARTICLES + "/#", ARTICLE_WITH_ID);

        return uriMatcher;
    }

    private ArticlesDbHelper dbHelper;

    @Override
    public boolean onCreate() {

        dbHelper = new ArticlesDbHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(
            @NonNull Uri uri,
            @Nullable String[] projection,
            @Nullable String selection,
            @Nullable String[] selectionArgs,
            @Nullable String sortOrder) {

        // Get access to underlying database (read-only for query)
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Write URI match code and set a variable to return a Cursor
        int match = uriMatcher.match(uri);
        Cursor retCursor;

        // Query for the tasks directory and write a default case
        switch (match) {
            // Query for the tasks directory
            case ARTICLES:
                retCursor = db.query(
                        ArticlesContract.ArticlesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            // Default exception
            default:
                db.close();
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // Get access to the task database (to write new data to)
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Write URI matching code to identify the match for the tasks directory
        int match = uriMatcher.match(uri);
        Uri returnUri; // URI to be returned

        switch (match) {
            case ARTICLES:
                // Insert new values into the database
                // Inserting values into favorites table
                long id = db.insert(ArticlesContract.ArticlesEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(ArticlesContract.ArticlesEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // Set the value for the returnedUri and write the default case for unknown URI's
            // Default case throws an UnsupportedOperationException
            default:
                db.close();
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        db.close();
        // Return constructed uri (this points to the newly inserted row of data)
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get access to the database and write URI matching code to recognize a single item
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);
        // Keep track of the number of deleted tasks
        int articleDeleted; // starts as 0

        // The code to delete a single row of data
        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case ARTICLES:
                // Use selections/selectionArgs to filter for this ID
                articleDeleted = db.delete(ArticlesContract.ArticlesEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                db.close();
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (articleDeleted != 0) {
            // A favorite was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }
        db.close();
        // Return the number of favorites deleted
        return articleDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
