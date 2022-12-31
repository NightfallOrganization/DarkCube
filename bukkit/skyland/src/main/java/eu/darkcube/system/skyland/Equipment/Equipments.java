package eu.darkcube.system.skyland.Equipment;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Equipments implements Equipment{

    int haltbarkeit=0;
    ItemStack model;
    Rarity rarity;
    int lvl;
    ArrayList<Components> components;
    EquipmentType equipmentType;


    public Equipments(int haltbarkeit, ItemStack model, Rarity rarity, int lvl, ArrayList<Components> components, EquipmentType equipmentType) {
        this.haltbarkeit = haltbarkeit;
        this.model = model;
        this.rarity = rarity;
        this.lvl = lvl;
        this.components = components;
        this.equipmentType = equipmentType;
        //todo color weapon
    }

    public Equipments(ItemStack itemStack){
        //todo load from item
    }

    //todo to string and parse from string


    @Override
    public int getHaltbarkeit() {
        return haltbarkeit;
    }

    @Override
    public void setHaltbarkeit(int haltbarkeit) {
        this.haltbarkeit = haltbarkeit;
    }


    @Override
    public ItemStack getModel() {
        return model;
    }

    @Override
    public void setModel(ItemStack model) {
        this.model = model;
    }

    @Override
    public Rarity getRarity() {
        return rarity;
    }

    @Override
    public void setRarity() {
        this.rarity = rarity;
    }

    @Override
    public int getLvl() {
        return lvl;
    }

    @Override
    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    @Override
    public PlayerStats[] getStats() {
        //todo make it get stats from components
        return new PlayerStats[0];
    }

    @Override
    public void setStats(PlayerStats[] stats) {

    }
}
