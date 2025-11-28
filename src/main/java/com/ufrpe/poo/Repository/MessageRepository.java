package com.ufrpe.poo.Repository;

import com.ufrpe.poo.Database.Database;
import com.ufrpe.poo.model.Message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageRepository {
    private Connection connection;

    MessageRepository(){
        this.connection = new Database().getConnection();
    }

    public boolean sendMessage(Message message) {
        String sql = "INSERT INTO message (senderId, recipientId, content) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, message.getSenderId());
            stmt.setInt(2, message.getRecipientId());
            stmt.setString(3, message.getContent());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao enviar mensagem: " + e.getMessage());
            return false;
        }
    }

    public List<Message> getConversation(int user1Id, int user2Id) {
        List<Message> messages = new ArrayList<>();
        String sql = """
            SELECT m.*, u1.username as sender_name, u2.username as recipient_name 
            FROM message m 
            JOIN user u1 ON m.senderId = u1.id 
            JOIN user u2 ON m.recipientId = u2.id 
            WHERE (m.senderId = ? AND m.recipientId = ?) 
               OR (m.senderId = ? AND m.recipientId = ?) 
            ORDER BY m.created_at
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, user1Id);
            stmt.setInt(2, user2Id);
            stmt.setInt(3, user2Id);
            stmt.setInt(4, user1Id);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                messages.add(mapResultSetToMessage(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar conversa: " + e.getMessage());
        }

        return messages;
    }

    public List<Message> getReceivedMessages(int userId) {
        return getMessagesByRecipient(userId);
    }

    public List<Message> getSentMessages(int userId) {
        return getMessagesBySender(userId);
    }

    private List<Message> getMessagesByRecipient(int recipientId) {
        List<Message> messages = new ArrayList<>();
        String sql = """
            SELECT m.*, u.username as sender_name 
            FROM message m 
            JOIN user u ON m.senderId = u.id 
            WHERE m.recipientId = ? 
            ORDER BY m.created_at DESC
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, recipientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                messages.add(mapResultSetToMessage(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar mensagens recebidas: " + e.getMessage());
        }

        return messages;
    }

    private List<Message> getMessagesBySender(int senderId) {
        List<Message> messages = new ArrayList<>();
        String sql = """
            SELECT m.*, u.username as recipient_name 
            FROM message m 
            JOIN user u ON m.recipientId = u.id 
            WHERE m.senderId = ? 
            ORDER BY m.created_at DESC
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, senderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                messages.add(mapResultSetToMessage(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar mensagens enviadas: " + e.getMessage());
        }

        return messages;
    }

    private Message mapResultSetToMessage(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setId(rs.getInt("id"));
        message.setSenderId(rs.getInt("senderId"));
        message.setRecipientId(rs.getInt("recipientId"));
        message.setContent(rs.getString("content"));
        message.setCreatedAt(rs.getTimestamp("created_at"));

        /*
        // Campos adicionais dos JOINs
        try {
            message.setSenderName(rs.getString("sender_name"));
        } catch (SQLException e) {
            // Campo não existe neste ResultSet
        }

        try {
            message.setRecipientName(rs.getString("recipient_name"));
        } catch (SQLException e) {
            // Campo não existe neste ResultSet
        }
         */

        return message;
    }

}
