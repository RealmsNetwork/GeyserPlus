package dev.letsgoaway.geyserextras.core.menus;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MenuButtonPolicyTest {
    @Test
    void acceptsSafeBedrockTexturePaths() {
        assertTrue(MenuButtonPolicy.validImage("PATH", "textures/ui/settings.png"));
        assertFalse(MenuButtonPolicy.validImage("PATH", "../outside.png"));
        assertFalse(MenuButtonPolicy.validImage("PATH", "textures\\..\\outside.png"));
        assertFalse(MenuButtonPolicy.validImage("PATH", "/absolute/path.png"));
    }

    @Test
    void acceptsOnlyHttpOrHttpsImageUrlsWithHosts() {
        assertTrue(MenuButtonPolicy.validImage("URL", "https://example.com/icon.png"));
        assertTrue(MenuButtonPolicy.validImage("URL", "http://example.com/icon.png"));
        assertFalse(MenuButtonPolicy.validImage("URL", "file:///etc/passwd"));
        assertFalse(MenuButtonPolicy.validImage("URL", "https:///missing-host.png"));
        assertFalse(MenuButtonPolicy.validImage("URL", "not a url"));
    }

    @Test
    void noneAndUnknownImageTypesDoNotProduceImages() {
        assertNull(MenuButtonPolicy.parseImageType("NONE"));
        assertNull(MenuButtonPolicy.parseImageType("not-a-real-type"));
        assertEquals("URL", MenuButtonPolicy.parseImageType(" url "));
    }

    @Test
    void fallbackAndLengthLimitsAreDeterministic() {
        assertEquals("Default title", MenuButtonPolicy.nonBlank("  ", "Default title"));
        assertEquals("Custom title", MenuButtonPolicy.nonBlank(" Custom title ", "Default title"));
        assertEquals("", MenuButtonPolicy.limit(null, 5));
        assertEquals("hello", MenuButtonPolicy.limit("hello world", 5));
    }
}
