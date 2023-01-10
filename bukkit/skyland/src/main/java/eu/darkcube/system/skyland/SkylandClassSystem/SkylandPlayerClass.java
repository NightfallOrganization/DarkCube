package eu.darkcube.system.skyland.SkylandClassSystem;

import eu.darkcube.system.skyland.Equipment.Equipment;
import eu.darkcube.system.skyland.Equipment.PlayerStats;
import eu.darkcube.system.skyland.SkylandClassSystem.SkylandClassTemplate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

public class SkylandPlayerClass {

    SkylandPlayer skylandPlayer;
    SkylandClassTemplate sClass;
    int lvl;
    LinkedList<PlayerStats> baseStats;



    @Override
    public String toString() {

        //todo
        return super.toString();
    }

    public SkylandPlayerClass parseString(String s){
        //todo
        return null;
    }

    public SkylandClassTemplate getsClass() {
        return sClass;
    }
}
