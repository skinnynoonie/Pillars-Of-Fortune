package me.skinnynoonie.pillarsoffortune.util;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public final class Messages {

    public static Component text(String message, String... messages) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        Component formattedMessage = miniMessage.deserialize(message);
        for (String lineMessage : messages) {
            formattedMessage = formattedMessage.appendNewline().append(miniMessage.deserialize(lineMessage));
        }

        return formattedMessage;
    }

    public static void broadcast(String message, String... messages) {
        Bukkit.broadcast(text(message, messages));
    }

    public static void send(Audience audience, String message, String... messages) {
        audience.sendMessage(text(message, messages));
    }

    private Messages() {
    }

}
