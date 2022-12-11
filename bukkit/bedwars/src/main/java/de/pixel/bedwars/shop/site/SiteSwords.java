/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.shop.site;

import de.pixel.bedwars.shop.ShopItem;

public class SiteSwords extends ShopSite<SiteSwords> {

	public SiteSwords() {
		super(0, ShopItem.S_SWORDS, ShopItem.S_KNOCKBACK_STICK, ShopItem.S_SWORD_1, ShopItem.S_SWORD_2,
				ShopItem.S_SWORD_3);
	}
}
