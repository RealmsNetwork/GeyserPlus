package dev.letsgoaway.geyserextras.core.menus;

import dev.letsgoaway.geyserextras.core.ExtrasPlayer;
import dev.letsgoaway.geyserextras.core.config.GeyserExtrasConfig.MenuButtonConfig;
import dev.letsgoaway.geyserextras.core.config.GeyserExtrasConfig.MenuSettings;
import dev.letsgoaway.geyserextras.core.form.BedrockMenu;
import dev.letsgoaway.geyserextras.core.form.elements.Button;
import dev.letsgoaway.geyserextras.core.locale.BedrockLocale;
import dev.letsgoaway.geyserextras.core.menus.settings.menus.SettingsMenu;
import dev.letsgoaway.geyserextras.core.parity.java.menus.packs.PackLoader;
import dev.letsgoaway.geyserextras.core.parity.java.menus.packs.PackMenu;
import dev.letsgoaway.geyserextras.core.parity.java.menus.serverlinks.ServerLinksMenu;
import dev.letsgoaway.geyserextras.core.preferences.bindings.Action;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static dev.letsgoaway.geyserextras.core.GeyserExtras.GE;

public class MainMenu extends BedrockMenu {
    public MainMenu() {
        super();
    }

    @Override
    public SimpleForm create(ExtrasPlayer player) {
        MenuSettings settings = GE.getConfig().menuSettings();
        setTitle(settings.title("main", player.translateGE("ge.menu")));
        setHeader(settings.content("main", settings.getMainContent()));

        List<OrderedButton> configured = new ArrayList<>();
        int sequence = 0;
        sequence = add(configured, player, settings, sequence, "swap-offhand",
                Action.SWAP_OFFHAND.translate(player), "PATH", "textures/ui/move.png", true,
                () -> Action.SWAP_OFFHAND.run(player));
        sequence = add(configured, player, settings, sequence, "reconnect",
                Action.RECONNECT.translate(player), "PATH", "textures/ui/refresh_hover.png", true,
                () -> Action.RECONNECT.run(player));
        sequence = add(configured, player, settings, sequence, "settings",
                BedrockLocale.SETTINGS, "PATH", "textures/ui/settings_glyph_color_2x.png", true,
                () -> player.sendForm(new SettingsMenu()));
        sequence = add(configured, player, settings, sequence, "advancements",
                Action.OPEN_ADVANCEMENTS.translate(player), "PATH", "textures/ui/achievements.png", true,
                () -> Action.OPEN_ADVANCEMENTS.run(player));
        sequence = add(configured, player, settings, sequence, "statistics",
                Action.OPEN_STATISTICS.translate(player), "PATH", "textures/ui/world_glyph_color_2x_black_outline.png", true,
                () -> Action.OPEN_STATISTICS.run(player));
        sequence = add(configured, player, settings, sequence, "player-list",
                Action.PLAYER_LIST.translate(player), "PATH", "textures/ui/Local.png", true,
                () -> Action.PLAYER_LIST.run(player));
        sequence = add(configured, player, settings, sequence, "server-links",
                player.translate("menu.server_links.title"), "PATH", "textures/ui/external_link.png",
                !player.getServerLinksData().getServerLinks().isEmpty(),
                () -> player.sendForm(new ServerLinksMenu()));
        add(configured, player, settings, sequence, "resource-packs",
                BedrockLocale.MENU.RESOURCE_PACKS, "PATH", "textures/ui/glyph_resource_pack.png",
                !PackLoader.PACKS.isEmpty(), () -> player.sendForm(new PackMenu()));

        configured.sort(Comparator.comparingInt(OrderedButton::order).thenComparingInt(OrderedButton::sequence));
        for (OrderedButton button : configured) {
            add(button.button());
        }
        return super.create(player);
    }

    private int add(List<OrderedButton> target, ExtrasPlayer player, MenuSettings settings, int sequence,
                    String id, String defaultTitle, String defaultImageType, String defaultImage,
                    boolean available, Runnable action) {
        MenuButtonConfig config = settings.button(id, (sequence + 1) * 10, defaultImageType, defaultImage);
        if (!config.isEnabled()) return sequence + 1;
        if (!available && !config.isShowWhenUnavailable() && !config.isDisabled()) return sequence + 1;

        boolean disabled = config.isDisabled() || !available;
        String title = MenuButtonPolicy.nonBlank(config.getTitle(), defaultTitle);
        String imageType = MenuButtonPolicy.nonBlank(config.getImageType(), defaultImageType);
        String image = MenuButtonPolicy.nonBlank(config.getImage(), defaultImage);
        Runnable callback = action;
        if (disabled) {
            title = MenuButtonPolicy.nonBlank(config.getDisabledTitle(), title + " §c(Disabled)");
            imageType = MenuButtonPolicy.nonBlank(config.getDisabledImageType(), imageType);
            image = MenuButtonPolicy.nonBlank(config.getDisabledImage(), image);
            String message = config.getDisabledMessage();
            callback = () -> {
                if (message != null && !message.isBlank()) player.sendMessage(message);
            };
        }

        String validatedImageType = MenuButtonPolicy.parseImageType(imageType);
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
        button.setDisabled(disabled);
        int order = config.getSortOrder() == Integer.MIN_VALUE ? (sequence + 1) * 10 : config.getSortOrder();
        target.add(new OrderedButton(order, sequence, button));
        return sequence + 1;
    }

    private record OrderedButton(int order, int sequence, Button button) { }
}
