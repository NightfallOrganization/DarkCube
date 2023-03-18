/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.inventory;

import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.net.kyori.adventure.text.format.TextColor;
import eu.darkcube.system.vanillaaddons.AUser;
import eu.darkcube.system.vanillaaddons.VanillaAddons;
import eu.darkcube.system.vanillaaddons.util.Teleporter;
import net.wesjd.anvilgui.AnvilGUI;
import net.wesjd.anvilgui.AnvilGUI.ResponseAction;
import org.bukkit.Material;

import java.util.Arrays;

public class TeleporterRenameInventory extends AbstractInventory<AnvilGUI, Teleporter> {
	public static final InventoryType TYPE = InventoryType.of("teleporterRename");

	@Override
	protected AnvilGUI openInventory(AUser user) {
		AnvilGUI gui = new AnvilGUI.Builder().plugin(VanillaAddons.instance()).title(data().name())
				.onComplete(completion -> {
					data().name(completion.getText());
					return Arrays.asList(ResponseAction.close());
				}).onClose(player -> {
					user.openInventory(TeleporterInventory.TYPE, data());
				}).itemLeft(ItemBuilder.item(Material.NAME_TAG).displayname(
						Component.text(data().name()).color(TextColor.color(170, 0, 170))).build())
				.open(user.user().asPlayer());
		return gui;
	}

	@Override
	protected void closeInventory(AUser user, AnvilGUI inventory) {
		inventory.closeInventory();
	}
}
