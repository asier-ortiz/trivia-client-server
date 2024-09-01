package com.ortiz.server.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:sqlite:";
    private static final String PATH = "db.sqlite3";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL + PATH);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}