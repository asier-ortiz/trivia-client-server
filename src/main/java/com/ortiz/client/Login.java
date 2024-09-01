package com.ortiz.client;

import com.ortiz.enums.RequestType;
import com.ortiz.model.Request;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

public class Login extends JFrame {
    private JPanel loginWindow;
    private JTextField nickTextField;
    private JPasswordField passwordTextField;
    private JButton loginButton;
    private JButton registerButton;
    private JSONObject jsonObject;
    private static int userID;
    private static String userNick;

    public Login() {
        super("Game Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setListeners();
    }

    public JPanel getLoginWindow() {
        return loginWindow;
    }

    public static int getUserID() {
        return userID;
    }

    private void setUserID(int userID) {
        Login.userID = userID;
    }

    public static String getUserNick() {
        return userNick;
    }

    public static void setUserNick(String userNick) {
        Login.userNick = userNick;
    }

    private void setListeners() {
        loginButton.addActionListener(e -> {
            try (Socket socket = new Socket("localhost", 4444);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
            ) {
                jsonObject = new JSONObject();
                jsonObject.put("nick", nickTextField.getText().trim());
                jsonObject.put("password", String.valueOf(passwordTextField.getPassword()));
                Request request = new Request(RequestType.LOG_IN, jsonObject.toString());
                out.writeObject(request);
                out.flush();
                Request answer = (Request) in.readObject();

                System.out.println(answer);
                System.out.println(answer);
                System.out.println(answer);
                System.out.println(answer);

                jsonObject = answer.getJson();
                if (jsonObject != null) {
                    if (jsonObject.has("result")) {
                        setUserID(jsonObject.getInt("userID"));
                        setUserNick(jsonObject.getString("userNick"));
                        SwingUtilities.invokeLater(() -> {
                            Point point = this.getLocation();
                            Arrays.asList(Window.getWindows()).forEach(Window::dispose);
                            MainWindow mainWindow = new MainWindow();
                            mainWindow.setContentPane(mainWindow.getMainWindow());
                            mainWindow.setSize(300, 300);
                            mainWindow.pack();
                            mainWindow.setLocation(point);
                            mainWindow.setResizable(false);
                            mainWindow.setVisible(true);
                        });
                    } else {
                        String error = (String) jsonObject.get("error");
                        JOptionPane.showMessageDialog(this, error, "", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Error de conexión con el servidor", "", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error de conexión con el servidor", "", JOptionPane.ERROR_MESSAGE);
            }
        });

        registerButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            Point point = this.getLocation();
            Arrays.asList(Window.getWindows()).forEach(Window::dispose);
            Register register = new Register();
            register.setContentPane(register.getNewUserWindow());
            register.pack();
            register.setLocation(point);
            register.setResizable(false);
            register.setVisible(true);
        }));
    }

    public static void main(String[] args) {
        try {
            String className = UIManager.getCrossPlatformLookAndFeelClassName();
            UIManager.setLookAndFeel(className);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            Login login = new Login();
            login.setContentPane(login.getLoginWindow());
            login.pack();
            login.setLocationRelativeTo(null);
            login.setResizable(false);
            login.setVisible(true);
        });
    }
}