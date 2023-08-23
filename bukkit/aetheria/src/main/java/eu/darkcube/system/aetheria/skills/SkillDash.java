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

public class SkillDash extends Skill {

    private static final long COOLDOWN_IN_SECONDS = 10; // Zum Beispiel 10 Sekunden
    private HashMap<Player, Long> cooldowns;// TODO use persistent data storage

    public SkillDash() {
        super("Dash");
        this.cooldowns = new HashMap<>();
    }

    @Override public void activate(Player player) {
        if (canUse(player)) {
            Vector direction = player.getLocation().getDirection();

            // Begrenze die Y-Komponente des Vektors.
            if (direction.getY() > 0.4) {
                direction.setY(0.4);
            }

            player.setVelocity(direction.multiply(2));
            player.sendMessage("§7Dash Skill §aactivated§7!");

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
