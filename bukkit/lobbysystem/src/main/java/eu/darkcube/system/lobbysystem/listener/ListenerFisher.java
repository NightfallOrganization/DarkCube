/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import eu.darkcube.system.labymod.emotes.Emotes;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.event.PlayerNPCInteractEvent;
import eu.darkcube.system.lobbysystem.npc.NPCManagement;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ListenerFisher extends BaseListener {
    @EventHandler public void handle(PlayerNPCInteractEvent e) {
        if (e.hand() != PlayerNPCInteractEvent.Hand.MAIN_HAND) {
            return;
        }
        NPCManagement.NPC npc = e.npc();
        if (npc.equals(Lobby.getInstance().getFisherNpc())) {
            if (e.useAction() == PlayerNPCInteractEvent.EntityUseAction.ATTACK) {
                List<Emotes> emotes = new ArrayList<>(Arrays.asList(Emotes.values()));
                emotes.remove(Emotes.INFINITY_SIT);
                npc.sendEmotes(e.player(), emotes.get(new Random().nextInt(emotes.size())).getId());
            } else {
                Player p = e.player();
                int fishCount = 0;
                User user = UserAPI.instance().user(p.getUniqueId());

                for (ItemStack item : p.getInventory().getContents()) {
                    if (item != null && item.getType() == Material.RAW_FISH) {
                        fishCount += item.getAmount() * 5;
                        p.getInventory().remove(item);
                    }
                }

                user.sendMessage(Message.REWARD_COINS, fishCount);
                p.playSound(p.getLocation(), Sound.LEVEL_UP, 50f, 1f);

                BigInteger newCubes = user.cubes().add(BigInteger.valueOf(fishCount));
                user.cubes(newCubes);
            }
        }
    }
}
