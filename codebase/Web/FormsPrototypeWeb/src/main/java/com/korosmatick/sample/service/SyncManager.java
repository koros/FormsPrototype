package com.korosmatick.sample.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.korosmatick.sample.model.api.Row;
import com.korosmatick.sample.model.api.SyncRequestPacket;
import com.korosmatick.sample.model.api.SyncResponse;
import com.korosmatick.sample.model.api.SyncResponseItem;
import com.korosmatick.sample.model.api.UpdatedItem;
import com.korosmatick.sample.model.api.UpdatedItemsPacket;
import com.korosmatick.sample.util.Constants.SyncType;

public class SyncManager {
	
	private static final Logger logger = LoggerFactory.getLogger(SyncManager.class);
	
	private Connection connection;
	
	public SyncManager(Connection connection){
		this.connection = connection;
	}
	
	public SyncResponse handleSyncRequest(SyncRequestPacket packet){
		SyncResponse syncResponse = new SyncResponse();
		syncResponse.setSyncedItems(new ArrayList<SyncResponseItem>());
		List<Row> rows = packet.getRecords();
		
		// process the new records list
		if (rows != null) {
			for (Row row : rows) {
				saveRowObjectToDb(row, syncResponse, null);
			}
		}
		
		// process the updated items list
		UpdatedItemsPacket updatedItems = packet.getUpdatedItemsPacket();
		List<Long> updatedItemsAck = handleUpdatedItemsPacket(updatedItems);
		syncResponse.setUpdatedItemsAck(updatedItemsAck);
		
		return syncResponse;
	}
	
