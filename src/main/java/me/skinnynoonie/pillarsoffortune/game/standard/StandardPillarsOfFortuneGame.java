package me.skinnynoonie.pillarsoffortune.game.standard;

import me.skinnynoonie.pillarsoffortune.PillarsOfFortune;
import me.skinnynoonie.pillarsoffortune.game.PillarsOfFortuneGame;
import me.skinnynoonie.pillarsoffortune.util.BukkitEventBus;
import me.skinnynoonie.pillarsoffortune.util.BukkitTaskScheduler;
import me.skinnynoonie.pillarsoffortune.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public final class StandardPillarsOfFortuneGame implements PillarsOfFortuneGame {

    private final PillarsOfFortune pillarsOfFortune;
    private final BukkitEventBus eventBus;
    private final BukkitTaskScheduler taskScheduler;

    private final Set<UUID> players;
    private final Set<UUID> alivePlayers;
    private boolean started;
    private boolean ended;

    private final World gameWorld;
    private final List<Location> spawnLocations;
    private final Location spectatorSpawn;

    private final List<Consumer<PillarsOfFortuneGame>> onEndListeners;

    public StandardPillarsOfFortuneGame(
            PillarsOfFortune pillarsOfFortune,
            Set<UUID> players,
            World gameWorld,
            List<Location> spawnLocations,
            Location spectatorSpawn
    ) {
        this.pillarsOfFortune = pillarsOfFortune;
        this.eventBus = new BukkitEventBus(pillarsOfFortune);
        this.taskScheduler = new BukkitTaskScheduler(pillarsOfFortune);

        this.players = new HashSet<>(players);
        this.alivePlayers = new HashSet<>(players);
        this.started = false;
        this.ended = false;

        this.gameWorld = gameWorld;
        this.spawnLocations = spawnLocations;
        this.spectatorSpawn = spectatorSpawn;

        this.onEndListeners = new ArrayList<>();
    }

    @Override
    public void start() {
        if (this.started || this.ended) {
            throw new IllegalStateException("this game has already been started or it has ended already");
        }

        this.started = true;

        this.eventBus.subscribe(PlayerDeathEvent.class, event -> {
            Player player = event.getEntity();
            if (this.isInGameAlive(player)) {
                this.alivePlayers.remove(player.getUniqueId());
                event.setCancelled(true);
                player.setGameMode(GameMode.SPECTATOR);
                Messages.broadcast("<red>" + player.getName() + " has died.");
            }
        });

        this.taskScheduler.later(this::end, 20 * 60 * 3);

        this.scatterAndSetUpPlayers();

        Messages.broadcast("<green>The game has started.");
    }

    public void end() {
        Messages.broadcast("<yellow>Game ended!");

        this.dispose();
    }

    @Override
    public void handlePlayerJoin(Player player) {
        if (!this.isActive()) {
            throw new IllegalStateException("game has not started or ended already");
        }

        player.setGameMode(GameMode.SPECTATOR);
        player.teleport(this.spectatorSpawn);
    }

    @Override
    public void handlePlayerLeave(Player player) {
        if (!this.isActive()) {
            throw new IllegalStateException("game has not started or ended already");
        }

        if (!this.isInGameAlive(player)) {
            return;
        }

        this.alivePlayers.remove(player.getUniqueId());

        Messages.broadcast("<red>" + player.getName() + " has quit.");
    }

    @Override
    public void dispose() {
        if (this.ended) {
            return;
        }

        this.ended = true;
        this.runOnEndListeners();
        this.eventBus.dispose();
        this.taskScheduler.dispose();
        this.players.clear();
        this.alivePlayers.clear();
    }

    @Override
    public boolean isActive() {
        return this.started && !this.ended;
    }

    @Override
    public void onEnd(Consumer<PillarsOfFortuneGame> onEndListener) {
        this.onEndListeners.add(onEndListener);
    }

    @Override
    public Set<UUID> getPlayers() {
        return Collections.unmodifiableSet(this.players);
    }

    @Override
    public Set<UUID> getAlivePlayers() {
        return Collections.unmodifiableSet(this.alivePlayers);
    }

    @Override
    public World getWorld() {
        return this.gameWorld;
    }

    private void scatterAndSetUpPlayers() {
        Iterator<Location> spawnLocIterator = this.spawnLocations.iterator();
        for (UUID playerId : this.players) {
            Player player = Bukkit.getPlayer(playerId);
            if (player == null) {
                this.alivePlayers.remove(playerId);
                continue;
            }

            player.setGameMode(GameMode.SURVIVAL);
            player.setFoodLevel(20);

            PlayerInventory inventory = player.getInventory();
            inventory.clear();
            inventory.addItem(new ItemStack(Material.ELYTRA));

            if (!spawnLocIterator.hasNext()) {
                spawnLocIterator = this.spawnLocations.iterator();
            }
            player.teleport(spawnLocIterator.next());
        }
    }

    private boolean isInGameAlive(Player player) {
        return this.alivePlayers.contains(player.getUniqueId());
    }

    private void runOnEndListeners() {
        for (Consumer<PillarsOfFortuneGame> listener : this.onEndListeners) {
            try {
                listener.accept(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
