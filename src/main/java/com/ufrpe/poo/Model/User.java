package com.ufrpe.poo.Model;

import java.sql.Timestamp;
import java.util.Date;


public class User {
    public String username;
    public String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }


    public String getUsername() {
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }
}

