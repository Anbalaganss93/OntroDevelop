package com.ontro;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ontro.dto.MyTeamDataBaseModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Android on 08-May-17.
 */

public class MyTeamDataBaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "OntroMyTeam.db";
    public static final String CONTACTS_TABLE_NAME = "myteam";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NAME = "teamname";
    public static final String CONTACTS_COLUMN_TEAMID = "teamid";
    public static final String MYTEAM_COLUMN_SPORTID = "sportid";
    private HashMap hp;

    public MyTeamDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table myteam " +
                        "(id integer primary key, teamname text,teamid text,sportid text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS myteam");
        onCreate(db);
    }

    public boolean insertContact(String mTeamname, String mTeamid, String mSportId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("teamname", mTeamname);
        contentValues.put("teamid", mTeamid);
        contentValues.put("sportid", mSportId);
        db.insert("myteam", null, contentValues);
        return true;
    }

    public void deletealldata() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + CONTACTS_TABLE_NAME);
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from myteam where id=" + id + "", null);
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact(Integer id, String mTeamname, String mTeamid, String mSportId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("teamname", mTeamname);
        contentValues.put("teamid", mTeamid);
        contentValues.put("sportid", mSportId);
        db.update("myteam", contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }

    public Integer deleteContact(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("myteam",
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public ArrayList<MyTeamDataBaseModel> getAllTeams() {
        ArrayList<MyTeamDataBaseModel> array_list = new ArrayList<MyTeamDataBaseModel>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from myteam", null);
        res.moveToFirst();

        while (!res.isAfterLast()) {
            MyTeamDataBaseModel m = new MyTeamDataBaseModel();
            m.setTeamid(res.getString(res.getColumnIndex(CONTACTS_COLUMN_TEAMID)));
            m.setTeamname(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            m.setSportid(res.getString(res.getColumnIndex(MYTEAM_COLUMN_SPORTID)));
            array_list.add(m);
            res.moveToNext();
        }
        return array_list;
    }
}