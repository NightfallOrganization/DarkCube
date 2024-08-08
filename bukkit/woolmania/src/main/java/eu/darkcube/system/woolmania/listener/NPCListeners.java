/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.listener;

import com.github.juliarn.npclib.api.Npc;
import com.github.juliarn.npclib.api.event.AttackNpcEvent;
import com.github.juliarn.npclib.api.event.InteractNpcEvent;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.woolmania.manager.NPCManager;
import eu.darkcube.system.woolmania.npc.NPCCreator;
import eu.darkcube.system.woolmania.util.message.Message;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class NPCListeners {
    public static void register(NPCManager manager, NPCCreator creator) {
        manager.getPlatform().eventManager().registerEventHandler(InteractNpcEvent.class, event -> {
            if (event.hand() != InteractNpcEvent.Hand.MAIN_HAND) {
                return;
            }
            Player player = event.player();
            User user = UserAPI.instance().user(player.getUniqueId());

            Npc<World, Player, ItemStack, Plugin> npc = event.npc();
            try {
                clickNpc(creator, player, user, npc);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });
        manager.getPlatform().eventManager().registerEventHandler(AttackNpcEvent.class, event -> {
            Player player = event.player();
            User user = UserAPI.instance().user(player.getUniqueId());
            Npc<World, Player, ItemStack, Plugin> npc = event.npc();
            try {
                clickNpc(creator, player, user, npc);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private static void clickNpc(NPCCreator creator, Player player, User user, Npc<World, Player, ItemStack, Plugin> npc) {
        if (npc == creator.zinus.npc()) {
            zinusExecute(player, user);
        } else if (npc == creator.zina.npc()) {
            zinaExecute(player, user);
        }
    }

    private static void zinusExecute(Player player, User user) {
        if (player.hasPermission("woolmania.level.2")) {
            user.sendMessage(Message.LEVEL_TO_LOW);
        } else {
            user.sendMessage(Message.LEVEL_TO_LOW);
        }
    }

    private static void zinaExecute(Player player, User user) {
        if (player.hasPermission("woolmania.level.2")) {
            boolean hasWool = false;
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() != null && item.getType().toString().endsWith("_WOOL")) {
                    hasWool = true;
                    break;
                }
            }
            if (hasWool) {
                user.sendMessage(Message.ZINA_GET_MONEY, "?");
            } else {
                user.sendMessage(Message.ZINA_NO_WOOL);
            }
        } else {
            user.sendMessage(Message.LEVEL_TO_LOW);
        }
    }


}
