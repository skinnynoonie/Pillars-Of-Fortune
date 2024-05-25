package me.skinnynoonie.pillarsoffortune.config;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Objects;

public final class PillarsOfFortuneConfig {

    private final String spawnWorldName;
    private final NoWorldLocation spawnLocation;
    private final String templateGameWorldName;
    private final List<NoWorldLocation> gameWorldSpawnLocations;
    private final NoWorldLocation gameWorldSpectatorSpawnLocation;

    public PillarsOfFortuneConfig(ConfigurationSection config) {
        this.spawnWorldName = Objects.requireNonNull(config.getString("spawn-world-name"));
        this.spawnLocation = NoWorldLocation.from(config.getString("spawn-world-location"));
        this.templateGameWorldName = Objects.requireNonNull(config.getString("template-game-world-name"));

        this.gameWorldSpawnLocations = config.getStringList("game-world-spawn-locations").stream()
                .map(NoWorldLocation::from)
                .toList();

        if (this.gameWorldSpawnLocations.isEmpty()) {
            throw new IllegalStateException("no spawn locations for game world found");
        }

        this.gameWorldSpectatorSpawnLocation = NoWorldLocation.from(config.getString("game-world-spectator-spawn-location"));
    }

    public Location getSpawnLocation() {
        return this.spawnLocation.withWorld(this.spawnWorldName);
    }

    public List<Location> getGameWorldSpawns() {
        return this.gameWorldSpawnLocations.stream()
                .map(noWorldLoc -> noWorldLoc.withWorld(this.templateGameWorldName))
                .toList();
    }

    public Location getSpectatorSpawnLocation() {
        return this.gameWorldSpectatorSpawnLocation.withWorld(this.templateGameWorldName);
    }

    public String getSpawnWorldName() {
        return this.spawnWorldName;
    }

    public String getTemplateGameWorldName() {
        return this.templateGameWorldName;
    }

}
