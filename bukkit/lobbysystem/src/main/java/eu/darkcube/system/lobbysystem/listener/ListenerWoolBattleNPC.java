/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import eu.darkcube.system.labymod.emotes.Emotes;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.event.PlayerNPCInteractEvent;
import eu.darkcube.system.lobbysystem.inventory.InventoryWoolBattle;
import eu.darkcube.system.lobbysystem.npc.NPCManagement;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ListenerWoolBattleNPC extends BaseListener {

    @EventHandler public void handle(PlayerNPCInteractEvent e) {
        NPCManagement.NPC npc = e.npc();
        if (npc.equals(Lobby.getInstance().getWoolBattleNPC())) {
            if (e.useAction() == PlayerNPCInteractEvent.EntityUseAction.ATTACK) {
                List<Emotes> emotes = new ArrayList<>(Arrays.asList(Emotes.values()));
                emotes.remove(Emotes.INFINITY_SIT);
                npc.sendEmotes(e.player(), emotes.get(new Random().nextInt(emotes.size())).getId());
                // npc.sendEmote(emotes.get(new Random().nextInt(emotes.size())));
            } else {
                Player p = e.player();
                LobbyUser user = UserWrapper.fromUser(UserAPI.instance().user(p.getUniqueId()));
                user.setOpenInventory(new InventoryWoolBattle(user.user()));
            }
        }
    }

}
