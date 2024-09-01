package com.ortiz.server;

import com.ortiz.model.Game;
import com.ortiz.model.User;
import com.ortiz.server.database.DatabaseManager;

import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Objects;

public class Server implements Runnable {
    private static Server server;
    private static final int SERVERPORT = 4444;
    private ServerSocket serverSocket = null;
    private final ArrayList<Game> games = new ArrayList<>();
    private final ArrayList<Socket> clients = new ArrayList<>();
    private SecretKey secretKey = null;

    private Server() {
        new Thread(this).start();
    }

    private synchronized static void createInstence() {
        if (server == null) server = new Server();
    }

    public synchronized static Server getInstance() {
        createInstence();
        return server;
    }

    private void startSever() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            secretKey = keyGenerator.generateKey();
            this.serverSocket = new ServerSocket(SERVERPORT);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void removeClient(Socket client) {
        clients.remove(client);
    }

    public void addGame(Game game) {
        games.add(game);
    }

    public void removeGame(Game game) {
        games.remove(game);
    }

    public void cancelGame(Game game) {
        if (games.contains(game)) {
            game.cancelGame();
            games.remove(game);
        }
    }

    public Game getPlayableGame(User user) {
        Game playableGame = null;
        if (games.size() == 0) {
            playableGame = new Game();
            games.add(playableGame);
        } else {
            for (Game game : games) {
                if ((game.getPlayer1() == null || game.getPlayer2() == null)
                        && (!Objects.equals(game.getPlayer1(), user) && !Objects.equals(game.getPlayer1(), user))) {
                    playableGame = game;
                }
            }
            if (playableGame == null) {
                playableGame = new Game();
                addGame(playableGame);
            }
        }
        return playableGame;
    }

    @Override
    public void run() {
        startSever();
        while (true) {
            Socket clientSocket;
            try {
                clientSocket = this.serverSocket.accept();
                clients.add(clientSocket);
                new ServerThread(clientSocket, secretKey).start();
            } catch (IOException | NoSuchPaddingException | NoSuchAlgorithmException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) {
        if (!new File("db.sqlite3").exists()) {
            DatabaseManager.getInstance().createTables();
            DatabaseManager.getInstance().createData();
        }
        getInstance();
    }
}