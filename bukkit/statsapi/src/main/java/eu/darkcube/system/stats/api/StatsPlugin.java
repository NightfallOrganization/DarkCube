package eu.darkcube.system.stats.api;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import eu.darkcube.system.Plugin;
import eu.darkcube.system.commandapi.CommandAPI;
import eu.darkcube.system.stats.api.Arrays.ConvertingRule;
import eu.darkcube.system.stats.api.command.CommandAStats;
import eu.darkcube.system.stats.api.command.CommandDStats;
import eu.darkcube.system.stats.api.command.CommandHStats;
import eu.darkcube.system.stats.api.command.CommandMStats;
import eu.darkcube.system.stats.api.command.CommandStats;
import eu.darkcube.system.stats.api.command.CommandStatsTop;
import eu.darkcube.system.stats.api.command.CommandWStats;
import eu.darkcube.system.stats.api.command.CommandYStats;
import eu.darkcube.system.stats.api.mysql.MySQL;

public class StatsPlugin extends Plugin implements Listener {

	private static StatsPlugin instance;

	private MySQL mysql;

	public StatsPlugin() {
		instance = this;
	}

	@Override
	public void onEnable() {

		saveDefaultConfig("mysql");
		createConfig("mysql");
		YamlConfiguration cfg = getConfig("mysql");
		mysql = new MySQL(cfg.getString("host"), cfg.getString("port"), cfg.getString("database"),
				cfg.getString("username"), cfg.getString("password"));
		mysql.connect();
		Bukkit.getPluginManager().registerEvents(this, this);

		Arrays.addConvertingRule(new ConvertingRule<Duration>() {
			@Override
			public Class<Duration> getConvertingClass() {
				return Duration.class;
			}

			@Override
			public String convert(Duration object) {
				return object.toString();
			}
		});

		CommandAPI.enable(this, CommandStatsTop.INSTANCE);

		CommandAPI.enable(this, new CommandStats());
		CommandAPI.enable(this, new CommandHStats());
		CommandAPI.enable(this, new CommandDStats());
		CommandAPI.enable(this, new CommandWStats());
		CommandAPI.enable(this, new CommandMStats());
		CommandAPI.enable(this, new CommandYStats());
		CommandAPI.enable(this, new CommandAStats());

	}

	@Override
	public String getCommandPrefix() {
		return "Stats";
	}

	public MySQL getMySQL() {
		return mysql;
	}

	public static StatsPlugin getInstance() {
		return instance;
	}
}