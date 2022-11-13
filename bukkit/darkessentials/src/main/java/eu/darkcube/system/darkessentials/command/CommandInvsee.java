package eu.darkcube.system.darkessentials.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.Argument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.Main;

public class CommandInvsee extends Command {

	public CommandInvsee() {
		super(Main.getInstance(), "invsee", new Command[0], "Zeigt das Inventar eines Spielers.",
				new Argument[] { new Argument("Spieler", "Der Spieler, dessen Inventar gezeigt werden soll.") });
		setAliases("d_invsee");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 0)
			return false;
		if (args.length > 1) {
			Main.getInstance().sendMessage(Main.cFail() + "Du darfst nur einen Spielernamen angeben!");
			return true;
		}
		if (!(sender instanceof Player)) {
			Main.getInstance().sendMessage(
					Main.cFail() + "Du bist kein Spieler, deshalb kannst du diesen Command nicht ausführen!");
			return true;
		}
		if (Bukkit.getPlayer(args[0]) != null) {
			((Player) sender).openInventory(Bukkit.getPlayer(args[0]).getInventory());
		} else {
			Main.getInstance()
					.sendMessage(new StringBuilder().append(Main.cFail()).append("Der Spieler §7\"").append(Main.cValue()).append(args[0])
							.append("§7\"").append(Main.cFail()).append(" konnte nicht gefunden werden!").toString(),
							sender);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(String[] args) {
		if (args.length == 1)
			return Main.getPlayersStartWith(args);
		return new ArrayList<>();
	}

}
