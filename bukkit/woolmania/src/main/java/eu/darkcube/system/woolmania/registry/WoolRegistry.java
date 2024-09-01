/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.registry;

import static eu.darkcube.system.woolmania.enums.Tiers.*;
import static eu.darkcube.system.woolmania.util.message.CustomItemNames.*;
import static org.bukkit.Material.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.ItemBuilder;
import eu.darkcube.system.server.item.component.ItemComponent;
import eu.darkcube.system.server.item.component.components.ItemBlockState;
import eu.darkcube.system.woolmania.enums.Tiers;
import eu.darkcube.system.woolmania.util.message.CustomItemNames;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.CopperBulb;

public class WoolRegistry {

    private final Map<Integer, Entry> byId = new HashMap<>();
    private final Map<Tiers, List<Entry>> byTier = new HashMap<>();
    private final Map<CustomItemNames, Entry> byName = new HashMap<>();
    private final Map<Material, List<Entry>> byBlock = new HashMap<>();

    public WoolRegistry() {
        registerBasic(1, ITEM_NAME_RED_WOOL, RED_WOOL, TIER1);
        registerCopperBulb(2, ITEM_NAME_RED_WOOL_CRYSTAL, COPPER_BULB, false, false, TIER2);
        registerBasic(3, ITEM_NAME_RED_WOOL_STAR, COPPER_GRATE, TIER3);

        registerBasic(4, ITEM_NAME_MAGENTA_WOOL, MAGENTA_WOOL, TIER4);
        registerCopperBulb(5, ITEM_NAME_MAGENTA_WOOL_CRYSTAL, EXPOSED_COPPER_BULB, false, false, TIER5);
        registerBasic(6, ITEM_NAME_MAGENTA_WOOL_STAR, EXPOSED_COPPER_GRATE, TIER6);

        registerBasic(7, ITEM_NAME_BROWN_WOOL, BROWN_WOOL, TIER7);
        registerCopperBulb(8, ITEM_NAME_BROWN_WOOL_CRYSTAL, OXIDIZED_COPPER_BULB, false, false, TIER8);
        registerBasic(9, ITEM_NAME_BROWN_WOOL_STAR, OXIDIZED_COPPER_GRATE, TIER9);

        registerBasic(10, ITEM_NAME_GREEN_WOOL, GREEN_WOOL, TIER10);
        registerCopperBulb(11, ITEM_NAME_GREEN_WOOL_CRYSTAL, WEATHERED_COPPER_BULB, false, false, TIER11);
        registerBasic(12, ITEM_NAME_GREEN_WOOL_STAR, WEATHERED_COPPER_GRATE, TIER12);

        // registerBasic(13, ITEM_NAME_WHITE_WOOL, WHITE_WOOL, TIER12);
        // register(14, ITEM_NAME_WHITE_WOOL_CRYSTAL, COPPER_BULB, TIER13);
        // register(15, ITEM_NAME_WHITE_WOOL_STAR, COPPER_GRATE, TIER14);

        // register(ITEM_NAME_ORANGE_WOOL, ORANGE_WOOL, TIER2);
        // register(ITEM_NAME_LIGHT_BLUE_WOOL, LIGHT_BLUE_WOOL, TIER4);
        // register(ITEM_NAME_YELLOW_WOOL, YELLOW_WOOL, TIER5);
        // register(ITEM_NAME_GRAY_WOOL, GRAY_WOOL, TIER8);
        // register(ITEM_NAME_LIME_WOOL, LIME_WOOL, TIER6);
        // register(ITEM_NAME_PINK_WOOL, PINK_WOOL, TIER7);
        // register(ITEM_NAME_LIGHT_GRAY_WOOL, LIGHT_GRAY_WOOL, TIER9);
        // register(ITEM_NAME_CYAN_WOOL, CYAN_WOOL, TIER10);
        // register(ITEM_NAME_PURPLE_WOOL, PURPLE_WOOL, TIER11);
        // register(ITEM_NAME_BLUE_WOOL, BLUE_WOOL, TIER12);
        // register(ITEM_NAME_BLACK_WOOL, BLACK_WOOL, TIER16);
    }

    private void registerBasic(int id, CustomItemNames name, Material material, Tiers tier) {
        register(id, name, new BasicWoolHandler(material), tier);
    }

