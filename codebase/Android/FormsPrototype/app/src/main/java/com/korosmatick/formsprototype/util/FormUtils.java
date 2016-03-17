package com.korosmatick.formsprototype.util;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.provider.BaseColumns;
import android.test.mock.MockApplication;
import android.util.Log;

import com.korosmatick.formsprototype.database.MySQLiteHelper;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by koros on 9/28/15.
 */
public class FormUtils {

    static FormUtils instance;
    Context ctx;
    MySQLiteHelper mySQLiteHelper;

    private static final String CONTENT_FIELD = "content";

    public FormUtils(Context context){
        ctx = context;
        mySQLiteHelper = MySQLiteHelper.getInstance(context);
    }

    public static FormUtils getInstance(Context ctx){
        if (instance == null){
            instance = new FormUtils(ctx);
        }
        return instance;
    }

    public long saveFormData(String xml) throws Exception {
        Long id = null;
        JSONObject json = XML.toJSONObject(xml);
        System.out.println(json);
        Iterator<?> keys = json.keys();
        while( keys.hasNext() ) {
            String key = (String)keys.next();
            if (json.get(key) instanceof JSONObject) {
                JSONObject object = json.getJSONObject(key);
                String tableName = retrieveTableNameOrColForField(key);
                if (mySQLiteHelper.tableExists(tableName)){
                    id = saveJsonObjectFields(object, tableName, null, null);
                    boolean isNewRecord = !object.has("_id");
                    if (id != null && isNewRecord){
                        //create an insert ref in the sync table
                        createNewRecordInsertRecordInSyncTable(id, tableName);
                    }
                }
            }
        }

        return id;
    }

