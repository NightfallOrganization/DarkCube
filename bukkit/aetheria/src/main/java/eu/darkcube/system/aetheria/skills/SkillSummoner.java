/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.skills;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class SkillSummoner extends Skill {

    private static final long COOLDOWN_IN_SECONDS = 15; // Zum Beispiel 15 Sekunden
    private HashMap<Player, Long> cooldowns;// TODO use persistent data storage

    public SkillSummoner() {
        super("Beschwörer");
        this.cooldowns = new HashMap<>();
    }

    @Override public void activate(Player player) {
        if (canUse(player)) {
            // Hier könnten Sie den Beschwörungs-Effekt hinzufügen. Zum Beispiel:
            // player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE); // Einen Zombie beschwören

            player.sendMessage("§7Beschwörer Skill §aaktiviert§7!"); // Nachricht an den Spieler

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
