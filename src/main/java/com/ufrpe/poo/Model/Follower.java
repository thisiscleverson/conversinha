package com.ufrpe.poo.Model;

import java.sql.Timestamp;

public class Follower {
    public int id;
    public int userId;
    public int followerId;
    public Timestamp createdAt;
    public String userUsername;
    public String followerUsername;

    public void setId(int id) {
        this.id = id;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setFollowerId(int followerId) {
        this.followerId = followerId;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setFollowerUsername(String followerUsername) {
        this.followerUsername = followerUsername;
    }

    public void setUserUsername(String userUsername) {
        this.userUsername = userUsername;
    }
}
