package com.firstapp.quyen.freakingmath;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScoreSaveHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "game_scores.db";
    private static final String TABLE_NAME = "gameScore";
    public static final String SCORE_COLUMN_ID = "id";
    public static final String SCORE_COLUMN_CONTENT = "scores";

    private ScoreSaveOpenHelper openHelper;
    private SQLiteDatabase database;


    public ScoreSaveHelper(Context context) {
        openHelper = new ScoreSaveOpenHelper(context);
        database = openHelper.getWritableDatabase();
    }
    public void updateScores(int score, String position) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(SCORE_COLUMN_CONTENT, score);
        database.update(TABLE_NAME,contentValues,"id = ?", new String[] {position});
    }

    public void insertValue(int score){
        ContentValues contentValues = new ContentValues();
        contentValues.put(SCORE_COLUMN_CONTENT, score);
        database.insert(TABLE_NAME, null, contentValues);
    }
    public Cursor getScoresList() {
        return database.rawQuery("select * from " + TABLE_NAME, null);
    }

    public boolean isTableNull(){
        String count = "SELECT COUNT(*) FROM " + TABLE_NAME;
        Cursor mcursor = database.rawQuery(count, null);
        mcursor.moveToFirst();
        int icount = mcursor.getInt(0);
        if(icount>0) {
            mcursor.close();
            return false;
        }
        else{
            mcursor.close();
            return true;
        }

    }


    private class ScoreSaveOpenHelper extends SQLiteOpenHelper {
        ScoreSaveOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( "
                            + SCORE_COLUMN_ID + " INTEGER PRIMARY KEY, "
                            + SCORE_COLUMN_CONTENT + " INTEGER )"
            );
        }
        public void onUpgrade(SQLiteDatabase database,
                              int oldVersion, int newVersion) {
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(database);
        }
    }
}
