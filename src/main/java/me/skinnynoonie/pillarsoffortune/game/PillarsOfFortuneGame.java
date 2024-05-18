package me.skinnynoonie.pillarsoffortune.game;

import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public interface PillarsOfFortuneGame {

    void start();

    void handlePlayerJoin(Player player);

    void handlePlayerLeave(Player player);

    void dispose();

    boolean isActive();

    Set<UUID> getPlayers();

    Set<UUID> getAlivePlayers();

}
