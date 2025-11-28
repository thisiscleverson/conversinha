package com.ufrpe.poo.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private Connection conn;
    private static Database instance;

    private String url = "jdbc:postgresql://localhost:5432/conversinhadb";
    private String user = "conversinha";
    private String password = "123";

    public Database() {
        try {
            this.conn = DriverManager.getConnection(this.url, this.user, this.password);
            System.out.println("Successful database connection!");

        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Connection getConnection() {
        return this.conn;
    }

    public void closeConnection() {
        try {
            if (this.conn != null && !this.conn.isClosed()) {
                this.conn.close();
                System.out.println("Connection closed!");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}