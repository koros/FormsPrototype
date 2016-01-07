package com.korosmatick.sample.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.korosmatick.sample.dao.DbChangeLogDao;
import com.korosmatick.sample.dao.DbChangeLogTransactionDao;
import com.korosmatick.sample.model.api.Row;
import com.korosmatick.sample.model.api.SyncRequestPacket;
import com.korosmatick.sample.model.api.SyncResponse;
import com.korosmatick.sample.model.api.SyncResponseItem;
import com.korosmatick.sample.model.db.DbChangeLog;
import com.korosmatick.sample.model.db.DbChangeLogTransaction;
import com.korosmatick.sample.util.Constants.SyncType;

public class SyncManager {
	
	private static final Logger logger = LoggerFactory.getLogger(SyncManager.class);
	
	private DataSource dataSource;
	Connection connection;
	private DbChangeLogTransactionDao dbChangeLogTransactionDao;
	private DbChangeLogDao dbChangeLogDao;
	private DbChangeLogTransaction dbChangeLogTransaction;
	
	public SyncManager(DataSource dataSource, DbChangeLogTransactionDao dbChangeLogTransactionDao, DbChangeLogDao dbChangeLogDao) throws SQLException{
		this.dataSource = dataSource;
		connection = dataSource.getConnection();
		this.dbChangeLogTransactionDao = dbChangeLogTransactionDao;
		this.dbChangeLogDao = dbChangeLogDao;
	}
	
	public SyncResponse handleSyncRequest(SyncRequestPacket packet){
		this.dbChangeLogTransaction = createDbChangeLogTransaction();
		SyncResponse syncResponse = new SyncResponse();
		syncResponse.setSyncedItems(new ArrayList<SyncResponseItem>());
		List<Row> rows = packet.getRecords();
		
		for (Row row : rows) {
			saveRowObjectToDb(row, syncResponse, null);
		}
		
		return syncResponse;
	}
	
	private DbChangeLogTransaction createDbChangeLogTransaction(){
		DbChangeLogTransaction dbChangeLogTransaction = new DbChangeLogTransaction();
		dbChangeLogTransaction.setTimeStamp(new Date());
		dbChangeLogTransaction.setUserId("1");
		dbChangeLogTransactionDao.add(dbChangeLogTransaction);
		logger.debug(" ---- dbChangeLogTransaction saved with id : " + dbChangeLogTransaction.getId());
		return dbChangeLogTransaction;
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
			//Connection connection = dataSource.getConnection();
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
    	        createChangeLogForMap(map, tableName, id);
    	    } catch (SQLException e ) {
    	        e.printStackTrace();
    	        logger.error(e.toString());
    	    } finally {
    	        if (stmt != null) { stmt.close(); }
    	    }
			
			return id;
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}
		
		return null;
	}
	
	private boolean isValidCulumnName(String field, String tableName) {
		return !field.equalsIgnoreCase("_id") && HttpService.tableContainsColumn(tableName, field, connection);
	}

	private void createChangeLogForMap(Map<String, String> map, String tableName, Long recordId){
		if (map != null) {
			Set<String> keys = map.keySet();
			for (String s : keys) {
				String columnName = s;
				String value = map.get(s);
				DbChangeLog dbChangeLog = new DbChangeLog();
				dbChangeLog.setChangeLogTransactionId(dbChangeLogTransaction.getId());
				dbChangeLog.setChangeType(SyncType.NEW_RECORD_CREATION);
				dbChangeLog.setColumnName(columnName);
				dbChangeLog.setTableName(tableName);
				dbChangeLog.setValue(value);
				dbChangeLog.setRecordId(recordId);
				dbChangeLogDao.add(dbChangeLog);
			}
		}
	}
	
	private boolean isLinkToParentTable(String fieldName, String tableName){
		boolean linkFound = false;
        try {
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
                throw new SQLException("Creating new record failed, no ID obtained.");
            }
        }
	}
}
