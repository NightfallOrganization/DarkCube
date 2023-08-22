/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.skills;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class SkillVerticalDash extends Skill {

    private static final long COOLDOWN_IN_SECONDS = 10; // Zum Beispiel 10 Sekunden
    private HashMap<Player, Long> cooldowns;

    public SkillVerticalDash() {
        super("VerticalDash");
        this.cooldowns = new HashMap<>();
    }

    @Override public void activate(Player player) {
        if (canUse(player)) {
            Vector direction = player.getLocation().getDirection();
            direction.setY(0); // Y-Komponente ignorieren
            direction.normalize();

            Vector velocity = direction.multiply(1);
            velocity.setY(1.5);

            player.setVelocity(velocity);
            player.sendMessage("§7VerticalDash Skill §aactivated§7!");

            cooldowns.put(player, System.currentTimeMillis());
        } else {
            long timeLeft = (cooldowns.get(player) + COOLDOWN_IN_SECONDS * 1000 - System.currentTimeMillis()) / 1000;
            player.sendMessage("§7Du musst noch §a" + timeLeft + " §7Sekunden warten, bevor du diesen Skill wieder verwenden kannst");
        }
    }

    private boolean canUse(Player player) {
        if (!cooldowns.containsKey(player)) {
            return true;
        }

        long timeSinceLastUse = System.currentTimeMillis() - cooldowns.get(player);
        return timeSinceLastUse >= COOLDOWN_IN_SECONDS * 1000;
    }
}
