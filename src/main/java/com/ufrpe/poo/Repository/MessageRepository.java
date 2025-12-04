package com.ufrpe.poo.Repository;

import com.ufrpe.poo.Database.Database;
import com.ufrpe.poo.Model.Message;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageRepository {
    private Connection connection;

    public MessageRepository(Connection connection) {
        this.connection = connection;
    }

    public int registerMessage(Message message) {
        String sql = "INSERT INTO messages (sender, recipient, title, content) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, message.getSender());
            stmt.setString(2, message.getRecipient());
            stmt.setString(3, message.getTitle());
            stmt.setString(4, message.getContent());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return -1; // Falha

        } catch (SQLException e) {
            System.err.println("Erro ao enviar mensagem: " + e.getMessage());
            return -1;
        }
    }

    public Optional<Message> getMessageById(int messageId) {
        String sql = """
            SELECT *
            FROM messages
            WHERE id = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToMessage(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar mensagem: " + e.getMessage());
        }

        return Optional.empty();
    }

    public List<Message> getReceivedMessages(String recipient) {
        return getMessagesByRecipient(recipient);
    }

    public List<Message> getSentMessages(String sender) {
        return getMessagesBySender(sender);
    }

    public boolean updateDelivereStatus(int messageId){
        String sql = """
            UPDATE messages
            SET is_delivered = true
            WHERE id = ? and is_delivered = false;
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, messageId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Não foi possivel atualizar o status dessa conversa: " + e.getMessage());
            return false;
        }
    }


    private List<Message> getMessagesByRecipient(String recipient) {
        List<Message> messages = new ArrayList<>();
        String sql = """
            SELECT *
            FROM messages m
            WHERE m.recipient = ? AND m.is_delivered = false
            ORDER BY m.created_at DESC
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, recipient);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                messages.add(mapResultSetToMessage(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar mensagens recebidas: " + e.getMessage());
        }

        return messages;
    }

    private List<Message> getMessagesBySender(String sender) {
        List<Message> messages = new ArrayList<>();
        String sql = """
            SELECT *
            FROM messages m
            WHERE m.sender = ?
            ORDER BY m.created_at DESC
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, sender);
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
        Message message = new Message(
                rs.getInt("id"),
                rs.getString("sender"),
                rs.getString("recipient"),
                rs.getString("title"),
                rs.getString("content"),
                rs.getBoolean("is_delivered"),
                rs.getTimestamp("created_at")
        );


        return message;
    }

}
