package com.ontro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ontro.dto.PlayerProfileData;
import com.ontro.dto.PlayerTextColor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IDEOMIND02 on 06-09-2017.
 */

public class FirebaseUserColorHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "firebaseUserColor";
    private static final String TABLE_PLAYER_COLOR = "color";
    private static final String PLAYER_NAME = "player_name";
    private static final String PLAYER_COLOR = "player_color";


    public FirebaseUserColorHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_COLOR_TABLE = "CREATE TABLE " + TABLE_PLAYER_COLOR + "("
                + PLAYER_NAME + " TEXT,"
                + PLAYER_COLOR + " TEXT"
                + ")";
        db.execSQL(CREATE_COLOR_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYER_COLOR);

        // Create tables again
        onCreate(db);
    }

    public void insertPlayerProfile(PlayerTextColor playerTextColor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PLAYER_NAME, playerTextColor.getPlayerName());
        values.put(PLAYER_COLOR, playerTextColor.getPlayerColor());
        db.insert(TABLE_PLAYER_COLOR, null, values);
        db.close(); // Closing database connection
    }

    public List<PlayerTextColor> getPlayerColorInfo() {
        List<PlayerTextColor> playerTextColors = new ArrayList<>();
        PlayerTextColor playerTextColor = new PlayerTextColor();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_PLAYER_COLOR;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    playerTextColor.setPlayerName(cursor.getString(0));
                    playerTextColor.setPlayerColor(cursor.getInt(1));
                    playerTextColors.add(playerTextColor);
                } while (cursor.moveToNext());
            }
        }
        return playerTextColors;
    }

    public int getPlayerColor(String playerName) {
        int playerColor = 0;
        PlayerTextColor playerTextColor = new PlayerTextColor();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_PLAYER_COLOR + " WHERE "+ PLAYER_NAME + " = " + playerName;
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    playerColor = cursor.getInt(1);
                } while (cursor.moveToNext());
            }
        }
        return playerColor;
    }

}