    private void registerCopperBulb(int id, CustomItemNames name, Material material, boolean lit, boolean powered, Tiers tier) {
        register(id, name, new CopperBulbWoolHandler(material, lit, powered), tier);
    }

    private void register(int id, CustomItemNames name, WoolHandler woolHandler, Tiers tier) {
        if (byId.containsKey(id)) throw new IllegalStateException("Duplicate ID: " + id);
        Entry entry = new Entry(name, woolHandler, tier);
        byTier.computeIfAbsent(tier, _ -> new ArrayList<>()).add(entry);
        byName.put(name, entry);
        byId.put(id, entry);
        byBlock.compute(woolHandler.blockMaterial(), (_, list) -> {
            if (list == null) list = new ArrayList<>();
            list.add(entry);
            return list;
        });
    }

    public @NotNull Entry get(int id) {
        if (!byId.containsKey(id)) throw new IllegalStateException("No entry with id: " + id);
        return byId.get(id);
    }

    public @Nullable Collection<Entry> get(Tiers tier) {
        if (!byTier.containsKey(tier)) throw new IllegalStateException("WoolRegistry does not contain an entry with tier " + tier);
        return Collections.unmodifiableCollection(byTier.get(tier));
    }

    public Entry getSingle(Tiers tier) {
        List<Entry> c = byTier.get(tier);
        if (c == null || c.size() != 1) throw new IllegalStateException("WoolRegistry does not contain a single entry with tier " + tier);
        return c.getFirst();
    }

    public Entry get(CustomItemNames name) {
        if (!byName.containsKey(name)) throw new IllegalStateException("WoolRegistry does not contain a single entry with name " + name);
        return byName.get(name);
    }

    public Entry get(Block block) {
        Material material = block.getType();
        if (!byBlock.containsKey(material)) throw new IllegalStateException("WoolRegistry does not contain a single entry with block material " + material);
        List<Entry> entries = byBlock.get(material);
        for (Entry entry : entries) {
            if (entry.handler.test(block)) {
                return entry;
            }
        }
        throw new IllegalStateException("No entry fit block with material " + material + " at " + block.getLocation());
    }

    public boolean contains(Block block) {
        Material material = block.getType();
        if (!byBlock.containsKey(material)) return false;
        List<Entry> entries = byBlock.get(material);
        for (Entry entry : entries) {
            if (entry.handler.test(block)) {
                return true;
            }
        }
        return false;
    }

    public record Entry(CustomItemNames name, WoolHandler handler, Tiers tier) {
    }

    private record BasicWoolHandler(Material material) implements WoolHandler {
        @Override
        public ItemBuilder createItem() {
            return ItemBuilder.item(material);
        }

        @Override
        public Material blockMaterial() {
            return material;
        }

        @Override
        public boolean test(Block block) {
            return block.getType() == material;
        }

        @Override
        public void apply(Block block) {
            block.setType(material, false);
        }
    }

    private record CopperBulbWoolHandler(Material material, boolean lit, boolean powered) implements WoolHandler {
        public CopperBulbWoolHandler {
            if (!(material.createBlockData() instanceof CopperBulb)) throw new IllegalArgumentException("Not a copper bulb: " + material);
        }

        @Override
        public ItemBuilder createItem() {
            return ItemBuilder.item(material).set(ItemComponent.BLOCK_STATE, new ItemBlockState(Map.of("lit", Boolean.toString(lit), "powered", Boolean.toString(powered))));
        }

        @Override
        public Material blockMaterial() {
            return material;
        }

        @Override
        public boolean test(Block block) {
            if (block.getType() != material) return false;
            CopperBulb bulb = (CopperBulb) block.getBlockData();
            if (bulb.isLit() != lit) return false;
            return bulb.isPowered() == powered;
        }

        @Override
        public void apply(Block block) {
            CopperBulb bulb = (CopperBulb) material.createBlockData();
            bulb.setLit(lit);
            bulb.setPowered(powered);
            block.setBlockData(bulb, false);
        }
    }

    public interface WoolHandler {
        ItemBuilder createItem();

        Material blockMaterial();

        boolean test(Block block);

        void apply(Block block);
    }
}
