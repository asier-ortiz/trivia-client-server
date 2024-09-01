package com.ortiz.server;

import com.ortiz.enums.RequestType;
import com.ortiz.model.Game;
import com.ortiz.model.Question;
import com.ortiz.model.Request;
import com.ortiz.model.User;
import com.ortiz.server.database.DatabaseManager;
import com.ortiz.server.util.RegexValidator;
import org.apache.commons.lang3.SerializationUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.*;
import java.util.Base64;

public class ServerThread extends Thread {
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private Game game = null;
    private final SecretKey secretKey;
    private final Cipher cipher;

    public ServerThread(Socket socket, SecretKey secretKey) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException {
        this.socket = socket;
        this.secretKey = secretKey;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        cipher = Cipher.getInstance("AES");
    }

    @Override
    public void run() {
        Request request;
        try {
            boolean isOpenned = true;
            while (isOpenned) {
                if (!socket.isClosed()) {
                    Object object = in.readObject();
                    if (object instanceof Request) {
                        request = (Request) object;
                        handleRequest(request);
                    } else if (object instanceof String) {
                        String encryptedString = (String) object;
                        byte[] encryptedRequest = Base64.getDecoder().decode(encryptedString);
                        request = getDecryptedRequest(encryptedRequest);
                        handleRequest(request);
                    } else {
                        throw new ClassNotFoundException();
                    }
                } else {
                    isOpenned = false;
                    Server.getInstance().cancelGame(game);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                in.close();
                out.close();
                socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void handleRequest(final Request request) {
        switch (request.getRequestType()) {
            case REGISTER_USER -> registerUser(request);
            case LOG_IN -> loginUser(request);
            case GAME_RULES -> sendGameRules(request);
            case PLAY -> starGame(request);
            case ANSWER -> answerQuestion(request);
            case GAME_CANCELED -> cancelGame();
            case EXIT -> exit();
        }
    }

    private void sendEncrypedRequest(final Request request) throws BadPaddingException, IllegalBlockSizeException, IOException, InvalidKeyException {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] data = SerializationUtils.serialize(request);
        byte[] encryptedRequest = cipher.doFinal(data);
        String encrptedString = Base64.getEncoder().encodeToString(encryptedRequest);
        out.writeObject(encrptedString);
        out.flush();
    }

    private Request getDecryptedRequest(final byte[] encryptedRequest) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] data = cipher.doFinal(encryptedRequest);
        return SerializationUtils.deserialize(data);
    }

    public boolean isSocketOpenned() {
        return !socket.isClosed();
    }

    private void registerUser(final Request request) {
        try {
            JSONObject jsonObject = request.getJson();
            if (jsonObject != null) {
                if (jsonObject.has("name")
                        && jsonObject.has("surname")
                        && jsonObject.has("age")
                        && jsonObject.has("nick")
                        && jsonObject.has("password")) {
                    try {
                        String age = (String) jsonObject.get("age");
                        Integer.parseInt(age);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        returnError(request);
                    }
                    User user = new User(
                            jsonObject.getString("name"),
                            jsonObject.getString("surname"),
                            jsonObject.getInt("age"),
                            jsonObject.getString("nick"),
                            jsonObject.getString("password")
                    );
                    if (RegexValidator.validateUser(user)) {
                        int userId = DatabaseManager.getInstance().getUserDAO().save(user);
                        if (userId > 0) {
                            JSONObject jsonAnswer = new JSONObject();
                            jsonAnswer.put("result", "Registro correcto");
                            Request answer = new Request(request.getRequestType(), jsonAnswer.toString());
                            out.writeObject(answer);
                            out.flush();
                        } else {
                            returnError(request);
                        }
                    } else {
                        returnError(request);
                    }
                } else {
                    returnError(request);
                }
            } else {
                returnError(request);
            }
        } catch (IOException e) {
            e.printStackTrace();
            returnError(request);
        }
    }

    private void loginUser(final Request request) {
        JSONObject jsonObject = request.getJson();
        if (jsonObject != null) {
            if (jsonObject.has("nick") && jsonObject.has("password")) {
                String nick = jsonObject.getString("nick");
                String password = jsonObject.getString("password");
                if (nick.length() > 0 && password.length() > 0) {
                    User user = DatabaseManager.getInstance().getUserDAO().login(nick, password);
                    if (user != null) {
                        try {
                            JSONObject jsonAnswer = new JSONObject();
                            jsonAnswer.put("result", "Login correcto");
                            jsonAnswer.put("userID", user.getId());
                            jsonAnswer.put("userNick", user.getNick());
                            Request answer = new Request(request.getRequestType(), jsonAnswer.toString());
                            out.writeObject(answer);
                            out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        returnError(request);
                    }
                } else {
                    returnError(request);
                }
            } else {
                returnError(request);
            }
        } else {
            returnError(request);
        }
    }

    private void sendGameRules(final Request request) {
        try {
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
            KeyPair pair = keygen.generateKeyPair();
            PrivateKey privateKey = pair.getPrivate();
            PublicKey publicKey = pair.getPublic();
            out.writeObject(publicKey);
            String rules = """
                    *** REGLAS DEL JUEGO ***
                                        
                    Cada jugador dispondrá de 15 segundos para responder a una pregunta aleatoria.
                    Si la respuesta es correcta el jugador se anotará un punto.
                    Si transcurridos los 15 segundos el jugador no ha respondido, el turno pasará a ser del oponente.
                    El juego finalizará tras tres rondas de preguntas.
                                       
                    """;
            out.writeObject(rules);
            Signature rsa = Signature.getInstance("SHA1WITHRSA");
            rsa.initSign(privateKey);
            rsa.update(rules.getBytes());
            byte[] signature = rsa.sign();
            out.writeObject(signature);
            out.writeObject(secretKey);
            out.flush();
        } catch (NoSuchAlgorithmException | IOException | SignatureException | InvalidKeyException e) {
            e.printStackTrace();
            returnError(request);
        }
    }

    private void starGame(final Request request) {
        JSONObject jsonObject = request.getJson();
        if (jsonObject != null) {
            if (jsonObject.has("userId")) {
                int userId = jsonObject.getInt("userId");
                User user = DatabaseManager.getInstance().getUserDAO().getUserById(userId);
                if (user != null) {
                    game = Server.getInstance().getPlayableGame(user);
                    if (game.getPlayer1() == null) {
                        game.setPlayer1(user);
                        game.setSocketPlayer1(this);
                        game.sendGreetingMessage(1);
                    } else if (game.getPlayer2() == null) {
                        game.setPlayer2(user);
                        game.setSocketPlayer2(this);
                        game.sendGreetingMessage(2);
                    }
                    if (game.getPlayer1() != null && game.getPlayer2() != null) {
                        game.startGame();
                        game.sendNicks();
                    }
                } else {
                    returnError(request);
                }
            } else {
                returnError(request);
            }
        } else {
            returnError(request);
        }
    }

    public void sendMessage(final String message, final int option) {
        try {
            if (option == 1) {
                JSONObject jsonAnswer = new JSONObject();
                jsonAnswer.put("result", "ok");
                jsonAnswer.put("greeting", message);
                Request answer = new Request(RequestType.PLAY, jsonAnswer.toString());
                sendEncrypedRequest(answer);
            } else if (option == 2) {
                JSONObject jsonAnswer = new JSONObject();
                jsonAnswer.put("result", "ok");
                jsonAnswer.put("opponentNick", message);
                Request answer = new Request(RequestType.PLAY, jsonAnswer.toString());
                sendEncrypedRequest(answer);
            } else if (option == 3) {
                JSONObject jsonAnswer = new JSONObject();
                jsonAnswer.put("result", "ok");
                jsonAnswer.put("scores", message);
                Request answer = new Request(RequestType.PLAY, jsonAnswer.toString());
                sendEncrypedRequest(answer);
            }
        } catch (IOException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            returnError(new Request(RequestType.GAME_CANCELED));
            e.printStackTrace();
        }
    }

    public void sendQuestion(final Question question) {
        try {
            JSONObject jsonAnswer = new JSONObject();
            jsonAnswer.put("result", "ok");
            jsonAnswer.put("question", question.getQuestion());
            jsonAnswer.put("category", question.getCategory());
            JSONArray answersJsonArray = new JSONArray();
            JSONObject answer1JsonObject = new JSONObject();
            answer1JsonObject.put("answer", question.getAnswers().get(0).getAnswer());
            answer1JsonObject.put("correct", question.getAnswers().get(0).isCorrect());
            answersJsonArray.put(answer1JsonObject);
            JSONObject answer2JsonObject = new JSONObject();
            answer2JsonObject.put("answer", question.getAnswers().get(1).getAnswer());
            answer2JsonObject.put("correct", question.getAnswers().get(1).isCorrect());
            answersJsonArray.put(answer2JsonObject);
            JSONObject answer3JsonObject = new JSONObject();
            answer3JsonObject.put("answer", question.getAnswers().get(2).getAnswer());
            answer3JsonObject.put("correct", question.getAnswers().get(2).isCorrect());
            answersJsonArray.put(answer3JsonObject);
            JSONObject answer4JsonObject = new JSONObject();
            answer4JsonObject.put("answer", question.getAnswers().get(3).getAnswer());
            answer4JsonObject.put("correct", question.getAnswers().get(3).isCorrect());
            answersJsonArray.put(answer4JsonObject);
            jsonAnswer.put("answers", answersJsonArray);
            Request answer = new Request(RequestType.PLAY, jsonAnswer.toString());
            sendEncrypedRequest(answer);
        } catch (IOException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
            returnError(new Request(RequestType.QUESTION));
        }
    }

    private void answerQuestion(final Request request) {
        JSONObject jsonObject = request.getJson();
        if (jsonObject != null) {
            if (jsonObject.has("correct_answer")) {
                boolean correctAnswer = jsonObject.getBoolean("correct_answer");
                game.nextTurn(correctAnswer ? 1 : 0);
            } else {
                returnError(request);
            }
        } else {
            returnError(request);
        }
    }

    public void cancelGame() {
        returnError(new Request(RequestType.GAME_CANCELED));
    }

    public void endGame() {
        game.sendScores();
        Server.getInstance().removeGame(game);
    }

    private void exit() {
        Server.getInstance().removeClient(socket);
    }

    private void returnError(final Request request) {
        String error = switch (request.getRequestType()) {
            case REGISTER_USER -> "No se ha podido registrar el jugador. Datos incorrectos y/o el usuario ya existe";
            case LOG_IN -> "Nick y/o contraseña incorrectos";
            case GAME_RULES -> "Error del servidor, imposible recuperar las reglas de juego";
            case PLAY -> "Imposible iniciar partida";
            case ANSWER, GAME_CANCELED -> "Tu oponente ha abandonado";
            case QUESTION -> "Error del servidor, se cancela la partida";
            case EXIT -> "";
        };
        try {
            JSONObject jsonAnswer = new JSONObject();
            jsonAnswer.put("error", error);
            Request answer = new Request(request.getRequestType(), jsonAnswer.toString());
            if (request.getRequestType() == RequestType.ANSWER
                    || request.getRequestType() == RequestType.GAME_CANCELED
                    || request.getRequestType() == RequestType.QUESTION) {
                sendEncrypedRequest(answer);
            } else {
                out.writeObject(answer);
                out.flush();
            }
            socket.close();
        } catch (IOException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
            returnError(request);
        }
    }
}