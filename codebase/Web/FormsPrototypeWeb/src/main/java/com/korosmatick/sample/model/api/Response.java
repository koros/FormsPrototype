package com.korosmatick.sample.model.api;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.korosmatick.sample.model.db.Form;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Response {
	
	private double version = 1.0;
	private String status;
	private String error;
	
	private List<Form> forms;
	
	public double getVersion() {
		return version;
	}

	public void setVersion(double version) {
		this.version = version;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public List<Form> getForms() {
		return forms;
	}

	public void setForms(List<Form> forms) {
		this.forms = forms;
	}
	
}

