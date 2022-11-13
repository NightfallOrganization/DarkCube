package eu.darkcube.system.darkessentials.command;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.Main;
import eu.darkcube.system.darkessentials.util.NumbzUtils;

public class CommandNear extends Command {

	public CommandNear() {
		super(Main.getInstance(), "near", new Command[0], "Gibt den nächsten Spieler wieder.", new Argument[] {
				new Argument("Spieler", "Der Spieler, dessen nächster Spieler angezeit wird.", false) });
		setAliases("d_near");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				Main.sendMessagePlayernameRequired(sender);
				return true;
			}
			if (getClosestPlayer((Player) sender) != null) {
				Player closest = getClosestPlayer((Player) sender);
				Main.getInstance().sendMessage(
						new StringBuilder().append(Main.cConfirm()).append("Der nächste Spieler ist ")
								.append(Main.cValue()).append(closest.getName()).append(Main.cConfirm()).append(" (")
								.append(Main.cValue())
								.append((int) NumbzUtils.getDistance(((Player) sender).getLocation(),
										closest.getLocation()))
								.append(Main.cConfirm()).append(" Blöcke).").toString(),
						sender);
			} else {
				Main.getInstance().sendMessage(Main.cFail() + "Es befindet sich kein Spieler in deiner Nähe!", sender);
			}
			return true;
		}
		Set<String> unresolvedNames = new HashSet<>();
		for (String playerName : args) {
			if (Bukkit.getPlayer(playerName) != null) {
				Player current = Bukkit.getPlayer(playerName);
				if (getClosestPlayer(current) != null) {
					Player closest = getClosestPlayer(current);
					Main.getInstance()
							.sendMessage(new StringBuilder().append(Main.cConfirm()).append("Der nächste Spieler von ")
									.append(current.getName()).append(" ist ").append(Main.cValue())
									.append(closest.getName()).append(Main.cConfirm()).append(" (")
									.append(Main.cValue())
									.append((int) NumbzUtils.getDistance(current.getLocation(), closest.getLocation()))
									.append(Main.cConfirm()).append(" Blöcke).").toString(), sender);
				} else {
					Main.getInstance().sendMessage(
							Main.cFail() + "Es befindet sich kein Spieler in der Nähe von " + current.getName() + "!",
							sender);
				}
			} else {
				unresolvedNames.add(playerName);
			}
		}
		return true;
	}

	private Player getClosestPlayer(Player pl) {
		double dist = Double.MAX_VALUE;
		Player closest = null;
		for (Player current : Bukkit.getOnlinePlayers()) {
			if (NumbzUtils.getDistance(pl.getLocation(), current.getLocation()) < dist && !current.equals(pl)) {
				closest = current;
				dist = NumbzUtils.getDistance(pl.getLocation(), current.getLocation());
			}
		}
		return closest;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length != 0)
			return Main.getPlayersStartWith(args);
		return new ArrayList<>();
	}
}
