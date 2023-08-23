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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
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
            case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> pattern.add("Ḧ");
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
            String skillName = skillManager.getSkillFromSlot(player, slot);
            if (!skillName.equals("Unskilled")) {
                Skill skill = skillManager.getSkillByName(skillName);
                if (skill != null) {
                    skill.activate(player);
                }
            }
            pattern.clear(); // Clear the pattern after activation
            showClickPatternTitle(player, pattern);
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
