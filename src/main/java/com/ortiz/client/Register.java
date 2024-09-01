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

public class Register extends JFrame {
    private JTextField nameTextField;
    private JPasswordField passwordTextField;
    private JButton backButton;
    private JButton registerButton;
    private JTextField surnameTextField;
    private JTextField ageTextField;
    private JTextField nickTextField;
    private JPanel newUserWindow;
    private JSONObject jsonObject;

    public Register() {
        super("Game Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setListeners();
    }

    public JPanel getNewUserWindow() {
        return newUserWindow;
    }

    private void setListeners() {
        registerButton.addActionListener(e -> {
            try (Socket socket = new Socket("localhost", 4444);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
            ) {
                jsonObject = new JSONObject();
                jsonObject.put("name", nameTextField.getText().trim());
                jsonObject.put("surname", surnameTextField.getText().trim());
                jsonObject.put("age", ageTextField.getText().trim());
                jsonObject.put("nick", nickTextField.getText().trim());
                jsonObject.put("password", String.valueOf(passwordTextField.getPassword()));
                Request request = new Request(RequestType.REGISTER_USER, jsonObject.toString());
                out.writeObject(request);
                out.flush();
                Request answer = (Request) in.readObject();
                jsonObject = answer.getJson();
                if (jsonObject != null) {
                    if (jsonObject.has("result")) {
                        String result = (String) jsonObject.get("result");
                        clearFields();
                        JOptionPane.showMessageDialog(this, result, "", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        String error = (String) jsonObject.get("error");
                        JOptionPane.showMessageDialog(this, error, "", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Error de conexión con el servidor", "", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException | ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Error de conexión con el servidor", "", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        backButton.addActionListener(e -> SwingUtilities.invokeLater(() -> {
            clearFields();
            Point point = this.getLocation();
            Arrays.asList(Window.getWindows()).forEach(Window::dispose);
            Login login = new Login();
            login.setContentPane(login.getLoginWindow());
            login.pack();
            login.setLocation(point);
            login.setResizable(false);
            login.setVisible(true);
        }));
    }

    private void clearFields() {
        nameTextField.setText("");
        surnameTextField.setText("");
        ageTextField.setText("");
        nickTextField.setText("");
        passwordTextField.setText("");
    }
}