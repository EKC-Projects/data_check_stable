package com.sec.datacheck.checkdata.view.POJO;

import androidx.annotation.Nullable;

import com.esri.arcgisruntime.data.CodedValueDomain;
import com.esri.arcgisruntime.data.Domain;

import java.util.Map;

public class FieldModel {

    private int type; // 1: Domain 2: text/int/double

    private String title;

    private String alias;

    private String textValue;

    private CodedValueDomain choiceDomain;

    private CodedValueDomain domain;

    private Object selectedDomainIndex;

    public FieldModel() {
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public CodedValueDomain getDomain() {
        return domain;
    }

    public void setDomain(CodedValueDomain domain) {
        this.domain = domain;
    }

    public Object getSelectedDomainIndex() {
        return selectedDomainIndex;
    }

    public void setSelectedDomainIndex(Object selectedDomainIndex) {
        this.selectedDomainIndex = selectedDomainIndex;
    }

    public CodedValueDomain getChoiceDomain() {
        return choiceDomain;
    }

    public void setChoiceDomain(CodedValueDomain choiceDomain) {
        this.choiceDomain = choiceDomain;
    }
}
