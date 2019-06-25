package com.cloud.cms.form;

import java.io.Serializable;

/**
 * File: BaseForm.java
 * Author: Landy
 * Create: 2019/6/10 14:40
 */
public class BaseForm implements Serializable {
    private Long id;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
