package com.ufrpe.poo.Interfaces;

import com.ufrpe.poo.Model.User;

import java.util.List;


public interface UserInterface {
    User getAccessUser(String username, String password);
    User createUser(String name, String password);
    List<User> searchUsers(String username);
}
