package com.zenologia.zbedrockblock;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Locale;

public final class LoginBlockListener implements Listener {

    private final ZBedRockBlock plugin;

    public LoginBlockListener(ZBedRockBlock plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(PlayerLoginEvent event) {
        final ZBedRockBlock.Config cfg = plugin.cfg();
        if (!cfg.enabled) return;
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) return;

        final var player = event.getPlayer();

        // Bypass permission first
        if (player.hasPermission("zbedrockblock.bypass")) {
            plugin.debug("Bypass: " + player.getName());
            return;
        }

        // If ignoreOps = false, treat ops as whitelisted
        if (!cfg.ignoreOps && player.isOp()) {
            plugin.debug("OP allowed by config: " + player.getName());
            return;
        }

        final String nameLower = player.getName().toLowerCase(Locale.ROOT);
        final String prefixLower = (cfg.prefix == null ? "" : cfg.prefix.toLowerCase(Locale.ROOT));

        final boolean match = !prefixLower.isEmpty() && nameLower.startsWith(prefixLower);
        plugin.debug("Check " + player.getName() + " vs prefix '" + cfg.prefix + "': " + match);

        if (match) {
            Component msg = plugin.parse(cfg.kickMessage);
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, msg);
        }
    }
}
