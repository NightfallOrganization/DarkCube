/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.skills;

import org.bukkit.entity.Player;

import java.util.HashMap;

public abstract class Skill {
    protected String name;
    protected static final long COOLDOWN_IN_SECONDS = 10; // Zum Beispiel 10 Sekunden
    protected HashMap<Player, Long> cooldowns = new HashMap<>();
    private boolean isActive;

    public Skill(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public final void activate(Player player) {
        if (!isActive) {
            player.sendMessage("§7Der Skill §a" + name + " §7ist ein passiver Skill und kann nicht aktiviert werden.");
            return;
        }
        if (canUse(player)) {
            activateSkill(player);
            cooldowns.put(player, System.currentTimeMillis());
        } else {
            long timeLeft = (cooldowns.get(player) + COOLDOWN_IN_SECONDS * 1000 - System.currentTimeMillis()) / 1000;
            player.sendMessage("§7Du musst noch §a" + timeLeft + " §7Sekunden warten, bevor du diesen Skill wieder verwenden kannst");
        }
    }

    public Skill(String name, boolean isActive) {
        this.name = name;
        this.isActive = isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    protected abstract void activateSkill(Player player);

    private boolean canUse(Player player) {
        if (!cooldowns.containsKey(player)) {
            return true;
        }

        long timeSinceLastUse = System.currentTimeMillis() - cooldowns.get(player);
        return timeSinceLastUse >= COOLDOWN_IN_SECONDS * 1000;
    }
}
