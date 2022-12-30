package eu.darkcube.system.skyland.Equipment;

import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public interface Equipment {

    int getHaltbarkeit();
    void setHaltbarkeit(int haltbarkeit);
    String getName();
    void setName(String s);
    ItemStack getModel();
    void setModel(ItemStack model);
    Rarity getRarity();
    void setRarity();
    int getLvl();
    void setLvl(int lvl);
    PlayerStats[] getStats();
    void setStats(PlayerStats[] stats);


}
