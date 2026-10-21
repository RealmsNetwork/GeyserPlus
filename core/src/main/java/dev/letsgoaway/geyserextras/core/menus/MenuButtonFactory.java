package dev.letsgoaway.geyserextras.core.menus;

import dev.letsgoaway.geyserextras.core.ExtrasPlayer;
import dev.letsgoaway.geyserextras.core.config.GeyserExtrasConfig.MenuButtonConfig;
import dev.letsgoaway.geyserextras.core.form.elements.Button;
import org.geysermc.cumulus.util.FormImage;

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

        String title = MenuButtonPolicy.nonBlank(config.getTitle(), defaultTitle);
        String configuredImageType = MenuButtonPolicy.nonBlank(config.getImageType(), imageType);
        String image = MenuButtonPolicy.nonBlank(config.getImage(), defaultImage);
        Runnable callback = action == null ? () -> { } : action;
        if (config.isDisabled()) {
            title = MenuButtonPolicy.nonBlank(config.getDisabledTitle(), title + " §c(Disabled)");
            configuredImageType = MenuButtonPolicy.nonBlank(config.getDisabledImageType(), configuredImageType);
            image = MenuButtonPolicy.nonBlank(config.getDisabledImage(), image);
            String message = config.getDisabledMessage();
            callback = () -> {
                if (message != null && !message.isBlank()) player.sendMessage(message);
            };
        }

        String validatedImageType = MenuButtonPolicy.parseImageType(configuredImageType);
        String safeTitle = MenuButtonPolicy.limit(title, 256);
        String safeImage = MenuButtonPolicy.limit(image, 2048);
        if (!MenuButtonPolicy.validImage(validatedImageType, safeImage)) {
            validatedImageType = null;
            safeImage = "";
        }
        FormImage.Type type = validatedImageType == null ? null : FormImage.Type.valueOf(validatedImageType);
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
}
