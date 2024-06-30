/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.skills.Skill;
import eu.darkcube.system.aetheria.util.SkillManager;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class AddSkillSlotCommand implements CommandExecutor {
    private SkillManager skillManager;

    public AddSkillSlotCommand(SkillManager skillManager) {
        this.skillManager = skillManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage("§7Usage: /addskillslot <skill> <slot>");
            return true;
        }

        String skill = args[0];
        Skill skillObj = skillManager.getSkillByName(skill);

        if (skillObj == null) {
            sender.sendMessage("§a" + skill + " §7ist kein Skill");
            return true;
        }

        int slot;
        try {
            slot = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§7Es muss ein Slot von 1 bis 8 sein");
            return true;
        }

        if (slot < 1 || slot > 8) {
            sender.sendMessage("§7Es muss ein Slot von 1 bis 8 sein");
            return true;
        }

        PersistentDataContainer container = player.getPersistentDataContainer();
        NamespacedKey key = skillManager.getKeyName(slot);

        // Überprüfen, ob der Skill bereits in einem anderen Slot ist
        for (int i = 1; i <= 8; i++) {
            String existingSkill = container.get(skillManager.getKeyName(i), PersistentDataType.STRING);
            if (existingSkill != null && existingSkill.equals(skill)) {
                container.remove(skillManager.getKeyName(i));
                break;
            }
        }

        if (slot <= 4) {
            if (skillManager.getActiveSkillByName(skill) != null) {
                container.set(key, PersistentDataType.STRING, skill);
                sender.sendMessage("§7Der Skill §a" + skill + " §7wurde zu Slot §a" + slot + " §7hinzugefügt");
            } else {
                sender.sendMessage("§7Der Skill §a" + skill + " §7kann nicht zu einem aktiven Slot hinzugefügt werden");
            }
        } else {
            if (skillManager.getPassiveSkillByName(skill) != null) {
                container.set(key, PersistentDataType.STRING, skill);
                sender.sendMessage("§7Der Skill §a" + skill + " §7wurde zu Slot §a" + slot + " §7hinzugefügt");
            } else {
                sender.sendMessage("§7Der Skill §a" + skill + " §7kann nicht zu einem passiven Slot hinzugefügt werden");
            }
        }

        return true;
    }
}
