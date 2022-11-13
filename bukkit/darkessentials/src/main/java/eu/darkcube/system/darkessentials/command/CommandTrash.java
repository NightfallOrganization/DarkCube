package eu.darkcube.system.darkessentials.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.darkessentials.Main;

public class CommandTrash extends Command {

	public CommandTrash() {
		super(Main.getInstance(), "trash", new Command[0], "Öffnet einen Mülleimer.");
		setAliases("d_trash");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			Main.getInstance().sendMessage(Main.cFail()+"Du musst ein Spieler sein um diesen Command auszuführen!", sender);
			return true;
		}
		if (args.length == 0) {
			((Player) sender).openInventory(Bukkit.createInventory(null, 3 * 9, "§6Müllabfuhr <3"));
			return true;
		}
		return false;
	}
}
