/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.module.modules.flightchestplate;

import eu.darkcube.system.inventoryapi.item.EquipmentSlot;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.item.attribute.Attribute;
import eu.darkcube.system.inventoryapi.item.attribute.AttributeModifier;
import eu.darkcube.system.inventoryapi.item.attribute.Operation;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataTypes;
import eu.darkcube.system.vanillaaddons.VanillaAddons;
import eu.darkcube.system.vanillaaddons.event.ArmorEquipEvent;
import eu.darkcube.system.vanillaaddons.module.Module;
import eu.darkcube.system.vanillaaddons.module.modules.recipes.Recipe;
import eu.darkcube.system.vanillaaddons.util.Item;
import eu.darkcube.system.vanillaaddons.util.Item.Data;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRecipeDiscoverEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.inventory.SmithingRecipe;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FlightChestplateModule implements Listener, Module {
	private static final int MAX_SPEED = 20;
	private static final float MIN_FLY_SPEED = 0.02F;
	private static final float MAX_FLY_SPEED = 0.1F;
	public final NamespacedKey STORAGE_KEY;
	private final VanillaAddons addons;
	private final Key SPEED_KEY;
	private final Recipe recipe = new Recipe(Item.FLIGHT_CHESTPLATE,
			Recipe.shaped("flight_chestplate", 0, "eee", "ene", "eee").i('e', Material.ELYTRA)
					.i('n', Material.NETHERITE_BLOCK));

	public FlightChestplateModule(VanillaAddons addons) {
		this.addons = addons;
		STORAGE_KEY = new NamespacedKey(addons, "flight");
		SPEED_KEY = new Key(addons, "speed");
	}

	@Override
	public void onEnable() {
		SmithingRecipe recipe =
				new SmithingRecipe(new NamespacedKey(addons, "flight_chestplate_upgrade"),
						new ItemStack(Material.AIR),
						new MaterialChoice(Material.LEATHER_CHESTPLATE),
						new MaterialChoice(Material.NETHER_STAR), true);
		Bukkit.addRecipe(recipe);
		Bukkit.getPluginManager().registerEvents(this, addons);
		Recipe.registerRecipe(this.recipe);
	}

	@Override
	public void onDisable() {
		Bukkit.removeRecipe(new NamespacedKey(addons, "flight_chestplate_upgrade"));
		Recipe.unregisterRecipe(recipe);
	}

	@EventHandler(ignoreCancelled = true)
	public void handle(PrepareItemCraftEvent event) {
		if (event.getRecipe() == null)
			return;
		ItemBuilder item = ItemBuilder.item(event.getRecipe().getResult());
		if (!canFly(item))
			return;
		item.persistentDataStorage().set(SPEED_KEY, PersistentDataTypes.INTEGER, 0);
		item.lore(speed(0));
		event.getInventory().setResult(item.build());
	}

	@EventHandler(ignoreCancelled = true)
	public void handle(PrepareSmithingEvent event) {
		SmithingInventory inv = event.getInventory();
		ItemBuilder first = inv.getItem(0) == null ? null : ItemBuilder.item(inv.getItem(0));
		if (first == null)
			return;
		if (!first.persistentDataStorage().has(Data.TYPE_KEY))
			return;
		if (first.persistentDataStorage().get(Data.TYPE_KEY, Data.TYPE) != Item.FLIGHT_CHESTPLATE)
			return;
		int speed = 0;
		if (!first.persistentDataStorage().has(SPEED_KEY)) {
			first.persistentDataStorage().set(SPEED_KEY, PersistentDataTypes.INTEGER, 0);
			first.lore(speed(0));
			inv.setItem(0, first.build());
		} else {
			speed = first.persistentDataStorage().get(SPEED_KEY, PersistentDataTypes.INTEGER);
		}

		Collection<AttributeModifier> modifiers =
				first.attributeModifiers(Attribute.GENERIC_ARMOR);
		for (AttributeModifier modifier : modifiers) {
			if (modifier.name().equals("flight_chestplate") && (modifier.amount() < 0
					|| modifier.operation() != Operation.ADD_NUMBER
					|| modifier.equipmentSlot() != EquipmentSlot.CHEST)) {
				first.removeAttributeModifiers(Attribute.GENERIC_ARMOR, modifier);
				first.attributeModifier(Attribute.GENERIC_ARMOR,
						new AttributeModifier(modifier.uniqueId(), modifier.name(), 0,
								Operation.ADD_NUMBER, EquipmentSlot.CHEST));
				inv.setItem(0, first.build());
			}
		}

		ItemBuilder second = inv.getItem(1) == null ? null : ItemBuilder.item(inv.getItem(1));
		if (second == null)
			return;
		if (second.material() != Material.NETHER_STAR)
			return;
		if (speed >= 20)
			return;
		first.persistentDataStorage().set(SPEED_KEY, PersistentDataTypes.INTEGER, speed + 1);
		List<Component> lore = new ArrayList<>(first.lore());
		lore.set(1, speed(speed + 1));
		first.setLore(lore);
		event.setResult(first.build());
	}

	private Component speed(int speed) {
		float maxSpeed = MAX_FLY_SPEED;
		float minSpeed = MIN_FLY_SPEED;
		float diff = maxSpeed - minSpeed;
		float currentSpeed = diff / MAX_SPEED * speed;
		int percent = Math.round(minSpeed / maxSpeed * 100 + currentSpeed / maxSpeed * 100);
		return Component.text("Geschwindigkeit: " + percent + "%").color(NamedTextColor.GRAY);
	}

	@EventHandler
	public void handle(PlayerRecipeDiscoverEvent event) {
		if (event.getRecipe().equals(new NamespacedKey(addons, "flight_chestplate_upgrade"))) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void handle(ArmorEquipEvent event) {
		ItemStack newItem = event.getNewArmorPiece();
		if (newItem != null) {
			ItemBuilder item = ItemBuilder.item(newItem);
			if (this.canFly(item)) {
				event.getPlayer().getPersistentDataContainer()
						.set(STORAGE_KEY, PersistentDataType.BYTE, (byte) 1);
				int speed = 0;
				if (item.persistentDataStorage().has(SPEED_KEY))
					speed = item.persistentDataStorage()
							.get(SPEED_KEY, PersistentDataTypes.INTEGER);
				update(event.getPlayer(), speed);
			} else if (event.getPlayer().getPersistentDataContainer()
					.has(STORAGE_KEY, PersistentDataType.BYTE)) {
				event.getPlayer().getPersistentDataContainer().remove(STORAGE_KEY);
				update(event.getPlayer(), 0);
			}
		} else if (event.getPlayer().getPersistentDataContainer().has(STORAGE_KEY)) {
			event.getPlayer().getPersistentDataContainer().remove(STORAGE_KEY);
			update(event.getPlayer(), 0);
		}
	}

	private void update(Player player, int speed) {
		if (player.getPersistentDataContainer().has(STORAGE_KEY)) {
			float fspeed = MIN_FLY_SPEED + ((MAX_FLY_SPEED - MIN_FLY_SPEED) / MAX_SPEED * speed);
			player.setAllowFlight(true);
			player.setFlySpeed(fspeed);
		} else {
			if (player.getGameMode() == GameMode.SURVIVAL
					|| player.getGameMode() == GameMode.ADVENTURE) {
				player.setAllowFlight(false);
			}
			player.setFlySpeed(0.05F);
		}
	}

	//	@EventHandler
	//	public void handle(InventoryCloseEvent event) {
	//		ItemStack chest = event.getPlayer().getInventory().getItem(EquipmentSlot.CHEST);
	//		ItemBuilder item = ItemBuilder.item(chest);
	//		if (!this.canFly(item) && event.getPlayer().getPersistentDataContainer()
	//				.has(STORAGE_KEY, PersistentDataType.BYTE)) {
	//			Player p = (Player) event.getPlayer();
	//			p.setAllowFlight(false);
	//			p.setFlySpeed(0.05F);
	//		}
	//	}

	@EventHandler
	public void handle(PlayerJoinEvent event) {
		if (event.getPlayer().getPersistentDataContainer()
				.has(STORAGE_KEY, PersistentDataType.BYTE)) {
			int speed = 0;
			ItemBuilder item = event.getPlayer().getInventory().getChestplate() == null
					? null
					: ItemBuilder.item(event.getPlayer().getInventory().getChestplate());
			if (item == null) {
				event.getPlayer().getPersistentDataContainer().remove(STORAGE_KEY);
			} else if (item.persistentDataStorage().has(SPEED_KEY)) {
				speed = item.persistentDataStorage().get(SPEED_KEY, PersistentDataTypes.INTEGER);
			}
			update(event.getPlayer(), speed);
		}
	}

	@EventHandler
	public void handle(PlayerChangedWorldEvent event) {
		if (event.getPlayer().getPersistentDataContainer()
				.has(STORAGE_KEY, PersistentDataType.BYTE)) {
			int speed = 0;
			ItemBuilder item = event.getPlayer().getInventory().getChestplate() == null
					? null
					: ItemBuilder.item(event.getPlayer().getInventory().getChestplate());
			if (item == null) {
				event.getPlayer().getPersistentDataContainer().remove(STORAGE_KEY);
			} else if (item.persistentDataStorage().has(SPEED_KEY)) {
				speed = item.persistentDataStorage().get(SPEED_KEY, PersistentDataTypes.INTEGER);
			}
			update(event.getPlayer(), speed);
		}
	}

	public boolean canFly(ItemBuilder item) {
		if (item == null) {
			return false;
		} else {
			return item.persistentDataStorage().has(Data.TYPE_KEY)
					&& item.persistentDataStorage().get(Data.TYPE_KEY, Data.TYPE)
					== Item.FLIGHT_CHESTPLATE;
		}
	}
}
