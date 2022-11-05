package eu.darkcube.system.lobbysystem.command.lobbysystem;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.UUIDManager;

public class CommandGetCubes extends Command {

	public CommandGetCubes() {
		super(Lobby.getInstance(), "getCubes", new Command[0], "Ruft die Cubes eines Spielers ab",
				new Argument("Spieler|UUID", "Der Spieler oder seine UUID"));
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1) {
			return Bukkit.getOnlinePlayers().stream().map(Player::getName)
					.filter(p -> p.toLowerCase().startsWith(args[0].toLowerCase())).collect(Collectors.toList());
		}
		return super.onTabComplete(args);
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 1) {
			UUID uuid = null;
			try {
				uuid = UUID.fromString(args[0]);
			} catch (Exception ex) {
			}
			if (uuid == null) {
				Player p = UUIDManager.getPlayer(args[0]);
				if (p == null) {
					sender.sendMessage("§cDieser Spieler bzw. UUID konnte nicht gefunden werden bzw ist nicht gültig!");
					return true;
				}
				uuid = p.getUniqueId();
			}
			boolean unload = false;
			User user = UserWrapper.getUser(uuid);
//			User user = UserWrapper.loadUser(uuid);
			if (user == null) {
				user = UserWrapper.loadUser(uuid);
				unload = true;
			}
			sender.sendMessage("§7Dieser Spieler hat " + user.getCubes() + " Cubes!");
			user.save();
			if (unload)
				UserWrapper.unloadUser(user);
			return true;
		}
		return false;
	}
}
