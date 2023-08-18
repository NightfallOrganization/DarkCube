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

public class MySkillsCommand implements CommandExecutor {
    private SkillManager skillManager;

    public MySkillsCommand(SkillManager skillManager) {
        this.skillManager = skillManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        for (int i = 1; i <= 4; i++) {
            String skill = skillManager.getSkillFromSlot(player, i);
            if (skill.equals("Unskilled")) {
                sender.sendMessage("§7Skill " + i + ": None");
            } else {
                sender.sendMessage("§7Skill " + i + ": " + skill);
            }
        }

        return true;
    }
}
