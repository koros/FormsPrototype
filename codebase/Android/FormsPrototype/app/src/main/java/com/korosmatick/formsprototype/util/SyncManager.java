package com.korosmatick.formsprototype.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.korosmatick.formsprototype.database.MySQLiteHelper;
import com.korosmatick.formsprototype.model.Form;
import com.korosmatick.formsprototype.model.Row;
import com.korosmatick.formsprototype.model.SyncRequestPacket;
import com.korosmatick.formsprototype.model.SyncResponse;
import com.korosmatick.formsprototype.model.SyncResponseItem;
import com.korosmatick.formsprototype.model.UpdatedItem;
import com.korosmatick.formsprototype.model.UpdatedItemsPacket;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
        pullFomServer();
        pushToServer();
    }

    private void pullFomServer(){
        SyncRequestPacket packet = new SyncRequestPacket();
        Long syncPointerPosition = retriveSyncPointerPosition();
        if (syncPointerPosition != null){
            packet.setType(1); //pull
            packet.setSyncPointerPosition(syncPointerPosition);

            Gson gson = new Gson();
            String json = gson.toJson(packet);

            String serviceEndpoint = "http://10.0.2.2:8080/sample/rest/sync";//FIXME

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

                String responseStr = response.body().string();
                Log.d(TAG, responseStr);

                processApiResponse(responseStr);

            }catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    private void pushToServer(){
        SyncRequestPacket packet = new SyncRequestPacket();
        packet.setType(2); // push
        packet.setRecords(retrieveUnsyncedRows());
        UpdatedItemsPacket updatedItemsPacket = new UpdatedItemsPacket();
        updatedItemsPacket.setUpdatedItemList(mySQLiteHelper.getUpdatedItemList());
        packet.setUpdatedItemsPacket(updatedItemsPacket);

        Gson gson = new Gson();
        String json = gson.toJson(packet);

        String serviceEndpoint = "http://10.0.2.2:8080/sample/rest/sync";//FIXME

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

            String responseStr = response.body().string();
            Log.d(TAG, responseStr);

            processApiResponse(responseStr);

        }catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    private void processApiResponse(String responseStr) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);

        com.korosmatick.formsprototype.model.Response apiResponse = mapper.readValue(responseStr, com.korosmatick.formsprototype.model.Response.class);
        SyncResponse syncResponse = apiResponse.getSyncResponse();
        if (syncResponse != null){
            List<SyncResponseItem> syncedItems = syncResponse.getSyncedItems();
            handleSyncedItemsResponse(syncedItems);

            List<Long> updatedItemsAck = syncResponse.getUpdatedItemsAck();
            handleUpdatedItemsAck(updatedItemsAck);

            List<Row> newRecords = syncResponse.getNewRecords();
            handleNewRecords(newRecords);

            UpdatedItemsPacket updatedItems = syncResponse.getUpdatedItemsPacket();
            handleUpdatedItems(updatedItems);

            Long syncPointer = syncResponse.getSyncPointerPosition();
            updateSyncPointerPosition(syncPointer);

        }
    }

    private void handleUpdatedItems(UpdatedItemsPacket updatedItems){
        if (updatedItems != null){
            SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
            List<UpdatedItem> updatedItemList = updatedItems.getUpdatedItemList();
            if (updatedItemList != null){
                for (UpdatedItem i : updatedItemList){
                    String query = "UPDATE " + i.getTableName() + " SET " + i.getColumnName() + " = '" + i.getNewValue() +"' WHERE _serverid = " + i.getServerId();
                    db.execSQL(query);
                }
            }
        }
    }

    private void handleNewRecords(List<Row> rows){
        // process the new records list
        if (rows != null) {
            for (Row row : rows) {
                saveRowObjectToDb(row, null);
            }
        }
    }

    private void saveRowObjectToDb(Row row, Long parentRecordId){

        Map<String, String> object = row.getRow();
        Long id = saveSingleTableRowTodb(object, row.getTableName(), parentRecordId);

        //save the child records if any
        List<Row> childRows = row.getChildRows();
        if (childRows != null && !childRows.isEmpty()) {
            for (Row childRow : childRows) {
                saveRowObjectToDb(childRow, id);
            }
        }
    }

    private Long saveSingleTableRowTodb(Map<String, String> map, String tableName, Long parentId){
        try {
            SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            // generate insert sql statement
            Iterator<?> keys = map.keySet().iterator();
            while( keys.hasNext() ) {
                String field = (String)keys.next();
                if (field.equalsIgnoreCase("_id")){
                    values.put("_serverid", map.get(field).toString());
                    continue;
                }

                //exclude non relevant fields sent from the client side
                if (!isValidCulumnName(field, tableName)) {
                    continue;
                }

                String value = null;
                if (isLinkToParentTable(field, tableName)) {
                    value = String.valueOf(parentId);
                }else if(map.get(field) != null){
                    value = map.get(field).toString();
                }

                values.put(field, value);
            }

			/*
			 * generate the id for this record
			 */
            Long id = db.insertWithOnConflict(tableName, BaseColumns._ID, values, SQLiteDatabase.CONFLICT_REPLACE);
            return id;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private boolean isLinkToParentTable(String fieldName, String tableName){
        boolean linkFound = false;
        Cursor mCursor = null;
        try {
            SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
            mCursor = db.rawQuery("SELECT * FROM entity_relationships WHERE child_table ='" + tableName + "' AND parent_table='" + fieldName + "'", null);
            linkFound = mCursor != null && mCursor.getCount() > 0;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (mCursor != null) mCursor.close();
        }
        return linkFound;
    }

    private boolean isValidCulumnName(String field, String tableName) throws Exception{
        return mySQLiteHelper.tableContainsColumn(tableName, field);
    }

    private void handleSyncedItemsResponse(List<SyncResponseItem> syncedItems){
        if (syncedItems != null){
            for (SyncResponseItem syncResponseItem : syncedItems){
                handleSyncResponseItem(syncResponseItem);
            }
        }
    }

    private void handleUpdatedItemsAck(List<Long> updatedItemsAck){
        if (updatedItemsAck != null){
            SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
            for (Long id : updatedItemsAck){
                //delete the pointer on the sync table to avoid indefinite growth in the number of records
                String query = "DELETE FROM sync_table_updates WHERE _id = " + id;
                db.execSQL(query);
            }
        }
    }

    private void updateSyncPointerPosition(Long syncPointer){
        if (syncPointer != null){
            SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
            //sync_pointer (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, sync_time TIMESTAMP NOT NULL, position INTEGER NOT NULL)
            ContentValues values = new ContentValues();
            values.put("_id", 1); // always replace row 1
            values.put("sync_time", new Date().getTime());
            values.put("position", syncPointer);

            // we are always replacing the first row on the db with the value from the server
            Long id = db.insertWithOnConflict("sync_pointer", BaseColumns._ID, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    private Long retriveSyncPointerPosition(){
        Cursor mCursor = null;
        Long position = null;
        try {
            SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
            mCursor = db.rawQuery("SELECT * FROM sync_pointer" + " ", null);
            if (mCursor != null && mCursor.moveToFirst()){
                position = mCursor.getLong(mCursor.getColumnIndex("position"));
            }else if (mCursor.getCount() == 0){
                position = 0L;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (mCursor != null) mCursor.close();
        }
        return position;
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
                    Row row = retrieveUnsyncedRow(map, tableName, db);
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

    private Row retrieveUnsyncedRow(Map<String, String> dbRecord, String tableName, SQLiteDatabase db) throws Exception{
        Row row = new Row();
        row.setTableName(tableName);
        row.setRow(dbRecord);

        List<Row> childRows = new ArrayList<Row>();

        //SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();

        //iterate through the fields and fetch child records
        Iterator<?> keys = dbRecord.keySet().iterator();
        while( keys.hasNext() ) {
            String fieldName = (String)keys.next();
            if (isLinkToChildTable(fieldName, tableName)){
                Cursor c = db.rawQuery("SELECT * FROM " + fieldName + " where " + tableName + "='" + dbRecord.get("_id") + "'", null);
                //retrieve all the child records
                int i = 0;
                if (c != null && c.moveToFirst()){
                    do {
                        Map<String, String> map = mySQLiteHelper.sqliteRowToMap(c, i);
                        Row childRow = retrieveUnsyncedRow(map, fieldName, db);
                        childRows.add(childRow);
                        i++;
                    }while (c.moveToNext());
                }
                c.close();
            }
        }

        row.setChildRows(childRows);
        return row;
    }

    private boolean isLinkToChildTable(String fieldName, String tableName){
        boolean linkFound = false;
        Cursor cursor = null;
        try {
            //entity_relationships (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,  parent_table TEXT NOT NULL,  child_table TEXT NOT NULL,
            // field TEXT NOT NULL, kind TEXT NOT NULL, from_field TEXT NOT NULL, to_field TEXT NOT NULL)";
            SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
            cursor = db.rawQuery("SELECT * FROM entity_relationships WHERE parent_table ='" + tableName + "' AND child_table='" + fieldName + "'", null);
            linkFound = cursor.getCount() > 0;
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return linkFound;
    }

    private void handleSyncResponseItem(SyncResponseItem syncResponseItem){
        try {
            String tableName = syncResponseItem.getTableName();
            Long serverId = syncResponseItem.getServerId();
            Long localId = syncResponseItem.getLocalId();
            String updateSql = String.format("Update %s set _serverid=%d where _id=%d", tableName, serverId, localId);
            SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
            db.execSQL(updateSql);

            //delete the pointer on the sync table to avoid indefinite growth in the number of records
            String query = "DELETE FROM sync_table WHERE table_name = '" + tableName + "' AND record_id =" + localId + " AND type = 'insert'";
            db.execSQL(query);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
