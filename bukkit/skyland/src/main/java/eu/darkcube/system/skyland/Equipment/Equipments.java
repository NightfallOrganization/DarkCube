package eu.darkcube.system.skyland.Equipment;

import eu.darkcube.system.skyland.Skyland;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.HashMap;

public class Equipments implements Equipment{

    int haltbarkeit=0;
    ItemStack model;
    Rarity rarity;
    int lvl;
    ArrayList<Components> components;
    EquipmentType equipmentType;
    static NamespacedKey namespacedKey = new NamespacedKey(Skyland.getInstance(), "EquipInfo");




    public Equipments(int haltbarkeit, ItemStack model, Rarity rarity, int lvl, ArrayList<Components> components, EquipmentType equipmentType) {
        this.haltbarkeit = haltbarkeit;
        this.model = model;
        this.rarity = rarity;
        this.lvl = lvl;
        this.components = components;
        this.equipmentType = equipmentType;
        model.getItemMeta().getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, toString());
        //todo color weapon stack
        model.getItemMeta().setDisplayName(rarity.getPrefix() + model.getItemMeta().getDisplayName());
        setModelLore();
    }

    private void setModelLore(){
        ArrayList<String> out = new ArrayList<>();
        out.add("");
        out.add("§7§m      §7« §bStats §7»§m      ");
        out.add("");

        for (PlayerStats pl: getStats()) {
            out.add(pl.getType() + " " + pl.getMenge());
        }
        out.add("");
        out.add("§7§m      §7« §dReqir §7»§7§m      ");
        out.add("");
        out.add("Level " + lvl);
        out.add("Rarity " + rarity);

        /*out.add("");
        out.add("§7§m      §7« §eSmith §7»§7§m      ");
        out.add("");*/

        model.setLore(out);
    }


    public static Equipments loadFromItem(ItemStack itemStack){

        if(itemStack.getItemMeta().getPersistentDataContainer().has(namespacedKey)){
            String s = itemStack.getItemMeta().getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING);


            String[] temp = s.split(",");
            ArrayList<Components> comps = new ArrayList<>();
            for (int i = 8; i < temp.length; i++) {
                comps.add(Components.parseFromString(temp[i]));
            }

            return new Equipments(Integer.parseInt(temp[1]), itemStack, Rarity.valueOf(temp[3]), Integer.parseInt(temp[5]), comps, EquipmentType.valueOf(temp[7]));
        }

        return null;
    }

    //this method converts this class into a string to save on an item
    @Override
    public String toString() {
        String out = "Equipments{" +
                "haltbarkeit=," + haltbarkeit +
                ", rarity=," + rarity +
                ", lvl=," + lvl +
                ", equipmentType=," + equipmentType;

        for (Components c :components) {

            out = out + "," + c.toString();

        }
        return out;
    }




    //todo to string and parse from string

    @Override
    public void addComponent(Components components) {
        this.components.add(components);
    }
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
        ArrayList<PlayerStats> out = new ArrayList<>();
        HashMap<PlayerStatsType, Integer> temp = new HashMap<>();



        for (Components c:components) {
            for (PlayerStats p : c.getPStats()){
                if (!temp.containsKey(p.getType())){
                    temp.put(p.getType(), p.getMenge());
                }else {
                    temp.put(p.getType(), temp.get(p.getType()) + p.getMenge());
                }
            }

        }

        temp.forEach((playerStatsType, integer) -> {
            out.add(new PlayerStats(playerStatsType, integer));
        });
        //kann optimiert werden durch cachen
        return (PlayerStats[]) out.toArray();
    }

    @Override
    public void setStats(PlayerStats[] stats) {

    }


}
