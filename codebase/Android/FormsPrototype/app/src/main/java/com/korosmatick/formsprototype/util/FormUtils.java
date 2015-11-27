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

    public long saveFormData(String formData) throws Exception {
        Long id = null;
        JSONObject json = XML.toJSONObject(formData);
        System.out.println(json);
        Iterator<?> keys = json.keys();
        while( keys.hasNext() ) {
            String key = (String)keys.next();
            if (json.get(key) instanceof JSONObject) {
                JSONObject object = json.getJSONObject(key);
                id = saveJsonObjectFields(object, key, null, null);

                // Save the form submission record, useful while reconstructing the form later during edit, and syncing
//                if (id != null){
//                    String encounter_type = object.has("encounter_type") ? object.getString("encounter_type") : null;
//                    String form_id = object.has("id") ? object.getString("id") : null;
//                    Long version = json.has("version") ? object.getLong("version") : null;
//                    String table_name = key;
//                    Long data_id = id;
//                    String form_data = formData;
//                    saveFormSubmissionData(encounter_type, form_id, table_name, data_id, form_data);
//                }
            }
        }

        return id;
    }

    public Long saveJsonObjectFields(JSONObject jsonObject, String tableName, String foreignIdFieldName, Long foreignId){
        try {
            ContentValues values = new ContentValues();
            Map<String, String> alterTableSqlStatements = new HashMap<String, String>(); // alter table sql statements if necessary
            StringBuilder createTableSql = new StringBuilder("create table if not exists " + tableName + "(_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT");

            if (foreignIdFieldName != null && foreignId != null) {
                createTableSql.append(", " + foreignIdFieldName + " INTEGER NULL"); // Same table could be linking to multiple parents hence, we should allow null
                values.put(foreignIdFieldName, foreignId);
                String alterStmt = "alter table " + tableName + " add column " + foreignIdFieldName + " INTEGER NULL";
                alterTableSqlStatements.put(foreignIdFieldName, alterStmt);
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
                createTableSql.append(", " + field + " TEXT NULL"); // let all the fields be varchar
                String alterStmt = "alter table " + tableName + " add column " + field + " TEXT NULL";
                alterTableSqlStatements.put(field, alterStmt);

                if (jsonObject.get(field) instanceof JSONObject) {
                    //create a relationship reference in the form submissions
                    String parent_table = tableName;
                    String child_table = field;
                    createOrUpdateTableRelationshipLink(parent_table, child_table, field, "one_to_one", field, "_id");
                    values.put(field, "FETCH_OBJECT");
                }else if(jsonObject.get(field) instanceof JSONArray) {
                    JSONArray array = jsonObject.getJSONArray(field);
                    if (isArrayOfObjects(array)){
                        //create a relationship reference in the form submissions
                        String parent_table = tableName;
                        String child_table = field;
                        createOrUpdateTableRelationshipLink(parent_table, child_table, field, "one_to_many", field, "_id");
                        values.put(field, "FETCH_OBJECT");
                    }else {
                        values.put(field, array.get(0).toString());
                    }
                }else {
                    values.put(field, jsonObject.get(field).toString());
                }
            }

            createTableSql.append(");");
            executeCreateTableIfNotExistStatement(createTableSql.toString());
            executeAlterTableStatementsIfNecessarry(tableName, alterTableSqlStatements);

			/*
			 * generate the id for this record and fetch/create child records
			 */
            Long id = executeInsertStatement(values, tableName);
            for(String field : fields) {
                if (jsonObject.get(field) instanceof JSONObject) {
                    //concat the table name and field to get the name of the child table
                    Long childRecordId = saveJsonObjectFields(jsonObject.getJSONObject(field), field, tableName, id);
                    String updateSql = "update " + tableName + " set " + field  + " = " + childRecordId + " where _id=" + id;
                    executeSQL(updateSql);

                }else if(jsonObject.get(field) instanceof JSONArray) {
                    JSONArray array = jsonObject.getJSONArray(field);
                    if (isArrayOfObjects(array)){
                        values.put(field, "FETCH_OBJECT");
                        for (int i = 0; i < array.length(); i++) {
                            if (array.get(i) instanceof JSONObject) {
                                //concat the table name and field to get the name of the child table
                                saveJsonObjectFields(array.getJSONObject(i), field, tableName, id);
                            }
                        }
                    }
                    else {
                        values.put(field, array.get(0).toString());//hack for the version String
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

//    public Long saveFormSubmissionData(String encounter_type, String form_id, String table_name, Long data_id, String form_data){
//        ContentValues values = new ContentValues();
//        values.put("encounter_type", encounter_type);
//        values.put("form_id", form_id);
//        values.put("table_name", table_name);
//        values.put("data_id", data_id);
//        values.put("form_data", form_data);
//
//        SQLiteDatabase database = mySQLiteHelper.getWritableDatabase();
//        Long id = database.insertWithOnConflict("form_submissions", BaseColumns._ID, values, SQLiteDatabase.CONFLICT_REPLACE);
//        return id;
//    }

    /*
     * Create the table if it doesn't exist
     */
    private void executeCreateTableIfNotExistStatement(String createTableSql) {
        // TODO Auto-generated method stub
        SQLiteDatabase database = mySQLiteHelper.getWritableDatabase();
        database.execSQL(createTableSql);
    }

    private void executeAlterTableStatementsIfNecessarry(String tableName, Map<String, String> alterTableSqlStatements){
        Set<String> keys = alterTableSqlStatements.keySet();
        SQLiteDatabase database = mySQLiteHelper.getWritableDatabase();
        for(String key : keys){
            String columnName = key;
            String alterTableSql = alterTableSqlStatements.get(key);
            if (!mySQLiteHelper.tableContainsColumn(tableName, columnName)){
                try{
                    database.execSQL(alterTableSql);
                }
                catch (Exception doNothing){ /*col already exists*/ }
            }
        }
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
