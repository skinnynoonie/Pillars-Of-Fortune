package me.skinnynoonie.pillarsoffortune.game;

import org.bukkit.entity.Player;

public interface PillarsOfFortuneSpectatorManager {

    void setSpectator(Player player);

    void removeSpectator(Player spectator);

    void dispose();

}
