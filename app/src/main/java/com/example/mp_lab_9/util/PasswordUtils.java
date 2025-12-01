package com.example.mp_lab_9.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtils {

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

//    public static boolean verifyPassword(String password, String hashedPassword) {
//        return hashPassword(password).equals(hashedPassword);
//    }
//
//    public static boolean isPasswordStrong(String password) {
//        if (password.length() < 6) {
//            return false;
//        }
//
//        boolean hasLetter = false;
//        boolean hasDigit = false;
//
//        for (char c : password.toCharArray()) {
//            if (Character.isLetter(c)) hasLetter = true;
//            if (Character.isDigit(c)) hasDigit = true;
//        }
//
//        return hasLetter && hasDigit;
//    }
}