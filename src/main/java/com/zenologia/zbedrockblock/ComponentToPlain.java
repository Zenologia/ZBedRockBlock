package com.zenologia.zbedrockblock;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

final class ComponentToPlain {
    private ComponentToPlain() {}
    static String toPlain(Component c) {
        return PlainTextComponentSerializer.plainText().serialize(c);
    }
}
