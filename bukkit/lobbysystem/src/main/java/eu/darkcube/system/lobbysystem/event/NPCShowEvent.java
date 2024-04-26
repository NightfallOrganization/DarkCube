/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.event;

import eu.darkcube.system.lobbysystem.npc.NPCManagement;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NPCShowEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final NPCManagement.NPC npc;
    private final Player player;

    public NPCShowEvent(NPCManagement.NPC npc, Player player) {
        this.npc = npc;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public NPCManagement.NPC npc() {
        return npc;
    }

    public Player player() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
