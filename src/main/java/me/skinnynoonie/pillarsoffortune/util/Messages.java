package me.skinnynoonie.pillarsoffortune.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public final class Messages {

    public static Component text(String message, String... messages) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        Component formattedMessage = miniMessage.deserialize(message);
        for (String lineMessage : messages) {
            formattedMessage = formattedMessage.appendNewline().append(miniMessage.deserialize(lineMessage));
        }

        return formattedMessage;
    }

    private Messages() {
    }

}
