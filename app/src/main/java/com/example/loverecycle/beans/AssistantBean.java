package com.example.loverecycle.beans;

public class AssistantBean {

    private Long id;
    private Long accountId;
    private Long associationId;

    public AssistantBean(Long id, Long accountId, Long associationId) {
        this.id = id;
        this.accountId = accountId;
        this.associationId = associationId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Long getAssociationId() {
        return associationId;
    }

    public void setAssociationId(Long associationId) {
        this.associationId = associationId;
    }
}
