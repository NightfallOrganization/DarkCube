/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.event;

import eu.darkcube.system.lobbysystem.npc.NPCManagement;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerNPCInteractEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final NPCManagement.NPC npc;
    private final Hand hand;
    private final EntityUseAction useAction;

    public PlayerNPCInteractEvent(Player who, NPCManagement.NPC npc, Hand hand, EntityUseAction useAction) {
        super(who);
        this.npc = npc;
        this.hand = hand;
        this.useAction = useAction;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player player() {
        return getPlayer();
    }

    public Hand hand() {
        return hand;
    }

    public EntityUseAction useAction() {
        return useAction;
    }

    public NPCManagement.NPC npc() {
        return npc;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public enum Hand {
        MAIN_HAND, OFF_HAND
    }

    public enum EntityUseAction {
        ATTACK, INTERACT
    }
}
