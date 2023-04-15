package eu.darkcube.system.skyland.Equipment;

import eu.darkcube.system.skyland.Skyland;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class Weapons extends Equipments implements Weapon{

    Ability ability;
    static NamespacedKey namespacedKey = new NamespacedKey(Skyland.getInstance(), "EquipInfo");


    public Weapons(int haltbarkeit, ItemStack model, Rarity rarity, int lvl, ArrayList<Components> components, EquipmentType equipmentType, Ability ability) {
        super(haltbarkeit, model, rarity, lvl, components, equipmentType);

        this.ability = ability;

    }

/*    public Weapons(ItemStack itemStack) {
        super();

    }*/

    @Override
    public int getDamage() {
        int out = 0;

        for (Components c :super.components) {

            for (PlayerStats ps:c.getPStats()) {
                if (ps.getType() == PlayerStatsType.DAMAGE){
                    out += ps.getMenge();
                }
            }

        }
        return out;
    }

    @Override
    public Ability getAbility() {
        return ability;
    }

    public static Weapons loadFromItem(ItemStack itemStack){

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

            return new Weapons(Integer.parseInt(temp[1]), itemStack, Rarity.valueOf(temp[3]), Integer.parseInt(temp[5]), comps, EquipmentType.valueOf(temp[7]), Ability.valueOf(temp[9]));
        }
        System.out.println("key vaL: " + itemStack.getItemMeta().getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING));
        System.out.println("nenene null warum");
        return null;
    }

    //this method converts this class into a string to save on an item
    @Override
    public String toString() {
        String out = "Weapon{" +
                "haltbarkeit=," + haltbarkeit +
                ", rarity=," + rarity +
                ", lvl=," + lvl +
                ", equipmentType=," + equipmentType +
                ", Ability =," + ability.toString();

        for (Components c :components) {
            if (c != null) {
                out = out + "," + c;

            }

        }


        System.out.println(out);
        return out;
    }
}
