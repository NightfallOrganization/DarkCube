package eu.darkcube.system.lobbysystem.listener;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.WeatherType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.PlayerInventory;

import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.pserver.PServerSupport;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.DataManager;
import eu.darkcube.system.lobbysystem.util.Item;

public class ListenerSettingsJoin extends BaseListener {

	public static ListenerSettingsJoin instance;

	public ListenerSettingsJoin() {
		instance = this;
	}

	@EventHandler
	public void handle(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		User user = UserWrapper.loadUser(p.getUniqueId());
		Lobby lobby = Lobby.getInstance();
		DataManager dataManager = lobby.getDataManager();

		Location spawn = dataManager.getSpawn();

		p.setGameMode(GameMode.SURVIVAL);
		if (!p.getAllowFlight())
			p.setAllowFlight(true);
		p.setCompassTarget(spawn.getBlock().getLocation());
		p.setExhaustion(0);
		p.setSaturation(0);
		p.setFireTicks(0);
		p.setMaxHealth(20);
		p.setHealth(20);
		p.setLevel(0);
		p.setPlayerWeather(WeatherType.CLEAR);
		p.setFoodLevel(20);
		p.setExp(1);
		if (!"NoSpawnTeleport".equals(e.getJoinMessage())) {
			p.teleport(dataManager.getUserPos(p.getUniqueId()));
//				p.teleport(spawn);
		}
		PlayerInventory inv = p.getInventory();
		inv.clear();
		inv.setItem(0, Item.INVENTORY_COMPASS.getItem(user));
		inv.setItem(1, Item.INVENTORY_SETTINGS.getItem(user));
		inv.setItem(4, Item.byGadget(user.getGadget()).getItem(user));
		try {
			if (PServerSupport.isSupported()) {
				inv.setItem(6, Item.PSERVER_MAIN_ITEM.getItem(user));
			}
		} catch (Exception ex) {
		}

		inv.setItem(7, Item.INVENTORY_GADGET.getItem(user));
		inv.setItem(8, Item.INVENTORY_LOBBY_SWITCHER.getItem(user));

		user.playSound(Sound.FIREWORK_LAUNCH, .5F, 1);
	}
}
