package eu.darkcube.minigame.woolbattle.listener.ingame;

import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.user.User;
import eu.darkcube.minigame.woolbattle.util.Arrays;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;

public class ListenerInventoryClick extends Listener<InventoryClickEvent> {
	public static ListenerInventoryClick instance;

	public ListenerInventoryClick() {
		instance = this;
	}

	@Override
	@EventHandler(priority = EventPriority.HIGH)
	public void handle(InventoryClickEvent e) {
		if (!(e.getWhoClicked() instanceof Player))
			return;
		if (e.isCancelled())
			return;

		int hotbarSlot = e.getHotbarButton();
		int slot = e.getRawSlot();

		Player p = (Player) e.getWhoClicked();
		User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
		if (user.getTeam().getType() == TeamType.SPECTATOR) {
			e.setCancelled(true);
			if (user.getOpenInventory() != null) {
				switch (user.getOpenInventory()) {
				case COMPASS_TELEPORT:
					ItemStack item = e.getCurrentItem();
					if (item != null && item.getType() == Material.SKULL_ITEM) {
						SkullMeta meta = (SkullMeta) item.getItemMeta();
						Player t = Bukkit.getPlayerExact(meta.getOwner());
						if (t != null) {
							p.teleport(t);
							p.closeInventory();
						}
					}
					break;
				default:
					break;
				}
			}
			return;
		}
		if (e.getView().getType() == InventoryType.CRAFTING) {
			if (slot == 0 || slot == 1 || slot == 2 || slot == 3 || slot == 4 || slot == -999 || slot == -1 || slot == 5
					|| slot == 6 || slot == 7 || slot == 8) {
				if (!user.isTrollMode()) {
					e.setCancelled(true);
				}
				return;
			}
		} else if (!user.isTrollMode() && p.getGameMode() == GameMode.SURVIVAL) {
			e.setCancelled(true);
			return;
		} else {
			return;
		}

		ItemStack item = e.getCurrentItem();
		ItemStack cursor = e.getCursor();
//		Otherwise there is the possibility that I get the NORMAL slot 26 of the inventory and not a hotbarslot.
		ItemStack hotbar = hotbarSlot == -1 ? null : e.getView().getItem(hotbarSlot + 36);

		boolean var1 = item != null && item.getType() != Material.AIR;
		boolean var2 = cursor != null && cursor.getType() != Material.AIR;
		boolean var3 = hotbar != null && hotbar.getType() != Material.AIR;

		String tagItem = var1 ? ItemManager.getItemId(item) : "Unknown Perk";
		String tagCursor = var2 ? ItemManager.getItemId(cursor) : "Unknown Perk";
		String tagHotbar = var3 ? ItemManager.getItemId(hotbar) : "Unknown Perk";

		Handle[] handles = new Handle[0];
//			Arrays.addAfter(handles,
//					new Handle(user.getData().getPerks().getClass().getMethod("setSlotArrow", int.class),
//							user.getData().getPerks(), Item.DEFAULT_ARROW.getItemId()));
//			Arrays.addAfter(handles, new Handle(user.getData().getPerks().getClass().getMethod("setSlotBow", int.class),
//					user.getData().getPerks(), Item.DEFAULT_BOW.getItemId()));
//			Arrays.addAfter(handles,
//					new Handle(user.getData().getPerks().getClass().getMethod("setSlotShears", int.class),
//							user.getData().getPerks(), Item.DEFAULT_SHEARS.getItemId()));
//			Arrays.addAfter(handles, new Handle(user.getEnderPearl()));
		handles = Arrays.addAfter(handles,
				new Handle(user.getData().getPerks()::setSlotBow, Item.DEFAULT_BOW.getItemId()),
				new Handle(user.getData().getPerks()::setSlotArrow, Item.DEFAULT_ARROW.getItemId()),
//				new Handle(user.getData().getPerks()::setSlotPearl, Item.DEFAULT_PEARL.getItemId()),
				new Handle(user.getData().getPerks()::setSlotShears, Item.DEFAULT_SHEARS.getItemId()));
		for (Perk perk : Arrays.asList(user.getActivePerk1(), user.getActivePerk2(), user.getPassivePerk(),
				user.getEnderPearl())) {
			handles = Arrays.addAfter(handles, new Handle(perk));
		}

//		, user.getEnderPearl()
//		for (Perk perk : Arrays.asList(user.getActivePerk1(), user.getActivePerk2(), user.getPassivePerk())) {

//		System.out.println("Slot: " + slot);
//		System.out.println("HotbarButton: " + hotbarSlot);
//		System.out.println("Cursor: " + cursor);
//		System.out.println("Item: " + e.getCurrentItem());
//		System.out.println("Action" + e.getAction());
//		System.out.println("TagItem: " + tagItem);s
		for (Handle handle : handles) {
//			String tag = ItemManager.getItemId(perk.calculateItem());
			String tag = handle.getTag();
//			System.out.println("Tag: " + tag);
			if (var1 || var2 || var3) {
				switch (e.getAction()) {
				case HOTBAR_SWAP:
					if (tag.equals(tagHotbar)) {
						handle.invoke(slot);
//						perk.setSlotSilent(slot);
					} else if (tag.equals(tagItem)) {
						handle.invoke(hotbarSlot + 36);
//						perk.setSlotSilent(hotbarSlot + 36);
					} else if (tag.equals(tagCursor)) {
						tue(tagHotbar, tagItem, tagCursor, tag);
					}
					break;
				case CLONE_STACK:
					if (tag.equals(tagHotbar) || tag.equals(tagCursor)) {
						tue(tagHotbar, tagItem, tagCursor, tag);
					}
					break;
				case COLLECT_TO_CURSOR:
					if (tag.equals(tagHotbar)) {
						tue(tagHotbar, tagItem, tagCursor, tag);
					} else if (tag.equals(tagCursor)) {
						WoolBattle.getInstance().sendConsole("Player " + p.getName() + " had slot error. Values:");
						System.out.println("Slot: " + slot);
						System.out.println("Tag: " + tag);
						System.out.println("HotbarButton: " + hotbarSlot);
						System.out.println("Cursor: " + cursor);
					}
					break;
				case DROP_ALL_CURSOR:
				case DROP_ALL_SLOT:
				case DROP_ONE_CURSOR:
				case DROP_ONE_SLOT:
					if (item != null && item.getType() == Material.WOOL) {
						break;
					}
				case MOVE_TO_OTHER_INVENTORY:
					if (item != null) {
						if (item.getType() == Material.WOOL) {
							break;
						}
//						Main.getInstance().sendMessage(tagItem);
//						Main.getInstance().sendMessage(tagHotbar);
//						Main.getInstance().sendMessage(tagCursor);
						e.setCancelled(true);
					}
					break;
				case HOTBAR_MOVE_AND_READD:
					e.setCancelled(true);
					break;
				case NOTHING:
					break;
				case PICKUP_ALL:
					if (tag.equals(tagItem)) {
						handle.invoke(100);
//						perk.setSlotSilent(100);
					} else if (tag.equals(tagHotbar) || tag.equals(tagCursor)) {
						tue(tagHotbar, tagItem, tagCursor, tag);
					}
					break;
				case PICKUP_HALF:
					if (tag.equals(tagItem)) {
						e.setCancelled(true);
					} else if (tag.equals(tagHotbar) || tag.equals(tagCursor)) {
						tue(tagHotbar, tagItem, tagCursor, tag);
					}
					break;
				case PICKUP_ONE:
					if (tag.equals(tagItem)) {
						e.setCancelled(true);
					} else if (tag.equals(tagHotbar) || tag.equals(tagCursor)) {
						tue(tagHotbar, tagItem, tagCursor, tag);
					}
					break;
				case PICKUP_SOME:
					if (tag.equals(tagItem)) {
						e.setCancelled(true);
					} else if (tag.equals(tagHotbar) || tag.equals(tagCursor)) {
						tue(tagHotbar, tagItem, tagCursor, tag);
					}
					break;
				case PLACE_ALL:
					if (tag.equals(tagCursor)) {
						handle.invoke(slot);
//						perk.setSlotSilent(slot);
					} else if (tag.equals(tagItem)) {
						e.setCancelled(true);
					} else if (tag.equals(tagHotbar)) {
						tue(tagHotbar, tagItem, tagCursor, tag);
					}
					break;
				case PLACE_ONE:
					if (tag.equals(tagCursor)) {
//						e.setCancelled(true);
						new Scheduler() {
							@Override
							public void run() {
								handle.invoke(slot);
								handle.update();
								e.getView().setCursor(null);
							}
						}.runTask();
					} else if (tag.equals(tagHotbar) || tag.equals(tagItem)) {
						tue(tagHotbar, tagItem, tagCursor, tag);
					}
					break;
				case PLACE_SOME:
					if (tag.equals(tagCursor)) {
						e.setCancelled(true);
					} else if (tag.equals(tagHotbar) || tag.equals(tagItem)) {
						tue(tagHotbar, tagItem, tagCursor, tag);
					}
					break;
				case SWAP_WITH_CURSOR:
					if (tag.equals(tagItem)) {
						handle.invoke(100);
//						perk.setSlotSilent(100);
					} else if (tag.equals(tagCursor)) {
						handle.invoke(slot);
//						perk.setSlotSilent(slot);
					} else if (tag.equals(tagHotbar)) {
						throw new UnknownError("Can not access this code");
					}
					break;
				case UNKNOWN:
					tue(tagHotbar, tagItem, tagCursor, tag);
					break;
				}
			}

		}

	}

