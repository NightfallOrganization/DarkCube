package de.dasbabypixel.prefixplugin;

import java.util.UUID;

import org.bukkit.entity.Player;

public class PrefixPlugin {

	private static PrefixPlugin plugin = new PrefixPlugin(Main.getPlugin());

	public static PrefixPlugin getApi() {
		return plugin;
	}
	
	private Main main;
	
	private PrefixPlugin(Main main) {
		this.main = main;
	}
	
	public String getName(Player p) {
		return main.getScoreboardManager().replacePlaceHolders(p, "%prefix%%name%%suffix%");
	}
	
	public void setSuffix(UUID uuid, String suffix) {
		main.getScoreboardManager().setSuffix(uuid, suffix);
	}
	
	public void setPrefix(UUID uuid, String prefix) {
		main.getScoreboardManager().setPrefix(uuid, prefix);
	}
	
	public String getPrefix(UUID uuid) {
		return main.getScoreboardManager().getPrefix(uuid);
	}
	
	public String getSuffix(UUID uuid) {
		return main.getScoreboardManager().getSuffix(uuid);
	}
}
