package com.ufrpe.poo.Model;

import java.sql.Timestamp;

public class Message {
    public int id;
    public String sender;
    public  String recipient;
    public String title;
    public String content;
    public boolean isDelivered;
    public Timestamp createdAt;

    public Message(String title, String sender, String recipient, String content) {
        this.title = title;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
    }

    public Message(
            int id,
            String sender,
            String recipient,
            String title,
            String content,
            boolean isDelivered,
            Timestamp createdAt
    ) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.title = title;
        this.content = content;
        this.isDelivered = isDelivered;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getSender(){
        return this.sender;
    }

    public String getRecipient() {
        return this.recipient;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent(){
        return this.content;
    }

    public boolean isDelivered(){
        return this.isDelivered;
    }

    public Timestamp getCreatedAt() {
        return this.createdAt;
    }
}
