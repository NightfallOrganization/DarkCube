/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import de.dytanic.cloudnet.ext.bridge.BridgeServiceProperty;
import de.dytanic.cloudnet.wrapper.Wrapper;
import eu.darkcube.system.inventoryapi.ItemBuilder;
import eu.darkcube.system.lobbysystem.inventory.InventoryLobbySwitcher;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.lobbysystem.util.UUIDManager;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ListenerLobbySwitcher extends BaseListener {

	@EventHandler
	public void handle(InventoryClickEvent e) {
		ItemStack item = e.getCurrentItem();
		if (item == null)
			return;
		Player p = (Player) e.getWhoClicked();
		User user = UserAPI.getInstance().getUser(p);
		String serverId = new ItemBuilder(item).getUnsafe().getString("server");
		if (serverId == null || serverId.isEmpty())
			return;
		ServiceInfoSnapshot service =
				Wrapper.getInstance().getCloudServiceProvider().getCloudServiceByName(serverId);
		if (service == null) {
			UserWrapper.fromUser(user).setOpenInventory(new InventoryLobbySwitcher(user));
			return;
		}
		if (!service.isConnected()
				|| !service.getProperty(BridgeServiceProperty.IS_ONLINE).orElse(false)) {
			p.sendMessage(Message.SERVER_NOT_STARTED.getMessage(user));
			p.closeInventory();
			return;
		}
		p.closeInventory();
		UUIDManager.getManager().getPlayerExecutor(p.getUniqueId()).connect(serverId);
	}
}
