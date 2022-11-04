package de.dasbabypixel.prefixplugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.google.common.base.Joiner;

class CommandHandler implements TabExecutor {

	private Main main = Main.getPlugin();

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return new ArrayList<>();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String cmdname = command.getName();
		if (cmdname.equals("reloadprefixes")) {
			if (args.length != 0) {
				return false;
			}
			long millis = System.currentTimeMillis();
			sender.sendMessage(ChatColor.GREEN + "Reloading PrefixSystem...");
			Main.getPlugin().getScoreboardManager().reload();
			sender.sendMessage(
					ChatColor.GREEN + "Reload complete! (Took " + (System.currentTimeMillis() - millis) + "ms)");
			return true;
		} else if (cmdname.equals("addname")) {
			if (args.length >= 2) {
				String playername = args[0];
				String name = Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length));
				name = ChatColor.translateAlternateColorCodes('&', name);
				main.getScoreboardManager().names.put(playername, name);
				sender.sendMessage("New name for " + playername + ": " + name);
				main.cfg.set("names", main.getScoreboardManager().names);
				Main.getPlugin().saveConfig();
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (p.getName().equalsIgnoreCase(playername)) {
						p.setCustomName(name);
						p.setPlayerListName(name);
					}
				}
				return true;
			}
			return false;
		} else if (cmdname.equals("removenames")) {
			if (args.length == 1) {
				String playername = args[0];
				if (!main.getScoreboardManager().names.containsKey(playername)) {
					sender.sendMessage("Playername does not exist in names");
					return true;
				}
				main.getScoreboardManager().names.remove(playername);
				sender.sendMessage("Playername removed!");
				main.cfg.set("names", main.getScoreboardManager().names);
				Main.getPlugin().saveConfig();
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (p.getName().equalsIgnoreCase(playername)) {
						p.setCustomName(p.getName());
						p.setPlayerListName(p.getName());
					}
				}
				return true;
			}
			return false;
		} else if (cmdname.equals("listnames")) {
			for (String playername : main.getScoreboardManager().names.keySet()) {
				sender.sendMessage(playername + ": " + main.getScoreboardManager().names.get(playername));
			}
			return true;
		}
		return false;
	}

}
