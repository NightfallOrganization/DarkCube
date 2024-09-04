/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.utils;

import java.util.List;

import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.enums.Abilities;
import eu.darkcube.system.miners.enums.Sounds;
import eu.darkcube.system.miners.enums.TeleportLocations;
import io.leangen.geantyref.TypeToken;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public class MinersPlayer {
    PersistentDataValue<Integer> level;
    PersistentDataValue<Integer> money;
    PersistentDataValue<Integer> xp;
    PersistentDataValue<Integer> wins;
    PersistentDataValue<Integer> loses;
    PersistentDataValue<Integer> deaths;
    PersistentDataValue<Integer> kills;
    PersistentDataValue<Integer> minedBlocks;
    PersistentDataValue<Sounds> farmingSound;
    PersistentDataValue<List<Sounds>> unlockedSounds;
    PersistentDataValue<List<Abilities>> boughtAbilities;
    PersistentDataValue<Abilities> activeAbility;
    Player player;
    Miners miners = Miners.getInstance();

    public MinersPlayer(Player player) {
        this.player = player;
        initializePersistentData(player);
    }

    private void initializePersistentData(Player player) {
        level = new PersistentDataValue<>(new NamespacedKey(miners, "level"), Integer.class, player.getPersistentDataContainer(), 1);
        money = new PersistentDataValue<>(new NamespacedKey(miners, "money"), Integer.class, player.getPersistentDataContainer(), 0);
        xp = new PersistentDataValue<>(new NamespacedKey(miners, "xp"), Integer.class, player.getPersistentDataContainer(), 0);
        wins = new PersistentDataValue<>(new NamespacedKey(miners, "wins"), Integer.class, player.getPersistentDataContainer(), 0);
        loses = new PersistentDataValue<>(new NamespacedKey(miners, "loses"), Integer.class, player.getPersistentDataContainer(), 0);
        deaths = new PersistentDataValue<>(new NamespacedKey(miners, "deaths"), Integer.class, player.getPersistentDataContainer(), 0);
        kills = new PersistentDataValue<>(new NamespacedKey(miners, "kills"), Integer.class, player.getPersistentDataContainer(), 0);
        minedBlocks = new PersistentDataValue<>(new NamespacedKey(miners, "minedBlocks"), Integer.class, player.getPersistentDataContainer(), 0);
        farmingSound = new PersistentDataValue<>(new NamespacedKey(miners, "farmingSound"), Sounds.class, player.getPersistentDataContainer(), Sounds.FARMING_SOUND_STANDARD);
        unlockedSounds = new PersistentDataValue<>(new NamespacedKey(miners, "unlockedSounds"), new TypeToken<List<Sounds>>(){}.getType(), player.getPersistentDataContainer(),  Sounds.unlockedSounds());
        boughtAbilities = new PersistentDataValue<>(new NamespacedKey(miners, "boughtAbilities"), new TypeToken<List<Abilities>>(){}.getType(), player.getPersistentDataContainer(),  Abilities.boughtAbilities());
        activeAbility = new PersistentDataValue<>(new NamespacedKey(miners, "activeAbility"), Abilities.class, player.getPersistentDataContainer(), Abilities.DIGGER);
    }

    //<editor-fold desc="Teleport">

    public void teleportToLobby() {
        player.teleportAsync(TeleportLocations.LOBBY.getLocation());
    }

    public void teleportSyncToLobby() {
        player.teleport(TeleportLocations.LOBBY.getLocation());
    }
    //</editor-fold>

    //<editor-fold desc="Getter">
    public Abilities getActiveAbility() {
        return activeAbility.getOrDefault();
    }
    //</editor-fold>

    //<editor-fold desc="Setter">
    //</editor-fold>

    //<editor-fold desc="Adder">
    //</editor-fold>

    //<editor-fold desc="Remover">
    //</editor-fold>

}
