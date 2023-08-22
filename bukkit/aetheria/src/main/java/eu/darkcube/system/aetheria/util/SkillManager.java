/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.util;

import eu.darkcube.system.aetheria.skills.*;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class SkillManager {

    public static final String SKILL_PREFIX = "SKILLSLOT_";
    private JavaPlugin plugin;

    private Map<String, Skill> skillMap = new HashMap<>();

    public SkillManager(JavaPlugin plugin) {
        this.plugin = plugin;
        registerSkills();
    }

    private void registerSkills() {
        skillMap.put("Dash", new SkillDash());
        skillMap.put("VerticalDash", new SkillVerticalDash());
        skillMap.put("WindBlades", new SkillWindBlades());
        skillMap.put("Attraction", new SkillAttraction());
        skillMap.put("Summoner", new SkillSummoner());
    }

    public Skill getSkillByName(String skillName) {
        return skillMap.get(skillName);
    }

    public boolean isValidSkill(String skill) {
        return skillMap.containsKey(skill);
    }

    public String getSlotOfSkill(Player player, String skillName) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        for (int i = 1; i <= 4; i++) {
            String skill = container.get(getKeyName(i), PersistentDataType.STRING);
            if (skill != null && skill.equals(skillName)) {
                return skill;
            }
        }
        return "Unskilled";
    }

    public void assignSkillToSlot(Player player, String skill, int slot) {
        PersistentDataContainer container = player.getPersistentDataContainer();

        // Remove skill if it's already assigned to a slot
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
        return new NamespacedKey(plugin, SKILL_PREFIX + slot);
    }
}
