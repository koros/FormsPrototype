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
	 * this is a hack removed as soon as we have the scripts folder setup
	 */
	@Autowired
	private DataSource dataSource;
	
	private static final String CREATE_FOREIGN_KEYS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS entity_relationships (_id SERIAL PRIMARY KEY,  parent_table VARCHAR(255) NOT NULL, child_table VARCHAR(255) NOT NULL, field VARCHAR(255) NOT NULL, kind VARCHAR(255) NOT NULL, from_field VARCHAR(255) NOT NULL, to_field VARCHAR(255) NOT NULL)";
	
	@PostConstruct public void initDb(){
		try {
			Connection connection = dataSource.getConnection();
			Statement stmt = null;
		    try {
		        stmt = connection.createStatement();
		        stmt.execute(CREATE_FOREIGN_KEYS_TABLE_SQL);
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
