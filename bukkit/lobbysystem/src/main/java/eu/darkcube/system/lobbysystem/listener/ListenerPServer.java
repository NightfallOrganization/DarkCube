/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import eu.darkcube.system.commons.AsyncExecutor;
import eu.darkcube.system.inventory.api.util.ItemBuilder;
import eu.darkcube.system.inventory.api.v1.IInventory;
import eu.darkcube.system.lobbysystem.event.EventPServerMayJoin;
import eu.darkcube.system.lobbysystem.inventory.InventoryConfirm;
import eu.darkcube.system.lobbysystem.inventory.InventoryLoading;
import eu.darkcube.system.lobbysystem.inventory.pserver.InventoryGameServersSelection;
import eu.darkcube.system.lobbysystem.inventory.pserver.InventoryNewPServerSlot;
import eu.darkcube.system.lobbysystem.inventory.pserver.InventoryPServer;
import eu.darkcube.system.lobbysystem.inventory.pserver.InventoryPServerConfiguration;
import eu.darkcube.system.lobbysystem.inventory.pserver.InventoryPServerOwn;
import eu.darkcube.system.lobbysystem.inventory.pserver.gameserver.InventoryGameServerSelection;
import eu.darkcube.system.lobbysystem.inventory.pserver.gameserver.InventoryGameServerSelectionWoolBattle;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager.PServerUserSlot;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager.PServerUserSlots;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.pserver.common.PServer;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.UniqueIdProvider;
import eu.darkcube.system.userapi.UserAPI;

public class ListenerPServer extends BaseListener {

