package com.example.gravn.opengltest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mathiaspc on 16/05/2016.
 */
public class DBHandlerNew extends SQLiteOpenHelper
{
    private static final int DATABASE_VERSION = 17;
    private static final String DATABASE_NAME = "highscore";
    private static final String TABLE_ITEM = "highscore";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_SCORE = "score";

    public DBHandlerNew(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String query = "CREATE TABLE " + TABLE_ITEM + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME + " TEXT," + COLUMN_SCORE + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEM + ";");
        onCreate(db);
    }
    public void AddItem(Highscore highscore)
    {
        ContentValues value = new ContentValues();
        value.put(COLUMN_NAME, highscore.getName());
        value.put(COLUMN_SCORE, highscore.getScore());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_ITEM, null, value);
        db.close();
    }
    public void DeleteItem(String navn)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_ITEM + " WHERE " + COLUMN_NAME + "=\"" + navn + "\";");
    }

    public String ToString()
    {
        try
        {
            SQLiteDatabase db = getWritableDatabase();
            String res = "";
            String query = "SELECT * FROM " + TABLE_ITEM + " WHERE 1 ORDER BY CAST("+COLUMN_SCORE+" AS FLOAT) DESC LIMIT 0,10;";
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();
            while(!c.isAfterLast())
            {
                if (c.getString(c.getColumnIndex(COLUMN_NAME))!= null)
                {
                    res += c.getString(c.getColumnIndex(COLUMN_NAME)) + " " + c.getString(c.getColumnIndex(COLUMN_SCORE))+"//";
                    //res += "\n";
                }
                c.moveToNext();
            }
            db.close();
            if (res.equals("")){
                return "No highscores recorded";
            }
            else{
                return res;
            }
        }catch (Exception e){
            return "No highscores recorded";
        }


    }
}
