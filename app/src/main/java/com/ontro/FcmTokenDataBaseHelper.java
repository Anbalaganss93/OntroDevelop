package com.ontro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ontro.dto.FcmTokenModel;

/**
 * Created by IDEOMIND02 on 17-06-2017.
 */

public class FcmTokenDataBaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "fcmToken";
    private static final String TABLE_TOKEN = "token";
    private static final String KEY_ID = "id";
    private static final String KEY_TOKEN = "value";

    public FcmTokenDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_TOKEN
                + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TOKEN + " TEXT"
                + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOKEN);

        // Create tables again
        onCreate(db);
    }

    void addToken(FcmTokenModel fcmTokenModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, fcmTokenModel.getKeyId());
        values.put(KEY_TOKEN, fcmTokenModel.getToken());
        db.insert(TABLE_TOKEN, null, values);
        db.close();
    }

    public FcmTokenModel getFcmToken() {
        FcmTokenModel fcmTokenModel = new FcmTokenModel();
        String selectQuery = "SELECT  * FROM " + TABLE_TOKEN;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                fcmTokenModel.setKeyId(Integer.parseInt(cursor.getString(0)));
                fcmTokenModel.setToken(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        return fcmTokenModel;
    }

    public void deleteFcmToken(FcmTokenModel fcmTokenModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TOKEN, KEY_ID + " = ?",
                new String[] { String.valueOf(fcmTokenModel.getKeyId()) });
        db.close();
    }
}
