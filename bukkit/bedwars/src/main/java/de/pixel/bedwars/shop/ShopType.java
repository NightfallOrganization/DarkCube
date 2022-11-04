package de.pixel.bedwars.shop;

import static de.pixel.bedwars.shop.site.ShopSite.*;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;

import de.pixel.bedwars.shop.site.*;
import de.pixel.bedwars.util.*;
import eu.darkcube.system.inventory.api.util.*;

public enum ShopType {

	DEFAULT(Message.SHOP_INVENTORY_TITLE, BUILDING_BLOCKS, SWORDS, PICKAXES)

	;

	private final Message title;
	private final ShopSite<?>[] sites;
	private final int sitesSlots;

	private ShopType(Message title, ShopSite<?>... sites) {
		this.title = title;
		this.sites = sites;
		this.sitesSlots = ShopItem.getSlots(this.sites.length);
	}

	public final ShopSite<?>[] getSites() {
		return sites;
	}

	public final Inventory buildInventory(Player p, ShopSite<?> site) {
		final int slots = sitesSlots + 9 + site.getSize();
		final Inventory inv = Bukkit.createInventory(p, slots, title.getMessage(p));
		final ItemStack glassPane =
				new ItemBuilder(Material.STAINED_GLASS_PANE).durability((short) 7).displayname(" ").build();
		for (int i = 0; i < sites.length; i++) {
			inv.setItem(i, sites[i].getRepresentation().getItem(p));
		}
		for (int i = sitesSlots; i < sitesSlots + 9; i++) {
			inv.setItem(i, glassPane);
		}
		for (int i = slots - site.getSize(), j = 0; i < slots; i++, j++) {
			final ShopItem item = site.getItem(j);
			if (item != null) {
				inv.setItem(i, item.getItem(p));
			}
		}
		return inv;
	}
	
	public static ShopType getType(Player p) {
		return ShopType.DEFAULT;
	}
}
