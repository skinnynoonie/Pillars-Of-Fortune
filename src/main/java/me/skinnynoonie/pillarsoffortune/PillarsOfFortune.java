package me.skinnynoonie.pillarsoffortune;

import me.skinnynoonie.pillarsoffortune.game.PillarsOfFortuneGame;
import me.skinnynoonie.pillarsoffortune.game.standard.StandardPillarsOfFortuneGame;
import me.skinnynoonie.pillarsoffortune.util.Messages;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class PillarsOfFortune extends JavaPlugin {

    private static PillarsOfFortune instance;

    private PillarsOfFortuneGame currentGame;
    private ConfigurationSection config;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.config = this.getConfig();
        instance = this;

        super.getCommand("start").setExecutor(new PillarsOfFortuneGameStartCommand(this));
    }

    @Override
    public void onDisable() {
        try {
            this.disposeCurrentGame();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startNewGame() throws IOException {
        String spawnWorldName = this.config.getString("spawn-world");
        if (spawnWorldName == null) {
            throw new IllegalStateException("can not start a new game when the spawn world does not exist");
        }

        World spawnWorld = Bukkit.getWorld(spawnWorldName);
        if (spawnWorld == null) {
            throw new IllegalStateException("can not start a new game when the spawn world does not exist");
        }


        String gameWorldName = this.config.getString("world-template-name");
        if (gameWorldName == null) {
            throw new IllegalStateException("config template world does not exist");
        }

        Path pathToWorldTemplate = this.getDataFolder().toPath().resolve(gameWorldName);
        if (!Files.exists(pathToWorldTemplate)) {
            throw new IllegalStateException("config template world does not exist");
        }

        this.disposeCurrentGame();

        Path pathToWorldFolder = Bukkit.getWorldContainer().toPath();
        FileUtils.copyDirectoryToDirectory(pathToWorldTemplate.toFile(), pathToWorldFolder.toFile());

        World gameWorld = new WorldCreator(gameWorldName).createWorld();
        if (gameWorld == null) {
            throw new RuntimeException("something went wrong when loading a world");
        }

        Set<UUID> playerIds = Bukkit.getOnlinePlayers().stream().map(Entity::getUniqueId).collect(Collectors.toSet());

        this.currentGame = new StandardPillarsOfFortuneGame(
                this,
                playerIds,
                gameWorld.getUID(),
                Arrays.asList(new Location(gameWorld, 0, 100, 0))
        );

        this.currentGame.onEnd(game -> {
            game.getPlayers()
                    .stream()
                    .map(Bukkit::getPlayer)
                    .filter(Objects::nonNull)
                    .forEach(player -> player.teleport(new Location(spawnWorld, 0, 50, 0)));
        });

        Bukkit.getScheduler().runTaskLater(this, this.currentGame::start, 40);
    }

    private void disposeCurrentGame() throws IOException {
        if (this.currentGame == null) {
            return;
        }

        this.currentGame.dispose();
        World gameWorld = Bukkit.getWorld(this.currentGame.getWorldId());
        if (gameWorld == null) {
            return;
        }

        gameWorld.getPlayers().forEach(player -> player.kick(Messages.text("<red>World unloading!")));
        Bukkit.unloadWorld(gameWorld, false);
        Path pathToGameWorldFolder = Bukkit.getWorldContainer().toPath().resolve(gameWorld.getName());
        FileUtils.deleteDirectory(pathToGameWorldFolder.toFile());

        this.currentGame = null;
    }

    public Optional<PillarsOfFortuneGame> getCurrentGame() {
        return Optional.ofNullable(this.currentGame);
    }

    public static PillarsOfFortune getInstance() {
        return instance;
    }

}
