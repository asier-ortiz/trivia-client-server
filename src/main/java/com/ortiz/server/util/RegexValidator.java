package com.ortiz.server.util;

import com.ortiz.model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexValidator {
    private static final String STRING_PATTERN = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){1,18}[a-zA-Z0-9]$";
    private static final String AGE_PATTERN = "^(?:[1-9][0-9]?|1[01][0-9]|120)$";

    private static boolean isValidString(final String string) {
        Pattern pattern = Pattern.compile(STRING_PATTERN);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }

    private static boolean isValidAge(final String age) {
        Pattern pattern = Pattern.compile(AGE_PATTERN);
        Matcher matcher = pattern.matcher(age);
        return matcher.matches();
    }

    public static boolean validateUser(final User user) {
        return isValidString(user.getName()) &&
                isValidString(user.getSurname()) &&
                isValidAge(String.valueOf(user.getAge())) &&
                isValidString(user.getNick()) &&
                isValidString(user.getPassword());
    }
}