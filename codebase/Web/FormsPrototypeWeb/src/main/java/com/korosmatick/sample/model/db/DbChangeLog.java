package com.korosmatick.sample.model.db;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class DbChangeLog implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3006188811391335375L;
	
	@Id 
	@GeneratedValue
	private Long id;
	private Long changeLogTransactionId;
	private Long recordId;
	private int changeType;
	private String tableName;
	private String columnName;
	private String value; /** not necessarily the case all the time **/
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getChangeLogTransactionId() {
		return changeLogTransactionId;
	}

	public void setChangeLogTransactionId(Long changeLogTransactionId) {
		this.changeLogTransactionId = changeLogTransactionId;
	}

	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	public int getChangeType() {
		return changeType;
	}

	public void setChangeType(int changeType) {
		this.changeType = changeType;
	}
	
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
