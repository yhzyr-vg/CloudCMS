package com.cloud.cms.model;

import java.util.List;

/**
 * File: ProgramItem.java
 * Author: Landy
 * Create: 2019/1/7 16:01
 */
public class ProgramItem {
    public long programId;
    List<LoadItem> loadItemList;
    public long getProgramId() {
        return programId;
    }
    public void setProgramId(long programId) {
        this.programId = programId;
    }
    public List<LoadItem> getLoadItemList() {
        return loadItemList;
    }
    public void setLoadItemList(List<LoadItem> loadItemList) {
        this.loadItemList = loadItemList;
    }
    public ProgramItem(long programId, List<LoadItem> loadItemList) {
        super();
        this.programId = programId;
        this.loadItemList = loadItemList;
    }
}
