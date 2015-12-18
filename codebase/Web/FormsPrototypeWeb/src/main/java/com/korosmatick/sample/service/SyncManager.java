package com.korosmatick.sample.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.korosmatick.sample.model.api.Row;
import com.korosmatick.sample.model.api.SyncRequestPacket;
import com.korosmatick.sample.model.api.SyncResponse;
import com.korosmatick.sample.model.api.SyncResponseItem;

@Component
public class SyncManager {

	@Autowired
	private DataSource dataSource;
	
	public SyncResponse handleSyncRequest(SyncRequestPacket packet){
		
		SyncResponse syncResponse = new SyncResponse();
		syncResponse.setSyncedItems(new ArrayList<SyncResponseItem>());
		List<Row> rows = packet.getRecords();
		
		for (Row row : rows) {
			saveRowObjectToDb(row, syncResponse, null);
		}
		
		return syncResponse;
	}
	
	private SyncResponse saveRowObjectToDb(Row row, SyncResponse syncResponse, Long parentRecordId){
		SyncResponseItem syncResponseItem = new SyncResponseItem();
		syncResponseItem.setTableName(row.getTableName());
		
		Map<String, String> object = row.getRow();
		Long localId = Long.valueOf(object.get("_id"));
		syncResponseItem.setLocalId(localId);
		
		Long id = saveSingleTableRowTodb(object, row.getTableName(), parentRecordId);
		syncResponseItem.setServerId(id);
		
		//save the child records if any
		List<Row> childRows = row.getChildRows();
		if (childRows != null && !childRows.isEmpty()) {
			for (Row childRow : childRows) {
				saveRowObjectToDb(childRow, syncResponse, id);
			}
		}
		
		syncResponse.getSyncedItems().add(syncResponseItem);
		return syncResponse;
	}
	
	private Long saveSingleTableRowTodb(Map<String, String> map, String tableName, Long parentId){
		try {
			Connection connection = dataSource.getConnection();
    		Statement stmt = null;
    		
			StringBuilder insertSqlStmt = new StringBuilder("insert into " + tableName + " "); 
			StringBuilder columnsString = new StringBuilder("(");
			StringBuilder valuesString = new StringBuilder("values(");
			
			List<String> fields = new ArrayList<String>();
			Iterator<?> keys = map.keySet().iterator();
			while( keys.hasNext() ) {
			    String key = (String)keys.next();
			    fields.add(key);
			}
			
			/*
			 * generate insert sql statement
			 */
			for(String field : fields) {
				columnsString.append(field + ", ");
				String value = null;
				if (isLinkToParentTable(field, tableName)) {
					value = String.valueOf(parentId);
				}else{
					value = map.get(field).toString();
				}
				valuesString.append("'" + value + "', ");
			}
			
			columnsString.deleteCharAt(columnsString.length() - 2).append(")"); //remove trailing ,
			valuesString.deleteCharAt(valuesString.length() - 2).append(");"); //remove trailing ,
			insertSqlStmt.append(columnsString.toString()).append(" ").append(valuesString.toString());
			
			/*
			 * generate the id for this record
			 */
			stmt = connection.createStatement();
			Long id = null;
    		try {
    	        stmt = connection.createStatement();
    	        id = (long) stmt.executeUpdate(insertSqlStmt.toString(), Statement.RETURN_GENERATED_KEYS);
    	    } catch (SQLException e ) {
    	        e.printStackTrace();
    	    } finally {
    	        if (stmt != null) { stmt.close(); }
    	    }
			
			return id;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private boolean isLinkToParentTable(String fieldName, String tableName){
		boolean linkFound = false;
        try {
            //entity_relationships (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,  parent_table TEXT NOT NULL,  child_table TEXT NOT NULL,
            // field TEXT NOT NULL, kind TEXT NOT NULL, from_field TEXT NOT NULL, to_field TEXT NOT NULL)";
        	Connection connection = dataSource.getConnection();
    		String query = "SELECT * FROM entity_relationships WHERE child_table ='" + tableName + "' AND parent_table='" + fieldName + "'";
    		Statement stmt = null;
    		
    		try {
    	        stmt = connection.createStatement();
    	        ResultSet rs = stmt.executeQuery(query);
    	        if (rs.next()) {
    	        	linkFound = true;
    	        }
    	    } catch (SQLException e ) {
    	        e.printStackTrace();
    	    } finally {
    	        if (stmt != null) { stmt.close(); }
    	    }
    		
        }catch (Exception e){
            e.printStackTrace();
        }
        
        return linkFound;
    }
}
