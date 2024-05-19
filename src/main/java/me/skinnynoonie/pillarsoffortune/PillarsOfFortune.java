package me.skinnynoonie.pillarsoffortune;

import me.skinnynoonie.pillarsoffortune.config.PillarsOfFortuneConfig;
import me.skinnynoonie.pillarsoffortune.game.PillarsOfFortuneGame;
import me.skinnynoonie.pillarsoffortune.game.standard.StandardPillarsOfFortuneGame;
import me.skinnynoonie.pillarsoffortune.util.Messages;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class PillarsOfFortune extends JavaPlugin {

    private PillarsOfFortuneGame currentGame;
    private PillarsOfFortuneConfig config;

    @Override
    public void onEnable() {
        super.saveDefaultConfig();
        this.config = new PillarsOfFortuneConfig(super.getConfig());

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
        String gameWorldTemplateName = this.config.getTemplateGameWorldName();
        Path pathToWorldTemplate = super.getDataFolder().toPath().resolve(gameWorldTemplateName);
        if (!Files.exists(pathToWorldTemplate)) {
            throw new IllegalStateException("config template world does not exist");
        }

        this.disposeCurrentGame();
        FileUtils.copyDirectoryToDirectory(pathToWorldTemplate.toFile(), Bukkit.getWorldContainer().toPath().toFile());

        World gameWorld = new WorldCreator(gameWorldTemplateName).createWorld();
        if (gameWorld == null) {
            throw new RuntimeException("something went wrong when loading a world");
        }

        Set<UUID> playerIds = Bukkit.getOnlinePlayers().stream().map(Entity::getUniqueId).collect(Collectors.toSet());

        this.currentGame = new StandardPillarsOfFortuneGame(
                this,
                playerIds,
                gameWorld,
                this.config.getGameWorldSpawns(),
                this.config.getSpectatorSpawnLocation()
        );

        this.currentGame.onEnd(game -> {
            for (Player player : game.getWorld().getPlayers()) {
                player.teleport(this.config.getSpawnLocation());
            }
        });

        this.currentGame.start();
    }

    public Optional<PillarsOfFortuneGame> getCurrentGame() {
        return Optional.ofNullable(this.currentGame);
    }

    private void disposeCurrentGame() throws IOException {
        if (this.currentGame == null) {
            return;
        }

        this.currentGame.dispose();
        World gameWorld = this.currentGame.getWorld();

        gameWorld.getPlayers().forEach(player -> player.kick(Messages.text("<red>World unloading!")));
        Bukkit.unloadWorld(gameWorld, false);
        Path pathToGameWorldFolder = Bukkit.getWorldContainer().toPath().resolve(gameWorld.getName());
        FileUtils.deleteDirectory(pathToGameWorldFolder.toFile());

        this.currentGame = null;
    }

}
