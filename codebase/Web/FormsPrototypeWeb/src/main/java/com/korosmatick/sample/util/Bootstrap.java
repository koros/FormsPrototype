package com.korosmatick.sample.util;

import java.sql.Connection;
import java.sql.Statement;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Bootstrap {

	/**
	 * this class is a hack, removed as soon as we have the scripts and db migrations folder is setup
	 */
	
	@Autowired
	private DataSource dataSource;
	
	private static final String CREATE_FOREIGN_KEYS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS entity_relationships (_id SERIAL PRIMARY KEY,  parent_table VARCHAR(255) NOT NULL, child_table VARCHAR(255) NOT NULL, field VARCHAR(255) NOT NULL, kind VARCHAR(255) NOT NULL, from_field VARCHAR(255) NOT NULL, to_field VARCHAR(255) NOT NULL)";
	
	private static final String CREATE_SYNC_TABLE_SQL = "CREATE TABLE IF NOT EXISTS sync_table (_id SERIAL PRIMARY KEY, user_id VARCHAR(255) NOT NULL, type SMALLINT NOT NULL, table_name VARCHAR(255) NOT NULL, record_id INT NOT NULL)";

    private static final String CREATE_SYNC_TABLE_UPDATES_SQL = "CREATE TABLE IF NOT EXISTS updated_records (_id SERIAL PRIMARY KEY, table_name VARCHAR(255) NOT NULL, column_name VARCHAR(255) NOT NULL, new_value VARCHAR(255) NOT NULL, record_id INT NOT NULL)";

    private static final String CREATE_SYNC_TABLE_NEW_CHILD_RECORD_SQL = "CREATE TABLE IF NOT EXISTS new_child_records (_id SERIAL PRIMARY KEY, parent_table_name VARCHAR(255) NOT NULL, parent_id INT NOT NULL, table_name VARCHAR(255) NOT NULL, record_id INT NOT NULL)";
    
    private static final String CREATE_SYNC_TABLE_NEW_RECORD_SQL = "CREATE TABLE IF NOT EXISTS new_records (_id SERIAL PRIMARY KEY, table_name VARCHAR(255) NOT NULL, record_id INT NOT NULL)";
    
	@PostConstruct public void initDb(){
		try {
			Connection connection = dataSource.getConnection();
			Statement stmt = null;
		    try {
		        stmt = connection.createStatement();
		        stmt.execute(CREATE_FOREIGN_KEYS_TABLE_SQL);
		        stmt.execute(CREATE_SYNC_TABLE_SQL);
		        stmt.execute(CREATE_SYNC_TABLE_UPDATES_SQL);
		        stmt.execute(CREATE_SYNC_TABLE_NEW_CHILD_RECORD_SQL);
		        stmt.execute(CREATE_SYNC_TABLE_NEW_RECORD_SQL);
		    } catch (Exception e ) {
		        e.printStackTrace();
		    } finally {
		        if (stmt != null) { stmt.close(); }
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
