/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.skills;

import com.google.gson.Gson;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Skill {
    protected String name;
    protected int cooldown;
    protected boolean isActive;

    public Skill(String name, int cooldown) {
        this.name = name;
        this.cooldown = cooldown;
        this.isActive = false; // Standardmäßig ist ein Skill deaktiviert
    }

    public abstract void execute(Entity executor);


    // Aktiviert den Skill
    public void activate() {
        this.isActive = true;
        // Zusätzliche Logik bei Aktivierung kann hier implementiert werden
    }

    // Deaktiviert den Skill
    public void deactivate() {
        this.isActive = false;
        // Zusätzliche Logik bei Deaktivierung kann hier implementiert werden
    }

    // Überprüft, ob der Skill aktiviert ist
    public boolean isActive() {
        return this.isActive;
    }

    // Speichern des Skill-Zustands in der Entität
    public void saveState(Entity entity, JavaPlugin plugin) {
        NamespacedKey key = new NamespacedKey(plugin, "skill_active_" + name);
        entity.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) (this.isActive ? 1 : 0));
    }

    // Laden des Skill-Zustands aus der Entität
    public static boolean loadState(Entity entity, JavaPlugin plugin, String skillName) {
        NamespacedKey key = new NamespacedKey(plugin, "skill_active_" + skillName);
        Byte storedValue = entity.getPersistentDataContainer().get(key, PersistentDataType.BYTE);
        return storedValue != null && storedValue == 1;
    }

    // Methode zum Anwenden eines Skills auf eine Entität
    public void applySkillToEntity(Entity entity, JavaPlugin plugin) {
        this.activate(); // Aktiviere den Skill
        this.saveState(entity, plugin); // Speichere den Zustand des Skills in der Entität
    }

    // Methode zum Ausführen eines Skills von einer Entität, falls der Skill aktiv ist
    public static void executeSkillFromEntity(Entity entity, JavaPlugin plugin, String skillName, Skill skill) {
        boolean isActive = loadState(entity, plugin, skillName);
        if (isActive) {
            skill.execute(entity); // Führe den Skill aus, wenn er aktiv ist
        }
    }

}
