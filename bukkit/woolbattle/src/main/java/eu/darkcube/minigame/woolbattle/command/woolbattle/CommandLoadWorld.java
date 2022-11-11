package eu.darkcube.minigame.woolbattle.command.woolbattle;

import java.io.File;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import eu.darkcube.minigame.woolbattle.Main;
import eu.darkcube.minigame.woolbattle.command.CommandArgument;
import eu.darkcube.system.commandapi.Command;
import eu.darkcube.system.commandapi.SpacedCommand.SubCommand;

public class CommandLoadWorld extends SubCommand {

	public CommandLoadWorld() {
		super(Main.getInstance(), "loadWorld", new Command[0], "Nutzt eine Welt für WoolBattle", CommandArgument.WORLD);
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (args.length != 1)
				return false;
			String worldName = args[0];
			if (!new File(Main.getInstance().getServer().getWorldContainer(), worldName).exists()
					&& !new File(Main.getInstance().getServer().getWorldContainer().getParent(), worldName).exists()) {
				p.sendMessage("§cDiese Welt existiert nicht");
				return true;
			}
			YamlConfiguration cfg = Main.getInstance().getConfig("worlds");
			List<String> worlds = cfg.getStringList("worlds");
			if (worlds.contains(worldName)) {
				p.sendMessage("§cDiese Welt existiert bereits");
				return true;
			}
			worlds.add(worldName);
			cfg.set("worlds", worlds);
			Main.getInstance().saveConfig(cfg);
			Main.getInstance().loadWorld(worldName);
			p.sendMessage("§aWelt geladen");
		}
		return false;
	}

}
