package eu.darkcube.system.citybuild.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BackpackListener implements Listener {
	private final HashMap<UUID, Inventory> playerInventories = new HashMap<>();
	private final CustomItemManager itemManager;

	public BackpackListener(CustomItemManager itemManager) {
		this.itemManager = itemManager;
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();

		if (item.isSimilar(itemManager.getCustomFireworkStar())) {
			event.setCancelled(true);

			Inventory inventory = playerInventories.computeIfAbsent(player.getUniqueId(), k -> Bukkit.createInventory(player, 18, ChatColor.BLUE + "Backpack"));
			player.openInventory(inventory);
		}
	}

	public void saveInventories() {
		File file = new File("inventories.yml");
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

		for (Map.Entry<UUID, Inventory> entry : playerInventories.entrySet()) {
			UUID uuid = entry.getKey();
			Inventory inventory = entry.getValue();
			List<ItemStack> itemList = Arrays.asList(inventory.getContents());

			config.set(uuid.toString(), itemList);
		}

		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadInventories() {
		File file = new File("inventories.yml");
		if (!file.exists()) {
			return;
		}

		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

		for (String uuidString : config.getKeys(false)) {
			UUID uuid = UUID.fromString(uuidString);
			List<?> itemList = config.getList(uuidString);

			Inventory inventory = Bukkit.createInventory(null, 18, ChatColor.BLUE + "§f\uDAFF\uDFEFḀ");
			if (itemList != null) {
				for (int i = 0; i < Math.min(itemList.size(), 18); i++) {
					inventory.setItem(i, (ItemStack) itemList.get(i));
				}
			}

			playerInventories.put(uuid, inventory);
		}
	}
}
