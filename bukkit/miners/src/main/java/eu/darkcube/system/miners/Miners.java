package eu.darkcube.system.miners;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.miners.command.CommandTest;
import eu.darkcube.system.miners.command.CommandTimer;
import eu.darkcube.system.miners.gamephase.lobbyphase.Lobbyphase;
import eu.darkcube.system.miners.listener.ListenerPlayerJoin;
import eu.darkcube.system.miners.listener.ListenerPlayerLogin;
import eu.darkcube.system.miners.listener.ListenerPlayerQuit;
import eu.darkcube.system.miners.player.PlayerManager;
import eu.darkcube.system.miners.player.TeamManager;

public class Miners extends DarkCubePlugin {

	private static Miners instance;

	private static int gamephase = 0;

	private static PlayerManager playerManager;
	private static TeamManager teamManager;

	private static Lobbyphase gamephaseLobby;

	private static MinersConfig minersConfig;

	public Miners() {
		instance = this;
	}

	@Override
	public void onLoad() {
		this.saveDefaultConfig("config");
		this.createConfig("config");
		minersConfig = new MinersConfig();
	}

	@Override
	public void onEnable() {
		playerManager = new PlayerManager();
		teamManager = new TeamManager();

		gamephaseLobby = new Lobbyphase();

		CommandAPI api = CommandAPI.getInstance();
		api.register(new CommandTest());
		api.register(new CommandTimer());

		registerListeners(new ListenerPlayerQuit(), new ListenerPlayerLogin(), new ListenerPlayerJoin());
	}

	@Override
	public void onDisable() {
		Bukkit.getOnlinePlayers().forEach(p -> p.kickPlayer("§cServer Stoppt"));
	}

	public static Miners getInstance() {
		return instance;
	}

	public static MinersConfig getMinersConfig() {
		return minersConfig;
	}

	public static int getGamephase() {
		return gamephase;
	}

	public static PlayerManager getPlayerManager() {
		return playerManager;
	}

	public static TeamManager getTeamManager() {
		return teamManager;
	}

	public static Lobbyphase getLobbyPhase() {
		return gamephaseLobby;
	}

	public static void initGamephaseMining() {
		if (gamephase != 0)
			return;
		gamephase = 1;
	}

	private static void registerListeners(Listener... listeners) {
		for (Listener l : listeners)
			Miners.getInstance().getServer().getPluginManager().registerEvents(l, Miners.getInstance());
	}

	public static ArrayList<Player> getOnlinePlayers() {
		ArrayList<Player> result = new ArrayList<>();
		Bukkit.getOnlinePlayers().forEach(t -> result.add(t));
		return result;
	}

}
