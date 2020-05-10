package com.example.loverecycle.beans;

import java.io.Serializable;

/**
 * 赠送物品
 */
public class OrderBean implements Serializable {
    private Long orderId;
    private String category;
    private String info;
    private String picture;
    private String state;
    private String repay;
    private String date;
    private String palce;
    private Long accountId;
    private Long activityId;

    public OrderBean(Long id, String category, String info, String picture, String state, String repay, String date, String palce, Long accountId, Long activityId) {
        this.orderId = id;
        this.category = category;
        this.info = info;
        this.picture = picture;
        this.state = state;
        this.repay = repay;
        this.date = date;
        this.palce = palce;
        this.accountId = accountId;
        this.activityId = activityId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long id) {
        this.orderId = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRepay() {
        return repay;
    }

    public void setRepay(String repay) {
        this.repay = repay;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPalce() {
        return palce;
    }

    public void setPalce(String palce) {
        this.palce = palce;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
}
