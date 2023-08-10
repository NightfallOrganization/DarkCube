package eu.darkcube.system.citybuild.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class KillMobsCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player){
			Player player = (Player) sender;
			if(player.hasPermission("killmobs.use")) {
				for(Entity e : player.getWorld().getEntities()) {
					if(!(e instanceof Player)) {
						e.remove();
					}
				}
				player.sendMessage("§7Alle Mobs wurden §aentfernt§7!");
			} else {
				player.sendMessage("§7Du hast keine Berechtigung, diesen Befehl auszuführen!");
			}
		}
		return true;
	}
}
