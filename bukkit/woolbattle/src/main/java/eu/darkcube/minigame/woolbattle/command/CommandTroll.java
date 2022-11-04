package eu.darkcube.minigame.woolbattle.command;

import org.bukkit.command.CommandSender;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.command.troll.CommandSetPerk;
import eu.darkcube.minigame.woolbattle.command.troll.CommandSetPerkCooldown;
import eu.darkcube.minigame.woolbattle.command.troll.CommandSetPerkCost;
import eu.darkcube.minigame.woolbattle.command.troll.CommandToggle;
import eu.darkcube.minigame.woolbattle.command.troll.CommandVanish;
import eu.darkcube.system.commandapi.Command;

public class CommandTroll extends Command {

	public CommandTroll() {
		super(Main.getInstance(), "troll", new Command[] {
				new CommandSetPerkCooldown(), new CommandToggle(), new CommandSetPerkCost(), new CommandSetPerk(), new CommandVanish()
		}, "Troll Hauptcommand");
	}

	@Override
	public boolean execute(CommandSender arg0, String[] arg1) {
		return false;
	}

}
