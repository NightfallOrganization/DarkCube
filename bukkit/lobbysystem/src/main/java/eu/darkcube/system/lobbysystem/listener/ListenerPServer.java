/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import eu.darkcube.system.ChatUtils;
import eu.darkcube.system.commons.AsyncExecutor;
import eu.darkcube.system.inventory.api.util.ItemBuilder;
import eu.darkcube.system.inventory.api.v1.IInventory;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.event.EventPServerMayJoin;
import eu.darkcube.system.lobbysystem.inventory.InventoryConfirm;
import eu.darkcube.system.lobbysystem.inventory.InventoryLoading;
import eu.darkcube.system.lobbysystem.inventory.pserver.*;
import eu.darkcube.system.lobbysystem.inventory.pserver.gameserver.InventoryGameServerSelection;
import eu.darkcube.system.lobbysystem.inventory.pserver.gameserver.InventoryGameServerSelectionWoolBattle;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.pserver.common.PServer;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.UniqueIdProvider;
import eu.darkcube.system.pserver.common.packet.PServerSerializable;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

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
		LobbyUser user = UserWrapper.fromUser(
				UserAPI.getInstance().getUser(e.getWhoClicked().getUniqueId()));
		IInventory inv = user.getOpenInventory();
		if (itemid == null) {
			return;
		}

		if (inv instanceof InventoryPServerOwn) {
			if (itemid.equals(Item.INVENTORY_PSERVER_SLOT_EMPTY.getItemId())) {
				user.setOpenInventory(new InventoryNewPServer(user.getUser()));
			} else if (itemid.equals(InventoryPServerOwn.ITEMID_EXISTING)) {
				UniqueId pserverId = new UniqueId(
						itemb.getUnsafe().getString(InventoryPServerOwn.META_KEY_PSERVERID));
				user.setOpenInventory(new InventoryPServerConfiguration(user.getUser(), pserverId));
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
		} else if (inv instanceof InventoryNewPServer) {
			if (itemid.equals(Item.GAME_PSERVER.getItemId())) {
				user.setOpenInventory(new InventoryGameServersSelection(user.getUser()));
			} else if (itemid.equals(Item.WORLD_PSERVER.getItemId())) {
				user.setOpenInventory(
						new InventoryLoading("Creating Server...", user.getUser(), u -> {
							UniqueId pserverId = UniqueIdProvider.getInstance().newUniqueId();

							try {
								PServerProvider.getInstance()
										.addOwner(pserverId, user.getUser().getUniqueId()).get();
								Thread.sleep(1000);
							} catch (InterruptedException | ExecutionException ex) {
								throw new RuntimeException(ex);
							}

							return new InventoryPServerConfiguration(user.getUser(), pserverId);
						}));
			}
		} else if (inv instanceof InventoryGameServersSelection) {
			if (itemid.equals(Item.GAMESERVER_SELECTION_WOOLBATTLE.getItemId())) {
				user.setOpenInventory(new InventoryGameServerSelectionWoolBattle(user.getUser()));
			}
		} else if (inv instanceof InventoryGameServerSelection) {
			if (itemid.equals(InventoryGameServerSelection.ITEMID)) {
				user.setOpenInventory(
						new InventoryLoading(inv.getHandle().getTitle(), user.getUser(), u -> {
							JsonObject extra = new Gson().fromJson(itemb.getUnsafe()
											.getString(InventoryGameServerSelection.GAMESERVER_META_KEY),
									JsonObject.class);
							ServiceTask task = CloudNetDriver.getInstance().getServiceTaskProvider()
									.getServiceTask(
											extra.get(InventoryGameServerSelection.SERVICETASK)
													.getAsString());
							if (task == null) {
								return inv;
							}
							JsonDocument data = new JsonDocument();
							data.append("task", task.getName());
							data.append("private", false);
							UniqueId pserverId = UniqueIdProvider.getInstance().newUniqueId();

							try {
								PServerProvider.getInstance()
										.addOwner(pserverId, user.getUser().getUniqueId()).get();
								PServerProvider.getInstance().setPServerData(pserverId, data).get();
								Thread.sleep(1000);
							} catch (ExecutionException | InterruptedException ex) {
								throw new RuntimeException(ex);
							}

							return new InventoryPServerConfiguration(user.getUser(), pserverId);
						}));

			}
		} else if (inv instanceof InventoryPServerConfiguration) {
			InventoryPServerConfiguration cinv = (InventoryPServerConfiguration) inv;
			if (itemid.equals(Item.PSERVER_DELETE.getItemId())) {
				user.setOpenInventory(
						new InventoryConfirm(cinv.getHandle().getTitle(), user.getUser(), () -> {
							try {
								PServerProvider.getInstance()
										.removeOwner(cinv.pserverId, user.getUser().getUniqueId())
										.get();
								if (PServerProvider.getInstance().getOwners(cinv.pserverId)
										.isEmpty()) {
									PServerProvider.getInstance().delete(cinv.pserverId).get();
								}
							} catch (InterruptedException | ExecutionException ex) {
								throw new RuntimeException(ex);
							}
							user.setOpenInventory(new InventoryPServerOwn(user.getUser()));
						}, () -> {
							user.setOpenInventory(cinv);
						}));
			} else if (itemid.equals(Item.START_PSERVER.getItemId())) {
				AsyncExecutor.service().submit(() -> {
					new BukkitRunnable() {
						@Override
						public void run() {
							e.getWhoClicked().closeInventory();
						}
					}.runTask(Lobby.getInstance());
					for (PServer ps : PServerProvider.getInstance().getPServers()) {
						JsonDocument data = ps.getData();
						if (data.contains("startedBy")) {
							UUID uuid = UUID.fromString(data.getString("startedBy"));
							if (uuid.equals(user.getUser().getUniqueId())) {
								new BukkitRunnable() {
									@Override
									public void run() {
										ChatUtils.ChatEntry.buildArray(
														new ChatUtils.ChatEntry.Builder().text(
																Message.STOP_OTHER_PSERVER_BEFORE_STARTING_ANOTHER.getMessage(
																		user.getUser())).build())
												.sendPlayer(user.getUser().asPlayer());
									}
								}.runTask(Lobby.getInstance());
								return;
							}
						}
					}
					String serverName = PServerProvider.getInstance().newName();
					String taskName = PServerProvider.getInstance().getPServerData(cinv.pserverId)
							.getString("task");
					boolean temporary = taskName != null;
					PServerSerializable ser = new PServerSerializable(cinv.pserverId, 0, temporary,
							System.currentTimeMillis(), taskName, serverName,
							PServer.State.STARTING);
					JsonDocument data = PServerProvider.getInstance().getPServerData(ser.id);
					data.append("startedBy", user.getUser().getUniqueId().toString());
					PServerProvider.getInstance().setPServerData(ser.id, data);
					PServer ps = PServerProvider.getInstance().createPServer(ser);
					ps.start();
					Lobby.getInstance().getPServerJoinOnStart().register(user, ps);
				});
			} else if (itemid.equals(Item.STOP_PSERVER.getItemId())) {
				AsyncExecutor.service().submit(() -> {
					PServerProvider.getInstance().getPServer(cinv.pserverId).stop();
				});
			} else if (itemid.equals(Item.PSERVER_PUBLIC.getItemId())) {
				AsyncExecutor.service().submit(() -> {
					JsonDocument data =
							PServerProvider.getInstance().getPServerData(cinv.pserverId);
					data.append("private", true);
					try {
						PServerProvider.getInstance().setPServerData(cinv.pserverId, data).get();
					} catch (InterruptedException | ExecutionException ex) {
						throw new RuntimeException(ex);
					}
					cinv.recalculate();
				});
			} else if (itemid.equals(Item.PSERVER_PRIVATE.getItemId())) {
				AsyncExecutor.service().submit(() -> {
					JsonDocument data =
							PServerProvider.getInstance().getPServerData(cinv.pserverId);
					data.append("private", false);
					try {
						PServerProvider.getInstance().setPServerData(cinv.pserverId, data).get();
					} catch (InterruptedException | ExecutionException ex) {
						throw new RuntimeException(ex);
					}
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
