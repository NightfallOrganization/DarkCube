/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.util;

import eu.darkcube.system.citybuild.skills.Skill;
import eu.darkcube.system.citybuild.skills.SkillDash;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class SkillManager {
    private JavaPlugin plugin;
    private Map<String, Skill> skillMap;

    public SkillManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.skillMap = new HashMap<>();
        registerSkills();
    }

    private void registerSkills() {
        skillMap.put("Dash", new SkillDash());
        // Weitere Skills können hier registriert werden
    }

    public Skill getSkillByName(String name) {
        return skillMap.get(name);
    }

    private List<String> validSkills = new ArrayList<>(Arrays.asList("Dash"));  // Beispiel-Skill

    public boolean isValidSkill(String skill) {
        return validSkills.contains(skill);
    }

    public void assignSkillToSlot(Player player, String skill, int slot) {
        PersistentDataContainer container = player.getPersistentDataContainer();

        for (int i = 1; i <= 4; i++) {
            if (skill.equals(container.get(getKeyName(i), PersistentDataType.STRING))) {
                container.set(getKeyName(i), PersistentDataType.STRING, "Unskilled");
            }
        }

        container.set(getKeyName(slot), PersistentDataType.STRING, skill);
    }

    public String getSkillFromSlot(Player player, int slot) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        return container.has(getKeyName(slot), PersistentDataType.STRING)
                ? container.get(getKeyName(slot), PersistentDataType.STRING)
                : "Unskilled";
    }

    private NamespacedKey getKeyName(int slot) {
        return new NamespacedKey(plugin, "SKILLSLOT_" + slot);
    }
}
