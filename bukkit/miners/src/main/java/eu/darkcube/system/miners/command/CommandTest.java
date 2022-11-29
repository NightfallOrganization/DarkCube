package eu.darkcube.system.miners.command;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.v3.CommandExecutor;
import eu.darkcube.system.miners.gamephase.miningphase.MiningGenerator;

public class CommandTest extends CommandExecutor {

	public CommandTest() {
		super("miners", "test", new String[0], b -> b.executes(context -> {
			return test(context.getSource().asPlayer());
		}));
	}

	public static int test(Player p) {
		MiningGenerator.generateOreVein(MiningGenerator.pickRandomBlock(), Material.IRON_ORE, 5);
		return 0;
	}

}
