/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.util.SkillManager;
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

        sender.sendMessage(" ");
        sender.sendMessage("§7§nMeine Skills:");
        sender.sendMessage(" ");
        for (int i = 1; i <= 4; i++) {
            String skill = skillManager.getSkillFromSlot(player, i);
            sender.sendMessage("§7Slot " + i + ": §a" + (skill.equals("Unskilled") ? "§7Empty" : skill));
        }
        sender.sendMessage(" ");

        return true;
    }
}
