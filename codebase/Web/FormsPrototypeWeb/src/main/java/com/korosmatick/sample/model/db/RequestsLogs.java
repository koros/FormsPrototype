package com.korosmatick.sample.model.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class RequestsLogs implements Serializable {
	
	private static final long serialVersionUID = -7706556988070010372L;
	
	@Id 
	@GeneratedValue
	private Long id;
	
	private String time;
	
	@Column(columnDefinition = "TEXT")
	private String allRequestParams;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAllRequestParams() {
		return allRequestParams;
	}

	public void setAllRequestParams(String allRequestParams) {
		this.allRequestParams = allRequestParams;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
}