	private List<Long> handleUpdatedItemsPacket(UpdatedItemsPacket updatedItems) {
		List<Long> updatedItemsAck = new ArrayList<Long>();
		if (updatedItems != null) {
			List<UpdatedItem> updatedItemsList = updatedItems.getUpdatedItemList();
			
			if (updatedItemsList != null) {
				for (UpdatedItem updatedItem : updatedItemsList) {
					try {
						if (updateDatabase(updatedItem)) {
							updatedItemsAck.add(Long.valueOf(updatedItem.getSyncItemId()));
							createSyncPointerForUpdatedRecord(updatedItem.getServerId(), updatedItem.getTableName(), updatedItem.getColumnName(), updatedItem.getNewValue());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return updatedItemsAck;
	}

	/**
	 * Create a pointer in the sync table to denote a record update operation
	 * 
	 * @param serverId
	 * @param tableName
	 * @param columnName
	 * @param newValue
	 */
	private void createSyncPointerForUpdatedRecord(Long id, String tableName, String columnName, String newValue) throws Exception{
		//Connection connection = dataSource.getConnection();
		//updated_records (_id SERIAL PRIMARY KEY, table_name VARCHAR(255) NOT NULL, column_name VARCHAR(255) NOT NULL, new_value VARCHAR(255) NOT NULL, record_id INT NOT NULL)
		String insert_sql = "INSERT INTO updated_records (table_name, column_name, new_value, record_id) "
				+ "VALUES ('" + tableName + "', '" + columnName + "', '" + newValue + "', '" + id + "')";
		Long record_id = insertNewRecordAndReturnId(insert_sql);
		
		//sync_table (_id SERIAL PRIMARY KEY, user_id VARCHAR(255) NOT NULL, type SMALLINT NOT NULL, table_name VARCHAR(255) NOT NULL, record_id INT NOT NULL)
		String insertTableSQL = "INSERT INTO sync_table"
    			+ "(user_id, type, table_name, record_id) VALUES"
    			+ "(?, ?, ?, ?)";
    	PreparedStatement preparedStatement = connection.prepareStatement(insertTableSQL);
    	preparedStatement.setString(1, "dummy_user");
    	preparedStatement.setInt(2, SyncType.UPDATE);
    	preparedStatement.setString(3, "updated_records");
    	preparedStatement.setLong(4, record_id);
    	// execute insert SQL stetement
    	preparedStatement .executeUpdate();
	}

	private boolean updateDatabase(UpdatedItem updatedItem) {
		try {
			//Connection connection = dataSource.getConnection();
			String updateTableSql = "UPDATE " + updatedItem.getTableName() + " SET " + updatedItem.getColumnName() + " = '" + updatedItem.getNewValue() + "' WHERE _id = " + updatedItem.getServerId();
    		Statement stmt = null;
    		
    		try {
    	        stmt = connection.createStatement();
    	        int rs = stmt.executeUpdate(updateTableSql);
    	        return rs > 0;
    	    } catch (SQLException e ) {
    	        e.printStackTrace();
    	        logger.error(e.toString());
    	    } finally {
    	        if (stmt != null) { stmt.close(); }
    	    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
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
			
			StringBuilder insertSqlStmt = new StringBuilder("insert into " + tableName + " "); 
			StringBuilder columnsString = new StringBuilder("(");
			StringBuilder valuesString = new StringBuilder("values(");
			
			// generate insert sql statement 
			Iterator<?> keys = map.keySet().iterator();
			while( keys.hasNext() ) {
			    String field = (String)keys.next();
			    //exclude non relevant fields sent from the client side
				if (!isValidCulumnName(field, tableName)) {
					continue;
				}
				
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
			
			logger.debug(">>>" + insertSqlStmt.toString());
			
			/*
			 * generate the id for this record
			 */
			Long id = null;
    		try {
    			id = insertNewRecordAndReturnId(insertSqlStmt.toString());
    			createSyncPointerForRootRecord(id, tableName, parentId);
    	    } catch (SQLException e ) {
    	        e.printStackTrace();
    	        logger.error(e.toString());
    	    }
			
			return id;
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		
		return null;
	}
	
	/**
	 * Inserts a record to the sync pointer table if the record is a root record i.e has no parent record
	 * At the moment we only keep the reference of the root record
	 * 
	 * @param id
	 * @param tableName
	 * @param parentId
	 */
	private void createSyncPointerForRootRecord(Long id, String tableName, Long parentId) throws Exception{
		//Connection connection = dataSource.getConnection();
		if (id != null && parentId == null) {  
			//new_records (_id SERIAL PRIMARY KEY, table_name VARCHAR(255) NOT NULL, record_id INT NOT NULL)
			String insert_into_new_records = "INSERT INTO new_records (table_name, record_id) VALUES ('" + tableName + "', '" + id + "')";
    		Long record_id = insertNewRecordAndReturnId(insert_into_new_records);
    		
    		//sync_table (_id SERIAL PRIMARY KEY, user_id VARCHAR(255) NOT NULL, type SMALLINT NOT NULL, table_name VARCHAR(255) NOT NULL, record_id INT NOT NULL)
    		String insertTableSQL = "INSERT INTO sync_table"
        			+ "(user_id, type, table_name, record_id) VALUES"
        			+ "(?, ?, ?, ?)";
        	PreparedStatement preparedStatement = connection.prepareStatement(insertTableSQL);
        	preparedStatement.setString(1, "dummy_user");
        	preparedStatement.setInt(2, SyncType.NEW_RECORD_CREATION);
        	preparedStatement.setString(3, "new_records");
        	preparedStatement.setLong(4, record_id);
        	// execute insert SQL stetement
        	preparedStatement .executeUpdate();
		}
	}

	private boolean isValidCulumnName(String field, String tableName) throws Exception{
		//Connection connection = dataSource.getConnection();
		return !field.equalsIgnoreCase("_id") && HttpService.tableContainsColumn(tableName, field, connection);
	}

	private boolean isLinkToParentTable(String fieldName, String tableName){
		boolean linkFound = false;
        try {
        	//Connection connection = dataSource.getConnection();
            //entity_relationships (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,  parent_table TEXT NOT NULL,  child_table TEXT NOT NULL,
            // field TEXT NOT NULL, kind TEXT NOT NULL, from_field TEXT NOT NULL, to_field TEXT NOT NULL)";
        	//Connection connection = dataSource.getConnection();
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
    	        logger.error(e.toString());
    	    } finally {
    	        if (stmt != null) { stmt.close(); }
    	    }
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.toString());
        }
        
        return linkFound;
    }
	
	private long insertNewRecordAndReturnId(String sql) throws Exception {
		//Connection connection = dataSource.getConnection();
		PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        int affectedRows = statement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Creating new record failed, no rows affected.");
        }

        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getLong(1);
            }
            else {
                throw new SQLException("Creating new record failed, no id obtained.");
            }
        }
	}
}
