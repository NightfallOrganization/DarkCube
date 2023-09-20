/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight.wool;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.util.Vector;


public class WoolBreaker implements Listener {

    private final WoolManager woolManager;
    private final Map<Block, Integer> arrowHitCount = new HashMap<>();

    public WoolBreaker(WoolManager woolManager) {
        this.woolManager = woolManager;
    }

    @EventHandler
    public void onArrowHit(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();

        if (damager instanceof Arrow) {
            Arrow arrow = (Arrow) damager;

            Block hitBlock = getHitBlock(arrow);
            if (hitBlock != null && hitBlock.getType() == Material.WOOL && woolManager.isPlayerPlacedBlock(hitBlock)) {
                int currentHits = arrowHitCount.getOrDefault(hitBlock, 0);
                if (currentHits >= 2) {
                    hitBlock.setType(Material.AIR);
                    arrowHitCount.remove(hitBlock);
                    woolManager.removePlayerPlacedBlock(hitBlock);
                } else {
                    arrowHitCount.put(hitBlock, currentHits + 1);
                }
            }
            arrow.remove(); // Entferne den Pfeil nach der Verarbeitung
        }
    }

    private Block getHitBlock(Arrow arrow) {
        Location loc = arrow.getLocation();
        Vector dir = arrow.getVelocity().normalize();
        for (int i = 0; i < 5; i++) {
            loc.add(dir);
            Block block = loc.getBlock();
            if (block.getType() != Material.AIR) {
                return block;
            }
        }
        return null;
    }
}
