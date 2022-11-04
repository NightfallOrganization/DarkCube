package de.pixel.bedwars.command;

import org.bukkit.command.CommandSender;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.state.Lobby;
import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;

public class CommandTimer extends Command {

	public CommandTimer() {
		super(Main.getInstance(), "timer", new Command[0], "Setzt den Timer",
				new Argument("timer", "Timer in Sekunden"));
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 1) {
			int timer = 0;
			if (Main.getInstance().getLobby().isActive()) {
				try {
					timer = Integer.parseInt(args[0]);
				} catch (Exception ex) {
					sender.sendMessage("§cUngültiger Wert: " + args[0]);
					return true;
				}
				Lobby.lobbyCountdown.setTimerSeconds(timer);
			} else {
				sender.sendMessage("§cNur während der Lobby-Phase möglich!");
			}
			return true;
		}
		return false;
	}
}
