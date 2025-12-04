CREATE DATABASE IF NOT EXISTS conversinha;
USE conversinha;


CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(50) NOT NULL UNIQUE primary key,
    password VARCHAR(255) NOT NULL,
);


CREATE TABLE IF NOT EXISTS messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    sender VARCHAR(50) NOT NULL,
    recipient VARCHAR(50) NOT NULL,
    title VARCHAR(500) NOT NULL
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender) REFERENCES users(username) ON DELETE CASCADE,
    FOREIGN KEY (recipient) REFERENCES users(username) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS follower (
    id INT AUTO_INCREMENT PRIMARY KEY,
    "user" VARCHAR(50) NOT NULL,
    follower VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY ("user") REFERENCES users(username) ON DELETE CASCADE,
    FOREIGN KEY (follower) REFERENCES users(username) ON DELETE CASCADE,
    -- Evitar duplicatas de seguidores
    UNIQUE KEY unique_follow ("user", follower),
);


ALTER TABLE follower ADD CONSTRAINT check_self_follow
    CHECK ("user" != "follower");