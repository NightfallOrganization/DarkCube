package eu.darkcube.system.skyland.Equipment;

public interface Weapon extends Equipment{

    Damage[] getDamage();
    Ability getAbility();
}
