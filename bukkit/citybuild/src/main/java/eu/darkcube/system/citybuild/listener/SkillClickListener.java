/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.listener;

import eu.darkcube.system.citybuild.util.SkillManager;
import eu.darkcube.system.citybuild.skills.Skill;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.LinkedList;

public class SkillClickListener implements Listener {
    private SkillManager skillManager;
    private HashMap<Player, LinkedList<String>> clickPatterns = new HashMap<>();
    private HashMap<Player, Long> lastClickTime = new HashMap<>();

    public SkillClickListener(SkillManager skillManager) {
        this.skillManager = skillManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        // Überprüfen, ob der Spieler ein Item in der Hand hat
        if (player.getInventory().getItemInMainHand() == null || player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (lastClickTime.containsKey(player) && (currentTime - lastClickTime.get(player)) < 500) {
            return;
        }
        lastClickTime.put(player, currentTime);
        LinkedList<String> pattern = clickPatterns.getOrDefault(player, new LinkedList<>());

        switch (event.getAction()) {
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
                pattern.add("R");
                player.sendMessage("§7Du hast Rechtsklick gemacht!");
                break;
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                pattern.add("L");
                player.sendMessage("§7Du hast Linksklick gemacht!");
                break;
            default:
                return;
        }

        if (pattern.size() > 3) {
            pattern.poll(); // remove first element if size exceeds 3
        }

        clickPatterns.put(player, pattern);

        // Check if the pattern matches one of the desired combinations
        int slot = -1;
        if (pattern.equals(makePattern("R", "R", "R"))) {
            slot = 1;
        } else if (pattern.equals(makePattern("R", "L", "R"))) {
            slot = 2;
        } else if (pattern.equals(makePattern("R", "R", "L"))) {
            slot = 3;
        } else if (pattern.equals(makePattern("R", "L", "L"))) {
            slot = 4;
        }

        if (slot != -1) {
            String skillName = skillManager.getSkillFromSlot(player, slot);
            if (!skillName.equals("Unskilled")) {
                Skill skill = skillManager.getSkillByName(skillName);
                if (skill != null) {
                    skill.activate(player);
                }
            }
            pattern.clear(); // Clear the pattern after activation
        }
    }

    @EventHandler
    public void onPlayerAnimate(PlayerAnimationEvent event) {
        if (event.getAnimationType() == PlayerAnimationType.ARM_SWING) {
            Player player = event.getPlayer();
            // Behandeln Sie dies als Rechtsklick in die Luft
            // Hier können Sie Ihren bisherigen Code zum Hinzufügen zum Klickmuster und zum Überprüfen des Musters einfügen.
        }
    }

    private LinkedList<String> makePattern(String... actions) {
        LinkedList<String> pattern = new LinkedList<>();
        for (String action : actions) {
            pattern.add(action);
        }
        return pattern;
    }
}
