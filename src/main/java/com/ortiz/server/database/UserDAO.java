package com.ortiz.server.database;

import com.ortiz.model.User;
import com.ortiz.server.util.DBConnection;

import java.sql.*;

public class UserDAO {

    protected void createTable() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS USER
                      (
                      USER_ID         INTEGER  PRIMARY KEY AUTOINCREMENT ,
                      NAME            VARCHAR2 NOT NULL ,
                      SURNAME         VARCHAR2 NOT NULL ,
                      AGE             NUMBER   NOT NULL ,
                      NICK            VARCHAR2 NOT NULL UNIQUE ,
                      PASSWORD        VARCHAR2 NOT NULL
                     )
                     """)
        ) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int save(final User user) {
        int userId = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO USER (NAME, SURNAME, AGE, NICK, PASSWORD) VALUES (?, ?, ?, ?, ?)")
        ) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getSurname());
            ps.setInt(3, user.getAge());
            ps.setString(4, user.getNick());
            ps.setString(5, user.getPassword());
            int updateCount = ps.executeUpdate();
            if (updateCount > 0) {
                PreparedStatement ps2 = conn.prepareStatement("SELECT last_insert_rowid()");
                ResultSet rs = ps2.executeQuery();
                userId = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userId;
    }

    public User login(final String nick, final String password) {
        User user = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM USER WHERE NICK = ? AND PASSWORD = ?")
        ) {
            ps.setString(1, nick);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = new User(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getString(5),
                        rs.getString(6)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public User getUserById(final int userID) {
        User user = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM USER WHERE USER_ID = ?")
        ) {
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = new User(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getString(5),
                        rs.getString(6)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
}