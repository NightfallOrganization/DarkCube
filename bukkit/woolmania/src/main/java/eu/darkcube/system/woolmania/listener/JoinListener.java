/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.listener;

import static eu.darkcube.system.woolmania.npc.NPCCreator.*;
import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.hall.Hall;
import eu.darkcube.system.woolmania.util.WoolManiaPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        WoolManiaPlayer woolmaniaPlayer = new WoolManiaPlayer(player);
        event.joinMessage(null);
        WoolMania.getInstance().woolManiaPlayerMap.put(player, woolmaniaPlayer);
        WoolMania.getInstance().getGameScoreboard().createGameScoreboard(player);
        setupScoreboardForNPCs(player);

        Location location = player.getLocation();
        boolean isInBlockLocation = player.getLocation().getBlock().getType().isSolid();

        for (var hall : Hall.values()) {
            if (hall.getHallArea().isWithinBounds(location)) {
                if (hall.getPool().isWithinBounds(location) && isInBlockLocation) {
                    woolmaniaPlayer.teleportSyncTo(hall);
                } else {
                    woolmaniaPlayer.setHall(hall);
                }
                break;
            }
        }

        if (woolmaniaPlayer.getHall() == null) {
            woolmaniaPlayer.teleportSyncToSpawn();
        }
    }

    private void setupScoreboardForNPCs(Player player) {
        var scoreboard = player.getScoreboard();
        if (scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(scoreboard);
        }

        setupPrefix(scoreboard, "npc-1", NAME_ZINUS, empty(), text(" (", GRAY).append(text("SHOP", DARK_AQUA)).append(text(")", GRAY)), AQUA);
        setupPrefix(scoreboard, "npc-2", NAME_ZINA, empty(), text(" (", GRAY).append(text("TRADE", DARK_AQUA)).append(text(")", GRAY)), AQUA);

        setupPrefix(scoreboard, "npc-3", NAME_ZINUS, empty(), text(" (", GRAY).append(text("TRADE", DARK_AQUA)).append(text(")", GRAY)), AQUA);
        setupPrefix(scoreboard, "npc-4", NAME_ZINA, empty(), text(" (", GRAY).append(text("TRADE", DARK_AQUA)).append(text(")", GRAY)), AQUA);

        setupPrefix(scoreboard, "npc-5", NAME_ZINUS, empty(), text(" (", GRAY).append(text("TRADE", DARK_AQUA)).append(text(")", GRAY)), AQUA);
        setupPrefix(scoreboard, "npc-6", NAME_ZINA, empty(), text(" (", GRAY).append(text("TRADE", DARK_AQUA)).append(text(")", GRAY)), AQUA);

        setupPrefix(scoreboard, "npc-7", NAME_VARKAS, empty(), text(" (", GRAY).append(text("SMITH", GOLD)).append(text(")", GRAY)), YELLOW);
        setupPrefix(scoreboard, "npc-8", NAME_ASTAROTH, empty(), text(" (", GRAY).append(text("ABILITYS", DARK_PURPLE)).append(text(")", GRAY)), LIGHT_PURPLE);

        Bukkit.getScheduler().runTaskLater(WoolMania.getInstance(), () -> {
            WoolMania.getInstance().getGameScoreboard().updateWorld(player);
        }, 10L);
    }

    private void setupPrefix(Scoreboard scoreboard, String npcId, String npcName, Component prefix, Component suffix, NamedTextColor color) {
        var team = scoreboard.getTeam(npcId);
        if (team == null) {
            team = scoreboard.registerNewTeam(npcId);
            team.addEntry(npcName);
        }
        team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        team.prefix(prefix);
        team.suffix(suffix);
        team.color(color);
    }
}
