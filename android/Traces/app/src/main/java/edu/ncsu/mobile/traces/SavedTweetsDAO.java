package edu.ncsu.mobile.traces;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SavedTweetsDAO {

    private SQLiteDatabase database;
    private SQLiteDriver dbHelper;
    private String[] allColumns = { SQLiteDriver.COLUMN_ID, SQLiteDriver.COLUMN_LAT,
            SQLiteDriver.COLUMN_LNG, SQLiteDriver.COLUMN_CREATED_AT,
            SQLiteDriver.COLUMN_FAVORITE_COUNT, SQLiteDriver.COLUMN_TWEET_ID,
            SQLiteDriver.COLUMN_TEXT, SQLiteDriver.COLUMN_DESCRIPTION,
            SQLiteDriver.COLUMN_USER_ID, SQLiteDriver.COLUMN_USER_PROFILE_LOCATION,
            SQLiteDriver.COLUMN_USER_NAME, SQLiteDriver.COLUMN_PROFILE_IMAGE_URL,
            SQLiteDriver.COLUMN_USER_SCREEN_NAME, SQLiteDriver.COLUMN_USER_URL,
            SQLiteDriver.COLUMN_POSSIBLY_SENSITIVE, SQLiteDriver.COLUMN_DISTANCE };

    public SavedTweetsDAO(Context context) {
        dbHelper = new SQLiteDriver(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    //SQL Query needs to be updated, SHOULD NOT CREATE BASED OFF STRING UNTIL MO TELLS ME WHAT OBJECT WE'RE USING
    public SavedTweets createTweet(Intel tweet) {
        ContentValues values = new ContentValues();
        values.put(SQLiteDriver.TABLE_NAME, tweet.toString());
        long insertId = database.insert(SQLiteDriver.TABLE_NAME, null,
                values);
        Cursor cursor = database.query(SQLiteDriver.TABLE_NAME,
                allColumns, SQLiteDriver.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        SavedTweets newTweet = cursorToTweet(cursor);
        cursor.close();
        return newTweet;
    }

    public void deleteTweet(Long _id) {
        //long id = tweet.getColumnID();
        long id = _id;
        System.out.println("Tweet deleted with id: " + id);
        database.delete(SQLiteDriver.TABLE_NAME, SQLiteDriver.COLUMN_ID
                + " = " + id, null);
    }

    public List<SavedTweets> getAllTweets() {
        List<SavedTweets> tweets = new ArrayList<SavedTweets>();

        Cursor cursor = database.query(SQLiteDriver.TABLE_NAME,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            SavedTweets tweet = cursorToTweet(cursor);
            tweets.add(tweet);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return tweets;
    }

    private SavedTweets cursorToTweet(Cursor cursor) {
        SavedTweets tweet = new SavedTweets();
        tweet.setColumnID(cursor.getLong(0));
        tweet.setLatitude(cursor.getDouble(1));
        tweet.setLongitude(cursor.getDouble(2));
        tweet.setCreatedAt(cursor.getString(3));
        tweet.setFavoriteCount(cursor.getLong(4));
        tweet.setTweetID(cursor.getLong(5));
        tweet.setText(cursor.getString(6));
        tweet.setDescription(cursor.getString(7));
        tweet.setUserID(cursor.getLong(8));
        tweet.setUserProfileLocation(cursor.getString(9));
        tweet.setUserName(cursor.getString(10));
        tweet.setProfileImageUrlHttps(cursor.getString(11));
        tweet.setUserScreenName(cursor.getString(12));
        tweet.setUserURL(cursor.getString(13));
        tweet.setPossiblySensitive(cursor.getInt(14));
        tweet.setDistance(cursor.getDouble(15));
        return tweet;
    }
}
