package com.korosmatick.formsprototype.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.util.Log;
import android.util.Xml;


import com.korosmatick.formsprototype.database.MySQLiteHelper;
import com.korosmatick.formsprototype.model.Form;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by koros on 10/2/15.
 */
public class FormBuilder {

    Context context;
    MySQLiteHelper mySQLiteHelper;
    private static FormBuilder instance;
    SQLiteDatabase db;

    public FormBuilder(Context context){
        this.context = context;
        mySQLiteHelper = MySQLiteHelper.getInstance(context);
        db = mySQLiteHelper.getWritableDatabase();
    }

    public static FormBuilder getInstance(Context context){
        if (instance == null){
            instance = new FormBuilder(context);
        }
        return instance;
    }

    public String buildFormSubmissionXMLString(Form form, Long recordId){
        try{
            Document document = getXMLModelTemplateForFormName(form.getModelNode());

            XmlSerializer serializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);

            //<model><instance>
            Element el = (Element)document.getElementsByTagName("model").item(0).getFirstChild();
            NodeList entries = el.getChildNodes();
            int num = entries.getLength();
            for (int i = 0; i < num; i++) {
                Element node = (Element) entries.item(i);
                writeXML(node, serializer, recordId);
            }

            serializer.endDocument();

            String xml = writer.toString();
            //xml = xml.replaceAll("<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>","");//56 !!!this ain't working
            xml = xml.substring(56);
            System.out.println(xml);

            return xml;

        }catch(Exception e){
            e.printStackTrace();
        }

        return "";
    }

    private Document getXMLModelTemplateForFormName(String formStr) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        DocumentBuilder db = dbf.newDocumentBuilder();

        formStr = formStr.replaceAll("\\\\\"", "\"").replaceAll("\\\\/", "/"); // remove the escaped strings
        // convert String into InputStream
        InputStream is = new ByteArrayInputStream(formStr.getBytes());
        Document document = db.parse(is);

        return document;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void writeXML(Element node, XmlSerializer serializer, Long recordId){
        try {
            String nodeName = node.getNodeName();
            //String nodeContent = node.getTextContent();

            serializer.startTag("", nodeName);

            Cursor cursor = retrieveDatabaseRecordForNode(nodeName, recordId);
            if (cursor.moveToFirst()) {
                writeXMLAttributes(node, serializer, recordId);

                int valueIndex = cursor.getColumnIndex("content");
                if (valueIndex != -1 ){
                    String value = "";
                    if (cursor.getType(valueIndex) == Cursor.FIELD_TYPE_STRING){
                        value = cursor.getString(valueIndex);
                    }else if (cursor.getType(valueIndex) == Cursor.FIELD_TYPE_FLOAT){
                        value = String.valueOf(cursor.getFloat(valueIndex));
                    }else if (cursor.getType(valueIndex) == Cursor.FIELD_TYPE_INTEGER){
                        value = String.valueOf(cursor.getInt(valueIndex));
                    }
                    serializer.text(value);
                }


                Long id = cursor.getLong(cursor.getColumnIndex("_id"));

                NodeList entries = node.getChildNodes();
                int num = entries.getLength();
                for (int i = 0; i < num; i++) {
                    if (entries.item(i) instanceof Element) {
                        Element child = (Element) entries.item(i);

                        String fieldName = child.getNodeName();
                        String columnName = FormUtils.retrieveTableNameOrColForField(fieldName);
                        if (cursor.getColumnIndex(columnName) != -1){

                            String childTableName = columnName;
                            String parentTableName = FormUtils.retrieveTableNameOrColForField(nodeName);
                            // check if the field falls under foreign id cols and
                            if (mySQLiteHelper.tableExists(childTableName) && mySQLiteHelper.tableContainsColumn(childTableName, parentTableName)){
                                String sqlQuery = "select * from " + childTableName + " where " + parentTableName + " = \"" + id + "\"";
                                Cursor cursor2 = db.rawQuery(sqlQuery, null);

                                if (cursor2.moveToFirst()) {
                                    do {
                                        Long childId = cursor2.getLong(cursor2.getColumnIndex("_id"));
                                        writeXML(child, serializer, childId);
                                    } while (cursor2.moveToNext());
                                }
                                cursor2.close();
                            }
                            else{// its not a table
                                serializer.startTag("", fieldName);
                                String fieldValue = "";
                                int columnIdex = cursor.getColumnIndex(columnName);
                                if (cursor.getType(columnIdex) == Cursor.FIELD_TYPE_STRING){
                                    fieldValue = cursor.getString(columnIdex);
                                }else if (cursor.getType(columnIdex) == Cursor.FIELD_TYPE_FLOAT){
                                    fieldValue = String.valueOf(cursor.getFloat(columnIdex));
                                }else if (cursor.getType(columnIdex) == Cursor.FIELD_TYPE_INTEGER){
                                    fieldValue = String.valueOf(cursor.getInt(columnIdex));
                                }
                                serializer.text(fieldValue);
                                serializer.endTag("", fieldName);
                            }
                        }
                    }
                }
            }

            cursor.close();

            serializer.endTag("", node.getNodeName());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void writeXMLAttributes(Element node, XmlSerializer serializer, Long recordId){
        try {
            String tableName = FormUtils.retrieveTableNameOrColForField(node.getNodeName());
            if (mySQLiteHelper.tableExists(tableName)){
                Cursor cursor = retrieveDatabaseRecordForNode(tableName, recordId);

                if (cursor.moveToFirst()){

                    // write the _id field as an attribute useful if we are editing a repeat group and/or a record
                    serializer.attribute("", "_id", cursor.getString(cursor.getColumnIndex("_id")));

                    // get a map containing the attributes of this node
                    NamedNodeMap attributes = node.getAttributes();

                    // get the number of nodes in this map
                    int numAttrs = attributes.getLength();

                    for (int i = 0; i < numAttrs; i++) {
                        Attr attr = (Attr) attributes.item(i);
                        String attrName = attr.getNodeName();
                        String columnName = FormUtils.retrieveTableNameOrColForField(attrName);
                        try{
                            // setting attribute to element
                            if (cursor.getColumnIndex(columnName) != -1){
                                String attrValue = cursor.getString(cursor.getColumnIndex(columnName));
                                if (attrValue != null){
                                    serializer.attribute("", attrName, attrValue);
                                }
                            }
                        }catch (Exception doNothing){
                            doNothing.printStackTrace();
                        }
                    }
                    cursor.close();
                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Cursor retrieveDatabaseRecordForNode(String node, Long id){
        String tableName = FormUtils.retrieveTableNameOrColForField(node);
        String sqlQuery = "select * from " + tableName + " where _id = " + id;
        Cursor cursor = mySQLiteHelper.executeQuery(sqlQuery);
        return cursor;
    }

    public String readFileFromAssets(String fileName) {
        String fileContents = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            fileContents = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        Log.d("File", fileContents);
        return fileContents;
    }
}
