package dev.letsgoaway.geyserextras.core.menus;

import dev.letsgoaway.geyserextras.core.ExtrasPlayer;
import dev.letsgoaway.geyserextras.core.config.GeyserExtrasConfig.MenuButtonConfig;
import dev.letsgoaway.geyserextras.core.form.elements.Button;
import org.geysermc.cumulus.util.FormImage;

import java.net.URI;

import static dev.letsgoaway.geyserextras.core.GeyserExtras.GE;

/** Builds buttons using the server owner's label, icon, disabled-state and ordering overrides. */
public final class MenuButtonFactory {
    private MenuButtonFactory() {
    }

    public static Button create(ExtrasPlayer player, String key, int defaultOrder, String defaultTitle,
                                FormImage.Type defaultImageType, String defaultImage, Runnable action) {
        String imageType = defaultImageType == null ? "NONE" : defaultImageType.name();
        MenuButtonConfig config = GE.getConfig().menuSettings().button(key, defaultOrder, imageType, defaultImage);
        if (!config.isEnabled()) return null;

        String title = nonBlank(config.getTitle(), defaultTitle);
        String configuredImageType = nonBlank(config.getImageType(), imageType);
        String image = nonBlank(config.getImage(), defaultImage);
        Runnable callback = action == null ? () -> { } : action;
        if (config.isDisabled()) {
            title = nonBlank(config.getDisabledTitle(), title + " §c(Disabled)");
            configuredImageType = nonBlank(config.getDisabledImageType(), configuredImageType);
            image = nonBlank(config.getDisabledImage(), image);
            String message = config.getDisabledMessage();
            callback = () -> {
                if (message != null && !message.isBlank()) player.sendMessage(message);
            };
        }

        FormImage.Type type = parseImageType(configuredImageType);
        String safeTitle = limit(title, 256);
        String safeImage = limit(image, 2048);
        if (!validImage(type, safeImage)) {
            type = null;
            safeImage = "";
        }
        Button button = type == null || safeImage.isBlank()
                ? new Button(safeTitle, callback)
                : new Button(safeTitle, type, safeImage, callback);
        button.setSortOrder(config.getSortOrder() == Integer.MIN_VALUE ? defaultOrder : config.getSortOrder());
        button.setDisabled(config.isDisabled());
        return button;
    }

    public static void add(dev.letsgoaway.geyserextras.core.form.BedrockMenu menu, ExtrasPlayer player,
                           String key, int defaultOrder, String defaultTitle, FormImage.Type defaultImageType,
                           String defaultImage, Runnable action) {
        Button button = create(player, key, defaultOrder, defaultTitle, defaultImageType, defaultImage, action);
        if (button != null) menu.add(button);
    }

    private static FormImage.Type parseImageType(String value) {
        if (value == null || value.isBlank() || "NONE".equalsIgnoreCase(value.trim())) return null;
        try {
            return FormImage.Type.valueOf(value.trim().toUpperCase(java.util.Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static boolean validImage(FormImage.Type type, String image) {
        if (type == null || image == null || image.isBlank()) return false;
        if (type == FormImage.Type.PATH) {
            return !image.startsWith("/") && !image.contains("\\") && !image.contains("..")
                    && image.matches("[A-Za-z0-9_./-]+");
        }
        if (type == FormImage.Type.URL) {
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

    private static String nonBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private static String limit(String value, int max) {
        if (value == null) return "";
        return value.length() <= max ? value : value.substring(0, max);
    }
}
