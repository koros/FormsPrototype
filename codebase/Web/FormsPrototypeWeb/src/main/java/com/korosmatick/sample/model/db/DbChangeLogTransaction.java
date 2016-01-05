package com.korosmatick.sample.model.db;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class DbChangeLogTransaction implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5537306968022435584L;
	
	@Id 
	@GeneratedValue
	private Long id;
	private Date timeStamp;
	
	private String userId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
