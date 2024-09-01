package com.ortiz.model;

import com.ortiz.server.ServerThread;
import com.ortiz.server.database.DatabaseManager;

public class Game {
    private final static int MAX_ROUNDS = 3;
    private User player1 = null;
    private ServerThread socketPlayer1 = null;
    private int player1Score = 0;
    private User player2 = null;
    private ServerThread socketPlayer2 = null;
    private int player2Score = 0;
    private boolean player1Turn = true;
    private int currentRound = 0;

    public User getPlayer1() {
        return player1;
    }

    public void setPlayer1(User player1) {
        this.player1 = player1;
    }

    public User getPlayer2() {
        return player2;
    }

    public void setPlayer2(User player2) {
        this.player2 = player2;
    }

    public ServerThread getSocketPlayer1() {
        return socketPlayer1;
    }

    public void setSocketPlayer1(ServerThread socketPlayer1) {
        this.socketPlayer1 = socketPlayer1;
    }

    public ServerThread getSocketPlayer2() {
        return socketPlayer2;
    }

    public void setSocketPlayer2(ServerThread socketPlayer2) {
        this.socketPlayer2 = socketPlayer2;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public void setPlayer1Score(int player1Score) {
        this.player1Score = player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public void setPlayer2Score(int player2Score) {
        this.player2Score = player2Score;
    }

    public void startGame() {
        player1Turn = true;
        player1Score = 0;
        player2Score = 0;
        Question question = DatabaseManager.getInstance().getQuestionDAO().getRandom();
        this.socketPlayer1.sendQuestion(question);
    }

    public void sendGreetingMessage(int player) {
        if (player == 1) {
            this.socketPlayer1.sendMessage("Partida creada, eres el jugador 1. Esperando oponente", 1);
        } else if (player == 2) {
            this.socketPlayer2.sendMessage("Te has unido a una partida, eres el jugador 2. Turno jugador 1", 1);
        }
    }

    public void sendNicks() {
        this.socketPlayer1.sendMessage(player2.getNick(), 2);
        this.socketPlayer2.sendMessage(player1.getNick(), 2);
    }

    public void sendScores() {
        this.socketPlayer1.sendMessage("Tu puntuación: " + player1Score + " | " + "Puntuación oponente: " + player2Score, 3);
        this.socketPlayer2.sendMessage("Tu puntuación: " + player2Score + " | " + "Puntuación oponente: " + player1Score, 3);
    }

    public void cancelGame() {
        if (socketPlayer1.isSocketOpenned()) {
            socketPlayer1.cancelGame();
        }
        if (socketPlayer2.isSocketOpenned()) {
            socketPlayer2.cancelGame();
        }
    }

    public void nextTurn(int points) {
        if (player1Turn) {
            setPlayer1Score(getPlayer1Score() + points);
        } else {
            setPlayer2Score(getPlayer2Score() + points);
            currentRound++;
        }
        if (currentRound < MAX_ROUNDS) {
            player1Turn = !player1Turn;
            Question question = DatabaseManager.getInstance().getQuestionDAO().getRandom();
            if (player1Turn) {
                this.socketPlayer1.sendQuestion(question);
            } else {
                this.socketPlayer2.sendQuestion(question);
            }
        } else {
            this.socketPlayer1.endGame();
            this.socketPlayer2.endGame();
            sendScores();
        }
    }
}