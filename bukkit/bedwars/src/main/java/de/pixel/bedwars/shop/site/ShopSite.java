/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.shop.site;

import java.util.Collection;
import java.util.HashSet;

import de.pixel.bedwars.shop.ShopItem;

public abstract class ShopSite<SELF extends ShopSite<SELF>> {

	public static final Collection<ShopSite<?>> SITES = new HashSet<>();
	
	public static final ShopSite<SiteBuildingBlocks> BUILDING_BLOCKS = new SiteBuildingBlocks();
	public static final ShopSite<SiteSwords> SWORDS = new SiteSwords();
	public static final ShopSite<SitePickaxes> PICKAXES = new SitePickaxes();

	private SELF self;
	private final int size;
	private final ShopItem representation;
	private final ShopItem[] items;

	@SuppressWarnings("unchecked")
	public ShopSite(final int size, final ShopItem representation, final ShopItem... items) {
		SITES.add(this);
		self = (SELF) this;
		this.representation = representation;
		this.size = ShopItem.getSlots(size);
		this.items = new ShopItem[this.size];
		for (int i = 0; i < this.items.length && i < items.length; i++) {
			this.items[i] = items[i];
		}
	}

	public SELF getSelf() {
		return self;
	}

	public final int getSize() {
		return size;
	}
	
	public ShopItem getRepresentation() {
		return representation;
	}

	public final ShopItem getItem(int index) {
		return items[index];
	}
}
