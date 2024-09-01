package com.ortiz.server.database;

import com.ortiz.model.Answer;
import com.ortiz.model.Question;

import java.util.ArrayList;
import java.util.Arrays;

public class DatabaseManager {
    private static DatabaseManager databaseManager;
    private final UserDAO userDAO = new UserDAO();
    private final QuestionDAO questionDAO = new QuestionDAO();
    private final AnswerDAO answerDAO = new AnswerDAO();

    private synchronized static void createInstence() {
        if (databaseManager == null) databaseManager = new DatabaseManager();
    }

    public synchronized static DatabaseManager getInstance() {
        createInstence();
        return databaseManager;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public QuestionDAO getQuestionDAO() {
        return questionDAO;
    }

    public void createTables() {
        userDAO.createTable();
        questionDAO.createTable();
        answerDAO.createTable();
    }

    public void createData() {
        // GEOGRAFIA
        var question = new Question("¿Cuál es el idioma más hablado en Suiza?", "Geografía");
        question.setId(questionDAO.save(question));
        var answers = new ArrayList<>(
                Arrays.asList(
                        new Answer("Alemán", true),
                        new Answer("Suizo", false),
                        new Answer("Francés", false),
                        new Answer("Inglés", false)
                )
        );
        for (Answer answer : answers) answerDAO.save(answer, question.getId());

        question = new Question("¿Qué país está entre Perú y Colombia?", "Geografía");
        question.setId(questionDAO.save(question));
        answers = new ArrayList<>(
                Arrays.asList(
                        new Answer("Ecuador", true),
                        new Answer("Nicaragua", false),
                        new Answer("Bolivia", false),
                        new Answer("Paraguay", false)
                )
        );
        for (Answer answer : answers) answerDAO.save(answer, question.getId());

        question = new Question("¿Cuál es el río más largo de Europa Occidental?", "Geografía");
        question.setId(questionDAO.save(question));
        answers = new ArrayList<>(
                Arrays.asList(
                        new Answer("Rin", true),
                        new Answer("Danubio", false),
                        new Answer("Volga", false),
                        new Answer("Sena", false)
                )
        );
        for (Answer answer : answers) answerDAO.save(answer, question.getId());

        question = new Question("¿Qué lago baña la ciudad de Ginebra?", "Geografía");
        question.setId(questionDAO.save(question));
        answers = new ArrayList<>(
                Arrays.asList(
                        new Answer("El lago Leman", true),
                        new Answer("El lago Onega", false),
                        new Answer("El lago Peipus", false),
                        new Answer("El lago Vänern", false)
                )
        );
        for (Answer answer : answers) answerDAO.save(answer, question.getId());

        question = new Question("¿Qué palabra significa “hijo de” en los apellidos escoceses?", "Geografía");
        question.setId(questionDAO.save(question));
        answers = new ArrayList<>(
                Arrays.asList(
                        new Answer("Mac", true),
                        new Answer("Sen", false),
                        new Answer("Ich", false),
                        new Answer("Ova", false)
                )
        );
        for (Answer answer : answers) answerDAO.save(answer, question.getId());

        // ENTRETENIMIENTO
        question = new Question("¿Cuál es el oso más famoso del parque nacional de Yellowstone?", "Entretenimiento");
        question.setId(questionDAO.save(question));
        answers = new ArrayList<>(
                Arrays.asList(
                        new Answer("Yogui", true),
                        new Answer("Bubu", false),
                        new Answer("Winnie Pooh", false),
                        new Answer("Baloo", false)
                )
        );
        for (Answer answer : answers) answerDAO.save(answer, question.getId());

        question = new Question("¿Qué actor, que no era el feo ni el malo, era el bueno?", "Entretenimiento");
        question.setId(questionDAO.save(question));
        answers = new ArrayList<>(
                Arrays.asList(
                        new Answer("Clint Eastwood", true),
                        new Answer("John Wayne", false),
                        new Answer("Robert Duval", false),
                        new Answer("Gary Cooper", false)
                )
        );
        for (Answer answer : answers) answerDAO.save(answer, question.getId());

        question = new Question("¿Con qué director de cine italiano se casó la actriz Giulietta Masina?", "Entretenimiento");
        question.setId(questionDAO.save(question));
        answers = new ArrayList<>(
                Arrays.asList(
                        new Answer("Federico Fellini", true),
                        new Answer("Pier Paolo Pasolini", false),
                        new Answer("Roberto Rosselinni", false),
                        new Answer("Bernardo Bertolucci", false)
                )
        );
        for (Answer answer : answers) answerDAO.save(answer, question.getId());

        question = new Question("¿Quién fue la gran ganadora de los Grammy Latinos 2018?", "Entretenimiento");
        question.setId(questionDAO.save(question));
        answers = new ArrayList<>(
                Arrays.asList(
                        new Answer("Rosalía", true),
                        new Answer("Juanes", false),
                        new Answer("Shakira", false),
                        new Answer("Carlos Vives", false)
                )
        );
        for (Answer answer : answers) answerDAO.save(answer, question.getId());

        question = new Question("¿Cuál de los Siete Enanitos no tenía barba?", "Entretenimiento");
        question.setId(questionDAO.save(question));
        answers = new ArrayList<>(
                Arrays.asList(
                        new Answer("Mudito", true),
                        new Answer("Sabio", false),
                        new Answer("Gruñón", false),
                        new Answer("Dormilón", false)
                )
        );
        for (Answer answer : answers) answerDAO.save(answer, question.getId());

        // HISTORIA
        question = new Question("¿Qué reina británica era hija de los Reyes Católicos?", "Historia");
        question.setId(questionDAO.save(question));
        answers = new ArrayList<>(
                Arrays.asList(
                        new Answer("Catalina de Aragón", true),
                        new Answer("Isabel de Aragón", false),
                        new Answer("Juana la Loca", false),
                        new Answer("María de Aragón", false)
                )
        );
        for (Answer answer : answers) answerDAO.save(answer, question.getId());

        question = new Question("¿Qué país fue llamado la Galia por los romanos?", "Historia");
        question.setId(questionDAO.save(question));
        answers = new ArrayList<>(
                Arrays.asList(
                        new Answer("Francia", true),
                        new Answer("Bélgica", false),
                        new Answer("Luxemburgo", false),
                        new Answer("Suiza", false)
                )
        );
        for (Answer answer : answers) answerDAO.save(answer, question.getId());

        question = new Question("¿Qué batalla crucial tuvo lugar en 1815?", "Historia");
        question.setId(questionDAO.save(question));
        answers = new ArrayList<>(
                Arrays.asList(
                        new Answer("Waterloo", true),
                        new Answer("Gettysburg", false),
                        new Answer("Trafalgar", false),
                        new Answer("Termópilas", false)
                )
        );
        for (Answer answer : answers) answerDAO.save(answer, question.getId());

        question = new Question("¿Cuál era la ciudad hogar de Marco Polo?", "Historia");
        question.setId(questionDAO.save(question));
        answers = new ArrayList<>(
                Arrays.asList(
                        new Answer("Venecia", true),
                        new Answer("Roma", false),
                        new Answer("Florencia", false),
                        new Answer("Milán", false)
                )
        );
        for (Answer answer : answers) answerDAO.save(answer, question.getId());

        question = new Question("¿Quién era el emperador de Roma cuando murió Jesús?", "Historia");
        question.setId(questionDAO.save(question));
        answers = new ArrayList<>(
                Arrays.asList(
                        new Answer("Tiberio", true),
                        new Answer("Claudio", false),
                        new Answer("Nerón", false),
                        new Answer("Adriano", false)
                )
        );
        for (Answer answer : answers) answerDAO.save(answer, question.getId());

        // ARTE Y LITERATURA
        question = new Question("¿Qué escribía un testador?", "Arte y literatura");
        question.setId(questionDAO.save(question));
        answers = new ArrayList<>(
                Arrays.asList(
                        new Answer("Testamentos", true),
                        new Answer("Testimonios", false),
                        new Answer("Tests de examen", false),
                        new Answer("Testaferros", false)
                )
        );
        for (Answer answer : answers) answerDAO.save(answer, question.getId());

        question = new Question("¿Cuál era el lema de los Tres Mosqueteros?", "Arte y literatura");
        question.setId(questionDAO.save(question));
        answers = new ArrayList<>(
                Arrays.asList(
                        new Answer("Todos para uno y uno para todos", true),
                        new Answer("No hay que ir para atrás ni para darse impulso", false),
                        new Answer("Aprende a vivir y sabrás morir bien", false),
                        new Answer("Por Crom", false)
                )
        );
        for (Answer answer : answers) answerDAO.save(answer, question.getId());

        question = new Question("¿Qué tiene en Segovia 128 arcos?", "Arte y literatura");
        question.setId(questionDAO.save(question));
        answers = new ArrayList<>(
                Arrays.asList(
                        new Answer("El Acueducto", true),
                        new Answer("El Alcázar", false),
                        new Answer("Castillo de Coca", false),
                        new Answer("Iglesia de Sotosalbos", false)
                )
        );
        for (Answer answer : answers) answerDAO.save(answer, question.getId());

        question = new Question("¿Quién fue el italiano que puso música al Othelo de Shakespeare?", "Arte y literatura");
        question.setId(questionDAO.save(question));
        answers = new ArrayList<>(
                Arrays.asList(
                        new Answer("Verdi", true),
                        new Answer("Vivaldi", false),
                        new Answer("Puccini", false),
                        new Answer("Salieri", false)
                )
        );
        for (Answer answer : answers) answerDAO.save(answer, question.getId());

        question = new Question("¿Quién escribió La Guerra de los Mundos en 1898?", "Arte y literatura");
        question.setId(questionDAO.save(question));
        answers = new ArrayList<>(
                Arrays.asList(
                        new Answer("H.G. Wells", true),
                        new Answer("Isaac Asimov", false),
                        new Answer("Julio Verne", false),
                        new Answer("Mary Shelley", false)
                )
        );
        for (Answer answer : answers) answerDAO.save(answer, question.getId());

        // CIENCIA Y NATURALEZA
        question = new Question("¿Cómo se llaman las células nerviosas?", "Ciencia y naturaleza");
        question.setId(questionDAO.save(question));
        answers = new ArrayList<>(
                Arrays.asList(
                        new Answer("Neuronas", true),
                        new Answer("Fibroblastos", false),
                        new Answer("Fibrocitos", false),
                        new Answer("Glóbulos blancos", false)
                )
        );
        for (Answer answer : answers) answerDAO.save(answer, question.getId());

        question = new Question("¿En qué mes el sol está más cerca de la Tierra?", "Ciencia y naturaleza");
        question.setId(questionDAO.save(question));
        answers = new ArrayList<>(
                Arrays.asList(
                        new Answer("Diciembre", true),
                        new Answer("Agosto", false),
                        new Answer("Mayo", false),
                        new Answer("Septiembre", false)
                )
        );
        for (Answer answer : answers) answerDAO.save(answer, question.getId());

        question = new Question("¿En qué parte del cuerpo se encuentra la piel más gruesa?", "Ciencia y naturaleza");
        question.setId(questionDAO.save(question));
        answers = new ArrayList<>(
                Arrays.asList(
                        new Answer("Espalda", true),
                        new Answer("Pies", false),
                        new Answer("Muslos", false),
                        new Answer("Manos", false)
                )
        );
        for (Answer answer : answers) answerDAO.save(answer, question.getId());

        question = new Question("¿Cómo se llama la ciencia que estudia la sangre?", "Ciencia y naturaleza");
        question.setId(questionDAO.save(question));
        answers = new ArrayList<>(
                Arrays.asList(
                        new Answer("Hematología", true),
                        new Answer("Cardiología", false),
                        new Answer("Neurología", false),
                        new Answer("Radiografía", false)
                )
        );
        for (Answer answer : answers) answerDAO.save(answer, question.getId());

        question = new Question("¿Qué fabricó Alessandro Volta, por primera vez, en 1800?", "Ciencia y naturaleza");
        question.setId(questionDAO.save(question));
        answers = new ArrayList<>(
                Arrays.asList(
                        new Answer("Pila", true),
                        new Answer("Bombilla", false),
                        new Answer("Telégrafo", false),
                        new Answer("Motor de combustión", false)
                )
        );
        for (Answer answer : answers) answerDAO.save(answer, question.getId());
    }
}