	@EventHandler
	public void handle(InventoryCloseEvent e) {
		if (!(e.getPlayer() instanceof Player)) {
			return;
		}
		ItemStack cursor = e.getView().getCursor();
//		System.out.println(cursor);
		if (cursor == null || cursor.getType() == Material.AIR) {
			return;
		}
		String tag = ItemManager.getItemId(cursor);
//		System.out.println(tag);
		if (tag == null)
			return;
		Player p = (Player) e.getPlayer();
		User user = WoolBattle.getInstance().getUserWrapper().getUser(p.getUniqueId());
		Handle[] handles = new Handle[0];
		handles = Arrays.addAfter(handles,
				new Handle(user.getData().getPerks()::setSlotBow, Item.DEFAULT_BOW.getItemId()),
				new Handle(user.getData().getPerks()::setSlotArrow, Item.DEFAULT_ARROW.getItemId()),
				new Handle(user.getData().getPerks()::setSlotShears, Item.DEFAULT_SHEARS.getItemId()));
		for (Perk perk : Arrays.asList(user.getActivePerk1(), user.getActivePerk2(), user.getPassivePerk(),
				user.getEnderPearl())) {
			handles = Arrays.addAfter(handles, new Handle(perk));
		}

		for (Handle handle : handles) {
			if (tag.equals(handle.getTag())) {
//				System.out.println(1);
				PlayerInventory inv = p.getInventory();
				int first = inv.firstEmpty();
				if (-first == 1) {
					first = inv.first(Material.WOOL);
				}
				e.getView().setCursor(null);
				inv.setItem(first, cursor);
				handle.invoke(first < 9 ? first + 36 : first);
			}
		}
	}

	public static class Handle {

		private final Consumer<Integer> consumer;
		private final String tag;
		private Perk perk;

		Handle(Perk perk) {
			this(perk::setSlotSilent, ItemManager.getItemId(perk.calculateItem()));
			this.perk = perk;
		}

		Handle(Consumer<Integer> consumer, String tag) {
			this.consumer = consumer;
			this.tag = tag;
		}

		public String getTag() {
			return tag;
		}
		
		public void update() {
			if(perk != null) {
				perk.setItem();
			}
		}

		public void invoke(int slot) {
//			System.out.println("new sot: " + slot);
			consumer.accept(slot);
		}
	}

	private void tue(String tagHotbar, String tagItem, String tagCursor, String tag) {
		throw new UnknownError("Can not access this code: hotbar: " + tagHotbar + ", item: " + tagItem + ", cursor: "
				+ tagCursor + ", perkTag: " + tag);
	}
}