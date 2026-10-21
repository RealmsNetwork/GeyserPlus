package dev.letsgoaway.geyserextras.core.menus.settings.sections;

import dev.letsgoaway.geyserextras.core.ExtrasPlayer;
import dev.letsgoaway.geyserextras.core.form.BedrockForm;
import dev.letsgoaway.geyserextras.core.form.BedrockMenu;
import dev.letsgoaway.geyserextras.core.form.elements.Button;
import dev.letsgoaway.geyserextras.core.form.elements.Label;
import dev.letsgoaway.geyserextras.core.menus.settings.menus.SectionMenu;
import dev.letsgoaway.geyserextras.core.menus.MenuButtonFactory;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.geyser.session.GeyserSession;

import java.util.List;

import static dev.letsgoaway.geyserextras.core.GeyserExtras.GE;

public class Section {
    public void create(BedrockForm menu, GeyserSession session, ExtrasPlayer player, boolean includeHeader) {
        if (includeHeader) {
            String key = menuKey();
            List<String> header = getHeader(player);
            menu.add(new SectionLabel(GE.getConfig().menuTitle(key, header.get(0)),
                    GE.getConfig().menuContent(key, header.get(1))));
        }
        else {
            // Add padding because bedrock doesnt lmao
            menu.add(new Label(""));
        }
        build(menu, session, player);
    }

    public void create(BedrockMenu menu, GeyserSession session, ExtrasPlayer player) {
        String key = menuKey();
        List<String> header = getHeader(player);
        org.geysermc.cumulus.util.FormImage image = getImage();
        MenuButtonFactory.add(menu, player, key, menuOrder(), GE.getConfig().menuTitle(key, header.get(0)),
                image == null ? null : image.type(), image == null ? null : image.data(),
                () -> player.sendForm(new SectionMenu(this)));
    }

    public void build(BedrockForm menu, GeyserSession session, ExtrasPlayer player) {
    }

    public String menuKey() {
        String name = getClass().getSimpleName().replaceFirst("Section$", "");
        StringBuilder key = new StringBuilder("settings.section.");
        for (int i = 0; i < name.length(); i++) {
            char character = name.charAt(i);
            if (Character.isUpperCase(character) && i > 0) key.append('-');
            key.append(Character.toLowerCase(character));
        }
        return key.toString();
    }

    public int menuOrder() {
        return switch (getClass().getSimpleName()) {
            case "VideoSection" -> 10;
            case "MenuSection" -> 20;
            case "HudSection" -> 30;
            case "BindingsSection" -> 40;
            case "DebugSection" -> 50;
            case "CreditsSection" -> 60;
            default -> 100;
        };
    }

    public List<String> getHeader(ExtrasPlayer player) {
        return List.of("SectionTitle", "SectionDescription");
    }

    // Used in the GeyserExtras Menu Settings instead of the game settings as its easier to navigate on consoles
    public FormImage getImage() {
        return FormImage.of(FormImage.Type.PATH, "textures/ui/settings_glyph_color_2x.png");
    }
}
