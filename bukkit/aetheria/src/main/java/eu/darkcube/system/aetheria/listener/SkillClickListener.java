/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.listener;

import eu.darkcube.system.aetheria.Aetheria;
import eu.darkcube.system.aetheria.skills.Skill;
import eu.darkcube.system.aetheria.util.SkillManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class SkillClickListener implements Listener {
    private SkillManager skillManager;
    private HashMap<Player, LinkedList<String>> clickPatterns = new HashMap<>();
    private HashMap<Player, Long> lastClickTime = new HashMap<>(); // TODO use persistent data storage
    private HashMap<Player, BukkitTask> clickResetTasks = new HashMap<>();

    public SkillClickListener(SkillManager skillManager) {
        this.skillManager = skillManager;
    }

    @EventHandler public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (action == Action.RIGHT_CLICK_BLOCK || (action == Action.RIGHT_CLICK_AIR && isArmor(player.getInventory().getItemInMainHand()))) {
            return;  // Ignoriere das Event
        }

        if (clickResetTasks.containsKey(player)) {
            clickResetTasks.get(player).cancel();
            clickResetTasks.remove(player);
        }

        // Erstellen Sie einen neuen Timer für den Spieler, um den Klickzustand nach 3 Sekunden zurückzusetzen
        BukkitTask resetTask = new BukkitRunnable() {
            @Override public void run() {
                clickPatterns.remove(player);  // Hier setzen wir das Klicken zurück
                clickResetTasks.remove(player); // Entfernen Sie den Timer aus der Map, da er bereits ausgeführt wurde
            }
        }.runTaskLater(Aetheria.getInstance(), 60L); // 60 Ticks entsprechen 3 Sekunden (20 Ticks = 1 Sekunde)

        // Speichern Sie den Timer in der Map
        clickResetTasks.put(player, resetTask);
        long currentTime = System.currentTimeMillis();
        if (lastClickTime.containsKey(player) && (currentTime - lastClickTime.get(player)) < 10) {
            return;
        }

        lastClickTime.put(player, currentTime);
        LinkedList<String> pattern = clickPatterns.getOrDefault(player, new LinkedList<>());

        switch (event.getAction()) {
            case RIGHT_CLICK_AIR -> pattern.add("Ḧ");
            case LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> pattern.add("ḧ");
            default -> {
                return;
            }
        }



        // Muster bereinigen, wenn es nur Linksklicks enthält
        while (!pattern.isEmpty() && pattern.getFirst().equals("ḧ")) {
            pattern.poll();
        }

        clickPatterns.put(player, pattern);

        // Zeigen Sie den aktualisierten Titel
        showClickPatternTitle(player, pattern);

        if (pattern.size() > 3) {
            pattern.poll(); // remove first element if size exceeds 3
        }

        clickPatterns.put(player, pattern);

        // Check if the pattern matches one of the desired combinations
        int slot = -1;
        if (pattern.equals(makePattern("Ḧ", "Ḧ", "Ḧ"))) {
            slot = 1;
        } else if (pattern.equals(makePattern("Ḧ", "ḧ", "Ḧ"))) {
            slot = 2;
        } else if (pattern.equals(makePattern("Ḧ", "Ḧ", "ḧ"))) {
            slot = 3;
        } else if (pattern.equals(makePattern("Ḧ", "ḧ", "ḧ"))) {
            slot = 4;
        }

        if (slot != -1) {
            Skill skill = skillManager.getSkillFromSlot(player, slot);
            if (skill != null && !skill.getName().equals("Unskilled")) {
                skill.activate(player);
                pattern.clear(); // Clear the pattern after activation
                showClickPatternTitle(player, pattern);
            }
            pattern.clear(); // Clear the pattern after activation
            showClickPatternTitle(player, pattern);
        }
    }

    private boolean isArmor(ItemStack item) {
        if (item == null) return false;

        Material material = item.getType();
        switch (material) {
            case LEATHER_HELMET, LEATHER_CHESTPLATE, LEATHER_LEGGINGS, LEATHER_BOOTS,
                    IRON_HELMET, IRON_CHESTPLATE, IRON_LEGGINGS, IRON_BOOTS,
                    DIAMOND_HELMET, DIAMOND_CHESTPLATE, DIAMOND_LEGGINGS, DIAMOND_BOOTS,
                    NETHERITE_HELMET, NETHERITE_CHESTPLATE, NETHERITE_LEGGINGS, NETHERITE_BOOTS,
                    GOLDEN_HELMET, GOLDEN_CHESTPLATE, GOLDEN_LEGGINGS, GOLDEN_BOOTS,
                    CHAINMAIL_HELMET, CHAINMAIL_CHESTPLATE, CHAINMAIL_LEGGINGS, CHAINMAIL_BOOTS,
                    TURTLE_HELMET:
                return true;
            default:
                return false;
        }
    }

    private void showClickPatternTitle(Player player, List<String> pattern) {
        // Erzeugen Sie einen String aus dem aktuellen Muster
        StringBuilder patternString = new StringBuilder();
        boolean startAdding = false; // Ein Flag, um zu überprüfen, ob wir beginnen sollen, zum StringBuilder hinzuzufügen

        for (String action : pattern) {
            if (action.equals("Ḧ")) {
                startAdding = true;
            }

            if (startAdding) {
                patternString.append(action).append("Ḩ");
            }
        }

        // Entfernen Sie das letzte "-" Zeichen
        if (!patternString.isEmpty()) {
            patternString.setLength(patternString.length() - 1);
        }

        // Zeigen Sie es als Titel für den Spieler
        player.sendTitle("§x§4§e§5§c§2§4" + patternString, "", 3, 40, 3);
    }

    private LinkedList<String> makePattern(String... actions) {
        LinkedList<String> pattern = new LinkedList<>();
        Collections.addAll(pattern, actions);
        return pattern;
    }
}
