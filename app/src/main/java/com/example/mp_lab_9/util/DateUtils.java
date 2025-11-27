package com.example.mp_lab_9.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final String SERVER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String DISPLAY_DATE_FORMAT = "dd MMM yyyy";
    private static final String DISPLAY_DATETIME_FORMAT = "dd MMM yyyy, HH:mm";

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

    public static String formatDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return "";
        }

        try {
            SimpleDateFormat serverFormat = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat(DISPLAY_DATETIME_FORMAT, Locale.getDefault());
            Date date = serverFormat.parse(dateTimeString);
            return displayFormat.format(date);
        } catch (ParseException e) {
            return dateTimeString;
        }
    }

    public static String getCurrentDateTime() {
        SimpleDateFormat format = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());
        return format.format(new Date());
    }

    public static String getRelativeTime(String dateString) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.getDefault());
            Date date = format.parse(dateString);
            long timeDiff = System.currentTimeMillis() - date.getTime();

            long seconds = timeDiff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;

            if (days > 0) {
                return days + " д. назад";
            } else if (hours > 0) {
                return hours + " ч. назад";
            } else if (minutes > 0) {
                return minutes + " мин. назад";
            } else {
                return "только что";
            }
        } catch (ParseException e) {
            return dateString;
        }
    }
}