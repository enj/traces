package edu.ncsu.mobile.traces;

import android.content.Context;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SQLiteDriver extends SQLiteOpenHelper {

    //Point to the main folder in the project
    private static String DB_PATH = "/data/data/com.example/databases/";

    public static final String TABLE_NAME = "savedTweets";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LAT = "latitude";
    public static final String COLUMN_LNG = "longitude";
    public static final String COLUMN_CREATED_AT = "createdAt";
    public static final String COLUMN_FAVORITE_COUNT = "favoriteCount";
    public static final String COLUMN_TWEET_ID = "tweetID";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_USER_ID = "userID";
    public static final String COLUMN_USER_PROFILE_LOCATION = "userProfileLocation";
    public static final String COLUMN_USER_NAME = "userName";
    public static final String COLUMN_PROFILE_IMAGE_URL = "profileImageURLHttps";
    public static final String COLUMN_USER_SCREEN_NAME = "userScreenName";
    public static final String COLUMN_USER_URL = "userURL";
    public static final String COLUMN_POSSIBLY_SENSITIVE = "possibilySensitive";
    public static final String COLUMN_DISTANCE = "distance";

    private static final String DATABASE_NAME = "savedtweets.db";
    private static final int DATABASE_VERSION = 1;

    private static SQLiteDatabase mDataBase;
    private static SQLiteDriver sInstance = null;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_LAT
            + " double not null, " + COLUMN_LNG
            + " double not null, " + COLUMN_CREATED_AT
            + " text not null, " + COLUMN_FAVORITE_COUNT
            + " integer not null, " + COLUMN_TWEET_ID
            + " integer not null, " + COLUMN_TEXT
            + " text not null, " + COLUMN_DESCRIPTION
            + " text not null, " + COLUMN_USER_ID
            + " integer not null, " + COLUMN_USER_PROFILE_LOCATION
            + " text not null, " + COLUMN_USER_NAME
            + " text not null, " + COLUMN_PROFILE_IMAGE_URL
            + " text not null, " + COLUMN_USER_SCREEN_NAME
            + " text not null, " + COLUMN_USER_URL
            + " text not null, " + COLUMN_POSSIBLY_SENSITIVE
            + " boolean not null, " + COLUMN_DISTANCE
            + " double not null);";

    //Constructor
    public SQLiteDriver(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

//        try {
//            createDataBase(context);
//            openDataBase();
//        } catch(IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * Singleton for DataBase
     *
     * @return singleton instance
     */
    public static SQLiteDriver instance(Context context) {

        if (sInstance == null) {
            sInstance = new SQLiteDriver(context);
        }
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteDriver.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
//
//    /**
//     * Creates a empty database on the system and rewrites it with your own
//     * database.
//     *
//     * @throws java.io.IOException io exception
//     */
//    private void createDataBase(Context context) throws IOException {
//
//        boolean dbExist = checkDataBase();
//
//        if (dbExist) {
//            // do nothing - database already exist
//        } else {
//
//            // By calling this method an empty database will be created into
//            // the default system path
//            // of your application so we are gonna be able to overwrite that
//            // database with our database.
//            this.getReadableDatabase();
//
//            try {
//
//                copyDataBase(context);
//
//            } catch (IOException e) {
//
//                throw new Error("Error copying database");
//            }
//        }
//    }
//
//
//    /**
//     * Check if the database already exist to avoid re-copying the file each
//     * time you open the application.
//     *
//     * @return true if it exists, false if it doesn't
//     */
//    private boolean checkDataBase() {
//
//        SQLiteDatabase checkDB = null;
//
//        try {
//            String myPath = DB_PATH + DATABASE_NAME;
//            checkDB = SQLiteDatabase.openDatabase(myPath, null,
//                    SQLiteDatabase.OPEN_READONLY);
//
//        } catch (SQLiteException e) {
//
//            // database doesn't exist yet.
//
//        }
//
//        if (checkDB != null) {
//
//            checkDB.close();
//
//        }
//
//        return checkDB != null;
//    }
//
//    /**
//     * Copies your database from your local assets-folder to the just created
//     * empty database in the system folder, from where it can be accessed and
//     * handled. This is done by transfering bytestream.
//     *
//     * @throws java.io.IOException io exception
//     */
//    public void copyDataBase(Context context) throws IOException {
//
//        // Open your local db as the input stream
//        InputStream myInput = context.getAssets().open(DATABASE_NAME);
//
//        // Path to the just created empty db
//        String outFileName = DB_PATH + DATABASE_NAME;
//
//        // Open the empty db as the output stream
//        OutputStream myOutput = new FileOutputStream(outFileName);
//
//        // transfer bytes from the inputfile to the outputfile
//        byte[] buffer = new byte[1024];
//        int length;
//        while ((length = myInput.read(buffer)) > 0) {
//            myOutput.write(buffer, 0, length);
//        }
//
//        // Close the streams
//        myOutput.flush();
//        myOutput.close();
//        myInput.close();
//
//    }
//
//
//    /**
//     * Update method
//     *
//     * @param table  - table name
//     * @param values - values to update
//     * @param where  - WHERE clause, if pass null, all rows will be updated
//     */
//    public void update(String table, ContentValues values, String where) {
//
//        mDataBase.update(table, values, where, null);
//
//    }

    private void openDataBase() throws SQLException {

        // Open the database
        String myPath = DB_PATH + DATABASE_NAME;
        mDataBase = SQLiteDatabase.openDatabase(myPath, null,
                SQLiteDatabase.OPEN_READWRITE);
    }



}
