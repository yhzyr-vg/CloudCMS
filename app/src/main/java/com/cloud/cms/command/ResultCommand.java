package com.cloud.cms.command;
import java.io.Serializable;
/**
 * File: ResultCommand.java
 * Author: Landy
 * Create: 2019/6/3 18:39
 */
public class ResultCommand implements Serializable{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /** 返回结果. */
    private boolean result = true;

    /** 返回状态码（可选值）. */
    private String statusCode;

    /** 返回message. */
    private String message;

    /** 对应返回的数据对象. */
    private Object returnObject;

    public boolean isResult() {
        return result;
    }
    public void setResult(boolean result) {
        this.result = result;
    }

    public String getStatusCode() {
        return statusCode;
    }
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
    public Object getReturnObject() {
        return returnObject;
    }
    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}

