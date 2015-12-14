package com.korosmatick.formsprototype.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.korosmatick.formsprototype.database.MySQLiteHelper;
import com.korosmatick.formsprototype.model.Form;
import com.korosmatick.formsprototype.model.Row;
import com.korosmatick.formsprototype.model.SyncRequestPacket;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by koros on 11/29/15.
 */
public class SyncManager {

    static SyncManager instance;
    Context ctx;
    MySQLiteHelper mySQLiteHelper;

    public SyncManager(Context context){
        ctx = context;
        mySQLiteHelper = MySQLiteHelper.getInstance(context);
    }

    public static SyncManager getInstance(Context ctx){
        if (instance == null){
            instance = new SyncManager(ctx);
        }
        return instance;
    }

    public void sync(){
        SyncRequestPacket packet = new SyncRequestPacket();
        packet.setType(1);
        packet.setRecords(retrieveUnsyncedRows());
    }

    public List<Row> retrieveUnsyncedRows(){
        Cursor mCursor = null;
        List<Row> unsyncedRows = new ArrayList<Row>();
        try {
            SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
            mCursor = db.rawQuery("SELECT * FROM sync_table" + " ", null);
            if (mCursor != null && mCursor.moveToFirst()){
                do {
                    String tableName = mCursor.getString(mCursor.getColumnIndex("table_name"));
                    Long record_id = mCursor.getLong(mCursor.getColumnIndex("record_id"));

                    Cursor c = db.rawQuery("SELECT * FROM " + tableName + " where _id=" + record_id, null);
                    JSONObject json = mySQLiteHelper.sqliteRowToJson(c);

                    //iterate through the fields and fetch child records
                    Row row = retrieveUnsyncedRow(json, tableName);
                    unsyncedRows.add(row);

                }while (mCursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (mCursor != null) mCursor.close();
        }
        return unsyncedRows;
    }

    private Row retrieveUnsyncedRow(JSONObject dbRecord, String tableName) throws Exception{
        Row row = new Row();
        row.setRow(dbRecord);

        List<Row> childRows = new ArrayList<Row>();

        SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();

        //iterate through the fields and fetch child records
        Iterator<?> keys = dbRecord.keys();
        while( keys.hasNext() ) {
            String key = (String)keys.next();
            if (isLinkToChildTable()){
                Cursor c = db.rawQuery("SELECT * FROM " + key + " where " + tableName + "='" + dbRecord.get(key) + "'", null);
                JSONObject json = mySQLiteHelper.sqliteRowToJson(c);

                Row childRow = retrieveUnsyncedRow(json, key);
                childRows.add(childRow);
            }
        }

        row.setChildRows(childRows);
        return row;
    }

    private boolean isLinkToChildTable(){
        //TODO: implement this
        return false;
    }

}
