package eu.darkcube.minigame.woolbattle.command;

import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.util.StatsLink;
import eu.darkcube.system.commandapi.Command;

public class CommandDisableStats extends Command {

	public CommandDisableStats() {
		super(Main.getInstance(), "disablestats", new Command[0], "Deaktiviert die Stats");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!StatsLink.isStats()) {
			Main.getInstance().sendMessage("§cStats sind bereits deaktiviert!", sender);
		} else {
			StatsLink.enabled = false;
			Main.getInstance()
					.sendMessage("§aStats wurden deaktiviert! Alle weiteren Kills/Deaths/etc zählen nicht", sender);
		}
		return true;
	}
}
