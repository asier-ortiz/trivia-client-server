package com.ortiz.server.database;

import com.ortiz.model.Answer;
import com.ortiz.server.util.DBConnection;

import java.sql.*;

public class AnswerDAO {

    public void createTable() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS ANSWER
                      (
                      ID                INTEGER  PRIMARY KEY AUTOINCREMENT ,
                      ANSWER            VARCHAR2 NOT NULL ,
                      CORRECT           NUMBER   NOT NULL ,
                      QUESTION_ID       INTEGER , 
                      CONSTRAINT        ANS_QUE_ID FOREIGN KEY (QUESTION_ID) REFERENCES QUESTION (ID) ON DELETE CASCADE 
                     )
                     """)
        ) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Integer save(final Answer answer, final Integer questionId) {
        Integer id = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO ANSWER (ANSWER, CORRECT, QUESTION_ID) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, answer.getAnswer());
            ps.setBoolean(2, answer.isCorrect());
            ps.setInt(3, questionId);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                id = Math.toIntExact(rs.getLong(1));
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }
}