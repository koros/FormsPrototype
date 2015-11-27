package com.korosmatick.formsprototype.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Created by koros on 11/12/15.
 */
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class Form {

    private Long id;

    private String formVersion;

    private String formName;

    private String formId;

    private String tableName;

    private String modelNode;

    private String formNode;

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
