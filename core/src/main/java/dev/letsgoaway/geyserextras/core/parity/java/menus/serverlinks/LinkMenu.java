package dev.letsgoaway.geyserextras.core.parity.java.menus.serverlinks;

import dev.letsgoaway.geyserextras.core.ExtrasPlayer;
import dev.letsgoaway.geyserextras.core.menus.MenuButtonFactory;
import dev.letsgoaway.geyserextras.core.form.BedrockMenu;
import dev.letsgoaway.geyserextras.core.form.elements.Button;
import dev.letsgoaway.geyserextras.core.locale.BedrockLocale;
import static dev.letsgoaway.geyserextras.core.GeyserExtras.GE;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.mcprotocollib.protocol.data.game.ServerLink;

public class LinkMenu extends BedrockMenu {

    public ServerLink link;

    public LinkMenu(ServerLink link) {
        super();
        this.link = link;
    }

    @Override
    public SimpleForm create(ExtrasPlayer player) {
        String linkText = ServerLinksData.getLinkText(link, player);

        setTitle(GE.getConfig().menuTitle("server-links.preview", linkText));
        setHeader(GE.getConfig().menuContent("server-links.preview", link.link()));

        MenuButtonFactory.add(this, player, "server-links.back", 10, BedrockLocale.GUI.BACK, FormImage.Type.URL, getQRCode(link), () -> player.sendForm(new ServerLinksMenu()));
        return super.create(player);
    }

    private static String getQRCode(ServerLink link){
        return "https://api.qrserver.com/v1/create-qr-code/?data=" + link.link();
    }
}