    public void createNewRecordInsertRecordInSyncTable(Long record_id,  String tableName){
        try {
            //sync_table (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, table TEXT NOT NULL, record_id INTEGER NOT NULL, type TEXT NOT NULL, column TEXT NULL)
            // get reference to writable DB
            SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
            String query = "SELECT  * FROM sync_table WHERE table_name = '" + tableName + "' AND record_id =" + record_id + " AND type = 'insert'";
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.getCount() > 0) {
                // the reference to the record creation has already been saved
                cursor.close();
                return;
            }

            ContentValues values = new ContentValues();
            values.put("table_name", tableName);
            values.put("record_id", record_id);
            values.put("type", "insert");
            Long id = db.insertWithOnConflict("sync_table", BaseColumns._ID, values, SQLiteDatabase.CONFLICT_REPLACE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String retrieveTableNameOrColForField(String field){
        //replace all illegal xters in the table name
        String normalizedTblName = field.toLowerCase().replaceAll("[^A-Za-z0-9_]", "_");
        return normalizedTblName.startsWith("_") ? normalizedTblName : "_" + normalizedTblName;
    }

    public Long saveJsonObjectFields(JSONObject jsonObject, String tableName, String foreignIdFieldName, Long foreignId){
        try {
            ContentValues values = new ContentValues();
            if (foreignIdFieldName != null && foreignId != null) {
                values.put(foreignIdFieldName, foreignId);
            }

            List<String> fields = new ArrayList<String>();
            Iterator<?> keys = jsonObject.keys();
            while( keys.hasNext() ) {
                String key = (String)keys.next();
                fields.add(key);
            }

			/*
			 * generate create table & insert sql statement
			 */
            for(String field : fields) {
                String columnName = retrieveTableNameOrColForField(field);
                // skip the id attribute currently redundant and collides with the _id field
                if (field.equalsIgnoreCase("id"))
                    continue;
                if (jsonObject.get(field) instanceof JSONObject) {
                    JSONObject object = jsonObject.getJSONObject(field);
                    if (object.has(CONTENT_FIELD) && mySQLiteHelper.tableContainsColumn(tableName, columnName)){
                        values.put(columnName, object.getString(CONTENT_FIELD));
                    }
                } else if (jsonObject.get(field) instanceof JSONArray) {
                    JSONArray object = jsonObject.getJSONArray(field);
                    String value = object.length() > 0 ? String.valueOf(object.get(0)) : null;
                    values.put(columnName, value);
                } else {
                    if (mySQLiteHelper.tableContainsColumn(tableName, columnName)){
                        String fieldValue = String.valueOf(jsonObject.get(field));
                        values.put(columnName, fieldValue);
                    }
                }
            }

			// retrieve the current values for this record from the database
            Map<String, String> currentValues = null;
            String serverId = null;
            if (fields.contains("_serverid")){
                serverId = jsonObject.getString("_serverid");
                currentValues = getTheCurrentValuesForRecord(tableName, serverId);
            }

            // pad the missing content values with the values from the db for a linked/child record
            if (jsonObject.has("_id")){
                String recordId  = jsonObject.getString("_id");
                values = addMissingContentValuesForRecordId(recordId, tableName, values);
            }

            /*
			 * generate the id for this record and fetch/create child records
			 */
            Long id = executeInsertStatement(values, tableName);

            // update the sync table
            if (currentValues != null && serverId != null){
                updateTheSyncTable(values, tableName, serverId, currentValues);
            }

            for(String field : fields) {
                String childTableName = retrieveTableNameOrColForField(field);
                if (jsonObject.get(field) instanceof JSONObject) {
                    JSONObject object = jsonObject.getJSONObject(field);
                    if (mySQLiteHelper.tableExists(childTableName)){
                        Long childRecordId = saveJsonObjectFields(object, childTableName, tableName, id);
                        String updateSql = "update " + tableName + " set " + childTableName  + " = " + childRecordId + " where _id=" + id;
                        executeSQL(updateSql);
                    }
                }else if(jsonObject.get(field) instanceof JSONArray) {
                    JSONArray array = jsonObject.getJSONArray(field);
                    if (isArrayOfObjects(array)){
                        for (int i = 0; i < array.length(); i++) {
                            if (array.get(i) instanceof JSONObject) {
                                JSONObject object = array.getJSONObject(i);
                                if (mySQLiteHelper.tableExists(childTableName)){
                                    Long childRecordId = saveJsonObjectFields(object, childTableName, tableName, id);
                                    String updateSql = "update " + tableName + " set " + childTableName  + " = " + childRecordId + " where _id=" + id;
                                    executeSQL(updateSql);
                                }
                            }
                        }
                    }
                }
            }

            return id;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Insert the a new record to the database and returns its id,
     * on the Android client side the logic might change a little but the general idea is basically to insert and get the id
     **/
    private Long executeInsertStatement(ContentValues values, String tableName) {
        // TODO Auto-generated method stub
        SQLiteDatabase database = mySQLiteHelper.getWritableDatabase();
        Long id = database.insertWithOnConflict(tableName, BaseColumns._ID, values, SQLiteDatabase.CONFLICT_REPLACE);
        return id;
    }

    /**
     * create a record in the sync table to track the columns that have changed
     *
     * @param values
     * @param tableName
     * @param serverId
     */
    //TODO: push this to a background thread
    private void updateTheSyncTable(ContentValues values, String tableName, String serverId, Map<String, String> currentValues){
        try {
            SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
            Set<Map.Entry<String, Object>> s = values.valueSet();
            Iterator itr = s.iterator();
            while(itr.hasNext())
            {
                Map.Entry me = (Map.Entry)itr.next();
                String key = me.getKey().toString();
                String value =  String.valueOf(me.getValue());

                Log.d("DatabaseSync", "Key : "+ key + ", Current value : " + currentValues.get(key) + ", New value : "+ value);

                //check the current value of the
                if (currentValues.containsKey(key)){
                    String dbValue = currentValues.get(key);
                    if (!String.valueOf(dbValue).equalsIgnoreCase(String.valueOf(value))){
                        // the value has changed create a new record in the sync table
                        ContentValues cv = new ContentValues();
                        cv.put("table_name", tableName);
                        cv.put("column_name", key);
                        cv.put("new_value", value);
                        cv.put("server_id", serverId);
                        cv.put("synced", 0);
                        Long id = db.insertWithOnConflict("sync_table_updates", BaseColumns._ID, cv, SQLiteDatabase.CONFLICT_REPLACE);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * retrieve the current values for a record in the database
     *
     * @param tableName the name of the table to be queried
     * @param serverId the value for serverId
     * @return
     */
    private Map<String,String> getTheCurrentValuesForRecord(String tableName, String serverId){
        Map<String, String> dbValues = new HashMap<String, String>();
        SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
        String query = "SELECT  * FROM " + tableName + " WHERE _serverid = " + serverId;
        Cursor cursor = db.rawQuery(query, null);
        dbValues = mySQLiteHelper.sqliteRowToMap(cursor);
        return dbValues;
    }

    private ContentValues addMissingContentValuesForRecordId(String id, String tableName, ContentValues cv){
        Map<String, String> dbValues = new HashMap<String, String>();
        SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
        String query = "SELECT  * FROM " + tableName + " WHERE _id = " + id;
        Cursor cursor = db.rawQuery(query, null);
        dbValues = mySQLiteHelper.sqliteRowToMap(cursor);

        for (String key : dbValues.keySet()){
            if (!cv.containsKey(key)){
                cv.put(key, dbValues.get(key));
            }
        }

        return cv;
    }

    public static boolean isArrayOfObjects(JSONArray array) throws Exception {
        return (array != null && array.length() > 0 && array.get(0) instanceof JSONObject);
    }

    public void executeSQL(String sql){
        // get reference to writable DB
        SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
        db.execSQL(sql);
    }
}
