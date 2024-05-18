package me.skinnynoonie.pillarsoffortune;

import org.bukkit.plugin.java.JavaPlugin;

public final class PillarsOfFortune extends JavaPlugin {

    private static PillarsOfFortune instance;

    @Override
    public void onEnable() {
        instance = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static PillarsOfFortune getInstance() {
        return instance;
    }

}
