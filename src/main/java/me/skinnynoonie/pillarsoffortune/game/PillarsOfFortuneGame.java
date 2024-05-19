package me.skinnynoonie.pillarsoffortune.game;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public interface PillarsOfFortuneGame {

    void start();

    void handlePlayerJoin(Player player);

    void handlePlayerLeave(Player player);

    void dispose();

    boolean isActive();

    void onEnd(Consumer<PillarsOfFortuneGame> onEndListener);

    Set<UUID> getPlayers();

    Set<UUID> getAlivePlayers();

    World getWorld();

}
