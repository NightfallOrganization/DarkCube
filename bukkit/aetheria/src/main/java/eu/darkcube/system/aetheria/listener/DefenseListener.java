/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package eu.darkcube.system.aetheria.listener;

import eu.darkcube.system.aetheria.util.DefenseManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class DefenseListener implements Listener {
    private final DefenseManager defenseManager;

    public DefenseListener(DefenseManager defenseManager) {
        this.defenseManager = defenseManager;// 19
    }// 20

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player)event.getEntity();
            double defense = this.defenseManager.getDefense(player); // geändert von int zu double
            double finalDamage = Math.max(event.getDamage() - defense, 0.0);
            event.setDamage(finalDamage);
        }
    }
}
