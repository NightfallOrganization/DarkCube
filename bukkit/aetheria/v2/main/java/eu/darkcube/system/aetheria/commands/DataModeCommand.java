/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.commands;

import eu.darkcube.system.aetheria.Aetheria;
import eu.darkcube.system.aetheria.manager.monster.MonsterLevelManager;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

public class DataModeCommand implements CommandExecutor, Listener {

    private MonsterLevelManager monsterLevelManager;
    private final String dataModeKey = "DataModeActive";

    public DataModeCommand(MonsterLevelManager monsterLevelManager) {
        this.monsterLevelManager = monsterLevelManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Spieler können diesen Befehl nutzen.");
            return true;
        }

        Player player = (Player) sender;
        toggleDataMode(player);
        return true;
    }

    private void toggleDataMode(Player player) {
        boolean dataModeActive = player.getPersistentDataContainer().has(new NamespacedKey(Aetheria.getInstance(), dataModeKey), PersistentDataType.BYTE);
        if (dataModeActive) {
            player.getPersistentDataContainer().remove(new NamespacedKey(Aetheria.getInstance(), dataModeKey));
            player.sendMessage("§7DataMode §adeaktiviert");
        } else {
            player.getPersistentDataContainer().set(new NamespacedKey(Aetheria.getInstance(), dataModeKey), PersistentDataType.BYTE, (byte) 1);
            player.sendMessage("§7DataMode §aaktiviert");
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        if (!(event.getEntity() instanceof LivingEntity monster)) {
            return;
        }

        if (!player.getPersistentDataContainer().has(new NamespacedKey(Aetheria.getInstance(), dataModeKey), PersistentDataType.BYTE)) {
            return;
        }

        int level = monsterLevelManager.getMonsterLevel(monster);
        double health = monsterLevelManager.getMonsterHealth(monster);
        double damage = monsterLevelManager.getMonsterDamage(monster);

        player.sendMessage(" ");
        player.sendMessage("§7Level: §a" + level);
        player.sendMessage("§7Health: §a" + health);
        player.sendMessage("§7Damage: §a" + damage);
        player.sendMessage(" ");
    }
}
