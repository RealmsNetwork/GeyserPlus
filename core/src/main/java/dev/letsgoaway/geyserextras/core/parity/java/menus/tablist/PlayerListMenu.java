package dev.letsgoaway.geyserextras.core.parity.java.menus.tablist;

import static dev.letsgoaway.geyserextras.core.GeyserExtras.GE;

import dev.letsgoaway.geyserextras.core.ExtrasPlayer;
import dev.letsgoaway.geyserextras.core.menus.MenuButtonFactory;
import dev.letsgoaway.geyserextras.core.form.BedrockMenu;
import dev.letsgoaway.geyserextras.core.form.elements.Button;
import dev.letsgoaway.geyserextras.core.utils.IdUtils;
import org.cloudburstmc.protocol.bedrock.packet.ShowProfilePacket;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.geyser.text.ChatColor;
import org.geysermc.mcprotocollib.protocol.data.game.PlayerListEntry;

// TODO: Fix header and footer
public class PlayerListMenu extends BedrockMenu {
    private static String fixUnreadable(String text) {
        return text.replace(ChatColor.GRAY, ChatColor.DARK_GRAY);
    }

    @Override
    public SimpleForm create(ExtrasPlayer player) {
        setTitle(GE.getConfig().menuTitle("player-list", player.translate("key.playerlist")));
        setHeader(GE.getConfig().menuContent("player-list", player.getTabListData().getHeader() + "\n\n" + player.getTabListData().getFooter()));
        for (PlayerListEntry entry : player.getTabListData().getPlayers().values()) {
            if (entry.isListed()) {
                String name = fixUnreadable(createPlayerName(entry));
                Button configuredPlayerButton = MenuButtonFactory.create(player, "player-list.player", 100, name, FormImage.Type.URL, TabListData.getPlayerListHead(entry), () -> {
                    // Show the user profile menu if it is a bedrock player.
                    long XUID = IdUtils.getBedrockXUID(entry.getProfileId());
                    if (XUID != -1) {
                        ShowProfilePacket showProfilePacket = new ShowProfilePacket();
                        showProfilePacket.setXuid(String.valueOf(XUID));
                        player.getSession().sendUpstreamPacket(showProfilePacket);
                    } else {
                        // Otherwise refresh the menu
                        player.sendForm(new PlayerListMenu());
                    }
                });
                if (configuredPlayerButton != null) add(configuredPlayerButton);
            }
        }

        return super.create(player);
    }

    private String createPlayerName(PlayerListEntry entry) {
        StringBuilder builder = new StringBuilder(TabListData.getPlayerListName(entry));
        while (builder.length() < 30) {
            builder.append(" ");
        }
        builder.append(TabListData.getPingIcon(entry));
        return builder.toString();
    }
}
