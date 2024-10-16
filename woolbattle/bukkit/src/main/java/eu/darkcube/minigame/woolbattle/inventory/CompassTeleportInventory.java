/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.inventory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.ItemManager;
import eu.darkcube.system.bukkit.inventoryapi.v1.IInventoryClickEvent;
import eu.darkcube.system.bukkit.inventoryapi.v1.InventoryType;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.component.ItemComponent;
import eu.darkcube.system.server.item.component.components.HeadProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CompassTeleportInventory extends WoolBattlePagedInventory {
    public static final InventoryType TYPE = InventoryType.of("woolbattle_compass_teleport");
    private final Key USER;
    private boolean done;

    public CompassTeleportInventory(WoolBattleBukkit woolbattle, WBUser user) {
        super(woolbattle, TYPE, Message.INVENTORY_COMPASS.getMessage(user), user);
        USER = Key.key(woolbattle, "tp_user_id");
        done = true;
        complete();
    }

    @Override
    public boolean done() {
        return super.done() && done;
    }

    @Override
    protected void inventoryClick(IInventoryClickEvent event) {
        event.setCancelled(true);
        if (event.item() == null) return;
        String uid = ItemManager.getId(event.item(), USER);
        if (uid == null) return;
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
            if (user.getTeam().isSpectator()) {
                continue;
            }
            ItemBuilder b = ItemBuilder.item(Material.SKULL_ITEM).set(ItemComponent.PROFILE, new HeadProfile(user.getPlayerName(), null, List.of())).damage(3).displayname(user.getTeamPlayerName());
            ItemManager.setId(b, USER, user.getUniqueId().toString());
            items.put(i++, b.build());
        }
    }
}
