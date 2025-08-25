package com.zenologia.zbedrockblock;

import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class Commands implements TabExecutor {

    private final ZBedRockBlock plugin;

    public Commands(ZBedRockBlock plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("zbedrockblock.admin")) {
            sender.sendMessage(color(plugin.msg("no-permission", "&cYou don't have permission.")));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(color("&e/" + label + " reload &7- Reload config"));
            sender.sendMessage(color("&e/" + label + " test <name> &7- Check if a name would be blocked"));
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "reload" -> {
                plugin.reloadLocalConfig();
                sender.sendMessage(color(plugin.msg("reload-success", "&aZBedRockBlock config reloaded.")));
                return true;
            }
            case "test" -> {
                if (args.length < 2) {
                    sender.sendMessage(color(plugin.msg("usage-test", "&cUsage: /" + label + " test <name>")));
                    return true;
                }
                String name = args[1];
                var cfg = plugin.cfg();
                boolean match = name.toLowerCase(Locale.ROOT)
                        .startsWith((cfg.prefix == null ? "" : cfg.prefix.toLowerCase(Locale.ROOT)));

                String key = match ? "test-blocked" : "test-allowed";
                String def = match
                        ? "&eTest for &f%name%&e: &cBLOCKED (would be kicked)"
                        : "&eTest for &f%name%&e: &aALLOWED";

                sender.sendMessage(color(plugin.msg(key, def).replace("%name%", name)));

                if (match) {
                    Component preview = plugin.parse(cfg.kickMessage);
                    sender.sendMessage(color(plugin.msg("test-header", "&7Kick preview (as seen by player/proxy):")));
                    if (sender instanceof Player p) p.sendMessage(preview);
                    else sender.sendMessage(color("&f" + ChatColor.stripColor(ComponentToPlain.toPlain(preview))));
                }
                return true;
            }
            default -> {
                sender.sendMessage(color("&cUnknown subcommand."));
                return true;
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("zbedrockblock.admin")) return List.of();
        if (args.length == 1) {
            return partial(Arrays.asList("reload", "test"), args[0]);
        }
        if (args.length == 2 && "test".equalsIgnoreCase(args[0])) {
            return List.of("BedrockSteve", "._Example");
        }
        return List.of();
    }

    private static List<String> partial(List<String> base, String token) {
        String t = token.toLowerCase(Locale.ROOT);
        List<String> out = new ArrayList<>();
        for (String s : base) if (s.toLowerCase(Locale.ROOT).startsWith(t)) out.add(s);
        return out;
    }

    private static String color(String s) { return ChatColor.translateAlternateColorCodes('&', s); }
}
