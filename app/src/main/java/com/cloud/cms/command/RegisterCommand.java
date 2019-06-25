package com.cloud.cms.command;

/**
 * File: RegisterCommand.java
 * Author: Landy
 * Create: 2019/1/4 16:57
 */
public class RegisterCommand {

    //客户id
    private int customerId;
    //客户名
    private String customerName;

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
