CREATE DATABASE IF NOT EXISTS conversinha;
USE conversinha;


CREATE TABLE IF NOT EXISTS user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS message (
    id INT AUTO_INCREMENT PRIMARY KEY,
    senderId INT NOT NULL,
    recipientId INT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (senderId) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (recipientId) REFERENCES user(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS follower (
    id INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    followerId INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (userId) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (followerId) REFERENCES user(id) ON DELETE CASCADE,
    -- Evitar duplicatas de seguidores
    UNIQUE KEY unique_follow (userId, followerId),
    -- Impedir que um usuário siga a si mesmo
    CHECK (userId != followerId)
);