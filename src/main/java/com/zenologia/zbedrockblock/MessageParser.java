package com.zenologia.zbedrockblock;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.regex.Pattern;

final class MessageParser {
    private static final Pattern LEGACY_AMP = Pattern.compile("&[0-9a-fk-orx]", Pattern.CASE_INSENSITIVE);

    private MessageParser() {}

    static Component parse(String raw) {
        if (raw == null || raw.isEmpty()) return Component.empty();
        // Heuristic: if it looks like MiniMessage, prefer MiniMessage.
        boolean looksMini = raw.indexOf('<') != -1 && raw.indexOf('>') != -1;
        boolean looksLegacy = LEGACY_AMP.matcher(raw).find();

        if (looksMini) {
            try {
                return MiniMessage.miniMessage().deserialize(raw);
            } catch (Exception ignored) { /* fallback below */ }
        }
        if (looksLegacy) {
            try {
                return LegacyComponentSerializer.legacyAmpersand().deserialize(raw);
            } catch (Exception ignored) { /* try plain */ }
        }
        return Component.text(raw);
    }
}
