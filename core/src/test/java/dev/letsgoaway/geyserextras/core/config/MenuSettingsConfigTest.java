package dev.letsgoaway.geyserextras.core.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MenuSettingsConfigTest {
    @TempDir
    Path tempDir;

    @Test
    void loadsPartialMenuOverridesWithoutBreakingUnspecifiedButtons() throws Exception {
        Path configFile = tempDir.resolve("config.yml");
        Files.writeString(configFile, """
                menus:
                  title-overrides:
                    main: "Realms Network"
                  buttons:
                    reconnect:
                      enabled: false
                      sort-order: 73
                      disabled-title: "Reconnect unavailable"
                      disabled-message: ""
                """);

        GeyserExtrasConfig config = YamlConfigurationLoader.builder()
                .path(configFile)
                .build()
                .load()
                .get(GeyserExtrasConfig.class);

        assertNotNull(config);
        assertEquals("Realms Network", config.menuTitle("main", "default"));
        assertEquals("default", config.menuTitle("settings", "default"));

        GeyserExtrasConfig.MenuButtonConfig reconnect =
                config.menuSettings().button("reconnect", 20, "PATH", "textures/ui/refresh_hover.png");
        assertFalse(reconnect.isEnabled());
        assertEquals(73, reconnect.getSortOrder());
        assertEquals("Reconnect unavailable", reconnect.getDisabledTitle());
        assertEquals("", reconnect.getDisabledMessage());

        GeyserExtrasConfig.MenuButtonConfig settings =
                config.menuSettings().button("settings", 30, "PATH", "textures/ui/settings.png");
        assertTrue(settings.isEnabled());
        assertEquals(30, settings.getSortOrder());
        assertEquals("textures/ui/settings.png", settings.getImage());
    }

    @Test
    void missingMenuSectionUsesSafeDefaultsForLegacyConfigs() {
        GeyserExtrasConfig config = new GeyserExtrasConfig();

        assertEquals("fallback", config.menuTitle("main", "fallback"));
        assertEquals("", config.menuContent("main", ""));
        assertTrue(config.menuSettings().button(
                "reconnect", 20, "PATH", "textures/ui/refresh_hover.png").isEnabled());
    }
}
