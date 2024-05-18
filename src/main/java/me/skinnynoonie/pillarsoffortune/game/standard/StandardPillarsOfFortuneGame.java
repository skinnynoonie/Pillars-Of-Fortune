package me.skinnynoonie.pillarsoffortune.game.standard;

import me.skinnynoonie.pillarsoffortune.PillarsOfFortune;
import me.skinnynoonie.pillarsoffortune.game.PillarsOfFortuneGame;
import me.skinnynoonie.pillarsoffortune.game.PillarsOfFortuneSpectatorManager;
import me.skinnynoonie.pillarsoffortune.util.BukkitEventBus;
import me.skinnynoonie.pillarsoffortune.util.BukkitTaskScheduler;
import me.skinnynoonie.pillarsoffortune.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class StandardPillarsOfFortuneGame implements PillarsOfFortuneGame {

    private final PillarsOfFortune pillarsOfFortune;
    private final BukkitEventBus eventBus;
    private final BukkitTaskScheduler taskScheduler;

    private final PillarsOfFortuneSpectatorManager spectatorManager;

    private final Set<UUID> players;
    private final Set<UUID> alivePlayers;
    private boolean started;
    private boolean ended;

    private final UUID gameWorldId;
    private List<Location> spawnLocations;

    public StandardPillarsOfFortuneGame(
            PillarsOfFortune pillarsOfFortune,
            PillarsOfFortuneSpectatorManager spectatorManager,
            Set<UUID> players,
            UUID gameWorldId,
            List<Location> spawnLocations
    ) {
        this.pillarsOfFortune = pillarsOfFortune;
        this.eventBus = new BukkitEventBus(pillarsOfFortune);
        this.taskScheduler = new BukkitTaskScheduler(pillarsOfFortune);

        this.spectatorManager = spectatorManager;

        this.players = new HashSet<>(players);
        this.alivePlayers = new HashSet<>(players);
        this.started = false;
        this.ended = false;

        this.gameWorldId = gameWorldId;
        this.spawnLocations = spawnLocations;
    }

    @Override
    public void start() {
        if (this.started || this.ended) {
            throw new IllegalStateException("this game has already been started or it has ended already");
        }

        this.started = true;

        for (UUID playerId : this.players) {
            Player player = Bukkit.getPlayer(playerId);

            if (player == null) {
                this.alivePlayers.remove(playerId);
                continue;
            }

            PlayerInventory inventory = player.getInventory();
            inventory.clear();
            inventory.addItem(new ItemStack(Material.ELYTRA));
        }

        this.eventBus.subscribe(PlayerDeathEvent.class, event -> {
            Player player = event.getEntity();
            if (!this.isInGameAlive(player)) {
                return;
            }

            this.alivePlayers.remove(player.getUniqueId());
            this.spectatorManager.setSpectator(player);

            String deathMessage = "<red>" + player.getName() + " has died.";
            Bukkit.broadcast(Messages.text(deathMessage));
        });

        final int TWO_MINUTES_TICKS = 20 * 60 * 2;
        this.taskScheduler.later(this::end, TWO_MINUTES_TICKS);
    }

    public void end() {
        String endMessage = "<yellow>Game ended!";
        Bukkit.broadcast(Messages.text(endMessage));

        this.dispose();
    }

    @Override
    public void handlePlayerJoin(Player player) {
        if (!this.isActive()) {
            throw new IllegalStateException("game has not started or ended already");
        }

        this.spectatorManager.setSpectator(player);
    }

    @Override
    public void handlePlayerLeave(Player player) {
        if (!this.isActive()) {
            throw new IllegalStateException("game has not started or ended already");
        }

        this.spectatorManager.removeSpectator(player);

        if (!this.isInGameAlive(player)) {
            return;
        }

        this.alivePlayers.remove(player.getUniqueId());

        String disconnectMessage = "<red>" + player.getName() + " has quit.";
        Bukkit.broadcast(Messages.text(disconnectMessage));
    }

    @Override
    public void dispose() {
        this.ended = true;
        this.eventBus.dispose();
        this.taskScheduler.dispose();
        this.spectatorManager.dispose();
        this.players.clear();
        this.alivePlayers.clear();
    }

    @Override
    public boolean isActive() {
        return this.started && !this.ended;
    }

    @Override
    public Set<UUID> getPlayers() {
        return Collections.unmodifiableSet(this.players);
    }

    @Override
    public Set<UUID> getAlivePlayers() {
        return Collections.unmodifiableSet(this.alivePlayers);
    }

    private boolean isInGameAlive(Player player) {
        return this.alivePlayers.contains(player.getUniqueId());
    }

}
