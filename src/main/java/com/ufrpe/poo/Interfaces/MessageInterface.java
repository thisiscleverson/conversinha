package com.ufrpe.poo.Interfaces;

import com.ufrpe.poo.Model.Message;

import java.util.List;

public interface MessageInterface {
    int registerMessage(String title, String sender, String recipient, String content);
    List<Message> getMessagesNotRead(String recipient);
    List<Message> getSentMessages(String sender);
    Message getMessageById(int messageId);
    boolean updatedelivereStatus(int messageId);
}