	@EventHandler
	public void handle(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player)) {
			return;
		}
		ItemStack item = e.getCurrentItem();
		if (item == null) {
			return;
		}
		ItemBuilder itemb = new ItemBuilder(item);
		String itemid = Item.getItemId(item);
		LobbyUser user = UserWrapper
				.fromUser(UserAPI.getInstance().getUser(e.getWhoClicked().getUniqueId()));
		IInventory inv = user.getOpenInventory();
		if (itemid == null) {
			return;
		}

		if (inv instanceof InventoryPServerOwn) {
			if (itemid.equals(Item.INVENTORY_PSERVER_SLOT_EMPTY.getItemId())) {
				int slot = itemb.getUnsafe().getInt(InventoryPServerOwn.META_KEY_SLOT);
				PServerUserSlot psslot = user.getPServerUserSlots().getUserSlot(slot);
				user.setOpenInventory(new InventoryNewPServerSlot(user.getUser(), slot));
			} else if (itemid.equals(InventoryPServerOwn.ITEMID_EXISTING)) {
				int slot = itemb.getUnsafe().getInt(InventoryPServerOwn.META_KEY_SLOT);
				PServerUserSlot psslot = user.getPServerUserSlots().getUserSlot(slot);
				user.setOpenInventory(new InventoryPServerConfiguration(user.getUser(), slot));
			}
		} else if (inv instanceof InventoryPServer) {
			InventoryPServer cinv = (InventoryPServer) inv;
			if (itemid.equals(InventoryPServer.ITEMID)) {
				String psid = itemb.getUnsafe().getString(InventoryPServer.META_KEY_PSERVER);
				PServer ps = PServerProvider.getInstance().getPServer(new UniqueId(psid));
				if (ps == null) {
					cinv.recalculate();
					return;
				}
				e.getWhoClicked().closeInventory();
				if (ListenerPServer.mayJoin(user, ps)) {
					ps.connectPlayer(user.getUser().getUniqueId());
				} else {
					e.getWhoClicked()
							.sendMessage(Message.PSERVER_NOT_PUBLIC.getMessage(user.getUser()));
				}
			}
		} else if (inv instanceof InventoryNewPServerSlot) {
			InventoryNewPServerSlot cinv = (InventoryNewPServerSlot) inv;
			if (itemid.equals(Item.GAME_PSERVER.getItemId())) {
				user.setOpenInventory(
						new InventoryGameServersSelection(user.getUser(), cinv.psslot));
			} else if (itemid.equals(Item.WORLD_PSERVER.getItemId())) {
				user.setOpenInventory(
						new InventoryLoading("Creating Server...", user.getUser(), u -> {
							int psslot = cinv.psslot;
							PServerUserSlots slots = user.getPServerUserSlots();
							PServerUserSlot slot = slots.getUserSlot(psslot);
							slot.pserverId = UniqueIdProvider.getInstance().newUniqueId();
							user.setPServerUserSlots(slots);

							PServerProvider.getInstance().addOwner(slot.pserverId,
									user.getUser().getUniqueId());

							try {
								Thread.sleep(1000);
							} catch (InterruptedException ex) {
								ex.printStackTrace();
							}

							return new InventoryPServerConfiguration(user.getUser(), psslot);
						}));
			}
		} else if (inv instanceof InventoryGameServersSelection) {
			InventoryGameServersSelection cinv = (InventoryGameServersSelection) inv;
			if (itemid.equals(Item.GAMESERVER_SELECTION_WOOLBATTLE.getItemId())) {
				user.setOpenInventory(
						new InventoryGameServerSelectionWoolBattle(user.getUser(), cinv.psslot));
			}
		} else if (inv instanceof InventoryGameServerSelection) {
			InventoryGameServerSelection cinv = (InventoryGameServerSelection) inv;
			if (itemid.equals(InventoryGameServerSelection.ITEMID)) {
				user.setOpenInventory(
						new InventoryLoading(inv.getHandle().getTitle(), user.getUser(), u -> {
							JsonObject extra = new Gson().fromJson(
									itemb.getUnsafe().getString(
											InventoryGameServerSelection.GAMESERVER_META_KEY),
									JsonObject.class);
							ServiceTask task =
									CloudNetDriver.getInstance().getServiceTaskProvider()
											.getServiceTask(extra
													.get(InventoryGameServerSelection.SERVICETASK)
													.getAsString());
							if (task == null) {
								return inv;
							}
							PServerUserSlots slots = user.getPServerUserSlots();
							PServerUserSlot slot = slots.getUserSlot(cinv.psslot);
							slot.data.append("task", task.getName());
							UniqueId uid = UniqueIdProvider.getInstance().newUniqueId();
							slot.pserverId = uid;
							user.setPServerUserSlots(slots);
							PServerProvider.getInstance().addOwner(uid,
									user.getUser().getUniqueId());

							try {
								Thread.sleep(1000);
							} catch (InterruptedException ex) {
								ex.printStackTrace();
							}

							return new InventoryPServerConfiguration(user.getUser(), cinv.psslot);
						}));

			}
		} else if (inv instanceof InventoryPServerConfiguration) {
			InventoryPServerConfiguration cinv = (InventoryPServerConfiguration) inv;
			if (itemid.equals(Item.PSERVER_DELETE.getItemId())) {
				user.setOpenInventory(
						new InventoryConfirm(cinv.getHandle().getTitle(), user.getUser(), () -> {
							PServerUserSlots slots = user.getPServerUserSlots();
							slots.removeUserSlot(cinv.psslot);
							user.setPServerUserSlots(slots);
							user.setOpenInventory(new InventoryPServerOwn(user.getUser()));
						}, () -> {
							user.setOpenInventory(cinv);
						}));
			} else if (itemid.equals(Item.START_PSERVER.getItemId())) {
				AsyncExecutor.service().submit(() -> {
					// new BukkitRunnable() {
					//
					// @Override
					// public void run() {
					// e.getWhoClicked().closeInventory();
					// }
					//
					// }.runTask(Lobby.getInstance());
					// PServer ps = cinv.psslot.startPServer();
					// Lobby.getInstance().getPServerJoinOnStart().register(user, ps);
					Bukkit.broadcastMessage("start");
				});
			} else if (itemid.equals(Item.STOP_PSERVER.getItemId())) {
				AsyncExecutor.service().submit(() -> {
					// PServer ps = cinv.psslot.getPServer();
					// if (ps != null) {
					// ps.stop();
					// }
					Bukkit.broadcastMessage("stop");
				});
			} else if (itemid.equals(Item.PSERVER_PUBLIC.getItemId())) {
				AsyncExecutor.service().submit(() -> {
					// cinv.psslot.getData().addProperty("private", true);
					// cinv.psslot.setChanged();
					// PServer ps = cinv.psslot.getPServer();
					// if (ps != null) {
					// ps.setPrivate(true);
					// }
					Bukkit.broadcastMessage("Private: true" + cinv.psslot);
					cinv.recalculate();
				});
			} else if (itemid.equals(Item.PSERVER_PRIVATE.getItemId())) {
				AsyncExecutor.service().submit(() -> {
					// cinv.psslot.getData().addProperty("private", false);
					// cinv.psslot.setChanged();
					// PServer ps = cinv.psslot.getPServer();
					// if (ps != null) {
					// ps.setPrivate(false);
					// }
					Bukkit.broadcastMessage("Private: false" + cinv.psslot);
					cinv.recalculate();
				});
			}
		}
	}

	public static boolean mayJoin(LobbyUser user, PServer pserver) {
		boolean mayjoin = false;
		if (!pserver.isPrivate()) {
			mayjoin = true;
		}
		if (pserver.getOwners().contains(user.getUser().getUniqueId())) {
			mayjoin = true;
		}
		EventPServerMayJoin e = new EventPServerMayJoin(user, pserver, mayjoin);
		Bukkit.getPluginManager().callEvent(e);
		return e.mayJoin();
	}

}
