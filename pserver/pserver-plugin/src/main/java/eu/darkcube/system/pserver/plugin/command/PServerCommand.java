package eu.darkcube.system.pserver.plugin.command;

import java.util.function.*;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.darkcube.system.pserver.plugin.inventory.PServerInventory;
import eu.darkcube.system.pserver.plugin.user.UserManager;

public class PServerCommand implements BiFunction<Object, String[], Boolean> {
	@Override
	public Boolean apply(Object osender, String[] args) {
		if (osender instanceof CommandSender) {
			CommandSender sender = (CommandSender) osender;
			if (sender instanceof Player) {
				new PServerInventory(UserManager.getInstance().getUser((Player) sender)).open();
				return true;
			}
		}
		return false;
	}
}
