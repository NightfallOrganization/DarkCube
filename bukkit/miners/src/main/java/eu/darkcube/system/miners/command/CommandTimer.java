package eu.darkcube.system.miners.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mojang.brigadier.arguments.IntegerArgumentType;

import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.commandapi.v3.Commands;
import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.player.Message;

public class CommandTimer extends CommandExecutor {

    public CommandTimer() {
        super("miners", "timer", new String[0], b -> b.then(Commands.argument("time", IntegerArgumentType.integer(1))
                .executes(context -> setTime(IntegerArgumentType.getInteger(context, "time"), context.getSource().asPlayer()))));
    }

    public static int setTime(int secs, Player sender) {
        switch (Miners.getGamephase()) {
            case 0:
                if (!Miners.getLobbyPhase().getTimer().isRunning())
                    Miners.getLobbyPhase().getTimer().start(secs * 1000);
                else
                    Miners.getLobbyPhase().getTimer().setEndTime(System.currentTimeMillis() + secs * 1000);
                break;
            case 1:
                if (Miners.getMiningPhase().getTimer().isRunning())
                    Miners.getMiningPhase().getTimer().setEndTime(System.currentTimeMillis() + secs * 1000);
                break;
            case 2:
                Bukkit.getWorld(Miners.PVP_WORLD_NAME).getWorldBorder().setSize(0, secs);
                if (Miners.getPVPPhase().getTimer().isRunning())
                    Miners.getPVPPhase().getTimer().setEndTime(System.currentTimeMillis() + secs * 1000);
                break;
            default:
                break;
        }
        Miners.sendTranslatedMessage(sender, Message.COMMAND_TIMER_CHANGED, secs);
        return 0;
    }

}
