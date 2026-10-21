package dev.letsgoaway.geyserextras.core.parity.java.menus.serverlinks;

import static dev.letsgoaway.geyserextras.core.GeyserExtras.GE;

import dev.letsgoaway.geyserextras.core.ExtrasPlayer;
import dev.letsgoaway.geyserextras.core.menus.MenuButtonFactory;
import dev.letsgoaway.geyserextras.core.form.BedrockMenu;
import dev.letsgoaway.geyserextras.core.form.elements.Button;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.mcprotocollib.protocol.data.game.ServerLink;

import java.util.List;

public class ServerLinksMenu extends BedrockMenu {

    @Override
    public SimpleForm create(ExtrasPlayer player) {
        GeyserSession session = player.getSession();
        setTitle(GE.getConfig().menuTitle("server-links", player.translate("menu.server_links.title")));
        List<ServerLink> links = player.getServerLinksData().getServerLinks();
        for (ServerLink link : links) {
            String linkText = ServerLinksData.getLinkText(link, player);

            MenuButtonFactory.add(this, player, "server-links.link", 100, linkText, null, null, () -> {
                if (player.getPreferences().isPromptOnLinks()) {
                    player.sendForm(new LinkWarning(link));
                } else {
                    player.sendForm(new LinkMenu(link));
                }
            });
        }
        return super.create(player);
    }
}
