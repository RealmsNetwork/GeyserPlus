package dev.letsgoaway.geyserextras.core.menus.settings.menus;

import static dev.letsgoaway.geyserextras.core.GeyserExtras.GE;

import dev.letsgoaway.geyserextras.core.ExtrasPlayer;
import dev.letsgoaway.geyserextras.core.menus.MenuButtonFactory;
import dev.letsgoaway.geyserextras.core.form.BedrockMenu;
import dev.letsgoaway.geyserextras.core.form.elements.Button;
import dev.letsgoaway.geyserextras.core.locale.BedrockLocale;
import dev.letsgoaway.geyserextras.core.menus.settings.Settings;
import dev.letsgoaway.geyserextras.core.menus.settings.sections.Section;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.geyser.session.GeyserSession;

import java.util.List;

public class SettingsMenu extends BedrockMenu {
    @Override

    public SimpleForm create(ExtrasPlayer player) {
        setTitle(GE.getConfig().menuTitle("settings", player.translateGE("ge.settings")));
        setHeader(GE.getConfig().menuContent("settings", player.translateGE("ge.settings.section.submit_warning")));
        GeyserSession session = player.getSession();
        List<Section> sections = Settings.list();

        for (Section section : sections) {
            section.create(this, session, player);
        }

        MenuButtonFactory.add(this, player, "settings.reset", 10000, BedrockLocale.CONTROLLER.RESET_TO_DEFAULT, null, null, () -> player.sendForm(new ResetModal()));

        return super.create(player);
    }
}
