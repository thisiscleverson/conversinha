package com.ufrpe.poo.Repository;

import com.ufrpe.poo.Database.Database;
import com.ufrpe.poo.model.Follower;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FollowerRepository {
    private Connection connection;

    public FollowerRepository() throws SQLException {
        this.connection = new Database().getConnection();
    }


    public boolean followUser(int userId, int followerId) {
        if (userId == followerId) {
            System.err.println("Um usuário não pode seguir a si mesmo");
            return false;
        }

        String sql = "INSERT INTO follower (userId, followerId) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, followerId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao seguir usuário: " + e.getMessage());
            return false;
        }
    }


    public boolean unfollowUser(int userId, int followerId) {
        String sql = "DELETE FROM follower WHERE userId = ? AND followerId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, followerId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao deixar de seguir: " + e.getMessage());
            return false;
        }
    }


    public boolean isFollowing(int userId, int followerId) {
        String sql = "SELECT COUNT(*) FROM follower WHERE userId = ? AND followerId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, followerId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Erro ao verificar follow: " + e.getMessage());
        }

        return false;
    }


    public List<Follower> getFollowers(int userId) {
        List<Follower> followers = new ArrayList<>();
        String sql = """
            SELECT f.*, u.username as follower_username 
            FROM follower f 
            JOIN user u ON f.followerId = u.id 
            WHERE f.userId = ? 
            ORDER BY f.created_at DESC
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                followers.add(mapResultSetToFollower(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar seguidores: " + e.getMessage());
        }

        return followers;
    }


    public List<Follower> getFollowing(int followerId) {
        List<Follower> following = new ArrayList<>();
        String sql = """
            SELECT f.*, u.username as user_username 
            FROM follower f 
            JOIN user u ON f.userId = u.id 
            WHERE f.followerId = ? 
            ORDER BY f.created_at DESC
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, followerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                following.add(mapResultSetToFollower(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar seguindo: " + e.getMessage());
        }

        return following;
    }


    public int countFollowers(int userId) {
        String sql = "SELECT COUNT(*) FROM follower WHERE userId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao contar seguidores: " + e.getMessage());
        }

        return 0;
    }


    public int countFollowing(int followerId) {
        String sql = "SELECT COUNT(*) FROM follower WHERE followerId = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, followerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao contar seguindo: " + e.getMessage());
        }

        return 0;
    }

    private Follower mapResultSetToFollower(ResultSet rs) throws SQLException {
        Follower follower = new Follower();
        follower.setId(rs.getInt("id"));
        follower.setUserId(rs.getInt("userId"));
        follower.setFollowerId(rs.getInt("followerId"));
        follower.setCreatedAt(rs.getTimestamp("created_at"));

        // Campos adicionais dos JOINs
        try {
            follower.setFollowerUsername(rs.getString("follower_username"));
        } catch (SQLException e) {
            // Campo não existe neste ResultSet
        }

        try {
            follower.setUserUsername(rs.getString("user_username"));
        } catch (SQLException e) {
            // Campo não existe neste ResultSet
        }

        return follower;
    }
}
