package com.korosmatick.formsprototype.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.korosmatick.formsprototype.database.MySQLiteHelper;
import com.korosmatick.formsprototype.model.Form;
import com.korosmatick.formsprototype.model.Response;


import junit.framework.Assert;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by koros on 11/12/15.
 */
public class FormsHttpService {

    Context context = null;
    MySQLiteHelper mySQLiteHelper = null;

    public FormsHttpService(Context context){
        this.context = context;
        mySQLiteHelper = MySQLiteHelper.getInstance(context);
    }

    public List<Form> getAllForms(String serviceEndpoint) {

        try {

            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(serviceEndpoint);
            HttpResponse response;
            BufferedReader bufferedReader;
            Response syncResponse;

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);

            try {
                // Add your data
                //List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                //nameValuePairs.add(new BasicNameValuePair("ids", idsParam));
                //httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                response = httpclient.execute(httppost);

                Log.d("", "\n----------------------------------------------------------------\n");
                Log.d("", httppost.getURI().toString());
                Log.d("", "\n----------------------------------------------------------------\n");

                InputStream stream = response.getEntity().getContent();
                //stream = new GZIPInputStream(stream);
                bufferedReader = new BufferedReader( new InputStreamReader(stream));

//                String line = null;
//                while((line = bufferedReader.readLine()) != null){
//                    Log.d("", line);
//                }

                syncResponse = mapper.readValue(bufferedReader, Response.class);
                List<Form> forms = syncResponse.getForms();

                processForms(forms);

                return forms;

            }catch (Exception e) {
                Log.e("", e.getMessage(), e);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private void processForms(List<Form> forms){
        for (Form form : forms){
            processForm(form);
        }
    }

    private void processForm(Form form){
        String xmlModelNode = form.getModelNode();

        //process the xml node to create or update the existing sql tables
        processXMLModel(xmlModelNode, form);
    }

    public Form processXMLModel(String xmlModelString, Form form) {
        try {
            xmlModelString = xmlModelString.replaceAll("\\\\/", "/").replaceAll("\\\\\"", "\"");
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(xmlModelString.getBytes("UTF-8"));
            org.w3c.dom.Document document = docBuilder.parse(stream);

            //<model><instance>
            Node root = document.getDocumentElement();
            Node modelNode = root.getFirstChild();
            Node instanceNode = modelNode.getFirstChild();

            //retrieve version and id
            String version = retrieveAttribute(instanceNode, "version");
            String id = retrieveAttribute(instanceNode, "id");

            Assert.assertEquals(form.getFormId(), id);
            //form.setFormId(id);
            Assert.assertEquals(form.getFormVersion(), version);
            //form.setFormVersion(version);

            //set the tableName
            String tableName = retrieveTableNameOrColForNode(instanceNode);
            Assert.assertEquals(form.getTableName(), tableName);
            //form.setTableName(tableName);

            //process the xml model and create the respective tables
            processNode(instanceNode, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return form;
    }

    public String retrieveTableNameOrColForNode(Node node){
        //replace all illegal xters in the table name
        return "_" + node.getNodeName().toLowerCase().replaceAll("[^A-Za-z0-9_]", "_");
    }

    public static String retrieveAttribute(Node node, String attributeName) {
        // get a map containing the attributes of this node
        NamedNodeMap attributes = node.getAttributes();

        // get the number of nodes in this map
        int numAttrs = attributes.getLength();

        for (int i = 0; i < numAttrs; i++) {
            Attr attr = (Attr) attributes.item(i);

            String attrName = attr.getNodeName();
            String attrValue = attr.getNodeValue();

            if (attrName.equals(attributeName)) {
                System.out.println("Found attribute: " + attrName + " with value: " + attrValue);
                return attrValue;
            }
        }
        return null;
    }

    public void processNode(Node node, Node parentNode) {
        //replace all illegal xters in the table name
        String tableName = retrieveTableNameOrColForNode(node);
        String parentName = parentNode != null ? node.getParentNode().getNodeName() : "";
        System.out.println(parentName + " : " + tableName);

        StringBuilder createTableSql = new StringBuilder("create table if not exists " + tableName + "(_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, _serverid INTEGER UNIQUE");
        Map<String, String> alterTableSqlStatements = new HashMap<String, String>(); // alter table sql statements if necessary

        if (parentNode != null) {
            String foreignIdFieldName = retrieveTableNameOrColForNode(parentNode);
            createTableSql.append(", " + foreignIdFieldName  + " INTEGER DEFAULT NULL"); // Same table could be linking to multiple parents hence, we should allow null
            String alterStmt = "alter table " + tableName + " add column " + foreignIdFieldName + " INTEGER DEFAULT NULL";
            alterTableSqlStatements.put(foreignIdFieldName, alterStmt);

            // add a link to relatioship table
            createOrUpdateTableRelationshipLink(foreignIdFieldName, tableName, foreignIdFieldName, "one_to_many", foreignIdFieldName, "_id");
        }

        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
                // calls this method for all the children which is Element
                String colName = retrieveTableNameOrColForNode(currentNode);
                createTableSql.append(", " + colName + " TEXT NULL");
                String alterStmt = "alter table " + tableName + " add column " + colName + " TEXT NULL";
                alterTableSqlStatements.put(colName, alterStmt);
                if (hasChildElements(currentNode)) {
                    processNode(currentNode, node);
                }
            }
        }

        createTableSql.append(");");

        // execute sql statement to create the table
        try {
            executeCreateTableIfNotExistStatement(createTableSql.toString());
            executeAlterTableStatementsIfNecessarry(tableName, alterTableSqlStatements);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean tableContainsColumn(String tableName, String columnName) {
        return mySQLiteHelper.tableContainsColumn(tableName, columnName);
    }

    /*
     * Create the table if it doesn't exist
     */
    private void executeCreateTableIfNotExistStatement(final String createTableStmt) {
        mySQLiteHelper.executeSQL(createTableStmt);
    }

    private void executeAlterTableStatementsIfNecessarry(final String tableName, final Map<String, String> alterTableSqlStatements){
        try {
            Set<String> keys = alterTableSqlStatements.keySet();
            for(String key : keys){
                String columnName = key;
                String alterTableSql = alterTableSqlStatements.get(key);
                if (!tableContainsColumn(tableName, columnName)){
                    mySQLiteHelper.executeSQL(alterTableSql);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean hasChildElements(Node el) {
        NodeList children = el.getChildNodes();
        for (int i = 0;i < children.getLength();i++) {
            if (children.item(i).getNodeType() == Node.ELEMENT_NODE) {
                return true;
            }
        }
        return false;
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
}
