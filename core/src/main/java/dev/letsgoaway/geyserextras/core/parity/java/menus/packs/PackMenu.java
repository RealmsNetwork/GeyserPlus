package dev.letsgoaway.geyserextras.core.parity.java.menus.packs;

import static dev.letsgoaway.geyserextras.core.GeyserExtras.GE;

import dev.letsgoaway.geyserextras.core.ExtrasPlayer;
import dev.letsgoaway.geyserextras.core.menus.MenuButtonFactory;
import dev.letsgoaway.geyserextras.core.form.BedrockMenu;
import dev.letsgoaway.geyserextras.core.form.elements.Button;
import dev.letsgoaway.geyserextras.core.locale.BedrockLocale;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.geyser.api.pack.ResourcePack;
import org.geysermc.geyser.text.ChatColor;

import java.util.List;
import java.util.UUID;

public class PackMenu extends BedrockMenu {
    private boolean openingSubMenu = false;

    @Override
    public SimpleForm create(ExtrasPlayer player) {
        setTitle(GE.getConfig().menuTitle("packs", BedrockLocale.MENU.RESOURCE_PACKS));

        int order = 10;
        MenuButtonFactory.add(this, player, "packs.selected", order, ChatColor.BOLD + player.translate("pack.selected.title"), null, null, () -> player.sendForm(new PackMenu()));
        order += 10;

        List<UUID> selectedPacks = player.getPreferences().getSelectedPacks();
        for (UUID uuid : selectedPacks) {
            ResourcePack pack = PackLoader.PACKS.get(uuid);
            if (pack != null) {
                createPackButton(pack, player, order);
                order += 10;
            }
        }

        MenuButtonFactory.add(this, player, "packs.available", order, ChatColor.BOLD + player.translate("pack.available.title"), null, null, () -> player.sendForm(new PackMenu()));
        order += 10;

        for (var entry : PackLoader.PACKS.entrySet()) {
            if (!selectedPacks.contains(entry.getKey())) {
                createPackButton(entry.getValue(), player, order);
                order += 10;
            }
        }

        return super.create(player);
    }

    @Override
    public void onClose(ExtrasPlayer player) {
        super.onClose(player);
        if (!openingSubMenu && player.isPacksUpdated()) {
            player.getPreferences().save();
            player.reconnect();
        }
    }

    private void createPackButton(ResourcePack pack, ExtrasPlayer player, int defaultOrder) {
        MenuButtonFactory.add(this, player, "packs.pack", defaultOrder, pack.manifest().header().name(), null, null, () -> {
            openingSubMenu = true;
            player.sendForm(new PackManagerMenu(pack));
        });
    }
}
