package eu.darkcube.system.skyland.SkylandClassSystem;

import eu.darkcube.system.skyland.Equipment.Equipment;
import eu.darkcube.system.skyland.Equipment.PlayerStats;
import eu.darkcube.system.skyland.SkylandClassSystem.SkylandClassTemplate;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class SkylandPlayerClass {

    Player player;

    SkylandClassTemplate sClass;
    LinkedList<PlayerStats> baseStats;

    public List<Equipment> getEquipment() {
        //todo get from inv
        return null;
    }


}
