# Server-owner menu customization

GeyserPlus reads the `menus` section from `plugins/GeyserPlus/config.yml`. Changes are applied after reloading GeyserPlus/restarting Geyser; the current menu configuration is not a live editor.

## Example

```yaml
menus:
  title-overrides:
    main: "Network Menu"
    settings: "Player Settings"
    settings.ingame: "Quick Settings"
    packs: "Resource Pack Center"
    packs.manager: "Manage Resource Pack"
    packs.settings: "Pack Options"
    server-links: "Network Links"
    server-links.preview: "Link Details"
    player-list: "Online Players"

  main-content: "Choose what you want to open."

  buttons:
    reconnect:
      enabled: true
      sort-order: 5
      title: "Reconnect to Network"
      image-type: PATH
      image: "textures/ui/refresh_hover.png"

    advancements:
      enabled: true
      disabled: true
      sort-order: 40
      title: "Advancements"
      image-type: PATH
      image: "textures/ui/achievements.png"
      disabled-title: "§cAdvancements (Unavailable)"
      disabled-image-type: PATH
      disabled-image: "textures/ui/lock"
      disabled-message: "Advancements are currently disabled on this network."

    server-links:
      enabled: true
      disabled: true
      show-when-unavailable: true
      disabled-title: "§7Network Links (Coming Soon)"
      disabled-image-type: PATH
      disabled-image: "textures/ui/lock"
      disabled-message: "Network links are not configured yet."

    packs.remove:
      enabled: true
      title: "Remove Pack"
      image-type: URL
      image: "https://example.com/remove-pack.png"
```

## Supported title keys

`main`, `settings`, `settings.ingame`, `packs`, `packs.manager`, `packs.settings`, `server-links`, `server-links.preview`, `player-list`, and `settings.section.*` for settings sections. A missing or blank title keeps the game's translated/default title.

## Button override fields

Each item under `menus.buttons` accepts:

- `enabled`: `false` removes the button completely.
- `disabled`: `true` leaves a visible button but prevents its normal action from running. It may still send `disabled-message`.
- `show-when-unavailable`: on the main menu, allows the server-links/resource-packs button to stay visible when that feature has no available entries. Combined with `disabled: true`, this becomes a customizable unavailable placeholder.
- `sort-order`: lower integers appear earlier. Existing/default order is retained when the field is omitted.
- `title`: normal label override.
- `image-type`: `PATH`, `URL`, or `NONE`. `PATH` uses an in-game Bedrock texture path; `URL` must be reachable by the Bedrock client; `NONE` removes the icon.
- `image`: normal path or URL.
- `disabled-title`, `disabled-image-type`, `disabled-image`: separate appearance for disabled buttons. Blank values inherit the regular label/icon, except the disabled label defaults to a `(Disabled)` suffix.
- `disabled-message`: feedback text when the player clicks a disabled-looking button; blank makes it silent.

Button keys available for customization:

| Menu | Keys |
|---|---|
| Main menu | `swap-offhand`, `reconnect`, `settings`, `advancements`, `statistics`, `player-list`, `server-links`, `resource-packs` |
| Settings | `settings.reset` |
| Resource packs | `packs.selected`, `packs.available`, `packs.pack`, `packs.select`, `packs.up`, `packs.remove`, `packs.down` |
| Server links | `server-links.link`, `server-links.back` |
| Player list | `player-list.player` |

Dynamic entries such as individual resource packs, links, and players share one override key per entry type, so owners can standardize their labels/icons across those entries. A configured `title` replaces each dynamic label; leave it blank to preserve the per-pack/link/player name.

Bedrock's SimpleForm API does not expose a native disabled button state, so GeyserPlus implements disabled entries as visible buttons whose normal callback is suppressed. A custom disabled label/icon is how the disabled state is communicated visually.

## Content and translation overrides

Use `menus.content-overrides` to replace the content/header for a whole menu by the same menu key used under `title-overrides`. Use `menus.text-overrides` for a direct override of any string requested by `ExtrasPlayer.translate(...)` or `ExtrasPlayer.translateGE(...)`; keys are the translation keys used by GeyserPlus/Minecraft. This also lets owners replace many setting names, toggle labels, and action names without forking the language files.

```yaml
menus:
  content-overrides:
    main: "Welcome to the network. Choose an option below."
    settings: "Settings are saved to the player's preferences."
    settings.ingame: "Quick toggles for the current session."
    server-links.preview: "Use the QR code below to open this link."

  text-overrides:
    ge.menu: "§dNetwork Menu"
    ge.settings: "§dNetwork Settings"
    ge.reconnect: "Reconnect to the Network"
    menu.server_links.title: "Community Links"
```

Settings section buttons also support overrides. Their keys are `settings.section.video`, `settings.section.menu`, `settings.section.hud`, `settings.section.bindings`, `settings.section.debug`, and `settings.section.credits`. Those entries use the same button fields as other buttons. Debug is only shown when GeyserPlus debug mode is enabled.
