/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.inventory;

import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.IInventory;
import eu.darkcube.system.inventoryapi.v1.IInventoryClickEvent;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.JoinConfiguration;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.NamedTextColor;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataTypes;
import eu.darkcube.system.vanillaaddons.AUser;
import eu.darkcube.system.vanillaaddons.util.Teleporter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class TeleporterInventory extends AbstractInventory<AddonsAsyncPagedInventory, Teleporter> {
	public static final InventoryType TYPE = InventoryType.of("teleporter");

	@Override
	protected AddonsAsyncPagedInventory openInventory(AUser user) {
		Teleporter teleporter = data();
		AddonsAsyncPagedInventory i = new AddonsAsyncPagedInventory(TYPE,
				Component.text(teleporter.name()).color(NamedTextColor.GOLD), () -> true) {
			private final Key KEY_TYPE = new Key(user.addons(), "teleInvType");

			@Override
			protected void inventoryClick(IInventoryClickEvent event) {
				event.setCancelled(true);
				if (event.item() == null)
					return;
				if (!event.item().persistentDataStorage().has(KEY_TYPE))
					return;
				@SuppressWarnings("DataFlowIssue")
				int type = event.item().persistentDataStorage()
						.get(KEY_TYPE, PersistentDataTypes.INTEGER);
				if (type == 0) {
					//noinspection DataFlowIssue
					if (event.bukkitEvent().getCursor() == null || !event.bukkitEvent().getCursor()
							.hasItemMeta())
						return;
					if (data().owner() == null)
						data().owner(user.user().getUniqueId());
					if (!user.user().getUniqueId().equals(data().owner()))
						return;
					//noinspection DataFlowIssue
					data().icon(event.bukkitEvent().getCursor().getType());
					insertFallbackItems();
					updateSlots.add(IInventory.slot(3, 4));
				} else if (type == 1) {
					
				}
			}

			@Override
			protected void insertFallbackItems() {
				ItemStack icon = ItemBuilder.item(teleporter.icon())
						.displayname(Component.text("Icon").color(TextColor.color(120, 120, 120)))
						.lore(Component.translatable(teleporter.icon().translationKey()))
						.persistentDataStorage().iset(KEY_TYPE, PersistentDataTypes.INTEGER, 0)
						.builder().build();
				ItemStack name = ItemBuilder.item(Material.NAME_TAG)
						.displayname(Component.text("Name").color(TextColor.color(120, 120, 120)))
						.lore(Component.join(JoinConfiguration.separator(Component.space()),
								Component.text("Name:").color(TextColor.color(120, 120, 120)),
								Component.text(teleporter.name())
										.color(TextColor.color(170, 0, 170))))
						.persistentDataStorage().iset(KEY_TYPE, PersistentDataTypes.INTEGER, 1)
						.builder().build();
				ItemStack access = ItemBuilder.item(teleporter.access().getType()).displayname(
								Component.join(JoinConfiguration.separator(Component.space()),
										Component.text("Zugriff:").color(TextColor.color(120, 120, 120)),
										Component.text(teleporter.access().name().toLowerCase())
												.color(TextColor.color(170, 0, 170))))
						.persistentDataStorage().iset(KEY_TYPE, PersistentDataTypes.INTEGER, 2)
						.builder().build();
				ItemStack trustedList = ItemBuilder.item(Material.POPPY).displayname(
								Component.text("Vertrauenswürdige Spieler")
										.color(TextColor.color(120, 120, 120))).persistentDataStorage()
						.iset(KEY_TYPE, PersistentDataTypes.INTEGER, 3).builder().build();
				if (teleporter.owner() == null)
					teleporter.owner(user.user().getUniqueId());
				if (user.user().getUniqueId().equals(teleporter.owner())) {
					fallbackItems.put(IInventory.slot(3, 2), name);
					fallbackItems.put(IInventory.slot(3, 4), icon);
					fallbackItems.put(IInventory.slot(3, 6), access);
					fallbackItems.put(IInventory.slot(3, 8), trustedList);
				} else {
					fallbackItems.put(IInventory.slot(3, 4), name);
					fallbackItems.put(IInventory.slot(3, 6), icon);
				}
				super.insertFallbackItems();
			}
		};
		i.open(Objects.requireNonNull(Bukkit.getPlayer(user.user().

				getUniqueId())));
		return i;
	}

	@Override
	protected void closeInventory(AUser user, AddonsAsyncPagedInventory inventory) {
		user.user().asPlayer().closeInventory();
	}
}
