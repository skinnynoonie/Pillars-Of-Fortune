package me.skinnynoonie.pillarsoffortune.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public record NoWorldLocation(double x, double y, double z, float yaw, float pitch) {

    public static NoWorldLocation from(String string) {
        String[] components = string.split(",");
        if (components.length != 5) {
            throw new IllegalArgumentException("invalid wordless location given to parse: " + string);
        }

        double x = Double.parseDouble(components[0]);
        double y = Double.parseDouble(components[1]);
        double z = Double.parseDouble(components[2]);
        float yaw = Float.parseFloat(components[3]);
        float pitch = Float.parseFloat(components[4]);

        return new NoWorldLocation(x, y, z, yaw, pitch);
    }

    public Location withWorld(String worldName) {
        return this.withWorld(Bukkit.getWorld(worldName));
    }

    public Location withWorld(World world) {
        return new Location(world, this.x, this.y, this.z, this.yaw, this.pitch);
    }

}