/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.skyland.SkylandClassSystem;

import java.util.ArrayList;
import java.util.List;

import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.skyland.Equipment.Equipment;
import eu.darkcube.system.skyland.Equipment.EquipmentType;
import eu.darkcube.system.skyland.Equipment.PlayerStats;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;

public class

SkylandPlayerClass {

    private static final PersistentDataType<List<PlayerStats>> TYPE_PLAYER_STATS_LIST = PersistentDataTypes.list(PlayerStats.TYPE);

    public static final PersistentDataType<SkylandPlayerClass> TYPE = new PersistentDataType<SkylandPlayerClass>() {
        @Override
        public SkylandPlayerClass deserialize(JsonElement json) {
            var d = json.getAsJsonObject();
            var sClass = SkylandClassTemplate.valueOf(d.get("sClass").getAsString());
            var lvl = d.get("lvl").getAsInt();
            var baseStats = TYPE_PLAYER_STATS_LIST.deserialize(d.get("baseStats"));
            return new SkylandPlayerClass(sClass, lvl, baseStats);
        }

        @Override
        public JsonElement serialize(SkylandPlayerClass data) {
            var d = new JsonObject();
            d.addProperty("lvl", data.lvl);
            d.addProperty("sClass", data.sClass.name());
            d.add("baseStats", TYPE_PLAYER_STATS_LIST.serialize(data.baseStats));
            return d;
        }

        @Override
        public SkylandPlayerClass clone(SkylandPlayerClass object) {
            return new SkylandPlayerClass(object.getsClass(), object.getLvl(), new ArrayList<>(object.getBaseStats()));
        }
    };
    SkylandClassTemplate sClass;
    int lvl;
    List<PlayerStats> baseStats;

    public SkylandPlayerClass(SkylandClassTemplate sClass, int lvl, List<PlayerStats> baseStats) {
        this.sClass = sClass;
        this.lvl = lvl;
        this.baseStats = baseStats;
    }

    public SkylandClassTemplate getsClass() {
        return sClass;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public List<PlayerStats> getBaseStats() {
        return baseStats;
    }

    @Override
    public String toString() {
        return "SkylandPlayerClass{" + "sClass=" + getsClass() + ", lvl=" + getLvl() + ", baseStats=" + getBaseStats().toString() + '}';
    }

    public boolean isEqUsable(Equipment eq) {
        boolean isAllowedType = false;
        boolean isLvl = false;
        for (EquipmentType equipmentType : getsClass().getAllowedEquip()) {
            if (eq.getEquipmentType().equals(equipmentType)) {
                isAllowedType = true;
            }
        }

        if (lvl >= eq.getLvl()) {
            isLvl = true;
        }

        return isLvl && isAllowedType;

    }
}
