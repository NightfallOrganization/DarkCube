/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.SkylandClassSystem;

import eu.cloudnetservice.driver.document.Document;
import eu.darkcube.system.skyland.Equipment.Equipment;
import eu.darkcube.system.skyland.Equipment.EquipmentType;
import eu.darkcube.system.skyland.Equipment.PlayerStats;
import eu.darkcube.system.util.data.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class

SkylandPlayerClass {

    public static final PersistentDataType<SkylandPlayerClass> TYPE = new PersistentDataType<SkylandPlayerClass>() {
        @Override public SkylandPlayerClass deserialize(Document doc, String key) {
            return doc.readObject(key, SkylandPlayerClass.class);
        }

        @Override public void serialize(Document.Mutable doc, String key, SkylandPlayerClass data) {
            doc.append(key, data);
        }

        @Override public SkylandPlayerClass clone(SkylandPlayerClass object) {
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

    @Override public String toString() {
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
