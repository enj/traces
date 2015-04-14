package edu.ncsu.mobile.traces;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SavedTweetsDataSource {

    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_AUTHOR,
            MySQLiteHelper.COLUMN_AUTHORIMG, MySQLiteHelper.COLUMN_TWEETCONTENT,
            MySQLiteHelper.COLUMN_TWEETURL};

    public SavedTweetsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public SavedTweets createTweet(String tweetInfo) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.TABLE_NAME, tweetInfo);
        long insertId = database.insert(MySQLiteHelper.TABLE_NAME, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        SavedTweets newTweet = cursorToTweet(cursor);
        cursor.close();
        return newTweet;
    }

    public void deleteComment(SavedTweets tweet) {
        long id = tweet.getID();
        System.out.println("Tweet deleted with id: " + id);
        database.delete(MySQLiteHelper.TABLE_NAME, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<SavedTweets> getAllTweets() {
        List<SavedTweets> tweets = new ArrayList<SavedTweets>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME,
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
        tweet.setID(cursor.getLong(0));
        tweet.setAuthor(cursor.getString(1));
        tweet.setAuthorImageURL(cursor.getString(2));
        tweet.setTweetContent(cursor.getString(3));
        tweet.setTweetURL(cursor.getString(4));
        return tweet;
    }
}
