/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.module.modules.teleporter;

import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.vanillaaddons.AUser;
import eu.darkcube.system.vanillaaddons.VanillaAddons;
import eu.darkcube.system.vanillaaddons.inventory.AbstractInventory;
import eu.darkcube.system.vanillaaddons.module.modules.teleporter.TeleporterModule.TeleporterListener;
import net.wesjd.anvilgui.AnvilGUI;
import net.wesjd.anvilgui.AnvilGUI.ResponseAction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

public class TeleporterTrustedListAddInventory extends AbstractInventory<AnvilGUI, Teleporter> {
	public static final InventoryType TYPE = InventoryType.of("teleporter_trusted_list_add");

	@Override
	protected AnvilGUI openInventory(AUser user) {
		return new AnvilGUI.Builder().itemLeft(ItemBuilder.item(Material.NAME_TAG).displayname(
						Component.text(user.user().getName()).color(TextColor.color(170, 0, 170))).build())
				.onClick((slot, snapshot) -> {
					UUID uuid = Bukkit.getPlayerUniqueId(snapshot.getText());
					if (uuid == null) {
						return List.of(ResponseAction.replaceInputText("Ungültiger Name"));
					}
					data().trustedList().add(uuid);
					TeleporterListener.saveTeleporters(VanillaAddons.instance(),
							data().block().block().getWorld());
					return List.of(ResponseAction.close());
				}).onClose(player -> new BukkitRunnable() {
					@Override
					public void run() {
						user.openInventory(TeleporterTrustedListInventory.TYPE, data());
					}
				}.runTask(VanillaAddons.instance())).plugin(VanillaAddons.instance())
				.open(user.user().asPlayer());
	}

	@Override
	protected void closeInventory(AUser user, AnvilGUI inventory) {

	}
}
