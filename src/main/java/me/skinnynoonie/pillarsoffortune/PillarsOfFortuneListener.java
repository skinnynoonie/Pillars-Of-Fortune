package me.skinnynoonie.pillarsoffortune;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PillarsOfFortuneListener implements Listener {

    private final PillarsOfFortune pillarsOfFortune;

    public PillarsOfFortuneListener(PillarsOfFortune pillarsOfFortune) {
        this.pillarsOfFortune = pillarsOfFortune;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onConnect(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setGameMode(GameMode.SPECTATOR);
        this.pillarsOfFortune.getCurrentGame().ifPresent(game -> game.handlePlayerJoin(player));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onConnect(PlayerQuitEvent event) {
        this.pillarsOfFortune.getCurrentGame().ifPresent(game -> game.handlePlayerLeave(event.getPlayer()));
    }

}
