/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.perk.user;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.IntStream;

import eu.darkcube.minigame.woolbattle.api.perk.ActivationType;
import eu.darkcube.minigame.woolbattle.api.perk.PerkName;
import eu.darkcube.minigame.woolbattle.api.perk.PerkRegistry;
import eu.darkcube.minigame.woolbattle.api.user.PerksStorage;
import eu.darkcube.minigame.woolbattle.common.perk.perks.active.CapsulePerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.active.SwitcherPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.other.ArrowPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.other.BowPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.other.ShearsPerk;
import eu.darkcube.minigame.woolbattle.common.perk.perks.passive.RocketJumpPerk;
import eu.darkcube.minigame.woolbattle.common.util.Slot.Hotbar;
import eu.darkcube.minigame.woolbattle.common.util.Slot.Inventory;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;

public class CommonPerksStorage implements PerksStorage {
    private static final PersistentDataType<PerkName[]> PERK_NAME_ARRAY = PersistentDataTypes.array(PerkName.TYPE, PerkName.class);
    public static final PersistentDataType<CommonPerksStorage> TYPE = new PersistentDataType<>() {
        @Override
        public @NotNull CommonPerksStorage deserialize(JsonElement json) {
            var d = json.getAsJsonObject();
            var docPerks = d.getAsJsonObject("perks");
            var docPerkSlots = d.getAsJsonObject("perkSlots");
            var perks = new EnumMap<ActivationType, PerkName[]>(ActivationType.class);
            var perkSlots = new EnumMap<ActivationType, int[]>(ActivationType.class);
            for (var documentKey : docPerks.keySet()) {
                var type = ActivationType.valueOf(documentKey);
                var array = PERK_NAME_ARRAY.deserialize(docPerks.get(documentKey));
                perks.put(type, array);
            }
            for (var documentKey : docPerkSlots.keySet()) {
                var type = ActivationType.valueOf(documentKey);
                var array = PersistentDataTypes.INT_ARRAY.deserialize(docPerkSlots.get(documentKey));
                perkSlots.put(type, array);
            }
            return new CommonPerksStorage(perks, perkSlots);
        }

        @Override
        public @NotNull JsonElement serialize(@NotNull CommonPerksStorage data) {
            var d = new JsonObject();
            var docPerks = new JsonObject();
            var docPerkSlots = new JsonObject();
            for (var type : ActivationType.values()) {
                var p = data.perks(type);
                var n = new ArrayList<PerkName>();
                for (var perkName : p) if (perkName != null) n.add(perkName);
                p = n.toArray(PerkName[]::new);
                docPerks.add(type.name(), PERK_NAME_ARRAY.serialize(p));
                docPerkSlots.add(type.name(), PersistentDataTypes.INT_ARRAY.serialize(data.perkInvSlots(type)));
            }
            d.add("perks", docPerks);
            d.add("perkSlots", docPerkSlots);
            return d;
        }

        @Override
        public @NotNull CommonPerksStorage clone(CommonPerksStorage object) {
            return object.clone();
        }
    };
    private final Map<ActivationType, PerkName[]> perks;
    private final Map<ActivationType, int[]> perkSlots;

    public CommonPerksStorage(PerkRegistry registry) {
        this();
        reset(registry);
    }

    private CommonPerksStorage() {
        var perks = new EnumMap<ActivationType, PerkName[]>(ActivationType.class);
        var perkSlots = new EnumMap<ActivationType, int[]>(ActivationType.class);
        for (var type : ActivationType.values()) {
            perks.put(type, new PerkName[type.maxCount()]);
            perkSlots.put(type, new int[type.maxCount()]);
        }
        this.perks = Collections.unmodifiableMap(perks);
        this.perkSlots = Collections.unmodifiableMap(perkSlots);
    }

    private CommonPerksStorage(Map<ActivationType, PerkName[]> perks, Map<ActivationType, int[]> perkSlots) {
        this();
        perks(perks);
        for (var entry : perkSlots.entrySet()) {
            System.arraycopy(entry.getValue(), 0, this.perkSlots.get(entry.getKey()), 0, Math.min(entry.getValue().length, this.perkSlots.get(entry.getKey()).length));
        }
    }

    @Override
    public @NotNull CommonPerksStorage clone() {
        return new CommonPerksStorage(perks, perkSlots);
    }

    @Override
    public PerkName @NotNull [] perks(@NotNull ActivationType type) {
        return perks.get(type).clone();
    }

    @Override
    public @NotNull PerkName perk(@NotNull ActivationType type, int perkSlot) {
        return perks.get(type)[perkSlot];
    }

    @Override
    public void perk(@NotNull ActivationType type, int perkSlot, @NotNull PerkName perk) {
        perks.get(type)[perkSlot] = perk;
    }

    @Override
    public int @NotNull [] perkInvSlots(@NotNull ActivationType type) {
        return perkSlots.get(type).clone();
    }

    @Override
    public int perkInvSlot(@NotNull ActivationType type, int perkSlot) {
        return perkSlots.get(type)[perkSlot];
    }

