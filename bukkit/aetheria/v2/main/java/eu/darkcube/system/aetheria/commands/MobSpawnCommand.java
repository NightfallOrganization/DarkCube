/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.manager.monster.MonsterLevelManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

public class MobSpawnCommand implements CommandExecutor {
    private final MonsterLevelManager monsterLevelManager;

    public MobSpawnCommand(MonsterLevelManager monsterLevelManager) {
        this.monsterLevelManager = monsterLevelManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl verwenden.");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage("§cUsage: /mobspawn [Level]");
            return true;
        }

        int level;
        try {
            level = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cKeine gültige Zahl");
            return true;
        }

        Player player = (Player) sender;
        Zombie zombie = player.getWorld().spawn(player.getLocation(), Zombie.class);
        monsterLevelManager.updateMonsterLevel(zombie, level);
        sender.sendMessage("§7Ein Monster mit Level §a" + level + " §7wurde gespawnt");
        return true;
    }
}
