package com.example.loverecycle.beans;

import java.io.Serializable;

public class ActivityBean implements Serializable {

    private String name;
    private Long associationId;
    private String info;
    private String startDate;
    private String endDate;
    private Long activityId;
    private String icon;

    public ActivityBean(String name, Long associationId, String info, String startDate, String endDate, Long activityId, String icon) {
        this.name = name;
        this.associationId = associationId;
        this.info = info;
        this.startDate = startDate;
        this.endDate = endDate;
        this.activityId = activityId;
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAssociationId() {
        return associationId;
    }

    public void setAssociationId(Long associationId) {
        this.associationId = associationId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
}
