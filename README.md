# 🌐 ZBedRockBlock

Blocks **Bedrock** players (identified by a **name prefix**) from joining a **backend Paper** server behind Velocity.  
No Floodgate hooks; purely name-prefix based. Built for **Paper 1.21.x** on **Java 21**.

## What it does (and doesn’t) do

- ✅ **Blocks** players whose Java username **starts with** the configured prefix (case-insensitive).
- ✅ **Kick reason** supports **MiniMessage** (e.g., `<red>…</red>`) **and** legacy `&` color codes.
- ✅ **Bypass** permission for exceptions (trusted players, staff alts, etc).
- ✅ **Admin commands** for reload and test.
- ❌ Doesn’t talk to Floodgate/Geyser APIs or Mojang auth; it’s lightweight and proxy-agnostic.
- ❌ Doesn’t manage your queue; if you use a queue plugin (e.g., ajQueue), configure it to **remove** players on this kick reason.

---

## Optional dependencies
- LuckPerms (or any permissions plugin)

---

## Installation

1. Place the JAR into the backend server’s `plugins/` folder.
2. Start the server once to generate `plugins/ZBedRockBlock/config.yml`.
3. Edit `config.yml` (see below), then `/zbedrockblock reload`.

> **Backend requirements:**  
> - Server is **behind Velocity** and typically **offline mode**.  
> - Make sure Paper is proxy-aware (standard Velocity modern forwarding setup).

---

## Configuration (`config.yml`)

```yaml
enabled: true

# Ops are *not* automatically exempt; they must have the bypass permission.
ignore-ops: true

# Single, exact prefix (case-insensitive). Example: "." or "bedrock_"
bedrock-prefix: "."

# Kick message supports MiniMessage and legacy & codes.
kick-message: "<red>Bedrock accounts aren’t allowed on this server.</red>"

# Logs match decisions to console if true.
debug: false

messages:
  reload-success: "&aZBedRockBlock config reloaded."
  no-permission: "&cYou don't have permission."
  usage-test: "&cUsage: /zbedrockblock test <name>"
  test-header: "&7Kick preview (as seen by player/proxy):"
  test-allowed: "&eTest for &f%name%&e: &aALLOWED"
  test-blocked: "&eTest for &f%name%&e: &cBLOCKED (would be kicked)"
```

### Tips
- **Choosing the prefix:**  
  Use the same prefix your Bedrock players receive on Java (e.g., `"."`, `"BD_"`, or `"bedrock_"`).
- **MiniMessage example:**  
  `kick-message: "<red><bold>No Bedrock accounts on this server.</bold></red>"`
- **Legacy example:**  
  `kick-message: "&c&lNo Bedrock accounts on this server."`

> **YAML gotcha:** Keep MiniMessage tags **inside** the quotes.  
> ✅ `"<red>text</red>"`  
> ❌ `"<red>text"</red>`

---

## Commands

| Command | Description | Permission |
|---|---|---|
| `/zbedrockblock reload` | Reloads the plugin configuration | `zbedrockblock.admin` |
| `/zbedrockblock test <name>` | Shows whether a name would be blocked; previews the kick message | `zbedrockblock.admin` |

**Tab-completion** is included.

---

## Permissions

| Node | Who should get it | Effect |
|---|---|---|
| `zbedrockblock.admin` | Admins/staff | Access to `/zbedrockblock reload` and `/zbedrockblock test` |
| `zbedrockblock.bypass` | Specific players | Allows joining even if their name matches the Bedrock prefix |

> To **whitelist** a specific Bedrock player, grant them `zbedrockblock.bypass`.

---

## ajQueue / Queue plugin note (important)

If you use **ajQueue** (or similar), make sure it **removes** players from the queue when they’re kicked by this backend.  
A simple way is to include a keyword in your kick message (e.g., “Bedrock”) and configure ajQueue’s “kick reasons” list to **match** that word. ajQueue will then pop them out of the queue instead of looping them.

Example approach:
- Keep the default kick message (`"Bedrock …"`) or any message containing a consistent keyword.
- Add that keyword to your queue plugin’s configuration where it looks for **contains** matches in backend kick messages.

---

## How it blocks

- Uses `PlayerLoginEvent` at **HIGHEST** priority:
  - Respects **permissions** cleanly (bypass/admin checks).
  - Sends a proper **kick reason** back to the proxy (so queue plugins can see it).
  - Avoids quirks sometimes seen with `AsyncPlayerPreLoginEvent` in offline backends.

**Match rule:**  
`playerName.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT))`

---

## Compatibility

- **Server:** Paper **1.21.x** (including 1.21.8)
- **Java:** 21
- **Proxy:** Works fine behind Velocity. No Velocity/Waterfall helper plugin required.
- **Other plugins:** Plays nicely with virtually everything; it only reads config and listens for `PlayerLoginEvent`.

---

## Common scenarios

**Block all Bedrock users across one backend:**
```yaml
bedrock-prefix: "."
kick-message: "<red>No Bedrock accounts on this server.</red>"
```

**Allow a specific Bedrock user (staff alt):**
- Grant them `zbedrockblock.bypass` via your permissions plugin.

**Temporarily disable the plugin without removing it:**
```yaml
enabled: false
```
Then `/zbedrockblock reload`.

---

## Troubleshooting

- **YAML error on reload:**  
  Most often a quoting issue in `kick-message`. Keep the entire message in quotes if you use `<tags>` or `&` codes.

- **Players looping in queue:**  
  The queue plugin isn’t configured to drop them on this kick reason. Add a **keyword** from your kick message to the queue plugin’s “kick reasons” list (or equivalent).

- **“429” Mojang warnings on startup:**  
  That’s Mojang rate-limiting for **profile lookups** (skins), usually triggered by skins/heads/cosmetics/tablist plugins—not this plugin. Consider enabling caches or disabling startup fetches in those plugins.

- **Ops getting blocked:**  
  This is by design. If you want ops exempt, either set `ignore-ops: false` (lets ops in even if they match) or grant ops `zbedrockblock.bypass`.

---

## Uninstall

1. Remove the JAR from `plugins/`.
2. (Optional) Delete `plugins/ZBedRockBlock/` if you don’t need the config anymore.
3. Restart the server.

---

## 🧑‍💻 Author

- **Zenologia**
- [GitHub Repository](https://github.com/Zenologia/ZBedRockBlock)
- [License](https://github.com/Zenologia/ZBedRockBlock/blob/main/LICENSE)
