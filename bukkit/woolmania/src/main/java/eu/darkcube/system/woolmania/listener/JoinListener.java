/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.listener;

import static eu.darkcube.system.woolmania.npc.NPCCreator.NAME_ZINA;
import static eu.darkcube.system.woolmania.npc.NPCCreator.NAME_ZINUS;
import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.format.NamedTextColor.*;

import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.npc.NPCCreator;
import eu.darkcube.system.woolmania.util.WoolManiaPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
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
        WoolMania.getInstance().woolManiaPlayerMap.put(player, new WoolManiaPlayer(player));
        WoolMania.getInstance().getGameScoreboard().createGameScoreboard(player);
        setupScoreboardForNPCs(player);
    }

    private void setupScoreboardForNPCs(Player player) {
        var scoreboard = player.getScoreboard();
        if (scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(scoreboard);
        }

        setupPrefix(scoreboard, "npc-1", NAME_ZINUS, empty(), text(" (", GRAY).append(text("SHOP", DARK_AQUA)).append(text(")", GRAY)), AQUA);
        setupPrefix(scoreboard, "npc-2", NAME_ZINA, empty(), text(" (", GRAY).append(text("TRADE", DARK_AQUA)).append(text(")", GRAY)), AQUA);

        Bukkit.getScheduler().runTaskLater(WoolMania.getInstance(), () -> {
            WoolMania.getInstance().getGameScoreboard().updateWorld(player);
            player.sendMessage("§7Player aktualisiert");
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
