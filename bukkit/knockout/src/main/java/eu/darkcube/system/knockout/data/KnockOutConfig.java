package eu.darkcube.system.knockout.data;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import eu.darkcube.system.knockout.KnockOut;

public class KnockOutConfig {

	private String shopEntityName = "Shop";
	private int mapSwitchCooldownInSecond = 1200;

	private int lostCoinsOnDeath = 5;
	private int receivedCoinsOnKill = 10;

	private int killStreakCoins = 20;
	private double killStreakMultiplier = 2.0;

	public KnockOutConfig() {
		KnockOut.getInstance().saveDefaultConfig("config");
		KnockOut.getInstance().createConfig("config");
	}

	public String getShopEntityName() {
		return shopEntityName;
	}

	public int getMapSwitchCooldownInSecond() {
		return mapSwitchCooldownInSecond;
	}

	public int getLostCoinsOnDeath() {
		return lostCoinsOnDeath;
	}

	public int getReceivedCoinsOnKill() {
		return receivedCoinsOnKill;
	}

	public int getKillStreakCoins() {
		return killStreakCoins;
	}

	public double getKillStreakMultiplier() {
		return killStreakMultiplier;
	}

	public void update() {
		YamlConfiguration cfg = KnockOut.getInstance().reloadConfig("config");

		mapSwitchCooldownInSecond = cfg.getInt("mapSwitchCooldownSeconds");
		shopEntityName = ChatColor.translateAlternateColorCodes('&', cfg.getString("shopEntityName"));

		lostCoinsOnDeath = cfg.getInt("coins.lostOnDeath");
		receivedCoinsOnKill = cfg.getInt("coins.receivedOnKill");

		killStreakCoins = cfg.getInt("killstreak.coins");
		killStreakMultiplier = cfg.getDouble("killstreak.multiplier");
	}
}
