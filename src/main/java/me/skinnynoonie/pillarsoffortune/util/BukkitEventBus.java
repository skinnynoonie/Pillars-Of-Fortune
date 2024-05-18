package me.skinnynoonie.pillarsoffortune.util;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

/**
 * A simple way to register an event without having to go through the class creation process.
 * <p>
 * Credit to <a href="https://github.com/IllusionTheDev">IllusionTheDev</a> for the idea.
 */
public final class BukkitEventBus implements Listener {

    private final Plugin plugin;

    public BukkitEventBus(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Registers a listener to Bukkit that has priority {@link EventPriority#NORMAL} and will ignore canceled events.
     *
     * @param eventClass The event of the class to listen for.
     * @param listenerConsumer The {@code Listener} that will handle the event.
     * @throws IllegalArgumentException If any argument is null.
     */
    public <T extends Event> void subscribe(Class<T> eventClass, Consumer<T> listenerConsumer) {
        this.subscribe(eventClass, listenerConsumer, EventPriority.NORMAL, true);
    }

    /**
     * Registers a listener to Bukkit with everything customizable.
     *
     * @param eventClass The event of the class to listen for.
     * @param listenerConsumer The "Listener" that will handle the event.
     * @param priority The priority of the event.
     * @param ignoreCancelled Whether to ignore already canceled events.
     * @throws IllegalArgumentException If any argument is null.
     */
    public <T extends Event> void subscribe(Class<T> eventClass, Consumer<T> listenerConsumer, EventPriority priority, boolean ignoreCancelled) {
        Bukkit.getPluginManager().registerEvent(eventClass, this, priority, (listener, event) -> {
            if (eventClass.isInstance(event)) {
                listenerConsumer.accept(eventClass.cast(event));
            }
        }, this.plugin, ignoreCancelled);
    }

    /**
     * Unregisters all listeners that were registered using this event bus.
     */
    public void dispose() {
        HandlerList.unregisterAll(this);
    }

    /**
     * Gets the plugin used to register events.
     *
     * @return The plugin associated with all events registered with this event bus.
     */
    public Plugin getPlugin() {
        return this.plugin;
    }

}
