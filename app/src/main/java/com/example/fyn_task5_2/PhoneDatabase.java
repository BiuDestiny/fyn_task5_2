package com.example.fyn_task5_2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PhoneDatabase {
    private static final String DB_NAME = "phoneDatabase.db";
    private static final String TABLE_NAME = "contact";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE = "phone";

    private DatabaseHelper databaseHelper;
    private Context context;
    private int version = 1;
    private SQLiteDatabase db;

    public PhoneDatabase(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper();
    }

    public static ContentValues encodeContentValues(String name, String phone) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, name);
        cv.put(KEY_PHONE, phone);
        return cv;
    }

    public void open() {
        if (db == null || !db.isOpen()) {
            db = databaseHelper.getWritableDatabase();
        }
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    public long insertData(String name, String phone) {
        ContentValues cv = encodeContentValues(name, phone);
        return db.insert(TABLE_NAME, null, cv);
    }

    public int delete(long id) {
        return db.delete(TABLE_NAME, "_id=" + id, null);
    }

    public void reset() {
        databaseHelper.resetData(db);
    }

    public Cursor quertAll() {
        String sql = String.format("select * from %s", TABLE_NAME);
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    public Cursor fuzzyQuery(String match) {
        String s = String.format("select * from %s where %s like ? or %s like ?", TABLE_NAME, KEY_NAME, KEY_PHONE);
        String[] args = new String[]{"%" + match + "%", "%" + match + "%"};
        Cursor c = db.rawQuery(s, args);
        return c;
    }

    public int updateData(String name, String phone, long id) {
        ContentValues cv = encodeContentValues(name, phone);
        return db.update(TABLE_NAME, cv, "_id=" + id, null);
    }

    class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper() {
            super(context, DB_NAME, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String sql = String.format("create table if not exists %s(_id INTEGER PRIMARY KEY AUTOINCREMENT, %s text, %s text)", TABLE_NAME, KEY_NAME, KEY_PHONE);
            db.execSQL(sql);
            insertData("Tom1", "4567");
            insertData("Tom2", "1122");
            insertData("Tom3", "3213");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            resetData(db);
        }

        public void resetData(SQLiteDatabase db) {
            String sql = String.format("drop table if exists %s", TABLE_NAME);
            db.execSQL(sql);
            onCreate(db);
        }
    }

    public PhoneCursor queryById(long id) {
        String s = String.format("select * from %s where _id = %d", TABLE_NAME, id);
        Cursor c = db.rawQuery(s, null);
        return new PhoneCursor(c);
    }

    public class PhoneCursor extends CursorWrapper {
        private Cursor c;

        public PhoneCursor(Cursor cursor) {
            super(cursor);
            this.c = cursor;
        }

        public String getName() {
            int index = c.getColumnIndex(KEY_NAME);
            return c.getString(index);
        }

        public String getPhone() {
            int index = c.getColumnIndex(KEY_PHONE);
            return c.getString(index);
        }


    }
}
