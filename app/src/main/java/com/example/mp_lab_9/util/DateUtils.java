package com.example.mp_lab_9.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String SERVER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String DISPLAY_DATE_FORMAT = "dd MMM yyyy";

    public static String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "";
        }

        try {
            SimpleDateFormat serverFormat = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.getDefault());
            Date date = serverFormat.parse(dateString);
            return displayFormat.format(date);
        } catch (ParseException e) {
            // Если парсинг не удался, возвращаем оригинальную строку
            return dateString;
        }
    }
}