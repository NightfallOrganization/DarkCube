/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.inventory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import eu.darkcube.minigame.woolbattle.WoolBattleBukkit;
import eu.darkcube.minigame.woolbattle.translation.Message;
import eu.darkcube.minigame.woolbattle.user.HeightDisplay;
import eu.darkcube.minigame.woolbattle.user.WBUser;
import eu.darkcube.minigame.woolbattle.util.Item;
import eu.darkcube.minigame.woolbattle.util.scheduler.Scheduler;
import eu.darkcube.system.bukkit.inventoryapi.v1.IInventory;
import eu.darkcube.system.bukkit.inventoryapi.v1.IInventoryClickEvent;
import eu.darkcube.system.bukkit.inventoryapi.v1.InventoryType;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class SettingsHeightDisplayColorInventory extends WoolBattlePagedInventory {
    public static final InventoryType TYPE = InventoryType.of("woolbattle-settings-height-display-color");
    private static final PersistentDataType<ChatColor> COLOR_TYPE = PersistentDataTypes.enumType(ChatColor.class);
    private final Key COLOR = new Key(woolbattle, "height_display_color");
    private final Scheduler scheduler;
    private int number = 0;

    public SettingsHeightDisplayColorInventory(WoolBattleBukkit woolbattle, WBUser user) {
        super(woolbattle, TYPE, Message.HEIGHT_DISPLAY_COLOR_SETTINGS_TITLE.getMessage(user), user);
        scheduler = new Scheduler(woolbattle) {
            @Override public void run() {
                number--;
                if (number < 0) number = 99;
                recalculate();
            }
        };
        scheduler.runTaskTimer(1);
        complete();
    }

    @Override protected boolean done() {
        return super.done() && COLOR != null;
    }

    @Override protected void startAnimation() {
        super.startAnimation();
    }

    @Override protected void fillItems(Map<Integer, ItemStack> items) {
        List<ChatColor> colors = Arrays.stream(ChatColor.values()).filter(ChatColor::isColor).sorted().collect(Collectors.toList());
        ItemStack[] arr = colors.stream().map(c -> {
            ItemBuilder b = ItemBuilder.item(Material.PAPER);
            b.displayname(LegacyComponentSerializer.legacySection().deserialize(c.toString() + number));
            if (user.heightDisplay().getColor() == c) {
                b.lore(Message.SELECTED.getMessage(user));
                b.glow(true);
            } else {
                b.lore(Message.CLICK_TO_SELECT.getMessage(user));
            }
            b.persistentDataStorage().set(COLOR, COLOR_TYPE, c);
            return b.build();
        }).toArray(ItemStack[]::new);
        for (int i = 0; i < arr.length; i++) {
            items.put(i, arr[i]);
        }
    }

    @Override protected void insertFallbackItems() {
        super.insertFallbackItems();
        fallbackItems.put(IInventory.slot(1, 5), Item.SETTINGS_HEIGHT_DISPLAY_COLOR.getItem(user));
    }

    @Override protected void destroy() {
        super.destroy();
        scheduler.cancel();
    }

    @Override protected void inventoryClick(IInventoryClickEvent event) {
        event.setCancelled(true);
        if (event.item() == null) return;
        if (!event.item().persistentDataStorage().has(COLOR)) return;
        ChatColor color = event.item().persistentDataStorage().get(COLOR, COLOR_TYPE);
        HeightDisplay display = user.heightDisplay();
        display.setColor(color);
        user.heightDisplay(display);
        recalculate();
    }
}
