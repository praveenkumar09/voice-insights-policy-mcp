package com.aia.voiceinsights.policy.model;

import java.util.Locale;
import java.util.regex.Pattern;

/** Singapore NRIC/FIN helpers. Format check only for now (the real API will reject unknown IDs); the NRIC must never be logged unmasked. */
public final class Nric {

    private static final Pattern FORMAT = Pattern.compile("^[STFGM]\\d{7}[A-Z]$");

    private Nric() {}

    public static String normalise(String raw) {
        return raw == null ? "" : raw.replaceAll("[\\s-]", "").toUpperCase(Locale.ROOT);
    }

    public static boolean isValid(String raw) {
        return FORMAT.matcher(normalise(raw)).matches();
    }

    /** S1234567D -> S****567D */
    public static String mask(String raw) {
        String n = normalise(raw);
        if (n.length() < 9) return "****";
        return n.charAt(0) + "****" + n.substring(5);
    }
}
