/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.skills;

import org.bukkit.entity.Player;
import java.util.HashMap;

public class SkillWindBlades extends Skill {

    private static final long COOLDOWN_IN_SECONDS = 10; // 10 Sekunden als Beispiel. Sie können dies anpassen.
    private HashMap<Player, Long> cooldowns;

    public SkillWindBlades() {
        super("Windklingen");
        this.cooldowns = new HashMap<>();
    }

    @Override
    public void activate(Player player) {
        if (canUse(player)) {
            // Hier fügen Sie die spezifische Funktionalität für den Windklingen-Skill hinzu.
            // Zum Beispiel könnten die Windklingen einen Schaden um den Spieler herum verursachen.
            // Dies ist nur ein Platzhalter und sollte durch die tatsächliche Fähigkeit ersetzt werden.
            player.sendMessage("§7Windklingen Skill §aaktiviert§7!");

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
