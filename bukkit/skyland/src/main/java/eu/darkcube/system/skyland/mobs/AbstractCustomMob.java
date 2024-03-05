/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.skyland.mobs;

import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.skyland.Skyland;
import eu.darkcube.system.skyland.equipment.Material;
import eu.darkcube.system.skyland.equipment.PlayerStats;
import eu.darkcube.system.skyland.equipment.Rarity;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public abstract class AbstractCustomMob implements CustomMob {

    Mob mob;
    EntityType entityType;

    Gson gson = new Gson();

    NamespacedKey nameKey = new NamespacedKey(Skyland.getInstance(), "name");

    NamespacedKey rarityKey = new NamespacedKey(Skyland.getInstance(), "rarity");
    NamespacedKey speedKey = new NamespacedKey(Skyland.getInstance(), "speed");
    //int speed;

    NamespacedKey statsKey = new NamespacedKey(Skyland.getInstance(), "stats");
    //PlayerStats[] stats;

    NamespacedKey abilitiesKey = new NamespacedKey(Skyland.getInstance(), "abilities");

    NamespacedKey dmgKey = new NamespacedKey(Skyland.getInstance(), "dmg");
    MonsterAbility[] abilities;



    public AbstractCustomMob(Mob mob, int dmg, PlayerStats[] stats, int speed, boolean isAware, String name, Rarity rarity, MonsterAbility[] monsterAbilities) {
        this.mob = mob;
        entityType = mob.getType();
        abilities = monsterAbilities;
        mob.setCustomNameVisible(true);
        mob.setAware(isAware);

        mob.getPersistentDataContainer().set(CustomMob.getCustomMobTypeKey(), PersistentDataType.INTEGER, 0);
        setSpeed(speed);
        setStats(stats);

        setRarity(rarity);
        setName(name);


        saveMonsterAbilities();

        //add the mob to the main registry so its ai tick gets called
        Skyland.getInstance().getMobs().add(this);
    }

    //todo rework constructor?


    public AbstractCustomMob(Mob mob){
        this.mob = mob;
        loadAbilities();
        Skyland.getInstance().getMobs().add(this);
    }

    @Override
    public void updateName() {
        mob.setCustomName(((int)mob.getHealth()) + "/" + ((int)mob.getMaxHealth()) + " hp   " + getRarity().getPrefix()  + getName());
    }

    @Override
    public Mob getMob() {
        return mob;
    }

    @Override
    public void aiTick() {

        if (abilities == null){
            loadAbilities();
        }

        for (MonsterAbility ability : abilities) {
            ability.trigger(this);
        }

    }

    @Override
    public EntityType getType() {
        return entityType;
    }

    private Player getClosestPlayer() {
        Collection<Player>
                nearbyPlayers = mob.getWorld().getNearbyPlayers(mob.getLocation(), 10.0, player ->
                !player.isDead() && player.getGameMode() != GameMode.SPECTATOR && player.isValid());
        double closestDistance = -1.0;
        Player closestPlayer = null;
        for (Player player : nearbyPlayers) {
            double distance = player.getLocation().distanceSquared(mob.getLocation());
            if (closestDistance != -1.0 && !(distance < closestDistance)) {
                continue;
            }
            closestDistance = distance;
            closestPlayer = player;
        }
        return closestPlayer;
    }

    @Override
    public PlayerStats[] getStats() {
        String temp = mob.getPersistentDataContainer().get(statsKey, PersistentDataType.STRING);
        return new Gson().fromJson(temp, PlayerStats[].class);
    }



    public void setMob(Mob mob) {
        this.mob = mob;
    }

    public void setSpeed(int speed) {
        mob.getPersistentDataContainer().set(speedKey, PersistentDataType.INTEGER, speed);
    }

    public void setStats(PlayerStats[] stats) {
        mob.getPersistentDataContainer().set(statsKey, PersistentDataType.STRING, new Gson().toJson(stats));
    }

    public String getName(){
        return mob.getPersistentDataContainer().get(nameKey, PersistentDataType.STRING);
    }

    public void setName(String name){
        mob.getPersistentDataContainer().set(nameKey, PersistentDataType.STRING, name);
    }

    public Rarity getRarity(){
        return Rarity.valueOf(mob.getPersistentDataContainer().get(rarityKey, PersistentDataType.STRING));
    }

    public void setRarity(Rarity rarity){
        mob.getPersistentDataContainer().set(rarityKey, PersistentDataType.STRING, rarity.toString());
    }

    public void setDmg(int dmg){
        mob.getPersistentDataContainer().set(dmgKey, PersistentDataType.INTEGER, dmg);
    }

    public int getDmg(){
        return mob.getPersistentDataContainer().get(dmgKey, PersistentDataType.INTEGER);
    }


    public MonsterAbility[] loadAbilities(){

        var abilityRefs = gson.fromJson(mob.getPersistentDataContainer().get(abilitiesKey, PersistentDataType.STRING), AbilityEntity[].class);

        List<MonsterAbility> monsterAbilities = new ArrayList<>();

        for (AbilityEntity abilityRef : abilityRefs) {
            monsterAbilities.add(abilityRef.loadAbility(mob, gson));
        }

        this.abilities = monsterAbilities.toArray(new MonsterAbility[0]);


        return abilities;
    }

    public void saveMonsterAbilities(){
        List<AbilityEntity> abilityEntities = new ArrayList<>();
        int i = 0;
        for (MonsterAbility ability : abilities){
            var abilityEntity = new AbilityEntity(ability.getCurrentClass(), "ability:" +i);

            abilityEntities.add(abilityEntity);
            i ++;
        }


        mob.getPersistentDataContainer().set(abilitiesKey, PersistentDataType.STRING, gson.toJson(abilityEntities));
    }

    private class AbilityEntity {

        Class<MonsterAbility> clazz;
        NamespacedKey key;

        private AbilityEntity(Class clazz, String name){
            this.clazz = clazz;
            this.key = new NamespacedKey(Skyland.getInstance(), name);
        }

        private MonsterAbility loadAbility(Mob mob, Gson gson){
            return gson.fromJson(mob.getPersistentDataContainer().get(key, PersistentDataType.STRING), clazz);
        }

        private void saveAbility(Mob mob, MonsterAbility ability){
            mob.getPersistentDataContainer().set(key, PersistentDataType.STRING, gson.toJson(ability));
        }

    }

    @Override
    public int getAttackDmg() {
        return getDmg();
    }


}
