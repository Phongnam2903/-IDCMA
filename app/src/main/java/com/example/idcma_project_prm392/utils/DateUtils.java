package com.example.idcma_project_prm392.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DateUtils {

    // Các pattern thường gặp — thêm nếu cần
    private static final List<String> DATE_PATTERNS = Arrays.asList(
            "yyyy-MM-dd",
            "dd/MM/yyyy",
            "yyyy/MM/dd",
            "dd-MM-yyyy",
            "MM/dd/yyyy"
    );

    /**
     * Try parse a date String using known patterns.
     * @param dateStr input string
     * @return Date or null if cannot parse
     */
    public static Date tryParse(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        String s = dateStr.trim();
        for (String p : DATE_PATTERNS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(p, Locale.getDefault());
                sdf.setLenient(false);
                return sdf.parse(s);
            } catch (ParseException ignored) { }
        }
        return null;
    }

    /**
     * Format a Date (or date string) into display format dd/MM/yyyy.
     * Accepts either Date or String input.
     */
    public static String formatDate(Date date) {
        if (date == null) return "-";
        SimpleDateFormat out = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return out.format(date);
    }

    public static String formatDate(String dateStr) {
        Date d = tryParse(dateStr);
        return formatDate(d);
    }

    /**
     * Check whether expiryDate (Date) is within thresholdDays from now (<= thresholdDays).
     * Accepts Date input.
     */
    public static boolean isExpiringSoon(Date expiryDate, int thresholdDays) {
        if (expiryDate == null) return false;
        long now = System.currentTimeMillis();
        long diff = expiryDate.getTime() - now;
        long days = diff / (1000L * 60 * 60 * 24);
        return days <= thresholdDays;
    }

    /**
     * Overload: Accept String date and default threshold 7 days.
     */
    public static boolean isExpiringSoon(String dateStr) {
        Date d = tryParse(dateStr);
        return isExpiringSoon(d, 7); // default <=7 days
    }

    /**
     * Overload: Accept Date and default threshold 7 days.
     */
    public static boolean isExpiringSoon(Date expiryDate) {
        return isExpiringSoon(expiryDate, 7);
    }
}
