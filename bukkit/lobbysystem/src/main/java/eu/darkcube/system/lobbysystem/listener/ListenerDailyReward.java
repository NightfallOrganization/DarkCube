/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.Set;

import eu.darkcube.system.bukkit.util.BukkitAdventureSupport;
import eu.darkcube.system.labymod.emotes.Emotes;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.event.PlayerNPCInteractEvent;
import eu.darkcube.system.lobbysystem.inventory.InventoryDailyReward;
import eu.darkcube.system.lobbysystem.npc.NPCManagement;
import eu.darkcube.system.lobbysystem.user.LobbyUser;
import eu.darkcube.system.lobbysystem.user.UserWrapper;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ListenerDailyReward extends BaseListener {

    private static int randomCubes(Calendar c) {
        int maxCubes = 200;
        int minCubes = 80;
        if (c.get(Calendar.MONTH) == Calendar.DECEMBER) {
            int day = c.get(Calendar.DAY_OF_MONTH);
            if (day == 24 || day == 25 || day == 26 || day == 27 || day == 28 || day == 29 || day == 30 || day == 31) {
                maxCubes *= 10;
                minCubes *= 10;
            }
        }

        return minCubes + new Random().nextInt(maxCubes - minCubes + 1);
    }

    @EventHandler public void handle(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        LobbyUser user = UserWrapper.fromUser(UserAPI.instance().user(p.getUniqueId()));
        if (user.getOpenInventory().getType() != InventoryDailyReward.type_daily_reward) {
            return;
        }
        ItemStack item = e.getCurrentItem();
        if (item == null) {
            return;
        }
        if (!ItemBuilder.item(item).persistentDataStorage().has(InventoryDailyReward.reward)) {
            return;
        }
        int id = ItemBuilder.item(item).persistentDataStorage().get(InventoryDailyReward.reward, PersistentDataTypes.INTEGER);

        Set<Integer> used = user.getRewardSlotsUsed();
        // used.clear();
        if (used.contains(id)) {
            p.playSound(p.getLocation(), Sound.VILLAGER_NO, 1, 1);
            return;
        }

        used.add(id);
        user.setRewardSlotsUsed(used);

        int cubes = randomCubes(Calendar.getInstance());
        user.user().cubes(user.user().cubes().add(BigInteger.valueOf(cubes)));
        item = new ItemStack(Material.SULPHUR);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§a" + cubes);
        item.setItemMeta(meta);
        user.setLastDailyReward(System.currentTimeMillis());
        e.setCurrentItem(item);
        BukkitAdventureSupport
                .adventureSupport()
                .audienceProvider()
                .player(p)
                .sendMessage(Message.REWARD_COINS.getMessage(user.user(), Integer.toString(cubes)));
        p.playSound(p.getLocation(), Sound.LEVEL_UP, 1, 1);
    }

    @EventHandler public void handle(PlayerNPCInteractEvent e) {
        if (e.hand() != PlayerNPCInteractEvent.Hand.MAIN_HAND) {
            return;
        }
        NPCManagement.NPC npc = e.npc();
        if (npc.equals(Lobby.getInstance().getDailyRewardNpc())) {
            if (e.useAction() == PlayerNPCInteractEvent.EntityUseAction.ATTACK) {
                List<Emotes> emotes = new ArrayList<>(Arrays.asList(Emotes.values()));
                emotes.remove(Emotes.INFINITY_SIT);
                npc.sendEmotes(e.player(), emotes.get(new Random().nextInt(emotes.size())).getId());
            } else {
                Player p = e.player();
                LobbyUser user = UserWrapper.fromUser(UserAPI.instance().user(p.getUniqueId()));
                user.setOpenInventory(new InventoryDailyReward(user.user()));
            }
        }
    }

}
