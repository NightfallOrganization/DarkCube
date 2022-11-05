package eu.darkcube.system.lobbysystem.command.lobbysystem;

import org.bukkit.command.CommandSender;

import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.util.SkullCache;

public class CommandShowSkullCache extends Command {

	public CommandShowSkullCache() {
		super(Lobby.getInstance(), "showSkullCache", new Command[0], "Zeigt den SkullCache an!");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		sender.sendMessage("SkullCache (" + SkullCache.cache.size() + "): ");
		SkullCache.cache.entrySet().forEach(e -> {
			sender.sendMessage(" - " + SkullCache.getCachedItem(e.getKey()).getItemMeta().getDisplayName());
		});
		return true;
	}
}
