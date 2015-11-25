package com.korosmatick.sample.model.db;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Form implements Serializable {
	
	private static final long serialVersionUID = -7706556988070010372L;
	
	@Id 
	@GeneratedValue
	private Long id;
	
	private String formVersion;
	
	private String formName;
	
	private String formId;
	
	@Column(nullable = false)
	private String tableName;
	
	@Column(columnDefinition = "TEXT", nullable = false)
	private String modelNode;
	
	@Column(columnDefinition = "TEXT", nullable = false)
	private String formNode;
	
	@Column(nullable = false)
	private String formUrl;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFormVersion() {
		return formVersion;
	}

	public void setFormVersion(String formVersion) {
		this.formVersion = formVersion;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getModelNode() {
		return modelNode;
	}

	public void setModelNode(String modelNode) {
		this.modelNode = modelNode;
	}

	public String getFormNode() {
		return formNode;
	}

	public void setFormNode(String formNode) {
		this.formNode = formNode;
	}

	public String getFormUrl() {
		return formUrl;
	}

	public void setFormUrl(String formUrl) {
		this.formUrl = formUrl;
	}
	
}
