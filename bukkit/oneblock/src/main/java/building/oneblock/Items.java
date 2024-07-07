/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock;

import static eu.darkcube.system.server.item.ItemBuilder.item;
import static org.bukkit.Material.*;

import building.oneblock.util.Message;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.meta.LeatherArmorBuilderMeta;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Color;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public enum Items {
    CUSTOM_STONE_SWORD_1(item(STONE_SWORD)),
    CUSTOM_STONE_SWORD_2(item(STONE_SWORD)),
    DRAGON_CHESTPLATE(item(LEATHER_CHESTPLATE).attributeModifier(Attribute.GENERIC_MOVEMENT_SPEED, new AttributeModifier("dragon_boost", 1, AttributeModifier.Operation.ADD_NUMBER)).meta(new LeatherArmorBuilderMeta(new eu.darkcube.system.util.Color(Color.PURPLE.asRGB())))),
    CUSTOM_IRON_SWORD(item(IRON_SWORD));

    private final ItemBuilder builder;

    Items(ItemBuilder builder) {
        this.builder = builder;
    }

    @EventHandler
    public void handle(InventoryClickEvent event) {
        ItemStack itemStack = event.getCurrentItem();
        ItemBuilder builder = ItemBuilder.item(itemStack);

    }

    public ItemStack getItem(Player player) {
        ItemBuilder builder = this.builder.clone();

        User user = UserAPI.instance().user(player.getUniqueId());
        Component displayName = Message.ONEBLOCK_CREATING_WORLD_1.getMessage(user, 5);

        builder.displayname(displayName);

        return builder.build();
    }
}
