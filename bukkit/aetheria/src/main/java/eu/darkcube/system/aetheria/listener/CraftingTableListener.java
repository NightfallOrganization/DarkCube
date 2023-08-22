/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.listener;

import eu.darkcube.system.aetheria.Aetheria;
import eu.darkcube.system.aetheria.util.Overlay;
import eu.darkcube.system.util.WorkbenchUtil;
import eu.darkcube.system.version.VersionSupport;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class CraftingTableListener implements Listener {

    private final Aetheria aetheria;

    public CraftingTableListener(Aetheria aetheria) {
        this.aetheria = aetheria;
    }

    @EventHandler public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() != Material.CRAFTING_TABLE) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        if (event.getPlayer().isSneaking()) {
            if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR) return;
            if (event.getPlayer().getInventory().getItemInOffHand().getType() != Material.AIR) return;
        }

        event.setCancelled(true);  // Verhindert das normale Öffnen des Crafting Tables

        Player player = event.getPlayer();

        VersionSupport
                .version()
                .provider()
                .service(WorkbenchUtil.class)
                .openWorkbench(player, event.getClickedBlock().getLocation(), false, Overlay.CRAFTING_TABLE.darkcubeComponent());

    }
}
