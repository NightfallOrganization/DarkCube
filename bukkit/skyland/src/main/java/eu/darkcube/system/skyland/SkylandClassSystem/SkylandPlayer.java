package eu.darkcube.system.skyland.SkylandClassSystem;

import eu.darkcube.system.skyland.Equipment.Equipment;
import eu.darkcube.system.skyland.Equipment.Equipments;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SkylandPlayer {

    ArrayList<SkylandPlayerClass> skylandPlayerClasses;
    SkylandPlayerClass activeClass;
    Player player;
    int money;
    //todo

    public SkylandPlayer(){
        //todo
    }

    private ArrayList<SkylandPlayerClass> loadSkylandPlayerClasses(){
        //todo
        player.getMetadata("skylandPlayer").get(0).asString();
        return null;
    }


    public Player getPlayer() {
        return player;
    }

    public List<Equipment> getEquipment() {
        ArrayList<Equipment> out = new ArrayList<>();
        for(ItemStack i : player.getEquipment().getArmorContents()){
            Equipments equipments = Equipments.loadFromItem(i);
            if (equipments != null) {
                if(activeClass.getsClass().allowedEquip.contains(equipments.getEquipmentType())){
                    out.add(equipments);
                }

            }
        }

        Equipments equipments = Equipments.loadFromItem(player.getInventory().getItemInMainHand());
        if (equipments != null) {
            if(activeClass.getsClass().allowedEquip.contains(equipments.getEquipmentType())){
                out.add(equipments);
            }
        }
        //todo get from inv need duckness for additional slot
        return out;
    }
}
