package com.korosmatick.formsprototype.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.List;

/**
 * Created by koros on 11/12/15.
 */
@JsonRootName(value="response")
@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY)
public class Response {

    @JsonProperty("version")
    private double version;

    @JsonProperty("status")
    private String status;

    @JsonProperty("error")
    private String error;

    @JsonProperty("forms")
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

    public SyncResponse getSyncResponse() {
        return syncResponse;
    }

    public void setSyncResponse(SyncResponse syncResponse) {
        this.syncResponse = syncResponse;
    }
}
