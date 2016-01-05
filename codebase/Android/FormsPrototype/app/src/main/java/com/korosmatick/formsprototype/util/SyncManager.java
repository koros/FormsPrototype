package com.korosmatick.formsprototype.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.korosmatick.formsprototype.database.MySQLiteHelper;
import com.korosmatick.formsprototype.model.Form;
import com.korosmatick.formsprototype.model.Row;
import com.korosmatick.formsprototype.model.SyncRequestPacket;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by koros on 11/29/15.
 */
public class SyncManager {

    private static final  String TAG = "SyncManager";
    static SyncManager instance;
    Context ctx;
    MySQLiteHelper mySQLiteHelper;

    private final OkHttpClient client = new OkHttpClient();

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

        Gson gson = new Gson();
        String json = gson.toJson(packet);

        String serviceEndpoint = "http://10.0.2.2:8080/sample/sync";//FIXME

        try {

            RequestBody formBody = new FormEncodingBuilder()
                    .add("payload", json)
                    .build();
            Request request = new Request.Builder()
                    .url(serviceEndpoint)
                    .post(formBody)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            Log.d(TAG, response.body().string());

        }catch (Exception e) {
            e.printStackTrace();
            return;
        }

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
                    Map<String, String> map = mySQLiteHelper.sqliteRowToMap(c);

                    //iterate through the fields and fetch child records
                    Row row = retrieveUnsyncedRow(map, tableName);
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

    private Row retrieveUnsyncedRow(Map<String, String> dbRecord, String tableName) throws Exception{
        Row row = new Row();
        row.setTableName(tableName);
        row.setRow(dbRecord);

        List<Row> childRows = new ArrayList<Row>();

        SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();

        //iterate through the fields and fetch child records
        Iterator<?> keys = dbRecord.keySet().iterator();
        while( keys.hasNext() ) {
            String fieldName = (String)keys.next();
            if (isLinkToChildTable(fieldName, tableName)){
                Cursor c = db.rawQuery("SELECT * FROM " + fieldName + " where " + tableName + "='" + dbRecord.get(fieldName) + "'", null);
                Map<String, String> map = mySQLiteHelper.sqliteRowToMap(c);

                Row childRow = retrieveUnsyncedRow(map, fieldName);
                childRows.add(childRow);
            }
        }

        row.setChildRows(childRows);
        return row;
    }

    private boolean isLinkToChildTable(String fieldName, String tableName){
        try {
            //entity_relationships (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,  parent_table TEXT NOT NULL,  child_table TEXT NOT NULL,
            // field TEXT NOT NULL, kind TEXT NOT NULL, from_field TEXT NOT NULL, to_field TEXT NOT NULL)";
            SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM entity_relationships WHERE parent_table ='" + tableName + "' AND child_table='" + fieldName + "'", null);
            return cursor.getCount() > 0;
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

}
