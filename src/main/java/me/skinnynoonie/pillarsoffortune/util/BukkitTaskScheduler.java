package me.skinnynoonie.pillarsoffortune.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public final class BukkitTaskScheduler {

    private final Plugin plugin;
    private final List<Integer> taskIds;

    public BukkitTaskScheduler(Plugin plugin) {
        this.plugin = plugin;
        this.taskIds = new ArrayList<>();
    }

    public void dispose() {
        this.taskIds.forEach(Bukkit.getScheduler()::cancelTask);
        this.taskIds.clear();
    }

    public void repeat(Runnable task, int periodTicks) {
        this.repeat(task, periodTicks, 0);
    }

    public void repeat(Runnable task, int periodTicks, int delayTicks) {
        int taskId = Bukkit.getScheduler().runTaskTimer(this.plugin, task, delayTicks, periodTicks).getTaskId();
        this.taskIds.add(taskId);
    }

    public void later(Runnable task, int delayTicks) {
        int taskId = Bukkit.getScheduler().runTaskLater(this.plugin, task, delayTicks).getTaskId();
        this.taskIds.add(taskId);
    }

}
