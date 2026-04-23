package com.favorites.utils;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;

public class URLValidator {

    private static final Pattern URL_PATTERN = Pattern.compile(
        "^(https?|ftp)://[a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(\\.[a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*"
        + "(:[0-9]{1,5})?"  // Support for port numbers
        + "(/[a-zA-Z0-9!$&'()*+,;=:@._~\\-/?#\\[\\]]*)?$"
    );

    public static boolean isValidUrl(String urlString) {
        if (urlString == null || urlString.trim().isEmpty()) {
            return false;
        }

        urlString = urlString.trim();

        if (urlString.length() > 2048) {
            return false;
        }

        if (!URL_PATTERN.matcher(urlString).matches()) {
            return false;
        }

        try {
            new URL(urlString).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    public static boolean isValidUrlSecure(String urlString, boolean allowLocalhost, boolean allowPrivateIPs) {
        if (!isValidUrl(urlString)) {
            return false;
        }

        try {
            URL url = new URL(urlString);
            String host = url.getHost();

            // Null check for host
            if (host == null) {
                return false;
            }

            if (!allowLocalhost && ("localhost".equals(host) || "127.0.0.1".equals(host))) {
                return false;
            }

            if (!allowPrivateIPs && isPrivateIP(host)) {
                return false;
            }

            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    /**
     * Validates if the given string is a valid IPv4 address
     */
    private static boolean isValidIPv4(String host) {
        if (host == null || host.isEmpty()) {
            return false;
        }

        String[] parts = host.split("\\.");
        if (parts.length != 4) {
            return false;
        }

        for (String part : parts) {
            try {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the given host is a private IP address
     */
    private static boolean isPrivateIP(String host) {
        if (host == null || host.isEmpty()) {
            return false;
        }

        // Check localhost and loopback
        if (host.equals("localhost") || host.equals("127.0.0.1")) {
            return true;
        }

        // Only check IP ranges if it's a valid IPv4
        if (!isValidIPv4(host)) {
            return false;
        }

        return host.startsWith("192.168.") ||
               host.startsWith("10.") ||
               host.startsWith("172.16.") ||
               host.startsWith("172.17.") ||
               host.startsWith("172.18.") ||
               host.startsWith("172.19.") ||
               host.startsWith("172.20.") ||
               host.startsWith("172.21.") ||
               host.startsWith("172.22.") ||
               host.startsWith("172.23.") ||
               host.startsWith("172.24.") ||
               host.startsWith("172.25.") ||
               host.startsWith("172.26.") ||
               host.startsWith("172.27.") ||
               host.startsWith("172.28.") ||
               host.startsWith("172.29.") ||
               host.startsWith("172.30.") ||
               host.startsWith("172.31.");
    }

    /**
     * Extracts the domain from a valid URL
     */
    public static String extractDomain(String urlString) {
        if (urlString == null || urlString.trim().isEmpty()) {
            return null;
        }

        try {
            return new URL(urlString.trim()).getHost();
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
