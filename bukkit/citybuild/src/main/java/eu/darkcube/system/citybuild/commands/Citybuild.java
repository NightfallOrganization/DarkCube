package eu.darkcube.system.citybuild.commands;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.event.Listener;

public class Citybuild extends JavaPlugin {

	private static Citybuild instance;
	private PlayerOnlineTimeTracker playerOnlineTimeTracker;

	public Citybuild() {
		instance = this;
	}

	public static Citybuild getInstance() {
		return instance;
	}

	public PlayerOnlineTimeTracker getPlayerOnlineTimeTracker() {
		return playerOnlineTimeTracker;
	}

	@Override
	public void onDisable() {
		// Speichern der Inventare
		for (RegisteredListener registeredListener : HandlerList.getRegisteredListeners(this)) {
			Listener listener = registeredListener.getListener();
			if (listener instanceof BackpackListener) {
				((BackpackListener) listener).saveInventories();
				break;
			}
		}
	}

	@Override
	public void onEnable() {
		// ...

		instance.getCommand("gm").setExecutor(new GM());
		instance.getCommand("heal").setExecutor(new Heal());
		instance.getCommand("day").setExecutor(new Day());
		instance.getCommand("night").setExecutor(new Night());
		instance.getCommand("god").setExecutor(new God());
		instance.getCommand("fly").setExecutor(new Fly());
		instance.getCommand("feed").setExecutor(new Feed());
		instance.getCommand("max").setExecutor(new Max());
		instance.getCommand("trash").setExecutor(new Trash());
		instance.getCommand("warp").setExecutor(new Warp());
		instance.getCommand("spawn").setExecutor(new SpawnCommand());
		instance.getCommand("getitem").setExecutor(new GetItem());
		instance.getCommand("killmobs").setExecutor(new KillMobs());

		new ActionBarTask("Ⲓ", "Ⲕ").runTaskTimer(this, 0L, 3L);

		CustomItemManager itemManager = new CustomItemManager(this);
		itemManager.registerItems();

		// Erstelle den BackpackListener und lade die gespeicherten Inventare
		BackpackListener backpackListener = new BackpackListener(itemManager);
		backpackListener.loadInventories();

		// Registriere den BackpackListener
		getServer().getPluginManager().registerEvents(backpackListener, this);

		ScoreboardHandler scoreboardHandler = new ScoreboardHandler();
		playerOnlineTimeTracker = new PlayerOnlineTimeTracker(scoreboardHandler);
		for (Player player : getServer().getOnlinePlayers()) {
			scoreboardHandler.showPlayerLevelScoreboard(player);
		}

		Fly flyCommand = new Fly();
		getServer().getPluginManager().registerEvents(flyCommand, this);
		getCommand("fly").setExecutor(flyCommand);

		new RingOfHealingEffectApplier(this).runTaskTimer(this, 0L, 1L);

		getServer().getPluginManager().registerEvents(new NoFriendlyFireHandler(), this);
		getServer().getPluginManager().registerEvents(new TimeHandler(), this);
		getServer().getPluginManager().registerEvents(new MonsterLevelHandler(), this);
		getServer().getPluginManager().registerEvents(new SwiftSwordListener(), this);
		getServer().getPluginManager().registerEvents(new RingOfHealingSwapHandler(), this);
		getServer().getPluginManager().registerEvents(new RingOfHealingListener(this), this);
		getServer().getPluginManager().registerEvents(new EnderBagListener(), this);
		getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
		getServer().getPluginManager().registerEvents(new PlayerJoinListener(scoreboardHandler), this);
		getServer().getPluginManager().registerEvents(new PlayerLevelChangeListener(scoreboardHandler), this);
		getServer().getPluginManager().registerEvents(playerOnlineTimeTracker, this);

		// Hinzufügen der ScoreboardUpdater Task
		new ScoreboardUpdater(scoreboardHandler).runTaskTimer(this, 0L, 9000L); // Aktualisiert alle 20 Ticks, d.h. etwa alle Sekunden
	}
}
