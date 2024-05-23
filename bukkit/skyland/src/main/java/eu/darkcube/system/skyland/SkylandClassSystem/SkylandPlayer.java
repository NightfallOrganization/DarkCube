/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.skyland.SkylandClassSystem;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.skyland.Equipment.Equipment;
import eu.darkcube.system.skyland.Equipment.EquipmentType;
import eu.darkcube.system.skyland.Equipment.Equipments;
import eu.darkcube.system.skyland.Equipment.PlayerStats;
import eu.darkcube.system.skyland.Equipment.PlayerStatsType;
import eu.darkcube.system.skyland.Equipment.Weapon;
import eu.darkcube.system.skyland.Equipment.Weapons;
import eu.darkcube.system.skyland.Skyland;
import eu.darkcube.system.skyland.staticval.Globals;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.util.data.PersistentDataType;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SkylandPlayer implements SkylandEntity {

    public static final PersistentDataType<List<SkylandPlayerClass>> skylandClassList = PersistentDataTypes.list(SkylandPlayerClass.TYPE);
    private static final Key MONEY_KEY = Key.key(Skyland.getInstance(), "money");
    private static final Key ACTIVE_CLASS_KEY = Key.key(Skyland.getInstance(), "active_class_key");
    private static final Key SKYLAND_PLAYER_CLASSES = Key.key(Skyland.getInstance(), "skyland_player_classes");
    private final User user;
    // todo

    public SkylandPlayer(User user) {
        this.user = user;
        user.persistentData().setIfNotPresent(MONEY_KEY, PersistentDataTypes.BIGINTEGER, BigInteger.ZERO);
        user.persistentData().setIfNotPresent(SKYLAND_PLAYER_CLASSES, skylandClassList, new ArrayList<>());

    }

    public boolean contains(EquipmentType[] collection, EquipmentType target) {
        for (EquipmentType eq : collection) {
            if (eq.equals(target)) {
                return true;
            }
        }

        return false;
    }

    public List<Equipment> getEquipment() {
        ArrayList<Equipment> out = new ArrayList<>();
        for (ItemStack i : getPlayer().getEquipment().getArmorContents()) {
            Equipments equipments = Equipments.loadFromItem(i);
            if (equipments != null) {
                if (getActiveClass().isEqUsable(equipments)) {
                    out.add(equipments);
                }

            }
        }

        Weapons equipments = Weapons.loadFromItem(getPlayer().getInventory().getItemInMainHand());
        if (equipments != null) {
            if (getActiveClass().isEqUsable(equipments)) {
                out.add(equipments);
            }
        }

        // todo get from inv need duckness for additional slot
        return out;
    }

    public Weapon getActiveWeapon() {
        // returns null if there isnt a weapon in the main hand
        System.out.println("loading from main hand");

        if (getPlayer().getInventory().getItemInMainHand().getItemMeta() != null) {

            Weapons equipments = Weapons.loadFromItem(getPlayer().getInventory().getItemInMainHand());
            if (equipments != null) {
                if (getActiveClass().isEqUsable(equipments)) {
                    System.out.println("allowed to use this weapon");
                    return equipments;
                } else {
                    System.out.println("not allowed to use this weapon");
                }
            }
        } else {
            System.out.println("item meta of active weapon is null");
        }

        return null;
    }

    @Override
    public PlayerStats[] getStats() {

        ArrayList<PlayerStats> playerStats = new ArrayList<>();
        // playerStats.add(activeClass);

        for (Equipment eq : getEquipment()) {
            playerStats.addAll(List.of(eq.getStats()));
        }

        return PlayerStats.mergePstats(playerStats);
    }

    @Override
    public int getAttackDmg() {
        int strength = 0;

        for (PlayerStats ps : getStats()) {
            if (ps.getType() == PlayerStatsType.STRENGHT) {
                strength += ps.getMenge();
            }
        }

        if (getActiveWeapon() != null) {
            return getActiveWeapon().getDamage() * (1 + (strength * Globals.strengthDmgMult / 100));
        } else {
            System.out.println("no weapon found default dmg = 1");
            return 1;
        }
        // here strength effectiveness is calculated

    }

    public User getUser() {
        return user;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(user.uniqueId());
    }

    public List<SkylandPlayerClass> getSkylandPlayerClasses() {
        return user.persistentData().get(SKYLAND_PLAYER_CLASSES, skylandClassList);
    }

    public void setSkylandPlayerClasses(List<SkylandPlayerClass> classes) {
        user.persistentData().set(SKYLAND_PLAYER_CLASSES, skylandClassList, classes);
    }

    public SkylandPlayerClass getActiveClass() {
        return getSkylandPlayerClasses().get(user.persistentData().get(ACTIVE_CLASS_KEY, PersistentDataTypes.INTEGER));
    }

    /*
        public void setActiveClass(SkylandPlayerClass activeClass) {
            for (int i = 0; i < getSkylandPlayerClasses().size(); i++) {
                if (getSkylandPlayerClasses().get(i).equals(activeClass)){
                    user.getPersistentDataStorage().set(ACTIVE_CLASS_KEY, PersistentDataTypes.INTEGER, i);
                    return;
                }
            }
            user.getPersistentDataStorage().set(ACTIVE_CLASS_KEY, PersistentDataTypes.INTEGER, 0);
            System.out.println("active class not found");
        }

     */
    public void setActiveClass(int activeClass) {
        user.persistentData().set(ACTIVE_CLASS_KEY, PersistentDataTypes.INTEGER, activeClass);

    }

    public BigInteger getMoney() {
        return user.persistentData().get(MONEY_KEY, PersistentDataTypes.BIGINTEGER);
    }

    public void setMoney(BigInteger money) {
        user.persistentData().set(MONEY_KEY, PersistentDataTypes.BIGINTEGER, money);
    }

    @Override
    public String toString() {
        return "SkylandPlayer{" + "user=" + user + "activeClass=" + getActiveClass().getsClass() + "Money=" + getMoney() + '}';
    }

    public boolean hastActiveClass() {
        return user.persistentData().has(ACTIVE_CLASS_KEY);
    }

    public int getActiveClassID() {
        return user.persistentData().get(ACTIVE_CLASS_KEY, PersistentDataTypes.INTEGER);
    }

    public void resetData() {
        user.persistentData().remove(ACTIVE_CLASS_KEY, SkylandPlayerClass.TYPE);
        setSkylandPlayerClasses(new ArrayList<>());

    }

}
