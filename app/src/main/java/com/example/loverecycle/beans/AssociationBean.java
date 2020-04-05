package com.example.loverecycle.beans;

public class AssociationBean {

    private Long associationId;
    private String name;
    private String info;
    private Long accountId;
    private String icon;

    public AssociationBean(Long associationId, String name, String info, Long accountId, String icon) {
        this.associationId = associationId;
        this.name = name;
        this.info = info;
        this.accountId = accountId;
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Long getAssociationId() {
        return associationId;
    }

    public void setAssociationId(Long associationId) {
        this.associationId = associationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
