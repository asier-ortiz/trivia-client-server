package com.ortiz.model;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
    private Integer id;
    private final String name;
    private final String surname;
    private final Integer age;
    private final String nick;
    private final String password;

    public User(Integer id, String name, String surname, Integer age, String nick, String password) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.nick = nick;
        this.password = password;
    }

    public User(String name, String surname, Integer age, String nick, String password) {
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.nick = nick;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public Integer getAge() {
        return age;
    }

    public String getNick() {
        return nick;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", age=" + age +
                ", nick='" + nick + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}