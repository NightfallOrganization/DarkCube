/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.module.modules.anvilmechanics;

import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.item.meta.EnchantmentStorageBuilderMeta;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.vanillaaddons.VanillaAddons;
import eu.darkcube.system.vanillaaddons.module.Module;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;

import java.util.HashMap;
import java.util.Map;

public class AnvilMechanicsModule implements Module, Listener {
	private final VanillaAddons addons;
	private final Map<Enchantment, Integer> enchantmentLimitation = new HashMap<>();
	private final Map<Enchantment, Map<Integer, Integer>> levelsAfter = new HashMap<>();

	public AnvilMechanicsModule(VanillaAddons addons) {
		this.addons = addons;
		enchantmentLimitation.put(Enchantment.DURABILITY, 8);
		levelsAfter.put(Enchantment.DURABILITY, new HashMap<>());
		levelsAfter.get(Enchantment.DURABILITY).put(4, 39);
	}

	// Lowest priority to initialize this event with the correct result and allow modification by
	// plugins later
	@EventHandler(priority = EventPriority.LOWEST)
	public void handle(PrepareAnvilEvent event) {
		if (event.getInventory().getLocation() == null)
			return;
		Material mat = event.getInventory().getLocation().getBlock().getType();
		if (mat != Material.ANVIL && mat != Material.CHIPPED_ANVIL && mat != Material.DAMAGED_ANVIL)
			return;
		ItemBuilder first = event.getInventory().getFirstItem() == null
				? null
				: ItemBuilder.item(event.getInventory().getFirstItem());
		ItemBuilder second = event.getInventory().getSecondItem() == null
				? null
				: ItemBuilder.item(event.getInventory().getSecondItem());
		ItemBuilder result = event.getResult() == null ? null :
				ItemBuilder.item(event.getResult());
		Data data = simulateAnvil(event.getView().getPlayer(), first, second, result,
				event.getInventory().getRenameText(), event.getInventory().getRepairCost(),
				event.getInventory().getRepairCostAmount(),
				event.getInventory().getMaximumRepairCost());
		result = data.result;
		event.getInventory().setRepairCost(Math.min(data.levelCost, 39));
		event.getInventory().setRepairCostAmount(data.itemCost);
		event.setResult(result == null ? null : result.build());
	}

