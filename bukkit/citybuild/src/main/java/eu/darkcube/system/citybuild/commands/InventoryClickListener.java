package eu.darkcube.system.citybuild.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Random;

public class InventoryClickListener implements Listener {

	private static final Random RANDOM = new Random();
	private static final int MAX_WORLD_COORDINATE = 5000; // Changed from 30000000 to 5000
	private static final int MIN_WORLD_HEIGHT = 50; // Added this new constant
	private static final int MAX_WORLD_HEIGHT = 150; // Changed from 256 to 150

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getView().getTitle().equals("§f\uDAFF\uDFEFḇ")) {
			event.setCancelled(true);

			// Prüfen, ob das geklickte Item das Plot-Item ist
			if (event.getCurrentItem().getType() == Material.DEEPSLATE_TILE_SLAB &&
					event.getCurrentItem().getItemMeta().getDisplayName().equals("§6Plot")) {
				Player player = (Player) event.getWhoClicked();
				player.performCommand("plot home");
				player.sendMessage("§7Du wurdest zu deinem §6Plot §7teleportiert!");
			}

			// Prüfen, ob das geklickte Item das Spawn-Item ist
			else if (event.getCurrentItem().getType() == Material.NETHER_STAR &&
					event.getCurrentItem().getItemMeta().getDisplayName().equals("§bSpawn")) {
				Player player = (Player) event.getWhoClicked();
				player.performCommand("spawn");
			}

			// Prüfen, ob das geklickte Item das Nether-Item ist
			else if (event.getCurrentItem().getType() == Material.CRIMSON_NYLIUM &&
					event.getCurrentItem().getItemMeta().getDisplayName().equals("§4Nether")) {
				// Teleportiert den Spieler zu zufälligen Koordinaten im Nether
				Player player = (Player) event.getWhoClicked();
				int x = RANDOM.nextInt(MAX_WORLD_COORDINATE * 2) - MAX_WORLD_COORDINATE;
				int y = RANDOM.nextInt(MAX_WORLD_HEIGHT - MIN_WORLD_HEIGHT + 1) + MIN_WORLD_HEIGHT;
				int z = RANDOM.nextInt(MAX_WORLD_COORDINATE * 2) - MAX_WORLD_COORDINATE;
				World nether = Bukkit.getServer().getWorld("nether");
				if (nether != null) {
					player.teleport(new Location(nether, x, y, z));
					player.sendMessage("§7Du wurdest in den §4Nether §7teleportiert!");
				}
			}

			// Prüfen, ob das geklickte Item das Farmworld-Item ist
			else if (event.getCurrentItem().getType() == Material.GRASS_BLOCK &&
					event.getCurrentItem().getItemMeta().getDisplayName().equals("§aFarmworld")) {
				// Teleportiert den Spieler zu zufälligen Koordinaten in der Farmwelt
				Player player = (Player) event.getWhoClicked();
				int x = RANDOM.nextInt(MAX_WORLD_COORDINATE * 2) - MAX_WORLD_COORDINATE;
				int y = RANDOM.nextInt(MAX_WORLD_HEIGHT - MIN_WORLD_HEIGHT + 1) + MIN_WORLD_HEIGHT;
				int z = RANDOM.nextInt(MAX_WORLD_COORDINATE * 2) - MAX_WORLD_COORDINATE;
				World farmworld = Bukkit.getServer().getWorld("farmworld");
				if (farmworld != null) {
					player.teleport(new Location(farmworld, x, y, z));
					player.sendMessage("§7Du wurdest in die §aFarmworld §7teleportiert!");
				}
			}
		}
	}
}
