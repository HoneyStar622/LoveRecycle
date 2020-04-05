package com.example.loverecycle.beans;

/**
 * 用户
 */
public class UserBean {
    private Long accountId;
    private String icon;
    private String studentId;
    private String info;
    private String realm;
    private String username;
    private String email;
    private Boolean emailVerified;

    public UserBean(Long accountId, String icon, String studentId, String info, String realm, String username, String email, Boolean emailVerified) {
        this.accountId = accountId;
        this.icon = icon;
        this.studentId = studentId;
        this.info = info;
        this.realm = realm;
        this.username = username;
        this.email = email;
        this.emailVerified = emailVerified;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
}
