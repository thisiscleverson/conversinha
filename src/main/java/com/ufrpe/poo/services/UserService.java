package com.ufrpe.poo.services;


import com.ufrpe.poo.Repository.FollowerRepository;
import com.ufrpe.poo.Interfaces.UserInterface;
import com.ufrpe.poo.Repository.UserRepository;
import com.ufrpe.poo.Model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserService implements UserInterface {

    private UserRepository userRepository;

    public UserService(Connection connection) throws SQLException {
        userRepository = new UserRepository(connection);
    }

    // TODO: Criar exceptions para substituir os println
    @Override
    public User createUser(String username, String password) {

        if(this.checkUsernameLength(username)){
            System.out.println("Username deve ter entre 5 caracteres!");
            return null;
        }

        if(userRepository.findByUsername(username).isPresent()){
            System.out.println("Usuario ja existente!");
            return null;
        }

        username =  username.trim().toLowerCase();

        User newUser = new User(username, password);

        boolean created = userRepository.createUser(newUser);

        if(!created){
            System.out.println("Nao foi possivel criar usuario");
            return null;
        }

        Optional<User> user = userRepository.findByUsername(username);

        return user.orElse(null);
    }

    @Override
    public User getAccessUser(String username, String password) {
        if(this.checkUsernameLength(username)){
            System.out.println("Username deve ter entre 5 caracteres!");
            return null;
        }

        username =  username.trim().toLowerCase();
        password =  password.trim().toLowerCase();

        // TODO: Verificar senha

        Optional<User> user = userRepository.findByUsername(username);

        if(user.isEmpty()){
            System.out.println("Usuario nao encontrado!");
            return null;
        }

        return user.orElse(null);
    }

    @Override
    public List<User> searchUsers(String username) {
        if(this.checkUsernameLength(username)){
            System.out.println("Username deve ter entre 3 caracteres!");
            return null;
        }

        username = username.trim().toLowerCase();

        List<User> users = userRepository.findByUsernameLike(username);

        if(users.isEmpty()){
            System.out.println("Nao foi possivel encontrar o usuario!");
            return null;
        }

        return users;
    }

    private boolean checkUsernameLength(String username){
        return username.length() <= 3;
    }
}
