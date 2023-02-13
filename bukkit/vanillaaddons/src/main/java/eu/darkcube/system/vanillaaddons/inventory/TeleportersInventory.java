/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.inventory;

import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.inventoryapi.v1.PageArrow;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.JoinConfiguration;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextDecoration;
import eu.darkcube.system.vanillaaddons.AUser;
import eu.darkcube.system.vanillaaddons.util.Teleporter;
import eu.darkcube.system.vanillaaddons.util.Teleporters;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

public class TeleportersInventory extends AbstractInventory<AddonsAsyncPagedInventory, Teleporter> {
	public static final InventoryType TYPE = InventoryType.of("teleporters");

	@Override
	protected AddonsAsyncPagedInventory openInventory(AUser user) {
		AddonsAsyncPagedInventory i = new AddonsAsyncPagedInventory(TYPE,
				Component.text("Teleporters").color(NamedTextColor.GOLD), () -> true) {
			@Override
			protected void fillItems(Map<Integer, ItemStack> items) {
				int i = 0;
				for (Entry<World, Teleporters> e : user.addons().teleporters().entrySet()) {
					for (Teleporter t : e.getValue().teleporters) {
						if (t == data())
							continue;
						ItemStack item =
								ItemBuilder.item(t.icon()).displayname(Component.text(t.name()))
										.lore(Component.join(JoinConfiguration.separator(
														Component.text().content(", ")
																.color(TextColor.color(80, 80, 80))
																.decoration(TextDecoration.ITALIC, false)),
												Component.join(JoinConfiguration.separator(
																Component.space()),
														Component.text().content("Position:")
																.color(TextColor.color(80, 80, 80))
																.decoration(TextDecoration.ITALIC,
																		false), Component.join(
																JoinConfiguration.separator(
																		Component.text()
																				.content(", ")
																				.color(TextColor.color(
																						80, 80, 80))
																				.decoration(
																						TextDecoration.ITALIC,
																						false)),
																Component.text().content(
																				Integer.toString(
																						t.block().x()))
																		.color(TextColor.color(255,
																				160, 0)).decoration(
																				TextDecoration.ITALIC,
																				false), Component.text()
																		.content(Integer.toString(
																				t.block().y()))
																		.color(TextColor.color(255,
																				160, 0)).decoration(
																				TextDecoration.ITALIC,
																				false),
																Component.text().content(
																				Integer.toString(
																						t.block().z()))
																		.color(TextColor.color(255,
																				160, 0)).decoration(
																				TextDecoration.ITALIC,
																				false))), Component.join(
														JoinConfiguration.separator(
																Component.space()),
														(Component.text().content("World:")
																.color(TextColor.color(80, 80,
																		80))).decoration(
																TextDecoration.ITALIC, false),
														(Component.text().content(
																		t.block().block().getWorld()
																				.getName())
																.color(TextColor.color(255, 160,
																		0))).decoration(
																TextDecoration.ITALIC, false))))
										.build();
						ItemMeta meta = item.getItemMeta();
						meta.getPersistentDataContainer()
								.set(new NamespacedKey("vanillaaddons", "teleporter"),
										Teleporter.TELEPORTER, t);
						item.setItemMeta(meta);
						items.put(i++, item);
					}
				}
			}

			@Override
			protected void insertArrowItems() {
				super.insertArrowItems();
				arrowItem.put(PageArrow.PREVIOUS, ItemBuilder.item(Material.ARROW)
						.displayname(Component.text("Previous " + "Page")).build());
				arrowItem.put(PageArrow.NEXT,
						ItemBuilder.item(Material.ARROW).displayname(Component.text("Next Page"))
								.build());
			}
		};
		i.open(Objects.requireNonNull(Bukkit.getPlayer(user.user().getUniqueId())));
		return i;
	}

	@Override
	protected void closeInventory(AUser user, AddonsAsyncPagedInventory inventory) {
		user.user().asPlayer().closeInventory();
	}
}
