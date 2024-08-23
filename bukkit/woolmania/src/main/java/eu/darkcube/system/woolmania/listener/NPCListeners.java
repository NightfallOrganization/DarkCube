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
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.items.CustomItem;
import eu.darkcube.system.woolmania.items.WoolItem;
import eu.darkcube.system.woolmania.manager.NPCManager;
import eu.darkcube.system.woolmania.npc.NPCCreator;
import eu.darkcube.system.woolmania.util.message.Message;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class NPCListeners {
    public static void register(NPCManager manager, NPCCreator creator) {
        manager.getPlatform().eventManager().registerEventHandler(InteractNpcEvent.class, event -> {
            new BukkitRunnable() {
                @Override
                public void run() {
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
                }
            }.runTask(WoolMania.getInstance());
        });
        manager.getPlatform().eventManager().registerEventHandler(AttackNpcEvent.class, event -> {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Player player = event.player();
                    User user = UserAPI.instance().user(player.getUniqueId());
                    Npc<World, Player, ItemStack, Plugin> npc = event.npc();
                    try {
                        clickNpc(creator, player, user, npc);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }.runTask(WoolMania.getInstance());
        });
    }

    private static void clickNpc(NPCCreator creator, Player player, User user, Npc<World, Player, ItemStack, Plugin> npc) {
        if (npc == creator.zinus.npc() || npc == creator.zinus2.npc() || npc == creator.zinus3.npc()) {
            zinusExecute(player);
        } else if (npc == creator.zina.npc() || npc == creator.zina2.npc() || npc == creator.zina3.npc()) {
            zinaExecute(player, user);
        } else if (npc == creator.varkas.npc()) {
            varkasExecute(player);
        } else if (npc == creator.astaroth.npc()) {
            astarothExecute(player);
        }
    }

    private static void astarothExecute(Player player) {
        WoolMania.getInstance().getAbilityInventory().openInventory(player);
    }

    private static void zinusExecute(Player player) {
        WoolMania.getInstance().getShopInventory().openInventory(player);
    }

    private static void varkasExecute(Player player) {
        WoolMania.getInstance().getSmithInventory().openInventory(player);
    }

    private static void zinaExecute(Player player, User user) {
        boolean hasWool = false;
        int woolValue = 0;

        for (ItemStack item : player.getInventory().getContents()) {

            if (item != null) {
                CustomItem customItem = new CustomItem(ItemBuilder.item(item));
                if (WoolItem.ITEM_ID.equals(customItem.getItemID())) {

                    int tierLevel = customItem.getTierID();
                    int itemAmount = item.getAmount();
                    woolValue += tierLevel * itemAmount;

                    player.getInventory().remove(item);
                    hasWool = true;
                }
            }
        }

        if (hasWool) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 50f, 2f);
            user.sendMessage(Message.ZINA_GET_MONEY, woolValue);
            WoolMania.getStaticPlayer(player).addMoney(woolValue, player);
        } else {
            user.sendMessage(Message.ZINA_NO_WOOL);
        }
    }
}
