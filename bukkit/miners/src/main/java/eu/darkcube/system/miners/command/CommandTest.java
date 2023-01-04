package eu.darkcube.system.miners.command;

import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.player.Message;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.miners.player.TNTManager;

public class CommandTest extends CommandExecutor {

    public CommandTest() {
        super("miners", "test", new String[0], b -> b.executes(context -> test(context.getSource().asPlayer())));
    }

    public static int test(Player p) {
        Miners.sendTranslatedMessage(p, Message.PLAYER_DIED, p.getName());
        return 0;
    }

}
