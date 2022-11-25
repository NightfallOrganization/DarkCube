package eu.darkcube.system.miners;

import org.bukkit.configuration.file.YamlConfiguration;

public class MinersConfig {

	public final int TEAM_COUNT;
	public final int TEAM_SIZE;
	public final int MAX_PLAYERS;
	public final int MIN_PLAYERS;
	public final int TIMER_DEFAULT;
	public final int TIMER_QUICK;

	public final YamlConfiguration CONFIG;

	public MinersConfig() {
		CONFIG = Miners.getInstance().getConfig("config");
		TEAM_COUNT = CONFIG.getInt("teamCount");
		TEAM_SIZE = CONFIG.getInt("teamSize");
		MAX_PLAYERS = TEAM_COUNT * TEAM_SIZE;
		MIN_PLAYERS = CONFIG.getInt("minPlayers");
		TIMER_DEFAULT = CONFIG.getInt("timerDefault");
		TIMER_QUICK = CONFIG.getInt("timerQuick");
	}

}
