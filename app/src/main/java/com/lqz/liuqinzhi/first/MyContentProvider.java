package com.lqz.liuqinzhi.first;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.Set;

public class MyContentProvider extends ContentProvider {

    private static final String TAG = "MyContentProvider";

    private static final int CONTACT = 1;
    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI("com.lqz.first.myprovider", "contact", CONTACT);
    }

    public MyContentProvider() {
    }

    private MyDBHelper dbHelper;
    private ContentResolver contentResolver;

    @Override
    public boolean onCreate() {

        Context context = getContext();
        contentResolver = context.getContentResolver();
        dbHelper = new MyDBHelper(context, "contact.db", null, 1);

        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.

        int id = 0;
        if (uriMatcher.match(uri) == CONTACT) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            id = db.delete("contact",selection, selectionArgs);
            contentResolver.notifyChange(uri, null);
        }
        return id;
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.

        Uri u = null;
        if (uriMatcher.match(uri) == CONTACT){
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            long d = db.insert("contact", "_id", values);
            u = ContentUris.withAppendedId(uri, d);
            contentResolver.notifyChange(u, null);
        }
        return u;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // TODO: Implement this to handle query requests from clients.

        Cursor cursor = null;
        if (uriMatcher.match(uri) == CONTACT) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            cursor = db.query("contact", projection, selection, selectionArgs, null, null, sortOrder);
            cursor.setNotificationUri(contentResolver, uri);
        }
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        Log.e(TAG, "method: " +  method);
        Log.e(TAG, "arg: " + (TextUtils.isEmpty(arg) ? "null." : arg));
        Log.e(TAG, "extras: size: " +  extras.size());
        Set<String> keySet = extras.keySet();
        for (String key: keySet) {
            Log.e(TAG, "extra: " + key + ": " + extras.get(key));
        }
        extras.putInt("age", 25);
        Bundle bundle = new Bundle();
        bundle.putBoolean(method, true);
        return bundle;
    }

    private static class MyDBHelper extends SQLiteOpenHelper {

        public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            String sql = "create table contact(_id integer primary key autoincrement, name text not null," +
                    "number text not null);";
            db.execSQL(sql);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onCreate(db);
        }
    }
}
