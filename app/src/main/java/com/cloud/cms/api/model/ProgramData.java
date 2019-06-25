package com.cloud.cms.api.model;

import java.util.List;

public class ProgramData {
    private int id;
    private String name;
    private boolean ispublish;
    private String stype;
    private List<ProgramSchedule> scheduleoftimes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIspublish() {
        return ispublish;
    }

    public void setIspublish(boolean ispublish) {
        this.ispublish = ispublish;
    }

    public String getStype() {
        return stype;
    }

    public void setStype(String stype) {
        this.stype = stype;
    }

    public List<ProgramSchedule> getScheduleoftimes() {
        return scheduleoftimes;
    }

    public void setScheduleoftimes(List<ProgramSchedule> scheduleoftimes) {
        this.scheduleoftimes = scheduleoftimes;
    }
}