    @Override
    public void perkInvSlot(@NotNull ActivationType type, int perkSlot, int slot) {
        perkSlots.get(type)[perkSlot] = slot;
    }

    @Override
    public @NotNull Map<ActivationType, PerkName[]> perks() {
        return perks;
    }

    @Override
    public void perks(@NotNull Map<ActivationType, PerkName[]> perks) {
        for (var entry : perks.entrySet()) {
            System.arraycopy(entry.getValue(), 0, this.perks.get(entry.getKey()), 0, Math.min(entry.getValue().length, this.perks.get(entry.getKey()).length));
        }
    }

    /**
     * Verifies the integrity of all perks and slots in this storage.
     * This also fixes any issues found with duplicate perks, slots, and any other issues found.
     *
     * @return false if any issues were found
     */
    public boolean verifyIntegrity(@NotNull PerkRegistry perkRegistry) {
        var types = ActivationType.values();
        var slotsQueryFrom = new ArrayDeque<>(IntStream.of(POSSIBLE_SLOTS).boxed().toList());

        var changed = false;
        var slotsUsed = new HashSet<Integer>();
        var perksUsed = new HashSet<PerkName>();
        for (var type : types) {
            var perkSlotArray = perkSlots.get(type);
            var perkArray = perks.get(type);
            for (var i = 0; i < perkSlotArray.length; i++) {
                var slot = perkSlotArray[i];

                var badSlot = false;
                while (!slotsUsed.add(slot)) {
                    badSlot = true;
                    slot = slotsQueryFrom.removeFirst();
                }
                if (badSlot) {
                    changed = true;
                    perkSlotArray[i] = slot;
                }

                var name = perkArray[i];
                if (name != null && !perkRegistry.contains(name)) name = null;
                var badName = name == null || perksUsed.contains(name);
                if (!badName) {
                    perksUsed.add(name);
                    // Slot and name are fine, continue
                    continue;
                }

                name = preferredPerk(type, i);
                if (perksUsed.contains(name)) name = null;
                if (name != null && !perkRegistry.contains(name)) name = null;

                if (name == null) {
                    for (var perk : perkRegistry.perks(type)) {
                        var n = perk.perkName();
                        if (perksUsed.contains(n)) continue;
                        name = n;
                        break;
                    }
                }
                if (name == null) throw new NullPointerException();
                perksUsed.add(name);
                perkArray[i] = name;
                changed = true;
            }
        }
        return !changed;
    }

    @Override
    public void reset(@NotNull PerkRegistry perkRegistry) {
        var types = ActivationType.values();
        var slotsQueryFrom = new ArrayDeque<>(IntStream.of(POSSIBLE_SLOTS).boxed().toList());

        var perksUsed = new ArrayList<PerkName>();
        for (var type : types) {
            for (var i = 0; i < perkSlots.get(type).length; i++) {
                int slot = slotsQueryFrom.removeFirst();

                perkSlots.get(type)[i] = slot;
                var preferred = preferredPerk(type, i);

                PerkName name = null;
                if (preferred != null && !perksUsed.contains(preferred)) name = preferred;
                if (name != null && !perkRegistry.contains(name)) name = null;

                if (name == null) {
                    for (var perk : perkRegistry.perks(type)) {
                        name = perk.perkName();
                        if (perksUsed.contains(name)) continue;
                        break;
                    }
                }
                if (name == null) throw new NullPointerException();
                perksUsed.add(name);
                perks.get(type)[i] = name;
            }
        }
    }

    private static final int[] POSSIBLE_SLOTS = IntStream.concat(IntStream.of(Hotbar.SLOT_1, // Bow
                    Hotbar.SLOT_2, // Shears
                    Hotbar.SLOT_3, // Active Perk 1
                    Hotbar.SLOT_4, // Active Perk 2
                    Hotbar.SLOT_5, // Enderpearl
                    Hotbar.SLOT_9, // Passive Perk
                    Inventory.SLOT_9, // Arrow
                    Inventory.SLOT_1 // Double jump
            ), IntStream.concat(IntStream.of(Hotbar.SLOTS), IntStream.of(Inventory.SLOTS)) // Any illegal perks
    ).distinct().toArray();

    private static @Nullable PerkName preferredPerk(ActivationType type, int i) {
        PerkName preferred = null;
        if (type == ActivationType.PRIMARY_WEAPON && i == 0) {
            preferred = BowPerk.BOW;
        } else if (type == ActivationType.SECONDARY_WEAPON && i == 0) {
            preferred = ShearsPerk.SHEARS;
        } else if (type == ActivationType.ARROW && i == 0) {
            preferred = ArrowPerk.ARROW;
        } else if (type == ActivationType.ACTIVE && i == 0) {
            preferred = CapsulePerk.CAPSULE;
        } else if (type == ActivationType.ACTIVE && i == 1) {
            preferred = SwitcherPerk.SWITCHER;
        } else if (type == ActivationType.PASSIVE && i == 0) {
            preferred = RocketJumpPerk.ROCKET_JUMP;
        }
        return preferred;
    }
}
