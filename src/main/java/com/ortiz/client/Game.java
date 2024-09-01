package com.ortiz.client;

import com.ortiz.enums.RequestType;
import com.ortiz.model.Answer;
import com.ortiz.model.Request;
import org.apache.commons.lang3.SerializationUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

public class Game extends JFrame implements Runnable {
    private JPanel gameWindow;
    private JLabel infoLabel;
    private JLabel questionLabel;
    private JButton answer1Button;
    private JButton answer2Button;
    private JButton answer3Button;
    private JButton answer4Button;
    private JPanel questionsPanel;
    private JLabel categoryLabel;
    private JLabel timerLabel;
    private JLabel opponentsLabel;
    private JLabel roundLabel;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private JSONObject jsonObject;
    private final int userId;
    private final String userNick;
    private Timer timer;
    private int countDown;
    private int round = 1;
    private ArrayList<Answer> answers = new ArrayList<>();
    private final SecretKey secretKey;
    private final Cipher cipher;

    public Game(int userId, String userNick, SecretKey secretKey, Cipher cipher) {
        super("Game Client");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(600, 125);
        setListeners();
        this.userId = userId;
        this.userNick = userNick;
        this.secretKey = secretKey;
        this.cipher = cipher;
        opponentsLabel.setText(userNick + "   VS   " + "???");
        questionsPanel.setVisible(false);
        roundLabel.setText("Ronda 1 de 3");
        new Thread(this).start();
    }

    public JPanel getGameWindow() {
        return gameWindow;
    }

    private void setListeners() {
        answer1Button.addActionListener(e -> sendAnswer(checkCorrectAnswer(0)));
        answer2Button.addActionListener(e -> sendAnswer(checkCorrectAnswer(1)));
        answer3Button.addActionListener(e -> sendAnswer(checkCorrectAnswer(2)));
        answer4Button.addActionListener(e -> sendAnswer(checkCorrectAnswer(3)));
    }

    private void showQuestionsPanel() {
        questionsPanel.setVisible(true);
        setSize(600, 600);
        infoLabel.setText("Tu turno");
    }

    private void hideQuestionsPanel() {
        questionsPanel.setVisible(false);
        setSize(600, 125);
        infoLabel.setText("Turno oponente");
    }

    private void sendEncryptedRequest(Request request) throws BadPaddingException, IllegalBlockSizeException, IOException, InvalidKeyException {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] data = SerializationUtils.serialize(request);
        byte[] encryptedRequest = cipher.doFinal(data);
        String encrptedString = Base64.getEncoder().encodeToString(encryptedRequest);
        out.writeObject(encrptedString);
        out.flush();
    }

    private Request getDecryptedRequest(byte[] encryptedRequest) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] data = cipher.doFinal(encryptedRequest);
        return SerializationUtils.deserialize(data);
    }

    private void sendAnswer(boolean isCorrect) {
        hideQuestionsPanel();
        round++;
        roundLabel.setText("Ronda " + round + " de 3");
        timerLabel.setText(". . .");
        try {
            jsonObject = new JSONObject();
            jsonObject.put("correct_answer", isCorrect);
            Request request = new Request(RequestType.ANSWER, jsonObject.toString());
            sendEncryptedRequest(request);
        } catch (IOException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException ex) {
            JOptionPane.showMessageDialog(this, "Error de conexión con el servidor", "", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            close(1);
        } finally {
            timer.stop();
        }
    }

    private boolean checkCorrectAnswer(int index) {
        timer.stop();
        if (answers.get(index).isCorrect()) {
            JOptionPane.showMessageDialog(this, "¡Respuesta correcta!", "", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } else {
            JOptionPane.showMessageDialog(this, "¡Has fallado!", "", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
    }

    private void startCountDown() {
        countDown = 15;
        timerLabel.setForeground(Color.BLACK);
        timer = new Timer(1000, e -> {
            countDown--;
            if (countDown >= 0) {
                timerLabel.setText(Integer.toString(countDown));
            } else {
                ((Timer) (e.getSource())).stop();
                timerLabel.setText(". . .");
                sendAnswer(false);
            }
            if (countDown == 5) timerLabel.setForeground(Color.RED);
        });
        timer.setInitialDelay(500);
        timer.start();
    }

    private void close(int option) {
        if (option == 1) {
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            SwingUtilities.invokeLater(() -> {
                Point point = this.getLocation();
                Arrays.asList(Window.getWindows()).forEach(Window::dispose);
                Login login = new Login();
                login.setContentPane(login.getLoginWindow());
                login.pack();
                login.setLocation(point);
                login.setResizable(false);
                login.setVisible(true);
            });
        } else if (option == 2) {
            SwingUtilities.invokeLater(() -> {
                Point point = this.getLocation();
                Arrays.asList(Window.getWindows()).forEach(Window::dispose);
                MainWindow.getInstance().setLocation(point);
                MainWindow.getInstance().setVisible(true);
            });
        }
    }

    @Override
    public void run() {
        try {
            socket = new Socket("localhost", 4444);
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            jsonObject = new JSONObject();
            jsonObject.put("userId", userId);
            Request request = new Request(RequestType.PLAY, jsonObject.toString());
            sendEncryptedRequest(request);
            boolean isOpenned = true;
            while (isOpenned) {
                if (!socket.isClosed()) {
                    Object object = in.readObject();
                    if (object instanceof String) {
                        String encrptedString = (String) object;
                        byte[] encryptedRequest = Base64.getDecoder().decode(encrptedString);
                        request = getDecryptedRequest(encryptedRequest);
                        jsonObject = request.getJson();
                        if (jsonObject != null) {
                            if (jsonObject.has("result")) {
                                if (jsonObject.has("greeting")) {
                                    infoLabel.setText((String) jsonObject.get("greeting"));
                                }
                                if (jsonObject.has("opponentNick")) {
                                    String opponentNick = (String) jsonObject.get("opponentNick");
                                    opponentsLabel.setText(userNick + "   VS   " + opponentNick);
                                }
                                if (jsonObject.has("question")) {
                                    showQuestionsPanel();
                                    categoryLabel.setText((String) jsonObject.get("category"));
                                    questionLabel.setText("\"" + jsonObject.get("question") + "\"");
                                    JSONArray jsonArray = jsonObject.getJSONArray("answers");
                                    answers = new ArrayList<>();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        answers.add(new Answer(jsonObject.getString("answer"), jsonObject.getBoolean("correct")));
                                        switch (i) {
                                            case 0 -> answer1Button.setText(answers.get(i).getAnswer());
                                            case 1 -> answer2Button.setText(answers.get(i).getAnswer());
                                            case 2 -> answer3Button.setText(answers.get(i).getAnswer());
                                            case 3 -> answer4Button.setText(answers.get(i).getAnswer());
                                        }
                                    }
                                    startCountDown();
                                }
                                if (jsonObject.has("scores")) {
                                    String scores = (String) jsonObject.get("scores");
                                    JOptionPane.showMessageDialog(this, scores);
                                    isOpenned = false;
                                    close(2);
                                }
                            } else {
                                String error = (String) jsonObject.get("error");
                                JOptionPane.showMessageDialog(this, error);
                                isOpenned = false;
                                close(2);
                            }
                        }
                    } else {
                        throw new ClassNotFoundException();
                    }
                } else {
                    isOpenned = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            close(1);
        }
    }
}