package eu.darkcube.system.miners;

import org.bukkit.configuration.file.YamlConfiguration;

public class MinersConfig {

	public final int TEAM_COUNT;
	public final int TEAM_SIZE;
	public final int MAX_PLAYERS;
	public final int MIN_PLAYERS;

	public final int LOBBY_TIMER_DEFAULT;
	public final int LOBBY_TIMER_QUICK;

	public final int MINING_PHASE_DURATION;

	public final YamlConfiguration CONFIG;

	public MinersConfig() {
		CONFIG = Miners.getInstance().getConfig("config");
		TEAM_COUNT = CONFIG.getInt("teamCount");
		TEAM_SIZE = CONFIG.getInt("teamSize");
		MAX_PLAYERS = TEAM_COUNT * TEAM_SIZE;
		MIN_PLAYERS = CONFIG.getInt("minPlayers");

		LOBBY_TIMER_DEFAULT = CONFIG.getInt("lobby.timerDefault");
		LOBBY_TIMER_QUICK = CONFIG.getInt("lobby.timerQuick");

		MINING_PHASE_DURATION = CONFIG.getInt("mining.duration");
	}

}
