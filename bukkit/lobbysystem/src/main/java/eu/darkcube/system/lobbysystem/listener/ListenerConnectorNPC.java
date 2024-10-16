/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.lobbysystem.listener;

import com.github.unldenis.hologram.event.PlayerHologramShowEvent;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.event.NPCHideEvent;
import eu.darkcube.system.lobbysystem.event.NPCShowEvent;
import eu.darkcube.system.lobbysystem.event.PlayerNPCInteractEvent;
import eu.darkcube.system.lobbysystem.npc.ConnectorNPC;
import eu.darkcube.system.lobbysystem.npc.NPCManagement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ListenerConnectorNPC extends BaseListener {
    private final Lobby lobby;

    public ListenerConnectorNPC(Lobby lobby) {
        this.lobby = lobby;
    }

    @EventHandler public void handle(PlayerNPCInteractEvent e) {
        NPCManagement.NPC npc = e.npc();
        ConnectorNPC n = ConnectorNPC.get(npc);
        if (n == null) return;
        n.connect(e.getPlayer());
    }

    @EventHandler public void handle(NPCHideEvent e) {
        NPCManagement.NPC npc = e.npc();
        ConnectorNPC c = ConnectorNPC.get(npc);
        if (c == null) return;
        Runnable r = () -> c.hologram().hide(e.player());

        if (Bukkit.isPrimaryThread()) r.run();
        else Bukkit.getScheduler().runTask(lobby, r);
    }

    @EventHandler public void handle(PlayerHologramShowEvent event) {
    }

    @EventHandler public void handle(NPCShowEvent e) {
        Player player = e.player();
        NPCManagement.NPC npc = e.npc();
        ConnectorNPC c = ConnectorNPC.get(npc);
        if (c == null) return;
        Runnable r = () -> {
            c.hologram().show(e.player());
            Scoreboard s = player.getScoreboard();
            if (s == Bukkit.getScoreboardManager().getMainScoreboard()) {
                s = Bukkit.getScoreboardManager().getNewScoreboard();
                player.setScoreboard(s);
            }
            Team team = s.getTeam("npc-lib-lobby");
            if (team == null) team = s.registerNewTeam("npc-lib-lobby");
            team.setNameTagVisibility(NameTagVisibility.NEVER);
            team.addEntry(npc.name());
        };
        if (Bukkit.isPrimaryThread()) r.run();
        else Bukkit.getScheduler().runTask(lobby, r);
    }
}
