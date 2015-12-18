package com.korosmatick.sample.model.api;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.korosmatick.sample.model.db.Form;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Response {
	
	private double version = 1.0;
	private String status;
	private List<Error> errors;
	
	private List<Form> forms;
	
	private SyncResponse syncResponse;
	
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
	
	public List<Error> getErrors() {
		return errors;
	}

	public void setErrors(List<Error> errors) {
		this.errors = errors;
	}

	public List<Form> getForms() {
		return forms;
	}

	public void setForms(List<Form> forms) {
		this.forms = forms;
	}

	public SyncResponse getSyncResponse() {
		return syncResponse;
	}

	public void setSyncResponse(SyncResponse syncResponse) {
		this.syncResponse = syncResponse;
	}
	
}

