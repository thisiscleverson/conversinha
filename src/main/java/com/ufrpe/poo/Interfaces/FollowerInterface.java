package com.ufrpe.poo.Interfaces;

import com.ufrpe.poo.Model.User;

import java.util.List;

public interface FollowerInterface {

    boolean followUser(String user, String follower);
    boolean unfollowUser(String user, String follower);
    List<String> getFollowers(String username);
}
