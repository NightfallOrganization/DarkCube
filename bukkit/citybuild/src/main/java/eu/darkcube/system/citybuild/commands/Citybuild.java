package eu.darkcube.system.citybuild.commands;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Citybuild extends JavaPlugin {

	private static Citybuild instance;

	public Citybuild() {
		instance = this;
	}

	public static Citybuild getInstance() {
		return instance;
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void onEnable() {
		instance.getCommand("gm").setExecutor(new GM());
		instance.getCommand("heal").setExecutor(new Heal());
		instance.getCommand("day").setExecutor(new Day());
		instance.getCommand("night").setExecutor(new Night());
		instance.getCommand("god").setExecutor(new God());
		instance.getCommand("fly").setExecutor(new Fly());
		instance.getCommand("feed").setExecutor(new Feed());
		instance.getCommand("max").setExecutor(new Max());
		instance.getCommand("trash").setExecutor(new Trash());

		new ActionBarTask("Ⲓ", "Ⲕ").runTaskTimer(this, 0L, 3L);

		ScoreboardHandler scoreboardHandler = new ScoreboardHandler();
		for (Player player : getServer().getOnlinePlayers()) {
			scoreboardHandler.showPlayerLevelScoreboard(player);
		}

		getServer().getPluginManager().registerEvents(new PlayerJoinListener(scoreboardHandler), this);
	}
}
