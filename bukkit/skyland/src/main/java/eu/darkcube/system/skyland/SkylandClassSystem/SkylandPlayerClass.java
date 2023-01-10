package eu.darkcube.system.skyland.SkylandClassSystem;

import eu.darkcube.system.skyland.Equipment.Equipment;
import eu.darkcube.system.skyland.Equipment.PlayerStats;
import eu.darkcube.system.skyland.Equipment.PlayerStatsType;
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


    public SkylandPlayerClass(SkylandPlayer skylandPlayer, SkylandClassTemplate sClass, int lvl, LinkedList<PlayerStats> baseStats) {
        this.skylandPlayer = skylandPlayer;
        this.sClass = sClass;
        this.lvl = lvl;
        this.baseStats = baseStats;
    }

    @Override
    public String toString() {

        String out = sClass.toString() + "´´´´";

        for (PlayerStats ps: baseStats){

            out = out + "´´";

        }
        out = out.substring(0, out.length()-3);
        out = out + "´´´´";
        out = out + lvl;

        return out;
    }

    public SkylandPlayerClass parseString(String s, SkylandPlayer sp){


        SkylandClassTemplate template;
        LinkedList<PlayerStats> pStat = new LinkedList<>();
        int lvl;


        String[] temp = s.split("´´´´");
        template = SkylandClassTemplate.valueOf(temp[0]);
        for (String str :temp[1].split("´´")){
            pStat.add(PlayerStats.parseString(str));
        }
        lvl = Integer.parseInt(temp[2]);

        return new SkylandPlayerClass(sp,template , lvl, pStat);
    }

    public SkylandClassTemplate getsClass() {
        return sClass;
    }
}
