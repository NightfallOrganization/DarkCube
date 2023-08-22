/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.util.CustomHealthManager;
import eu.darkcube.system.aetheria.util.DamageManager;
import eu.darkcube.system.aetheria.util.DefenseManager;
import eu.darkcube.system.aetheria.util.LevelXPManager;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public class ResetLevelCommand implements CommandExecutor {

    private LevelXPManager levelXPManager;
    private CustomHealthManager healthManager;
    private DefenseManager defenseManager;
    private DamageManager damageManager; // Hinzugefügt
    private NamespacedKey speedKey;
    private NamespacedKey strengthKey;
    private NamespacedKey hitSpeedKey;
    private NamespacedKey healthKey;
    private NamespacedKey aroundDamageKey;
    private NamespacedKey defenseKey;
    private NamespacedKey damageKey; // Hinzugefügt

    public ResetLevelCommand(LevelXPManager levelXPManager, CustomHealthManager healthManager, DefenseManager defenseManager, DamageManager damageManager, JavaPlugin plugin) {
        this.levelXPManager = levelXPManager;
        this.healthManager = healthManager;
        this.defenseManager = defenseManager;
        this.damageManager = damageManager; // Hinzugefügt
        this.speedKey = new NamespacedKey(plugin, "SpeedKey");
        this.strengthKey = new NamespacedKey(plugin, "StrengthKey");
        this.hitSpeedKey = new NamespacedKey(plugin, "HitSpeedKey");
        this.healthKey = new NamespacedKey(plugin, "HealthKey");
        this.aroundDamageKey = new NamespacedKey(plugin, "AroundDamageKey");
        this.defenseKey = new NamespacedKey(plugin, "DefenseKey");
        this.damageKey = new NamespacedKey(plugin, "Damage"); // Hinzugefügt
    }

    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target;

        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§7Gib den Spielernamen an, den du zurücksetzen möchtest.");
                return false;
            }
            target = (Player) sender;
        } else {
            target = sender.getServer().getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage("§7Spieler nicht gefunden.");
                return false;
            }
        }

        resetPlayerAttributes(target);

        if (target == sender) {
            target.sendMessage("§7Dein Level wurde §azurückgesetzt");
        } else {
            target.sendMessage("§7Dein Level wurde von " + sender.getName() + " §azurückgesetzt");
            sender.sendMessage("§7Das Level von " + target.getName() + " wurde §azurückgesetzt");
        }

        return true;
    }

    private void resetPlayerAttributes(Player target) {
        levelXPManager.resetXP(target);
        levelXPManager.resetAP(target);
        healthManager.resetHealth(target);
        healthManager.resetMaxHealth(target);
        healthManager.resetRegen(target);
        healthManager.resetAroundDamage(target);

        levelXPManager.resetAttribute(target, "speed");
        levelXPManager.resetAttribute(target, "strength");
        levelXPManager.resetAttribute(target, "aroundDamage");
        levelXPManager.resetAttribute(target, "hitSpeed");

        defenseManager.resetDefense(target);

        // Setzen Sie den Damage-Wert des Spielers zurück
        damageManager.resetDamage(target); // Falls Sie eine resetDamage-Methode in Ihrem DamageManager haben
        // Oder setzen Sie den Wert direkt zurück:
        PersistentDataContainer data = target.getPersistentDataContainer();
        data.set(damageKey, PersistentDataType.DOUBLE, 0.0);

        target.setWalkSpeed(0.2f);
        target
                .getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)
                .getModifiers()
                .forEach(target.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)::removeModifier);
        target
                .getAttribute(Attribute.GENERIC_ATTACK_SPEED)
                .getModifiers()
                .forEach(target.getAttribute(Attribute.GENERIC_ATTACK_SPEED)::removeModifier);

        PersistentDataContainer dataContainer = target.getPersistentDataContainer();
        dataContainer.set(this.speedKey, PersistentDataType.INTEGER, 0);
        dataContainer.set(this.strengthKey, PersistentDataType.INTEGER, 0);
        dataContainer.set(this.hitSpeedKey, PersistentDataType.INTEGER, 0);
        dataContainer.set(this.healthKey, PersistentDataType.INTEGER, 0);
        dataContainer.set(this.aroundDamageKey, PersistentDataType.DOUBLE, 0.0);
        dataContainer.set(this.defenseKey, PersistentDataType.INTEGER, 0);
    }
}
