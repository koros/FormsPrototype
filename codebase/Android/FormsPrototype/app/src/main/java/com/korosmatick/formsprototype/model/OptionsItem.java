package com.korosmatick.formsprototype.model;

/**
 * Created by koros on 3/6/16.
 */
public class OptionsItem {

    String formName;

    String displayName;

    Long formId;

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Long getFormId() {
        return formId;
    }

    public void setFormId(Long formId) {
        this.formId = formId;
    }
}
