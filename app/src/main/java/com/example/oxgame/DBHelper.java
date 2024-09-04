package com.example.oxgame;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper {

    private static DBHelper instance;
    private SQLiteDatabase db;

    // 私有构造函数，防止直接实例化
    private DBHelper(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "default_user");
        String dbName = username + ".db";

        // 创建或打开数据库
        db = context.openOrCreateDatabase(dbName, Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS times (difficulty TEXT PRIMARY KEY, time INTEGER)");
    }

    // 获取单例实例
    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context);
        }
        return instance;
    }


    //更新时间分数
    //使用方法
    //DBHelper dbHelper = DBHelper.getInstance(this);
    //dbHelper.updateTime("简单", 6);
    public void updateTime(String difficulty, int time) {
        Cursor cursor = db.rawQuery("SELECT * FROM times WHERE difficulty = ?", new String[]{difficulty});
        if (cursor.moveToFirst()) {
            int currentTime = cursor.getInt(cursor.getColumnIndexOrThrow("time"));
            if (time < currentTime) {
                ContentValues values = new ContentValues();
                values.put("time", time);
                db.update("times", values, "difficulty = ?", new String[]{difficulty});
            }
        } else {
            ContentValues values = new ContentValues();
            values.put("difficulty", difficulty);
            values.put("time", time);
            db.insert("times", null, values);
        }
        cursor.close();
    }

    public Cursor getTimes() {
        return db.rawQuery("SELECT * FROM times", null);
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}
