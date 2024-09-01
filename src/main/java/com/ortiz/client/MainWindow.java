package com.ortiz.client;

import com.ortiz.enums.RequestType;
import com.ortiz.model.Request;
import org.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.*;
import java.util.Arrays;

public class MainWindow extends JFrame {
    private static MainWindow mainWindowInstance;
    private JButton playButton;
    private JButton exitButton;
    private JPanel mainWindow;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private static SecretKey secretKey;
    private static Cipher cipher;
    private String rules;

    public MainWindow() {
        super("Game Client");
        mainWindowInstance = this;
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setListeners();
        playButton.setText("Jugar");
        exitButton.setText("Salir");
    }

    public synchronized static MainWindow getInstance() {
        return mainWindowInstance;
    }

    public JPanel getMainWindow() {
        if (getGameRules()) {
            JOptionPane.showMessageDialog(this, rules);
        } else {
            returnToLoginWindow();
        }
        return mainWindow;
    }

    private void setListeners() {
        playButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            Point point = this.getLocation();
            setVisible(false);
            Game game = new Game(Login.getUserID(), Login.getUserNick(), secretKey, cipher);
            game.setContentPane(game.getGameWindow());
            game.setLocation(point);
            game.setResizable(false);
            game.setVisible(true);
        }));

        exitButton.addActionListener(e -> returnToLoginWindow());
    }

    private boolean getGameRules() {
        try {
            socket = new Socket("localhost", 4444);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            Request request = new Request(RequestType.GAME_RULES);
            out.writeObject(request);
            out.flush();
            Object object = in.readObject();
            if (object instanceof Request) {
                Request answer = (Request) object;
                JSONObject jsonObject = answer.getJson();
                if (jsonObject != null) {
                    String error = (String) jsonObject.get("error");
                    JOptionPane.showMessageDialog(this, error, "", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                PublicKey publicKey = (PublicKey) object;
                rules = in.readObject().toString();
                Signature rsa = Signature.getInstance("SHA1WITHRSA");
                rsa.initVerify(publicKey);
                rsa.update(rules.getBytes());
                byte[] signatue = (byte[]) in.readObject();
                secretKey = (SecretKey) in.readObject();
                cipher = Cipher.getInstance("AES");
                return rsa.verify(signatue);
            }
        } catch (IOException | NoSuchAlgorithmException | ClassNotFoundException | InvalidKeyException | SignatureException | NoSuchPaddingException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error del servidor, imposible recuperar las reglas de juego", "", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                socket.close();
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void returnToLoginWindow() {
        SwingUtilities.invokeLater(() -> {
            try {
                socket = new Socket("localhost", 4444);
                out = new ObjectOutputStream(socket.getOutputStream());
                Request request = new Request(RequestType.EXIT);
                out.writeObject(request);
                out.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    socket.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Point point = this.getLocation();
            Arrays.asList(Window.getWindows()).forEach(Window::dispose);
            Login login = new Login();
            login.setContentPane(login.getLoginWindow());
            login.pack();
            login.setLocation(point);
            login.setResizable(false);
            login.setVisible(true);
        });
    }
}