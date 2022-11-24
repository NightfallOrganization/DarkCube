package eu.darkcube.system.miners;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

import eu.darkcube.system.DarkCubePlugin;
import eu.darkcube.system.commandapi.v3.CommandAPI;
import eu.darkcube.system.miners.command.CommandTest;
import eu.darkcube.system.miners.lobbyphase.Lobbyphase;
import eu.darkcube.system.miners.lobbyphase.listener.ListenerPlayerQuit;

public class Miners extends DarkCubePlugin {

	private static Miners instance;

	private static int gamephase = 0;

	private static Lobbyphase gamephaseLobby;

	private static YamlConfiguration config;

	public Miners() {
		instance = this;
	}

	@Override
	public void onLoad() {
		this.saveDefaultConfig("config");
		this.createConfig("config");
		config = this.getConfig("config");
	}

	@Override
	public void onEnable() {
		gamephaseLobby = new Lobbyphase();

		CommandAPI api = CommandAPI.getInstance();
		api.register(new CommandTest());

		registerListeners(new ListenerPlayerQuit());
	}

	public static Miners getInstance() {
		return instance;
	}

	public static YamlConfiguration getConfigFile() {
		return config;
	}

	public static int getGamephase() {
		return gamephase;
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

}
