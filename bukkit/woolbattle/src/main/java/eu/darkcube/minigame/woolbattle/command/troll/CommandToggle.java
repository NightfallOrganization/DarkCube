package eu.darkcube.minigame.woolbattle.command.troll;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.StatsLink;
import eu.darkcube.system.commandapi.Command;

public class CommandToggle extends Command {

	public CommandToggle() {
		super(WoolBattle.getInstance(), "toggle", new Command[0], "Troll Toggle");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Set<Player> players = new HashSet<>();
		if (args.length >= 1) {
			for (String pn : args) {
				Player p = Bukkit.getPlayer(pn);
				if (p != null) {
					players.add(p);
				}
			}
		} else {
			if (sender instanceof Player) {
				players.add((Player) sender);
			}
		}
		for (Player p : players) {
			User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
			user.setTrollMode(!user.isTrollMode());
			StatsLink.enabled = false;
		}
		return true;
	}
}
