package com.cloud.cms.api.model;

import java.util.List;

public class Program {
    private int total;
    private String messageId;
    private List<ProgramData> rows;

    private List<Template> uITemplateList;

    public List<Template> getuITemplateList() {
        return uITemplateList;
    }

    public void setuITemplateList(List<Template> uITemplateList) {
        this.uITemplateList = uITemplateList;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public List<ProgramData> getRows() {
        return rows;
    }

    public void setRows(List<ProgramData> rows) {
        this.rows = rows;
    }
}
