package eu.darkcube.system.lobbysystem.inventory.pserver;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.event.EventListener;
import eu.darkcube.system.inventory.api.util.ItemBuilder;
import eu.darkcube.system.inventory.api.v1.IInventory;
import eu.darkcube.system.inventory.api.v1.InventoryType;
import eu.darkcube.system.lobbysystem.inventory.abstraction.LobbyAsyncPagedInventory;
import eu.darkcube.system.lobbysystem.listener.ListenerPServer;
import eu.darkcube.system.lobbysystem.pserver.PServerDataManager;
import eu.darkcube.system.lobbysystem.user.User;
import eu.darkcube.system.lobbysystem.util.Item;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.lobbysystem.util.SkullCache;
import eu.darkcube.system.pserver.common.PServer;
import eu.darkcube.system.pserver.common.PServer.State;
import eu.darkcube.system.pserver.common.PServerProvider;
import eu.darkcube.system.pserver.wrapper.event.PServerUpdateEvent;

public class InventoryPServer extends LobbyAsyncPagedInventory {

	public static final String ITEMID = "lobbysystem.pserver.publiclist";

	public static final String META_KEY_PSERVER = "lobbysystem.pserver.id";

	private static final InventoryType type_pserver = InventoryType.of("pserver");

	private Listener listener;

	public InventoryPServer(User user) {
		super(InventoryPServer.type_pserver, Item.PSERVER_MAIN_ITEM.getDisplayName(user), user);
//		super(Item.PSERVER_MAIN_ITEM.getDisplayName(user), InventoryPServer.type_pserver);
		this.listener = new Listener();
		this.listener.register();
	}

	@Override
	protected void insertFallbackItems() {
		this.fallbackItems.put(IInventory.slot(1, 3), Item.LIME_GLASS_PANE.getItem(this.user));
		this.fallbackItems.put(IInventory.slot(1, 4), Item.INVENTORY_PSERVER_PUBLIC.getItem(this.user));
		this.fallbackItems.put(IInventory.slot(1, 5), Item.LIME_GLASS_PANE.getItem(this.user));
		this.fallbackItems.put(IInventory.slot(1, 6), Item.INVENTORY_PSERVER_PRIVATE.getItem(this.user));
		super.insertFallbackItems();
	}

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		super.fillItems(items);
		SortedMap<Long, ItemStack> sitems = new TreeMap<>();

		for (PServer ps : PServerProvider.getInstance().getPServers()) {
			if (ps.getState() != State.RUNNING || !ListenerPServer.mayJoin(this.user, ps)) {
				continue;
			}
			boolean publicServer = ps.isPublic();
			int online = ps.getOnlinePlayers();
			long ontime = ps.getOntime();
			UUID owner = ps.getOwners().stream().findAny().orElse(null);

			ItemBuilder b = null;
			boolean skull = false;

			if (ps.isGamemode()) {
				b = PServerDataManager.getDisplayItemGamemode(this.user, ps.getTaskName());
			}
			if (b == null) {
				b = new ItemBuilder(owner == null ? Material.BARRIER : Material.SKULL_ITEM)
						.durability((short) SkullType.PLAYER.ordinal());
				skull = b.getMaterial() == Material.SKULL_ITEM;
			}
			if (skull) {
				b.meta(SkullCache.getCachedItem(owner).getItemMeta());
			}
			b.unsafeStackSize(true).amount(online);
			b.displayname(Message.PSERVER_ITEM_TITLE.getMessage(this.user, ps.getServerName()));
			b.lore(publicServer ? Message.CLICK_TO_JOIN.getMessage(this.user)
					: Message.PSERVER_NOT_PUBLIC.getMessage(this.user));
			b.build();
			b.meta(null);
			Item.setItemId(b, InventoryPServer.ITEMID);
			b.getUnsafe().setString(InventoryPServer.META_KEY_PSERVER, ps.getId().toString());

			sitems.put(ontime, b.build());
		}

		int slot = 0;
		for (long ontime : sitems.keySet()) {
			items.put(slot, sitems.get(ontime));
			slot++;
		}
	}

	@Override
	protected void destroy() {
		super.destroy();
		this.listener.unregister();
	}

	public class Listener {

		public void register() {
			CloudNetDriver.getInstance().getEventManager().registerListener(this);
		}

		public void unregister() {
			CloudNetDriver.getInstance().getEventManager().unregisterListener(this);
		}

		@EventListener
		public void handle(PServerUpdateEvent event) {
			InventoryPServer.this.recalculate();
		}

	}

}
