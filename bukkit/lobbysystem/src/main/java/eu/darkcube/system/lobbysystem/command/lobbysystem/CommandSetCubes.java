package eu.darkcube.system.lobbysystem.command.lobbysystem;

import java.math.BigInteger;
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

public class CommandSetCubes extends Command {

	public CommandSetCubes() {
		super(Lobby.getInstance(), "setCubes", new Command[0], "Setzt die Cubes eines Spielers",
				new Argument("Spieler|UUID", "Der Spieler oder seine UUID"), new Argument("cubes", "Die Cubes"));
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
		if (args.length == 2) {
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
			BigInteger coins = null;
			try {
				coins = new BigInteger(args[1]);
			} catch (Exception ex) {
				sender.sendMessage("§cDies ist keine Zahl!");
				return true;
			}
			boolean unload = false;
			User user = UserWrapper.getUser(uuid);
//			User user = UserWrapper.loadUser(uuid);
			if (user == null) {
				user = UserWrapper.loadUser(uuid);
				unload = true;
			}
			user.setCubes(coins);
			user.save();
			if (unload)
				UserWrapper.unloadUser(user);
			sender.sendMessage("§aDieser Spieler hat nun §5" + coins + "§a Cubes!");
			return true;
		}
		return false;
	}
}
