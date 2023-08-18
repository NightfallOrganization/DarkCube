/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.citybuild.commands;

import eu.darkcube.system.citybuild.skills.Skill;
import eu.darkcube.system.citybuild.util.SkillManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkillCommand implements CommandExecutor {
    private SkillManager skillManager;

    public SkillCommand(SkillManager skillManager) {
        this.skillManager = skillManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("Usage: /skill <slot>");
            return true;
        }

        Player player = (Player) sender;
        int slot;

        try {
            slot = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage("Please provide a valid slot number between 1 and 4.");
            return true;
        }

        if (slot < 1 || slot > 4) {
            sender.sendMessage("Please provide a valid slot number between 1 and 4.");
            return true;
        }

        String skillName = skillManager.getSkillFromSlot(player, slot);
        if ("Unskilled".equals(skillName)) {
            sender.sendMessage("You have no skill assigned to slot " + slot);
            return true;
        }

        Skill skill = skillManager.getSkillByName(skillName);
        if (skill == null) {
            sender.sendMessage("The skill in slot " + slot + " is not recognized.");
            return true;
        }

        skill.execute(player);
        sender.sendMessage("Executed skill: " + skillName);

        return true;
    }
}
