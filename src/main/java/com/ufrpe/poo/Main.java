package com.ufrpe.poo;

import com.sun.net.httpserver.HttpServer;
import com.ufrpe.poo.Api.handlers.HtmlHandler;
import com.ufrpe.poo.Api.handlers.MessageHandler;
import com.ufrpe.poo.Api.handlers.UserHandler;
import com.ufrpe.poo.Api.handlers.FollowerHandler;
import com.ufrpe.poo.Database.Database;
import com.ufrpe.poo.Model.Message;
import com.ufrpe.poo.services.FollowerService;
import com.ufrpe.poo.services.MessageService;
import com.ufrpe.poo.services.UserService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.SQLException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        int port = 3030;

        Connection connection = new Database().getConnection();

        UserService userService = new UserService(connection);
        FollowerService followerService = new FollowerService(connection);
        MessageService messageService = new MessageService(connection);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/", new HtmlHandler());
        server.createContext("/api/users", new UserHandler(userService));
        server.createContext("/api/followers", new FollowerHandler(followerService));
        server.createContext("/api/messages", new MessageHandler(messageService));

        server.setExecutor(null);
        server.start();


        System.out.println("Servidor iniciado!");
        System.out.printf("Host: http://localhost:%d\n", port);
    }
}