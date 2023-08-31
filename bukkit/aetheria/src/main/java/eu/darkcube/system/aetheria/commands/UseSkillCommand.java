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

        Skill skillDash = new SkillDash();
        Skill skillVerticalDash = new SkillVerticalDash();
        Skill skillAttraction = new SkillAttraction();
        Skill skillSummoner = new SkillSummoner();
        Skill skillWindBlades = new SkillWindBlades();
        Skill skillDamageResistance = new SkillDamageResistance();

        this.skills.put(skillDash.getName(), skillDash);
        this.skills.put(skillVerticalDash.getName(), skillVerticalDash);
        this.skills.put(skillAttraction.getName(), skillAttraction);
        this.skills.put(skillSummoner.getName(), skillSummoner);
        this.skills.put(skillWindBlades.getName(), skillWindBlades);
        this.skills.put(skillDamageResistance.getName(), skillDamageResistance);
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

        Skill skill = skills.get(skillName);
        if (skill != null) {
            if (skillManager.getPassiveSkillByName(skillName) != null) {
                sender.sendMessage("§7Der Skill §a" + skillName + " §7ist ein passiver Skill und kann nicht aktiviert werden.");
                return true;
            }
            skill.activate(player);
        } else {
            sender.sendMessage("§7Unknown skill: §a" + skillName);
        }

        if (skillStatus.equals("Unskilled")) {
            sender.sendMessage("§7Du hast diesen Skill zu keinem Slot hinzugefügt!");
            return true;
        }


        return true;
    }
}
