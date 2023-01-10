package eu.darkcube.system.skyland.SkylandClassSystem;

import eu.darkcube.system.skyland.Equipment.EquipmentType;

import java.util.LinkedList;

public enum SkylandClassTemplate {

    ;

    LinkedList<EquipmentType> allowedEquip;

    public LinkedList<EquipmentType> getAllowedEquip() {
        return allowedEquip;
    }


}
