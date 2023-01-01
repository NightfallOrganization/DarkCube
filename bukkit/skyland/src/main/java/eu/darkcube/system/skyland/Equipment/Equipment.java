package eu.darkcube.system.skyland.Equipment;

import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public interface Equipment {

    int getHaltbarkeit();
    void setHaltbarkeit(int haltbarkeit);
    ItemStack getModel();
    void setModel(ItemStack model);
    Rarity getRarity();
    void setRarity();
    int getLvl();
    void setLvl(int lvl);
    PlayerStats[] getStats();
    void setStats(PlayerStats[] stats);
    void addComponent(Components components);


}