	private Data simulateAnvil(HumanEntity player, ItemBuilder first, ItemBuilder second,
			ItemBuilder result, String renameText, int levelCost, int itemCost,
			int maximumRepairCost) {
		Data originalData = new Data(first, second, result, levelCost, itemCost);
		if (first == null)
			return originalData;
		first = first.clone();

		int repairItemCountCost = 0;
		int cost;
		int extraCost = 0;

		result = first.clone();
		if (second != null)
			second = second.clone();
		Map<Enchantment, Integer> resultEnchantments = enchantments(result);
		int repairCost = first.repairCost() + (second == null ? 0 : second.repairCost());
		boolean repair = false;

		if (second != null) {
			boolean enchantedBook = second.material() == Material.ENCHANTED_BOOK && !second.meta(
					EnchantmentStorageBuilderMeta.class).enchantments().isEmpty();
			if (result.material().getMaxDurability() != 0 && first.canBeRepairedBy(second)) {
				int k = Math.min(result.damage(), result.material().getMaxDurability() / 4);
				if (k <= 0) {
					return originalData;
				}
				int i1;
				for (i1 = 0; k > 0 && i1 < second.amount(); ++i1) {
					int l = result.damage() - k;
					result.damage(Math.max(0, l));
					extraCost++;
					k = Math.min(result.damage(), result.damage() / 4);
				}
				if (extraCost > 0)
					repair = true;
				repairItemCountCost = i1;
			} else {
				if (!enchantedBook && (result.material() != second.material()
						|| result.material().getMaxDurability() == 0)) {
					return originalData;
				}
				if (result.material().getMaxDurability() != 0 && !enchantedBook) {
					int k = first.material().getMaxDurability() - first.damage();
					int i1 = second.material().getMaxDurability() - second.damage();
					int l = i1 + result.material().getMaxDurability() * 12 / 100;
					int j1 = k + l;
					int k1 = result.material().getMaxDurability() - j1;

					if (k1 < 0)
						k1 = 0;

					if (k1 < result.damage()) {
						result.damage(k1);
						extraCost += 2;
					}
				}
				Map<Enchantment, Integer> secondEnchantments = enchantments(second);
				boolean canEnchantItem = false;
				boolean cantEnchantItem = false;

				for (Enchantment enchantment : secondEnchantments.keySet()) {
					{
						int resultLevel = resultEnchantments.getOrDefault(enchantment, 0);
						int secondLevel = secondEnchantments.get(enchantment);

						secondLevel = resultLevel == secondLevel
								? secondLevel + 1
								: Math.max(secondLevel, resultLevel);
						boolean canEnchantItem1 = enchantment.canEnchantItem(first.build());

						if (player.getGameMode() == GameMode.CREATIVE
								|| first.material() == Material.ENCHANTED_BOOK) {
							canEnchantItem1 = true;
						}

						for (Enchantment enchantment1 : resultEnchantments.keySet()) {
							if (!enchantment1.equals(enchantment) && enchantment.conflictsWith(
									enchantment1)) {
								canEnchantItem1 = false;
								extraCost++;
							}
						}

						if (!canEnchantItem1) {
							cantEnchantItem = true;
						} else {
							canEnchantItem = true;
							if (secondLevel > maxLevel(enchantment)) {
								secondLevel = maxLevel(enchantment);
							}

							resultEnchantments.put(enchantment, secondLevel);

							if (levelsAfter.containsKey(enchantment)) {
								Map<Integer, Integer> map = levelsAfter.get(enchantment);
								int minlevel = -1;
								for (int i = secondLevel; i >= 0; i--) {
									if (map.containsKey(i)) {
										minlevel = map.get(i);
										break;
									}
								}
								extraCost += minlevel;
							}

							int rarityCost = switch (enchantment.getRarity()) {
								case COMMON -> 1;
								case UNCOMMON -> 2;
								case RARE -> 4;
								case VERY_RARE -> 8;
							};

							if (enchantedBook) {
								rarityCost = Math.max(1, rarityCost / 2);
							}

							extraCost += rarityCost * secondLevel;
							if (first.amount() > 1) {
								extraCost = 40;
							}
						}
					}
				}
				if (cantEnchantItem && !canEnchantItem) {
					return originalData;
				}
			}
		}
		byte b1;
		if (renameText == null || renameText.isEmpty()) {
			if (first.displayname() != null) {
				b1 = 1;
				extraCost += b1;
				result.displaynameRaw(null);
			}
		} else if (first.displayname() == null || !renameText.equals(
				LegacyComponentSerializer.legacySection().serialize(first.displayname()))) {
			b1 = 1;
			extraCost += b1;
			result.displaynameRaw(
					LegacyComponentSerializer.legacySection().deserialize(renameText));
		}

		cost = repairCost + extraCost;
		if (extraCost <= 0) {
			result = null;
		}
		//		if (b1 == extraCost && b1 > 0 && cost >= maximumRepairCost) {
		//			cost = maximumRepairCost - 1;
		//		}
		if (cost >= maximumRepairCost && player.getGameMode() != GameMode.CREATIVE) {
			cost = maximumRepairCost;
		}
		if (result != null) {
			int resultRepairCost = result.repairCost();

			if (second != null && resultRepairCost < second.repairCost()) {
				resultRepairCost = second.repairCost();
			}
			resultRepairCost = calculateIncreasedRepairCost(resultRepairCost);

			if (!repair)
				result.repairCost(resultRepairCost);
			else
				cost = 1;

			if (result.material() == Material.ENCHANTED_BOOK)
				result.meta(EnchantmentStorageBuilderMeta.class).enchantments(resultEnchantments);
			else
				result.enchantments(resultEnchantments);
		}
		return new Data(first, second, result, cost, repairItemCountCost);
	}

	private int maxLevel(Enchantment enchantment) {
		return enchantmentLimitation.getOrDefault(enchantment, enchantment.getMaxLevel());
	}

	private Map<Enchantment, Integer> enchantments(ItemBuilder item) {
		return new HashMap<>(item.material() == Material.ENCHANTED_BOOK ? item.meta(
				EnchantmentStorageBuilderMeta.class).enchantments() : item.enchantments());
	}

	private int calculateIncreasedRepairCost(int cost) {
		return cost * 2 + 1;
	}

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, addons);
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
	}

	private record Data(ItemBuilder first, ItemBuilder second, ItemBuilder result, int levelCost,
			int itemCost) {
	}
}
