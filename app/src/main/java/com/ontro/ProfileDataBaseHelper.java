package com.ontro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ontro.dto.PlayerProfileData;

/**
 * Created by IDEOMIND02 on 17-05-2017.
 */

public class ProfileDataBaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "playerProfile";
    private static final String TABLE_PLAYER_PROFILE = "profile";
    private static final String KEY_ID = "id";
    private static final String PLAYER_INFO = "player_info";

    public ProfileDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PROFILE_TABLE = "CREATE TABLE " + TABLE_PLAYER_PROFILE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + PLAYER_INFO + " TEXT"
                + ")";
        db.execSQL(CREATE_PROFILE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYER_PROFILE);

        // Create tables again
        onCreate(db);
    }

    public void insertPlayerProfile(PlayerProfileData playerInfo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PLAYER_INFO, playerInfo.getPlayerInfo());
        db.insert(TABLE_PLAYER_PROFILE, null, values);
        db.close(); // Closing database connection
    }

    public PlayerProfileData getProfile() {
        PlayerProfileData playerInfo = new PlayerProfileData();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_PLAYER_PROFILE;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    playerInfo.setPlayerInfo(cursor.getString(1));
                } while (cursor.moveToNext());
            }
        }
        return playerInfo;
    }

    public int updateProfile(PlayerProfileData playerInfo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PLAYER_INFO, playerInfo.getPlayerInfo());

        // updating row
        return db.update(TABLE_PLAYER_PROFILE, values, KEY_ID + " = ?",
                new String[] { String.valueOf(playerInfo.getKeyId()) });
    }

    public void deleteProfile(PlayerProfileData playerProfileData) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PLAYER_PROFILE, KEY_ID + " = ?",
                new String[] { String.valueOf(playerProfileData.getPlayerInfo()) });
        db.close();
    }
}
