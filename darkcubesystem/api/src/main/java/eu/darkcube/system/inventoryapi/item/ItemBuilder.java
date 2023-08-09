/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.inventoryapi.item;

import com.google.common.collect.Multimap;
import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.inventoryapi.item.attribute.Attribute;
import eu.darkcube.system.inventoryapi.item.attribute.AttributeModifier;
import eu.darkcube.system.inventoryapi.item.meta.BuilderMeta;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.version.VersionSupport;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ItemBuilder - API to create an {@link ItemStack} with just one line of Code
 *
 * @author DasBabyPixel
 */
@Api public interface ItemBuilder {
    @Api static ItemBuilder item() {
        return item(Material.AIR);
    }

    @Api static ItemBuilder item(Material material) {
        return VersionSupport.version().itemProvider().item(material);
    }

    @Api static ItemBuilder item(ItemStack item) {
        return VersionSupport.version().itemProvider().item(item);
    }

    @Api static ItemBuilder item(JsonElement json) {
        return VersionSupport.version().itemProvider().item(json);
    }

    @Api static ItemBuilder spawner() {
        return VersionSupport.version().itemProvider().spawner();
    }

    @Api Material material();

    @Api ItemBuilder material(Material material);

    @Api ItemBuilder amount(int amount);

    @Api boolean canBeRepairedBy(ItemBuilder item);

    @Api Multimap<Attribute, AttributeModifier> attributeModifiers();

    @Api Multimap<Attribute, AttributeModifier> attributeModifiers(EquipmentSlot slot);

    @Api Collection<AttributeModifier> attributeModifiers(Attribute attribute);

    @Api ItemBuilder attributeModifier(Attribute attribute, AttributeModifier modifier);

    @Api ItemBuilder attributeModifiers(Multimap<Attribute, AttributeModifier> attributeModifiers);

    @Api ItemBuilder removeAttributeModifiers(EquipmentSlot slot);

    @Api ItemBuilder removeAttributeModifiers(Attribute attribute);

    @Api ItemBuilder removeAttributeModifiers(Attribute attribute, AttributeModifier modifier);

    @Api int amount();

    @Api ItemBuilder damage(int damage);

    @Api int damage();

    @Api ItemBuilder enchant(Enchantment enchant, int level);

    @Api ItemBuilder enchant(Map<Enchantment, Integer> enchantments);

    @Api ItemBuilder enchantments(Map<Enchantment, Integer> enchantments);

    @Api Map<Enchantment, Integer> enchantments();

    /**
     * @param displayname the display name
     * @return this builder
     * @deprecated Use {@link ItemBuilder#displayname(Component)}
     */
    @Api @Deprecated ItemBuilder displayname(String displayname);

    /**
     * Displayname via this method will not start italic
     *
     * @param displayname the displayname
     * @return this builder
     */
    @Api ItemBuilder displayname(Component displayname);

    /**
     * Displayname via this method will start italic
     *
     * @param displayname the displayname
     * @return this builder
     */
    @Api ItemBuilder displaynameRaw(Component displayname);

    @Api Component displayname();

    @Api ItemBuilder lore(Component line);

    @Api ItemBuilder lore(Collection<Component> lore);

    @Api ItemBuilder lore(Component... lines);

    @Api ItemBuilder lore(Component line, int index);

    /**
     * @param lines the lore
     * @return this builder
     * @deprecated Use {@link ItemBuilder#lore(Component...)}
     */
    @Api @Deprecated ItemBuilder lore(String... lines);

    /**
     * @param lines the lore
     * @return this builder
     * @deprecated Use {@link ItemBuilder#lore(Collection)}
     */

    @Api @Deprecated ItemBuilder lores(Collection<String> lines);

    @Api ItemBuilder setLore(Collection<Component> lore);

    @Api List<Component> lore();

    @Api ItemBuilder flag(ItemFlag flag);

    @Api ItemBuilder flag(Collection<ItemFlag> flags);

    @Api ItemBuilder flag(ItemFlag... flags);

    @Api ItemBuilder setFlags(Collection<ItemFlag> flags);

    @Api List<ItemFlag> flags();

    @Api ItemBuilder unbreakable(boolean unbreakable);

    @Api boolean unbreakable();

    @Api ItemBuilder glow(boolean glow);

    @Api boolean glow();

    @Api ItemPersistentDataStorage persistentDataStorage();

    @Api <T extends BuilderMeta> T meta(Class<T> clazz);

    @Api ItemBuilder meta(BuilderMeta meta);

    @Api Set<BuilderMeta> metas();

    @Api ItemBuilder metas(Set<BuilderMeta> metas);

    @Api ItemStack build();

    @Api ItemBuilder clone();

    @Api int repairCost();

    @Api ItemBuilder repairCost(int repairCost);

    @Api JsonElement serialize();
}
