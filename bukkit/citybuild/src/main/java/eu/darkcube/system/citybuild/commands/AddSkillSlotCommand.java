/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.commands;

import eu.darkcube.system.citybuild.util.SkillManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AddSkillSlotCommand implements CommandExecutor {
    private SkillManager skillManager;

    public AddSkillSlotCommand(SkillManager skillManager) {
        this.skillManager = skillManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 2) {
            sender.sendMessage("§7Usage: /addskillslot <skill> <slot>");
            return true;
        }

        String skill = args[0];
        if (!skillManager.isValidSkill(skill)) {
            sender.sendMessage("§a" + skill + " §7ist kein Skill");
            return true;
        }

        int slot;
        try {
            slot = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§7Es muss ein Slot von 1 bis 4 sein");
            return true;
        }

        if (slot < 1 || slot > 4) {
            sender.sendMessage("§7Es muss ein Slot von 1 bis 4 sein");
            return true;
        }

        skillManager.assignSkillToSlot(player, skill, slot);
        sender.sendMessage("§7Der Skill §a" + skill + " §7wurde zu Slot §a" + slot + " §7hinzugefügt");

        return true;
    }
}
