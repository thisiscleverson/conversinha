package com.ufrpe.poo.services;

import com.ufrpe.poo.Interfaces.FollowerInterface;
import com.ufrpe.poo.Model.User;
import com.ufrpe.poo.Repository.FollowerRepository;
import com.ufrpe.poo.Repository.UserRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class FollowerService implements FollowerInterface {

    private FollowerRepository followerRepository;
    private UserRepository userRepository;

    public FollowerService(Connection connection) throws SQLException {
        this.followerRepository = new FollowerRepository(connection);
        this.userRepository = new UserRepository(connection);
    }

    @Override
    public boolean followUser(String user, String follower) {
        user = user.toLowerCase().toLowerCase().trim();
        follower = follower.toLowerCase().trim();

        if(userRepository.findByUsername(user).isEmpty()){
            return false;
        }

        if(userRepository.findByUsername(follower).isEmpty()){
            return false;
        }

        return followerRepository.followUser(user, follower);
    }

    @Override
    public boolean unfollowUser(String user, String follower) {
        user = user.toLowerCase().toLowerCase().trim();
        follower = follower.toLowerCase().trim();

        if(userRepository.findByUsername(user).isEmpty()){
            return false;
        }

        if(userRepository.findByUsername(follower).isEmpty()){
            return false;
        }

        return followerRepository.unfollowUser(user, follower);
    }

    @Override
    public List<String> getFollowers(String username) {

        username = username.toLowerCase().trim();

        List<String> followers = followerRepository.getFollowers(username);

        if(followers == null){
            System.out.println("Follower nao encontrado");
            return null;
        }

        return followers;
    }
}
