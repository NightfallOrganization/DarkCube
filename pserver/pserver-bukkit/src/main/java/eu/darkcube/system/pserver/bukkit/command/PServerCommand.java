package eu.darkcube.system.pserver.bukkit.command;

import org.bukkit.command.CommandSender;

public interface PServerCommand {

	boolean execute(CommandSender sender, String[] args);
	
}
