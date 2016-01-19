package com.korosmatick.formsprototype.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.korosmatick.formsprototype.model.Form;
import com.korosmatick.formsprototype.model.UpdatedItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by koros on 9/28/15.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    private static final String TAG = "MySQLiteHelper";
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "forms_prototype";

    private static final String FORMS_TABLE_NAME = "forms";

    //private static final String CREATE_FORMS_SUBMISSIONS_TABLE_SQL = "CREATE TABLE form_submissions (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,  encounter_type TEXT NOT NULL,  form_id TEXT NOT NULL, table_name TEXT NOT NULL, data_id INTEGER NOT NULL, form_data TEXT NOT NULL)";

    private static final String CREATE_FORMS_TABLE_SQL = "CREATE TABLE forms (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,  formVersion TEXT NOT NULL, tableName TEXT NOT NULL, formName TEXT NOT NULL, formId TEXT NULL, modelNode TEXT NOT NULL, formNode TEXT NOT NULL, formUrl TEXT NOT NULL)";

    private static final String CREATE_FOREIGN_KEYS_TABLE_SQL = "CREATE TABLE entity_relationships (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,  parent_table TEXT NOT NULL,  child_table TEXT NOT NULL, field TEXT NOT NULL, kind TEXT NOT NULL, from_field TEXT NOT NULL, to_field TEXT NOT NULL)";

    private static final String CREATE_SYNC_TABLE_SQL = "CREATE TABLE sync_table (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, table_name TEXT NOT NULL, record_id INTEGER NOT NULL, type TEXT NOT NULL, column TEXT NULL)";

    private static final String CREATE_SYNC_POINTER_TABLE_SQL = "CREATE TABLE sync_pointer (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, sync_time TIMESTAMP NOT NULL, position INTEGER NOT NULL)";

    private static final String CREATE_SYNC_TABLE_UPDATES_SQL = "CREATE TABLE sync_table_updates (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, server_id INTEGER NOT NULL, table_name TEXT NOT NULL, column_name TEXT NOT NULL, new_value TEXT NOT NULL, synced SMALLINT NOT NULL)";

    static MySQLiteHelper instance;

    public static MySQLiteHelper getInstance(Context context){
        if (instance == null){
            instance = new MySQLiteHelper(context);
        }
        return instance;
    }

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        instance = this;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create the table that holds the form submissions in form of xml data
        db.execSQL(CREATE_FORMS_TABLE_SQL);
        db.execSQL(CREATE_FOREIGN_KEYS_TABLE_SQL);
        db.execSQL(CREATE_SYNC_TABLE_SQL);
        db.execSQL(CREATE_SYNC_POINTER_TABLE_SQL);
        db.execSQL(CREATE_SYNC_TABLE_UPDATES_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //upgrade the database statements
    }

    public boolean tableExists(String tableName) {
        // get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
        if(cursor != null) {
            if(cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public Cursor executeQuery(String sqlQuery){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sqlQuery, null);
        return cursor;
    }

    public void executeSQL(String sqlQuery){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sqlQuery);
    }

    public boolean tableContainsColumn(String tableName, String columnName) {
        Cursor mCursor = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            mCursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 0", null);

            // getColumnIndex() gives us the index (0 to ...) of the column - otherwise we get a -1
            if (mCursor.getColumnIndex(columnName) != -1)
                return true;
            else
                return false;

        } catch (Exception e) {
            // Something went wrong. Missing the database? The table?
            e.printStackTrace();
            return false;
        } finally {
            if (mCursor != null) mCursor.close();
        }
    }

    public Map<String, String> retrieveForeigIdColumnsForTable(String tableName){
        // get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Map<String, String> foreignIdsColumns = new HashMap<String, String>();
        Cursor cursor = db.rawQuery("select * from entity_relationships where parent_table = '" + tableName + "'", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                foreignIdsColumns.put(cursor.getString(cursor.getColumnIndex("field")), cursor.getString(cursor.getColumnIndex("kind")));
            } while (cursor.moveToNext());
        }
        return foreignIdsColumns;
    }

    public long performInsertForContentValues(ContentValues values, String tableName){
        SQLiteDatabase db = this.getWritableDatabase();
        Long id = db.insertWithOnConflict(tableName, BaseColumns._ID, values, SQLiteDatabase.CONFLICT_REPLACE);
        return id;
    }

    public long saveMapValuesToDBTable(Map<String, String> map, String tableName){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        Iterator<String> iterator = map.keySet().iterator();

        while (iterator.hasNext()){
            String key = iterator.next();
            String value = map.get(key);

            if (tableContainsColumn(tableName, key)){
                values.put(key, value);
            }
        }

        Long id = db.insertWithOnConflict(tableName, BaseColumns._ID, values, SQLiteDatabase.CONFLICT_REPLACE);
        return id;
    }

    public String[] getColumnNamesForTable(String tableName) {
        Cursor mCursor = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            mCursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 0", null);
            return mCursor.getColumnNames();

        } catch (Exception e) {
            // Something went wrong. Missing the database? The table?
            e.printStackTrace();
            return new String[]{};
        } finally {
            if (mCursor != null) mCursor.close();
        }
    }

    public Long saveForm(Form form){
        try{
            Long id = null;
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("formVersion", form.getFormVersion());
            values.put("formId", form.getFormId());
            values.put("tableName", form.getTableName());
            values.put("formName", form.getFormName());
            values.put("modelNode", form.getModelNode());
            values.put("formNode", form.getFormNode());
            values.put("formUrl", form.getFormUrl());
            if ((id = getSavedForm(form)) != null){
                return id;
            }else{
                id = db.insertWithOnConflict(FORMS_TABLE_NAME, BaseColumns._ID, values, SQLiteDatabase.CONFLICT_REPLACE);
                return id;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Long getSavedForm(Form form){
        Cursor mCursor = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            mCursor = db.rawQuery("SELECT * FROM " + FORMS_TABLE_NAME + " where formName = '" + form.getFormName() + "' LIMIT 1", null);
            if (mCursor != null && mCursor.moveToFirst()){
                return mCursor.getLong(mCursor.getColumnIndex("_id"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (mCursor != null) mCursor.close();
        }
        return null;
    }

    public List<Form> retrieveAllForms(){
        Cursor mCursor = null;
        List<Form> forms = new ArrayList<Form>();
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            mCursor = db.rawQuery("SELECT * FROM " + FORMS_TABLE_NAME + " ", null);
            if (mCursor != null && mCursor.moveToFirst()){
                do {
                    Form form = new Form();
                    form.setId(mCursor.getLong(mCursor.getColumnIndex("_id")));
                    form.setFormVersion(mCursor.getString(mCursor.getColumnIndex("formVersion")));
                    form.setFormId(mCursor.getString(mCursor.getColumnIndex("formId")));
                    form.setTableName(mCursor.getString(mCursor.getColumnIndex("tableName")));
                    form.setFormName(mCursor.getString(mCursor.getColumnIndex("formName")));
                    form.setModelNode(mCursor.getString(mCursor.getColumnIndex("modelNode")));
                    form.setFormUrl(mCursor.getString(mCursor.getColumnIndex("formUrl")));
                    forms.add(form);
                }while (mCursor.moveToNext());
            }
            return forms;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (mCursor != null) mCursor.close();
        }
        return forms;
    }

    public Form getFormById(Long id){
        Cursor mCursor = null;
        Form form = null;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            mCursor = db.rawQuery("SELECT * FROM " + FORMS_TABLE_NAME + " where _id = '" + id + "' LIMIT 1", null);
            if (mCursor != null && mCursor.moveToFirst()){
                form = new Form();
                form.setFormVersion(mCursor.getString(mCursor.getColumnIndex("formVersion")));
                form.setFormId(mCursor.getString(mCursor.getColumnIndex("formId")));
                form.setTableName(mCursor.getString(mCursor.getColumnIndex("tableName")));
                form.setFormName(mCursor.getString(mCursor.getColumnIndex("formName")));
                form.setModelNode(mCursor.getString(mCursor.getColumnIndex("modelNode")));
                form.setFormNode(mCursor.getString(mCursor.getColumnIndex("formNode")));
                form.setFormUrl(mCursor.getString(mCursor.getColumnIndex("formUrl")));
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (mCursor != null) mCursor.close();
        }
        return form;
    }

    public Map<String, String> sqliteRowToMap(Cursor cursor) {
        int totalColumn = cursor.getColumnCount();
        Map<String, String> rowObject = new HashMap<String, String>();
        if (cursor != null && cursor.moveToFirst()){
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                    }
                }
            }
        }
        return rowObject;
    }

    public Map<String, String> sqliteRowToMap(Cursor cursor, int offset) {
        int totalColumn = cursor.getColumnCount();
        Map<String, String> rowObject = new HashMap<String, String>();
        if (cursor != null && cursor.moveToPosition(offset)){
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                    } catch (Exception e) {
                        Log.d(TAG, e.getMessage());
                    }
                }
            }
        }
        return rowObject;
    }

    public List<UpdatedItem> getUpdatedItemList(){
        Cursor mCursor = null;
        List<UpdatedItem> updatedItemList = new ArrayList<UpdatedItem>();
        //"CREATE TABLE sync_table_updates (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, server_id INTEGER NOT NULL, table_name TEXT NOT NULL, column_name TEXT NOT NULL, new_value TEXT NOT NULL, synced SMALLINT NOT NULL)";
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            mCursor = db.rawQuery("SELECT * FROM sync_table_updates", null);
            if (mCursor != null && mCursor.moveToFirst()){
                do {
                    UpdatedItem updatedItem = new UpdatedItem();
                    updatedItem.setTableName(mCursor.getString(mCursor.getColumnIndex("table_name")));
                    updatedItem.setColumnName(mCursor.getString(mCursor.getColumnIndex("column_name")));
                    updatedItem.setNewValue(mCursor.getString(mCursor.getColumnIndex("new_value")));
                    updatedItem.setServerId(mCursor.getLong(mCursor.getColumnIndex("server_id")));
                    updatedItem.setSyncItemId(mCursor.getString(mCursor.getColumnIndex("_id")));
                    updatedItemList.add(updatedItem);
                } while (mCursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (mCursor != null) mCursor.close();
        }
        return updatedItemList;
    }
}
