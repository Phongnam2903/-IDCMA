package com.example.idcma_project_prm392.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class DateUtils {
    private static final String ISO_8601 = "yyyy-MM-dd"; // simple date for UI

    private DateUtils() {}

    public static String formatIsoDate(Date date) {
        return new SimpleDateFormat(ISO_8601, Locale.US).format(date);
    }

    public static Date parseIsoDate(String iso) {
        try {
            return new SimpleDateFormat(ISO_8601, Locale.US).parse(iso);
        } catch (ParseException e) {
            return null;
        }
    }

    public static long daysUntil(Date target) {
        long now = System.currentTimeMillis();
        long diff = target.getTime() - now;
        return TimeUnit.MILLISECONDS.toDays(diff);
    }

    public static boolean isExpiringSoon(Date expiry, int thresholdDays) {
        return daysUntil(expiry) <= thresholdDays && expiry.getTime() > System.currentTimeMillis();
    }

    public static boolean isExpired(Date expiry) {
        return expiry.getTime() < System.currentTimeMillis();
    }
}
