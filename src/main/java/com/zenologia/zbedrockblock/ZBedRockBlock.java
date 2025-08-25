package com.zenologia.zbedrockblock;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class ZBedRockBlock extends JavaPlugin {

    private static ZBedRockBlock instance;
    private Config cfg;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        reloadLocalConfig();

        Bukkit.getPluginManager().registerEvents(new LoginBlockListener(this), this);

        final Commands commands = new Commands(this);
        PluginCommand cmd = getCommand("zbedrockblock");
        if (cmd != null) {
            cmd.setExecutor(commands);
            cmd.setTabCompleter(commands);
        }

        getLogger().info("ZBedRockBlock enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("ZBedRockBlock disabled.");
    }

    public void reloadLocalConfig() {
        reloadConfig();
        this.cfg = new Config(
                getConfig().getBoolean("enabled", true),
                getConfig().getBoolean("ignore-ops", true),
                getConfig().getString("bedrock-prefix", "."),
                getConfig().getString("kick-message", "<red>Bedrock accounts aren’t allowed on this server.</red>"),
                getConfig().getBoolean("debug", false),
                getConfig().getConfigurationSection("messages") == null ? null : getConfig().getConfigurationSection("messages").getValues(false)
        );
        // normalize once
        if (this.cfg.prefix != null) this.cfg.prefix = this.cfg.prefix.trim();
    }

    public Config cfg() { return cfg; }
    public static ZBedRockBlock inst() { return instance; }

    public void debug(String msg) { if (cfg.debug) getLogger().info("[DEBUG] " + msg); }

    public Component parse(String raw) { return MessageParser.parse(raw); }

    @SuppressWarnings("unchecked")
    public String msg(String path, String def) {
        if (cfg.messages == null) return def;
        Object v = cfg.messages.get(path);
        return v instanceof String ? (String) v : def;
    }

    public static final class Config {
        public final boolean enabled;
        public final boolean ignoreOps;
        public String prefix;
        public final String kickMessage;
        public final boolean debug;
        public final java.util.Map<String, Object> messages;

        public Config(boolean enabled, boolean ignoreOps, String prefix, String kickMessage, boolean debug,
                      java.util.Map<String, Object> messages) {
            this.enabled = enabled;
            this.ignoreOps = ignoreOps;
            this.prefix = prefix;
            this.kickMessage = kickMessage;
            this.debug = debug;
            this.messages = messages;
        }
    }
}
