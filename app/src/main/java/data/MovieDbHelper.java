package data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDbHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_TABLE = "CREATE TABLE " + MovieContract
                .MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry._ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + MovieContract
                .MovieEntry.COLUMN_ID + " TEXT NOT NULL, " + MovieContract
                .MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " + MovieContract
                .MovieEntry.COLUMN_IMAGE_PATH + " TEXT, " + MovieContract
                .MovieEntry.COLUMN_OVERVIEW + " TEXT, " + MovieContract
                .MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT);";

        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
