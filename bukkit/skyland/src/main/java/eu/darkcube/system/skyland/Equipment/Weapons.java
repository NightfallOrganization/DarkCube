package eu.darkcube.system.skyland.Equipment;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class Weapons extends Equipments implements Weapon{

    int damage;
    Ability ability;

    public Weapons(int haltbarkeit, ItemStack model, Rarity rarity, int lvl, ArrayList<Components> components, EquipmentType equipmentType, Ability ability, int damage) {
        super(haltbarkeit, model, rarity, lvl, components, equipmentType);

        this.damage = damage;
        this.ability = ability;

    }

    public Weapons(ItemStack itemStack) {
        super(itemStack);
    }

    @Override
    public int getDamage() {
        return damage;
    }

    @Override
    public Ability getAbility() {
        return ability;
    }
}
