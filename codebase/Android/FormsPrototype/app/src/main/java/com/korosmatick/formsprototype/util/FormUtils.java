package com.korosmatick.formsprototype.util;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.provider.BaseColumns;

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
                    if (id != null){
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
                if (jsonObject.get(field) instanceof JSONObject) {
                    JSONObject object = jsonObject.getJSONObject(field);
                    String columnName = retrieveTableNameOrColForField(field);
                    if (object.has(CONTENT_FIELD) && mySQLiteHelper.tableContainsColumn(tableName, columnName)){
                        values.put(columnName, object.getString(CONTENT_FIELD));
                    }
                }
            }

			/*
			 * generate the id for this record and fetch/create child records
			 */
            Long id = executeInsertStatement(values, tableName);

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

    private void addColumnValueForTable(String value, String columnName, String tableName, ContentValues values){
        tableName = retrieveTableNameOrColForField(tableName);
        columnName = retrieveTableNameOrColForField(columnName);
        if (mySQLiteHelper.tableExists(tableName) && mySQLiteHelper.tableContainsColumn(tableName, columnName)){
            values.put(columnName, value);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB) // needs refactoring not sound at the moment
    public void performCascadeDeleteOnAllObjectsBelongingToTableRow(String tableName, String filterField, Long filterValue){
        try {
            String query = "SELECT  * FROM " + tableName + " WHERE " + filterField + " = " + filterValue;

            // get reference to writable DB
            SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        String fieldName = cursor.getColumnName(i);
                        String fieldValue = null;
                        Long id = cursor.getLong(cursor.getColumnIndex("_id"));

                        if (cursor.getType(i) == Cursor.FIELD_TYPE_STRING && (fieldValue = cursor.getString(i)).equalsIgnoreCase("FETCH_OBJECT")) {
                            performCascadeDeleteOnAllObjectsBelongingToTableRow(fieldName, tableName, id);

                            //delete the record
                            String deleteQuery = "delete from " + tableName + " where _id=" + id;
                            db.execSQL(deleteQuery);
                        }
                    }
                } while (cursor.moveToNext());
            }

            //delete the record
            String deleteQuery = "DELETE FROM " + tableName + " WHERE " + filterField + " = " + filterValue;
            db.execSQL(deleteQuery);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    /*
     * Insert the a new record to the database and returns its id,
     * on the Android client side the logic might change a little but the general idea is basically to insert and get the id
     */
    private Long executeInsertStatement(ContentValues values, String tableName) {
        // TODO Auto-generated method stub
        SQLiteDatabase database = mySQLiteHelper.getWritableDatabase();
        Long id = database.insertWithOnConflict(tableName, BaseColumns._ID, values, SQLiteDatabase.CONFLICT_REPLACE);
        return id;
    }


    public void createOrUpdateTableRelationshipLink(String parent_table, String child_table, String field, String kind, String from, String to){
        String query = "SELECT  * FROM entity_relationships WHERE parent_table = \"" + parent_table + "\" AND child_table = \"" + child_table +"\" AND field=\"" + field +"\" AND from_field=\"" +
                from + "\" AND to_field=\"" + to + "\"";
        SQLiteDatabase db = mySQLiteHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            Long id = cursor.getLong(cursor.getColumnIndex("_id"));
            //update the relationship kind if necessary
            String currentRelationship = cursor.getString(cursor.getColumnIndex("kind"));
            if (currentRelationship.equalsIgnoreCase("one_to_one") && kind.equalsIgnoreCase("one_to_many")){
                String updateSql = "update entity_relationships set kind=\"" + kind + "\" where _id=" +id;
                db.execSQL(updateSql);
            }

        } else { // the relationship doesn't exist insert a new record to the database
            ContentValues values = new ContentValues();
            values.put("parent_table", parent_table);
            values.put("child_table", child_table);
            values.put("kind", kind);
            values.put("field", field);
            values.put("from_field", from);
            values.put("to_field", to);

            Long id = db.insertWithOnConflict("entity_relationships", BaseColumns._ID, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    public List<Long> retrieveAllForm1Data(){
        return mySQLiteHelper.getAllItemsFromTableName("FWNewHH");
    }

    public List<Long> retrieveAllForm2Data(){
        return mySQLiteHelper.getAllItemsFromTableName("register_anc");
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
