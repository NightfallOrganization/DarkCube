/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.listener.ingame;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.game.Ingame;
import eu.darkcube.minigame.woolbattle.listener.Listener;
import eu.darkcube.minigame.woolbattle.perk.PerkItem;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.system.server.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

public class ListenerBlockPlace extends Listener<BlockPlaceEvent> {
    private final WoolBattleBukkit woolbattle;

    public ListenerBlockPlace(WoolBattleBukkit woolbattle) {
        this.woolbattle = woolbattle;
    }

    @Override @EventHandler public void handle(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        WBUser user = WBUser.getUser(p);
        if (!user.isTrollMode()) {
            if (user.getTeam().isSpectator()) {
                e.setCancelled(true);
                return;
            }
        } else {
            return;
        }
        if (e.getItemInHand() != null) {
            ItemBuilder b = ItemBuilder.item(e.getItemInHand());
            if (b.persistentDataStorage().has(PerkItem.KEY_PERK_ID)) {
                e.setCancelled(true);
                return;
            }
            if (e.getItemInHand().getType() == Material.WOOL) {
                user.removeWool(1, false);
            }
        }
        Block block = e.getBlock();
        Ingame ingame = woolbattle.ingame();
        ingame.placedBlocks.add(block);
    }
}
