package eu.darkcube.system.citybuild.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ActionBarTask extends BukkitRunnable {

	private final JavaPlugin plugin;
	private final CustomHealthManager healthManager;
	private final LevelXPManager levelXPManager;

	public ActionBarTask(JavaPlugin plugin, CustomHealthManager healthManager, LevelXPManager levelXPManager) {
		this.plugin = plugin;
		this.healthManager = healthManager;
		this.levelXPManager = levelXPManager;
	}

	@Override
	public void run() {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			int xp = (int) levelXPManager.getXP(player);
			int maxXp = levelXPManager.getXPForLevel(levelXPManager.getLevel(player) + 1);
			int level = levelXPManager.getLevel(player);
			int health = healthManager.getHealth(player);
			int maxHealth = healthManager.getMaxHealth(player);

			Component message = Component.text("H", TextColor.fromHexString("#4e5c24"))
					.append(Component.text("[").color(TextColor.fromHexString("#7e7e7e")))
					.append(Component.text(health).color(TextColor.fromHexString("#ff4c4c")))
					.append(Component.text("/").color(TextColor.fromHexString("#7e7e7e")))
					.append(Component.text(maxHealth).color(TextColor.fromHexString("#ff4c4c")))
					.append(Component.text("] [").color(TextColor.fromHexString("#7e7e7e")))
					.append(Component.text(level).color(TextColor.fromHexString("#ffaa00")))
					.append(Component.text("] [").color(TextColor.fromHexString("#7e7e7e")))
					.append(Component.text(xp).color(TextColor.fromHexString("#00ff00")))
					.append(Component.text("/").color(TextColor.fromHexString("#7e7e7e")))
					.append(Component.text(maxXp).color(TextColor.fromHexString("#00ff00")))
					.append(Component.text("]").color(TextColor.fromHexString("#7e7e7e")));

			player.sendActionBar(message);
		}
	}
}
