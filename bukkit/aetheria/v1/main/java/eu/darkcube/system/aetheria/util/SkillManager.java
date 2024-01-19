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
    private Map<String, Skill> activeSkillMap = new HashMap<>();
    private Map<String, Skill> passiveSkillMap = new HashMap<>();

    public SkillManager(JavaPlugin plugin) {
        this.plugin = plugin;
        registerSkills();
    }

    public HashMap<String, Skill> getSkillMap() {
        return (HashMap<String, Skill>) skillMap;
    }

    private void registerSkills() {
        Skill skillDash = new SkillDash();
        Skill skillVerticalDash = new SkillVerticalDash();
        Skill skillAttraction = new SkillAttraction();
        Skill skillSummoner = new SkillSummoner();
        Skill skillWindBlades = new SkillWindBlades();
        Skill skillDamageResistance = new SkillDamageResistance();

        skillMap.put(skillDash.getName(), skillDash);
        skillMap.put(skillVerticalDash.getName(), skillVerticalDash);
        skillMap.put(skillAttraction.getName(), skillAttraction);
        skillMap.put(skillSummoner.getName(), skillSummoner);
        skillMap.put(skillWindBlades.getName(), skillWindBlades);
        skillMap.put(skillDamageResistance.getName(), skillDamageResistance);

        activeSkillMap.put(skillDash.getName(), skillDash);
        activeSkillMap.put(skillVerticalDash.getName(), skillVerticalDash);
        activeSkillMap.put(skillAttraction.getName(), skillAttraction);
        activeSkillMap.put(skillSummoner.getName(), skillSummoner);
        activeSkillMap.put(skillWindBlades.getName(), skillWindBlades);

        passiveSkillMap.put(skillDamageResistance.getName(), skillDamageResistance);

    }

    public Skill getActiveSkillByName(String skillName) {
        return activeSkillMap.get(skillName);
    }

    public Skill getPassiveSkillByName(String skillName) {
        return passiveSkillMap.get(skillName);
    }

    public Skill getSkillByName(String skillName) {
        return skillMap.get(skillName);
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

    public String getSkillNameFromSlot(Player player, int slot) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        return container.has(getKeyName(slot), PersistentDataType.STRING)
                ? container.get(getKeyName(slot), PersistentDataType.STRING)
                : "Unskilled";
    }

    public Skill getSkillFromSlot(Player player, int slot) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        String skillName = container.has(getKeyName(slot), PersistentDataType.STRING)
                ? container.get(getKeyName(slot), PersistentDataType.STRING)
                : "Unskilled";
        if (slot <= 4) {
            return activeSkillMap.get(skillName);
        } else {
            return passiveSkillMap.get(skillName);
        }
    }

    public NamespacedKey getKeyName(int slot) {
        return new NamespacedKey(plugin, SKILL_PREFIX + slot);
    }
}
