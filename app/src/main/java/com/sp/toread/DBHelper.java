package com.sp.toread;

/**
 * Created by my on 2016/11/12.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by my on 2016/10/23.
 */
public class DBHelper extends SQLiteOpenHelper {
    private final static String DB_NAME="my.db";
    private final static int DB_VERSION=1;
    private final  static String TABLE_NAME="info";
    private final static String CONTENT="title";
    private final static String TIME="writter";
    private final static String ID="_id";
    SQLiteDatabase database=getWritableDatabase();
    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }
    //paramSQLiteDatabase.execSQL("create table " + NAME + "(id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT,date TEXT,content TEXT)");
    @Override
    public void onCreate(SQLiteDatabase database){
        Log.e("0","database have been ceated");
        database.execSQL("create table " + TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT,writter TEXT)");

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
    public long insert(String mtitle,String mwritter){
        ContentValues contentValues=new ContentValues();
        contentValues.put("title",mtitle);
        contentValues.put("writter", mwritter);
        //获取系统时间
       /* SimpleDateFormat formatter=new SimpleDateFormat("yy-MM-dd HH:mm");
        Date curDate =  new Date(System.currentTimeMillis());
        String time=formatter.format(curDate);*/
        long row=database.insert(TABLE_NAME,null,contentValues);
        return row;
    }
    public Cursor select(){
        Cursor cursor=database.query(TABLE_NAME,null,null,null,null,null,null);
        return cursor;
    }
}
