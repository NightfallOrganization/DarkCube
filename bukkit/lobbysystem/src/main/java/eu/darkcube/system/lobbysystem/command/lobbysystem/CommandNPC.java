package eu.darkcube.system.lobbysystem.command.lobbysystem;

import org.bukkit.command.CommandSender;

import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.command.lobbysystem.npc.CommandSetDailyReward;
import eu.darkcube.system.lobbysystem.command.lobbysystem.npc.CommandSetWoolBattle;

public class CommandNPC extends Command {

	public CommandNPC() {
		super(Lobby.getInstance(), "npc", new Command[] {
				new CommandSetWoolBattle(), new CommandSetDailyReward()
		}, "NPC Hauptcommand");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		return false;
	}
}
