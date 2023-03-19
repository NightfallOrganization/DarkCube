/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.module.modules.anvilmechanics;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.vanillaaddons.VanillaAddons;
import eu.darkcube.system.vanillaaddons.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AnvilMechanicsModule implements Module, Listener {
	private final VanillaAddons addons;
	private final Map<Enchantment, Integer> enchantmentLimitation = new HashMap<>();
	private final Multimap<Material, Material> repairMaterial = HashMultimap.create();

	public AnvilMechanicsModule(VanillaAddons addons) {
		this.addons = addons;
		enchantmentLimitation.put(Enchantment.DURABILITY, 8);
		enchantmentLimitation.put(Enchantment.DAMAGE_ALL, 3);
		repair(Material.IRON_INGOT, Material.IRON_AXE, Material.IRON_SHOVEL, Material.IRON_HOE,
				Material.IRON_PICKAXE, Material.IRON_SWORD, Material.IRON_CHESTPLATE,
				Material.IRON_LEGGINGS, Material.IRON_BOOTS, Material.IRON_HELMET,
				Material.CHAINMAIL_BOOTS, Material.CHAINMAIL_LEGGINGS,
				Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_HELMET);
		repair(Material.DIAMOND, Material.DIAMOND_AXE, Material.DIAMOND_SHOVEL,
				Material.DIAMOND_HOE, Material.DIAMOND_PICKAXE, Material.DIAMOND_SWORD,
				Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
				Material.DIAMOND_HELMET);
		repair(Material.NETHERITE_INGOT, Material.NETHERITE_AXE, Material.NETHERITE_SHOVEL,
				Material.NETHERITE_HOE, Material.NETHERITE_PICKAXE, Material.NETHERITE_SWORD,
				Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS,
				Material.NETHERITE_BOOTS, Material.NETHERITE_HELMET);
		repair(Material.LEATHER, Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS,
				Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET);
		repair(new Material[] {Material.ACACIA_PLANKS, Material.BIRCH_PLANKS,
						Material.CRIMSON_PLANKS, Material.JUNGLE_PLANKS, Material.OAK_PLANKS,
						Material.DARK_OAK_PLANKS, Material.MANGROVE_PLANKS, Material.WARPED_PLANKS,
						Material.SPRUCE_PLANKS, Material.BAMBOO_PLANKS}, Material.WOODEN_SWORD,
				Material.WOODEN_AXE, Material.WOODEN_PICKAXE, Material.WOODEN_SHOVEL,
				Material.WOODEN_HOE, Material.SHIELD);
		repair(new Material[] {Material.COBBLED_DEEPSLATE, Material.COBBLESTONE,
						Material.BLACKSTONE}, Material.STONE_SWORD, Material.STONE_AXE,
				Material.STONE_PICKAXE, Material.STONE_SHOVEL, Material.STONE_HOE);
		repair(Material.GOLD_INGOT, Material.GOLDEN_BOOTS, Material.GOLDEN_LEGGINGS,
				Material.GOLDEN_CHESTPLATE, Material.GOLDEN_HELMET, Material.GOLDEN_SWORD,
				Material.GOLDEN_PICKAXE, Material.GOLDEN_AXE, Material.GOLDEN_SHOVEL,
				Material.GOLDEN_HOE);
		repair(Material.SCUTE, Material.TURTLE_HELMET);
		repair(Material.PHANTOM_MEMBRANE, Material.ELYTRA);
	}

	@EventHandler
	public void handle(PrepareAnvilEvent event) {
		if (event.getInventory().getRenameText() == null || event.getInventory().getRenameText()
				.isEmpty())
			return;
		if (event.getInventory().getLocation() == null)
			return;
		Material mat = event.getInventory().getLocation().getBlock().getType();
		if (mat != Material.ANVIL && mat != Material.CHIPPED_ANVIL && mat != Material.DAMAGED_ANVIL)
			return;
		if (event.getInventory().getMaximumRepairCost() < 500)
			event.getInventory().setMaximumRepairCost(500);
		ItemBuilder first = event.getInventory().getFirstItem() == null
				? null
				: ItemBuilder.item(event.getInventory().getFirstItem());
		if (first == null)
			return;
		ItemBuilder second = event.getInventory().getSecondItem() == null
				? null
				: ItemBuilder.item(event.getInventory().getSecondItem());
		ItemBuilder result = event.getResult() == null ? null :
				ItemBuilder.item(event.getResult());
		if (second != null) {

			if (result == null) {
				Collection<Material> repairMaterials = repairMaterial.get(first.material());
				if (repairMaterials.contains(second.material())) {

				}
			}
			if (result == null)
				return;
			for (Enchantment enchantment : Enchantment.values()) {
				if (first.enchantments().containsKey(enchantment) && second.enchantments()
						.containsKey(enchantment)) {
					int limit = enchantmentLimitation.containsKey(enchantment)
							? enchantmentLimitation.get(enchantment)
							: enchantment.getMaxLevel();
					int firstLevel = first.enchantments().get(enchantment);
					int secondLevel = second.enchantments().get(enchantment);
					int newLevel = Math.max(firstLevel, secondLevel) + (firstLevel == secondLevel ?
							firstLevel >= limit
									? 0
									: 1 : 0);
					result.enchant(enchantment, newLevel);
				}
			}
		} else {
			if (result == null)
				return;
			if (!first.enchantments().equals(result.enchantments()))
				return;
			if (first.damage() != result.damage())
				return;
			if (event.getInventory().getRepairCost() > 1)
				event.getInventory().setRepairCost(1);
			return;
		}

		if (result == null)
			return;
		if (result.displayname() != null)
			result.displayname(LegacyComponentSerializer.legacySection().deserialize(
					ChatColor.translateAlternateColorCodes('&',
							LegacyComponentSerializer.legacySection()
									.serialize(result.displayname()))));
		event.setResult(result.build());
	}

	private void repair(Material repairMaterial, Material... items) {
		for (Material item : items) {
			this.repairMaterial.put(item, repairMaterial);
		}
	}

	private void repair(Material[] repairMaterials, Material... items) {
		for (Material item : items) {
			this.repairMaterial.putAll(item, Arrays.asList(repairMaterials));
		}
	}

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, addons);
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
	}
}
