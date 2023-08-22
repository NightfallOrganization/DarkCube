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

public class UseSkillCommand implements CommandExecutor {
    private SkillManager skillManager;
    private HashMap<String, Skill> skills;

    public UseSkillCommand(SkillManager skillManager) {
        this.skillManager = skillManager;
        this.skills = new HashMap<>();

        this.skills.put("Dash", new SkillDash());
        this.skills.put("VerticalDash", new SkillVerticalDash());
        this.skills.put("Summoner", new SkillSummoner());
        this.skills.put("WindBlades", new SkillWindBlades());
        this.skills.put("Attraction", new SkillAttraction());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            sender.sendMessage("§7Usage: /useskill <skill>");
            return true;
        }

        String skillName = args[0];
        String skillStatus = skillManager.getSlotOfSkill(player, skillName);

        if (skillStatus.equals("Unskilled")) {
            sender.sendMessage("§7Du hast diesen Skill zu keinem Slot hinzugefügt!");
            return true;
        }

        Skill skill = skills.get(skillName);
        if (skill != null) {
            skill.activate(player);
        } else {
            sender.sendMessage("§7Unknown skill: §a" + skillName);
        }

        return true;
    }
}
