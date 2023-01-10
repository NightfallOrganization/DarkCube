package eu.darkcube.system.skyland.Equipment;

import eu.darkcube.system.skyland.Skyland;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Equipments implements Equipment{

    int haltbarkeit=0;
    ItemStack model;
    Rarity rarity;
    int lvl;
    ArrayList<Components> components;
    EquipmentType equipmentType;
    static NamespacedKey namespacedKey = new NamespacedKey(Skyland.getInstance(), "EquipInfo");
    String seperator = "**";//todo implement




    public Equipments(int haltbarkeit, ItemStack model, Rarity rarity, int lvl, ArrayList<Components> components, EquipmentType equipmentType) {
        this.haltbarkeit = haltbarkeit;
        this.model = model;
        this.rarity = rarity;
        this.lvl = lvl;
        this.components = components;
        this.equipmentType = equipmentType;
        ItemMeta meta = model.getItemMeta();
        meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, toString());
        model.setItemMeta(meta);
        //todo color weapon stack
        model.getItemMeta().setDisplayName(rarity.getPrefix() + model.getItemMeta().getDisplayName());
        setModelLore();
    }

    private ArrayList<String> setModelLore(){
        ArrayList<String> out = new ArrayList<>();
        out.add("");
        out.add("§7§m      §7« §bStats §7»§m      ");
        out.add("");

        for (PlayerStats pl: getStats()) {
            if(pl.getMenge() > 0){
                out.add(pl.getType() + " §a+" + pl.getMenge());
            }else {
                out.add(pl.getType() + " §c-" + pl.getMenge());
            }

        }
        out.add("");
        out.add("§7§m      §7« §dReqir §7»§7§m      ");
        out.add("");
        out.add("Level §a" + lvl);
        out.add("Rarity " + rarity.getPrefix() + rarity);

        /*out.add("");
        out.add("§7§m      §7« §eSmith §7»§7§m      ");
        out.add("");*/

        model.setLore(out);
        return out;
    }


    public static Equipments loadFromItem(ItemStack itemStack){

        //itemStack.getItemMeta().getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, "test");

        if(itemStack.getItemMeta().getPersistentDataContainer().has(namespacedKey, PersistentDataType.STRING)){
            System.out.println("Key found");
            String s = itemStack.getItemMeta().getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING);


            String[] temp = s.split(",");
            ArrayList<Components> comps = new ArrayList<>();
            for (int i = 8; i < temp.length; i++) {

                if (temp[i] != null) {
                    if(Components.parseFromString(temp[i]) == null){
                        System.out.println("parse is null!!");
                    }
                    comps.add(Components.parseFromString(temp[i]));
                    System.out.println("test: " + (temp[i]));
                }

            }

            return new Equipments(Integer.parseInt(temp[1]), itemStack, Rarity.valueOf(temp[3]), Integer.parseInt(temp[5]), comps, EquipmentType.valueOf(temp[7]));
        }
        System.out.println("key vaL: " + itemStack.getItemMeta().getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING));
        System.out.println("nenene null warum");
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
            if (c != null) {
                out = out + "," + c;

            }

        }


        System.out.println(out);
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

        PlayerStats[] out = new PlayerStats[temp.keySet().size()];
        AtomicInteger i = new AtomicInteger();
        temp.forEach((playerStatsType, integer) -> {
            out[i.get()] = new PlayerStats(playerStatsType, integer);
            i.getAndIncrement();
        });



        //kann optimiert werden durch cachen
        return out;
    }

    @Override
    public void setStats(PlayerStats[] stats) {

    }

    public EquipmentType getEquipmentType() {
        return equipmentType;
    }
}
