package com.ufrpe.poo.model;

import java.sql.Timestamp;
import java.util.Date;


public class User {
    public int id;
    public String username;
    public String password;
    public Date createdAt;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(int id, String username, String password, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.createdAt = createdAt;
    }

    public int getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }

    public Date getCreatedAt(){
        return this.createdAt;
    }
}

