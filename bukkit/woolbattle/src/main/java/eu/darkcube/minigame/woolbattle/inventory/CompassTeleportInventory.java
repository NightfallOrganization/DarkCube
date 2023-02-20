/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.inventory;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.team.TeamType;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.system.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.inventoryapi.item.meta.SkullBuilderMeta;
import eu.darkcube.system.inventoryapi.item.meta.SkullBuilderMeta.UserProfile;
import eu.darkcube.system.inventoryapi.v1.IInventoryClickEvent;
import eu.darkcube.system.inventoryapi.v1.InventoryType;
import eu.darkcube.system.util.data.Key;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class CompassTeleportInventory extends WoolBattlePagedInventory {
	public static final InventoryType TYPE = InventoryType.of("woolbattle-compass-teleport");
	private static final Key USER = new Key(WoolBattle.getInstance(), "tpUserId");

	public CompassTeleportInventory(WBUser user) {
		super(TYPE, Message.INVENTORY_COMPASS.getMessage(user), user);
	}

	@Override
	protected void inventoryClick(IInventoryClickEvent event) {
		event.setCancelled(true);
		if (event.item() == null)
			return;
		String uid = ItemManager.getId(event.item(), USER);
		if (uid == null)
			return;
		UUID uuid = UUID.fromString(uid);
		WBUser user = WBUser.getUser(Bukkit.getPlayer(uuid));
		if (user == null) {
			recalculate();
			return;
		}
		this.user.getBukkitEntity().teleport(user.getBukkitEntity());
		this.user.getBukkitEntity().closeInventory();
	}

	@Override
	protected void fillItems(Map<Integer, ItemStack> items) {
		int i = 0;
		for (WBUser user : WBUser.onlineUsers()) {
			if (user.getTeam().getType() == TeamType.SPECTATOR) {
				continue;
			}
			ItemBuilder b = ItemBuilder.item(Material.SKULL_ITEM)
					.meta(new SkullBuilderMeta(new UserProfile(null, user.getUniqueId()))).damage(3)
					.displayname(user.getTeamPlayerName());
			ItemManager.setId(b, USER, user.getUniqueId().toString());
			items.put(i++, b.build());
		}
	}
}
