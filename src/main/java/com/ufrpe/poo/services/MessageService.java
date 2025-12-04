package com.ufrpe.poo.services;

import com.ufrpe.poo.Interfaces.MessageInterface;
import com.ufrpe.poo.Model.Message;
import com.ufrpe.poo.Repository.MessageRepository;
import com.ufrpe.poo.Repository.UserRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageService implements MessageInterface {
    private UserRepository userRepository;
    private MessageRepository messageRepository;

    public MessageService(Connection connection) throws SQLException {
        this.userRepository = new UserRepository(connection);
        this.messageRepository = new MessageRepository(connection);
    }


    public int registerMessage(String title, String sender, String recipient, String content) {
        if(title==null || title.length()==0) return -1;

        if(userRepository.findByUsername(sender) == null){
            return -1;
        }

        if(userRepository.findByUsername(recipient) == null){
            return -1;
        }

        if(content.isEmpty()){
            return -1;
        }

        Message message = new Message(title, sender, recipient, content);

        return messageRepository.registerMessage(message);

    }

    public List<Message> getMessagesNotRead(String recipient) {
        recipient = recipient.toLowerCase().trim();

        if(recipient.isEmpty()){
            return new ArrayList<>();
        }

        return messageRepository.getReceivedMessages(recipient);
    }

    public List<Message> getSentMessages(String sender) {
        sender = sender.toLowerCase().trim();

        if(sender.isEmpty()){
            return new ArrayList<>();
        }

        return messageRepository.getSentMessages(sender);
    }

    public Message getMessageById(int messageId) {
        Optional<Message> message =  messageRepository.getMessageById(messageId);
        return message.orElse(null);
    }

    public boolean updatedelivereStatus(int messageId) {
        return messageRepository.updateDelivereStatus(messageId);
    }
}
