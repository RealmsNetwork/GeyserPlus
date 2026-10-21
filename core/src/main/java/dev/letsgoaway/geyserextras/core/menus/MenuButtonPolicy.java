package dev.letsgoaway.geyserextras.core.menus;

import java.net.URI;
import java.util.Locale;

/** Shared validation and fallback rules for all configurable Bedrock menu buttons. */
final class MenuButtonPolicy {
    private MenuButtonPolicy() {
    }

    static String parseImageType(String value) {
        if (value == null || value.isBlank() || "NONE".equalsIgnoreCase(value.trim())) return null;
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        return "PATH".equals(normalized) || "URL".equals(normalized) ? normalized : null;
    }

    static boolean validImage(String type, String image) {
        if (type == null || image == null || image.isBlank()) return false;
        if ("PATH".equals(type)) {
            return !image.startsWith("/") && !image.contains("\\") && !image.contains("..")
                    && image.matches("[A-Za-z0-9_./-]+");
        }
        if ("URL".equals(type)) {
            try {
                URI uri = URI.create(image.trim());
                return ("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                        && uri.getHost() != null && !uri.getHost().isBlank();
            } catch (IllegalArgumentException ignored) {
                return false;
            }
        }
        return false;
    }

    static String nonBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    static String limit(String value, int max) {
        if (value == null) return "";
        return value.length() <= max ? value : value.substring(0, max);
    }
}
