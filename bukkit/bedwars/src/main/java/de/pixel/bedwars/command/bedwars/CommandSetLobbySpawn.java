package de.pixel.bedwars.command.bedwars;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.util.Locations;
import eu.darkcube.system.commandapi.Command;

public class CommandSetLobbySpawn extends Command {

	public CommandSetLobbySpawn() {
		super(Main.getInstance(), "setLobbySpawn", new Command[0], "Setzt den Lobby-Spawn");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		((Player)sender).teleport(Locations.getNiceLocation(((Player) sender).getLocation()));
		Main.getInstance().getLobby().setSpawn(Locations.getNiceLocation(((Player) sender).getLocation()));
		sender.sendMessage("§7Spawn neugesetzt!");
		return true;
	}

}
