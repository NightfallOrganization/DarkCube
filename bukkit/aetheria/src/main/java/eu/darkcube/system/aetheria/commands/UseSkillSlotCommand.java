/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.skills.*;
import eu.darkcube.system.aetheria.util.SkillManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class UseSkillSlotCommand implements CommandExecutor {
    private SkillManager skillManager;
    private HashMap<String, Skill> skills;

    public UseSkillSlotCommand(SkillManager skillManager) {
        this.skillManager = skillManager;
        this.skills = skillManager.getSkillMap();
    }


    @Override public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("§7Usage: /useskillslot <slot>");
            return true;
        }

        int slot;
        try {
            slot = Integer.parseInt(args[0]);
            if (slot < 1 || slot > 8) {
                sender.sendMessage("§7Gib einen Slot zwischen §a1 §7und §a4 §7an");
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("§7Bitte gib eine gültige Slot-Nummer an §a(1-4)");
            return true;
        }

        String skillName = skillManager.getSkillNameFromSlot(player, slot);

        if (skillName.equals("Unskilled")) {
            sender.sendMessage("§7Es wurde kein Skill zu Slot §a" + slot + " §7zugewiesen!");
            return true;
        }

        Skill skill = skills.get(skillName);
        if (skill != null) {
            skill.activate(player);
        } else {
            sender.sendMessage("§7Ungültiger Skill: §a" + skillName);
        }

        return true;
    }
}
