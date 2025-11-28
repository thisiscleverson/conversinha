package com.ufrpe.poo.model;

import java.sql.Timestamp;

public class Message {
    public int id;
    public int senderId;
    public  int recipientId;
    public String content;
    public Timestamp createdAt;


    public int getSenderId(){
        return this.senderId;
    }

    public int getRecipientId() {
        return this.recipientId;
    }

    public String getContent(){
        return this.content;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public void setRecipientId(int recipientId) {
        this.recipientId = recipientId;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
