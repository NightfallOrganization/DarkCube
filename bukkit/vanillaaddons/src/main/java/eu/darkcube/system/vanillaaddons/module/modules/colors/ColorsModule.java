/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.vanillaaddons.module.modules.colors;

import eu.darkcube.system.bukkit.inventoryapi.item.ItemBuilder;
import eu.darkcube.system.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import eu.darkcube.system.vanillaaddons.VanillaAddons;
import eu.darkcube.system.vanillaaddons.module.Module;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;

public class ColorsModule implements Module, Listener {
    private final VanillaAddons addons;

    public ColorsModule(VanillaAddons addons) {
        this.addons = addons;
    }

    @EventHandler(priority = EventPriority.LOW) public void handle(PrepareAnvilEvent event) {
        if (event.getResult() != null) {
            ItemBuilder item = ItemBuilder.item(event.getResult());
            if (item.displayname() == null) return;
            String name = LegacyComponentSerializer.legacySection().serialize(item.displayname());
            name = ChatColor.translateAlternateColorCodes('&', name);
            item.displayname(LegacyComponentSerializer.legacySection().deserialize(name));
            event.setResult(item.build());
        }
    }

    @EventHandler public void onSignChange(SignChangeEvent e) {
        for (int i = 0; i < e.lines().size(); ++i) {
            Component cline = e.line(i);
            if (cline == null) continue;
            String line = net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().serialize(cline);
            e.line(i, net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer.legacyAmpersand().deserialize(line));
        }
    }

    @Override public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, addons);
    }

    @Override public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
