package com.ortiz.server.database;

import com.ortiz.model.Answer;
import com.ortiz.model.Question;
import com.ortiz.server.util.DBConnection;

import java.sql.*;

public class QuestionDAO {

    public void createTable() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                     CREATE TABLE IF NOT EXISTS QUESTION
                      (
                      ID                INTEGER  PRIMARY KEY AUTOINCREMENT ,
                      QUESTION          VARCHAR2 NOT NULL ,
                      CATEGORY          VARCHAR2 NOT NULL 
                     )
                     """)
        ) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Integer save(final Question question) {
        Integer id = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO QUESTION (QUESTION, CATEGORY) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, question.getQuestion());
            ps.setString(2, question.getCategory());
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

    public Question getRandom() {
        Question question = new Question();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT ID, QUESTION, CATEGORY FROM QUESTION ORDER BY RANDOM() LIMIT 1");
             PreparedStatement ps2 = conn.prepareStatement("SELECT ANSWER, CORRECT FROM ANSWER JOIN QUESTION ON ANSWER.QUESTION_ID = QUESTION.ID WHERE QUESTION.ID =? ORDER BY RANDOM()")
        ) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                question.setId(rs.getInt(1));
                question.setQuestion(rs.getString(2));
                question.setCategory(rs.getString(3));
            }
            ps2.setInt(1, question.getId());
            rs = ps2.executeQuery();
            while (rs.next()) question.getAnswers().add(new Answer(rs.getString(1), rs.getBoolean(2)));
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return question;
    }
}