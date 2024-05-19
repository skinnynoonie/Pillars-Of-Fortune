package me.skinnynoonie.pillarsoffortune;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class PillarsOfFortuneGameStartCommand implements CommandExecutor {

    private final PillarsOfFortune pillarsOfFortune;

    public PillarsOfFortuneGameStartCommand(PillarsOfFortune pillarsOfFortune) {
        this.pillarsOfFortune = pillarsOfFortune;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) {
            return true;
        }

        try {
            this.pillarsOfFortune.startNewGame();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

}
