package eu.darkcube.system.lobbysystem.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.ServiceTask;
import eu.darkcube.system.inventory.api.util.ItemBuilder;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.event.EventPServerMayJoin;
import eu.darkcube.system.lobbysystem.inventory.InventoryConfirm;
import eu.darkcube.system.lobbysystem.inventory.InventoryLoading;
import eu.darkcube.system.lobbysystem.inventory.abstraction.Inventory;
import eu.darkcube.system.lobbysystem.inventory.pserver.InventoryGameServerSelection;
import eu.darkcube.system.lobbysystem.inventory.pserver.InventoryNewPServerSlot;
import eu.darkcube.system.lobbysystem.inventory.pserver.InventoryPServer;
import eu.darkcube.system.lobbysystem.inventory.pserver.InventoryPServerConfiguration;
import eu.darkcube.system.lobbysystem.inventory.pserver.InventoryPServerOwn;
import eu.darkcube.system.lobbysystem.inventory.pserver.gameserver.InventoryGameServerSelectionWoolBattle;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager.PServerUserSlots.PServerUserSlot;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.AsyncExecutor;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.pserver.common.PServer;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.common.UniqueId;
import eu.darkcube.system.pserver.common.UniqueIdProvider;

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
		User user = UserWrapper.getUser(e.getWhoClicked().getUniqueId());
		Inventory inv = user.getOpenInventory();
		if (itemid == null) {
			return;
		}

		if (inv instanceof InventoryPServerOwn) {
			if (itemid.equals(Item.INVENTORY_PSERVER_SLOT_EMPTY.getItemId())) {
				int slot = itemb.getUnsafe().getInt(InventoryPServerOwn.META_KEY_SLOT);
				PServerUserSlot psslot = user.getSlots().getSlot(slot);
				user.setOpenInventory(new InventoryNewPServerSlot(user, psslot, slot + 1));
			} else if (itemid.equals(InventoryPServerOwn.ITEMID_EXISTING)) {
				int slot = itemb.getUnsafe().getInt(InventoryPServerOwn.META_KEY_SLOT);
				PServerUserSlot psslot = user.getSlots().getSlot(slot);
				user.setOpenInventory(new InventoryPServerConfiguration(user, psslot));
			}
		} else if (inv instanceof InventoryPServer) {
			InventoryPServer cinv = (InventoryPServer) inv;
			if (itemid.equals(InventoryPServer.ITEMID)) {
				String psid = itemb.getUnsafe().getString(InventoryPServer.META_KEY_PSERVER);
				PServer ps = PServerProvider.getInstance().getPServer(new UniqueId(psid));
				if (ps == null) {
					cinv.recalculate(user);
					return;
				}
				e.getWhoClicked().closeInventory();
				if (ListenerPServer.mayJoin(user, ps)) {
					ps.connectPlayer(user.getUniqueId());
				} else {
					e.getWhoClicked().sendMessage(Message.PSERVER_NOT_PUBLIC.getMessage(user));
				}
			}
		} else if (inv instanceof InventoryNewPServerSlot) {
			InventoryNewPServerSlot cinv = (InventoryNewPServerSlot) inv;
			if (itemid.equals(Item.GAME_PSERVER.getItemId())) {
				user.setOpenInventory(new InventoryGameServerSelection(user, cinv.psslot, cinv.slot));
			} else if (itemid.equals(Item.WORLD_PSERVER.getItemId())) {
				user.setOpenInventory(new InventoryLoading("Creating Server...", u -> {
					PServerUserSlot slot = cinv.psslot;
					UniqueId uid = UniqueIdProvider.getInstance().newUniqueId();
					slot.load(uid);
					PServerProvider.getInstance().addOwner(uid, user.getUniqueId());

					try {
						Thread.sleep(1000);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}

					return new InventoryPServerConfiguration(user, slot);
				}));
			}
		} else if (inv instanceof InventoryGameServerSelection) {
			InventoryGameServerSelection cinv = (InventoryGameServerSelection) inv;
			if (itemid.equals(Item.GAMESERVER_SELECTION_WOOLBATTLE.getItemId())) {
				user.setOpenInventory(new InventoryGameServerSelectionWoolBattle(user, cinv.psslot, cinv.slot));
			}
		} else if (inv instanceof eu.darkcube.system.lobbysystem.inventory.pserver.gameserver.InventoryGameServerSelection) {
			eu.darkcube.system.lobbysystem.inventory.pserver.gameserver.InventoryGameServerSelection cinv = (eu.darkcube.system.lobbysystem.inventory.pserver.gameserver.InventoryGameServerSelection) inv;
			if (itemid.equals(
					eu.darkcube.system.lobbysystem.inventory.pserver.gameserver.InventoryGameServerSelection.ITEMID)) {
				user.setOpenInventory(new InventoryLoading(inv.getHandle().getTitle(), u -> {
					JsonObject extra = new Gson().fromJson(itemb.getUnsafe()
							.getString(
									eu.darkcube.system.lobbysystem.inventory.pserver.gameserver.InventoryGameServerSelection.GAMESERVER_META_KEY),
							JsonObject.class);
					ServiceTask task = CloudNetDriver.getInstance()
							.getServiceTaskProvider()
							.getServiceTask(extra.get(
									eu.darkcube.system.lobbysystem.inventory.pserver.gameserver.InventoryGameServerSelection.SERVICETASK)
									.getAsString());
					if (task == null) {
						return inv;
					}
					JsonObject data = cinv.psslot.getData();
					data.addProperty("task", task.getName());
					UniqueId uid = UniqueIdProvider.getInstance().newUniqueId();
					cinv.psslot.load(uid);
					PServerProvider.getInstance().addOwner(cinv.psslot.getPServerId(), user.getUniqueId());

					try {
						Thread.sleep(1000);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}

					return new InventoryPServerConfiguration(user, cinv.psslot);
				}));

			}
		} else if (inv instanceof InventoryPServerConfiguration) {
			InventoryPServerConfiguration cinv = (InventoryPServerConfiguration) inv;
			if (itemid.equals(Item.PSERVER_DELETE.getItemId())) {
				user.setOpenInventory(new InventoryConfirm(cinv.getHandle().getTitle(), () -> {
					cinv.psslot.delete();
					user.setOpenInventory(new InventoryPServerOwn(user));
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
					PServer ps = cinv.psslot.startPServer();
					Lobby.getInstance().getPServerJoinOnStart().register(user, ps);
				});
			} else if (itemid.equals(Item.STOP_PSERVER.getItemId())) {
				AsyncExecutor.service().submit(() -> {
					PServer ps = cinv.psslot.getPServer();
					if (ps != null) {
						ps.stop();
					}
				});
			} else if (itemid.equals(Item.PSERVER_PUBLIC.getItemId())) {
				AsyncExecutor.service().submit(() -> {
					cinv.psslot.getData().addProperty("private", true);
					cinv.psslot.setChanged();
					PServer ps = cinv.psslot.getPServer();
					if (ps != null) {
						ps.setPrivate(true);
					}
					cinv.recalculateAll();
				});
			} else if (itemid.equals(Item.PSERVER_PRIVATE.getItemId())) {
				AsyncExecutor.service().submit(() -> {
					cinv.psslot.getData().addProperty("private", false);
					cinv.psslot.setChanged();
					PServer ps = cinv.psslot.getPServer();
					if (ps != null) {
						ps.setPrivate(false);
					}
					cinv.recalculateAll();
				});
			}
		}
	}

	public static boolean mayJoin(User user, PServer pserver) {
		boolean mayjoin = false;
		if (!pserver.isPrivate()) {
			mayjoin = true;
		}
		if (pserver.getOwners().contains(user.getUniqueId())) {
			mayjoin = true;
		}
		EventPServerMayJoin e = new EventPServerMayJoin(user, pserver, mayjoin);
		Bukkit.getPluginManager().callEvent(e);
		return e.mayJoin();
	}
}
