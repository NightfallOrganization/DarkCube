/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.mobs;

import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.skyland.equipment.Material;
import eu.darkcube.system.skyland.equipment.PlayerStats;
import eu.darkcube.system.skyland.equipment.Rarity;
import eu.darkcube.system.skyland.Skyland;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
@Deprecated(forRemoval = true)
public class FollowingMob implements CustomMob{

	Mob mob;
	EntityType entityType;


	Location targetL;
	LivingEntity targetE;
	boolean isTargetLoc;
	Gson gson = new Gson();

	NamespacedKey nameKey = new NamespacedKey(Skyland.getInstance(), "name");

	NamespacedKey rarityKey = new NamespacedKey(Skyland.getInstance(), "rarity");
	NamespacedKey speedKey = new NamespacedKey(Skyland.getInstance(), "speed");
	//int speed;

	NamespacedKey dmgKey = new NamespacedKey(Skyland.getInstance(), "dmg");
	//int dmg;

	NamespacedKey statsKey = new NamespacedKey(Skyland.getInstance(), "stats");
	//PlayerStats[] stats;

	NamespacedKey abilitiesKey = new NamespacedKey(Skyland.getInstance(), "abilities");
	MonsterAbility[] abilities;



	public FollowingMob(Mob mob, int dmg, PlayerStats[] stats, int speed, boolean isAware, String name, Rarity rarity, MonsterAbility[] monsterAbilities) {
		this.mob = mob;
		entityType = mob.getType();
		mob.setCustomNameVisible(true);
		mob.getPersistentDataContainer().set(CustomMob.getCustomMobTypeKey(), PersistentDataType.INTEGER, 0);
		//this.speed = speed;
		mob.getPersistentDataContainer().set(speedKey, PersistentDataType.INTEGER, speed);
		//this.dmg = dmg;
		mob.getPersistentDataContainer().set(dmgKey, PersistentDataType.INTEGER, dmg);
		//this.stats = stats;
		mob.getPersistentDataContainer().set(statsKey, PersistentDataType.STRING, new Gson().toJson(stats));
		mob.setAware(isAware);
		Skyland.getInstance().getMobs().add(this);

		mob.getPersistentDataContainer().set(nameKey, PersistentDataType.STRING, name);

		mob.getPersistentDataContainer().set(rarityKey, PersistentDataType.STRING, rarity.toString());


		mob.getPersistentDataContainer().set(abilitiesKey, PersistentDataType.STRING, gson.toJson(monsterAbilities));
		abilities = monsterAbilities;
	}

	//todo rework constructor?

	public FollowingMob(EntityType entityType, int dmg, PlayerStats[] stats, int speed, boolean isAware, String name, Rarity rarity) {
		this.entityType = entityType;
		mob.setCustomNameVisible(true);
		mob.getPersistentDataContainer().set(CustomMob.getCustomMobTypeKey(), PersistentDataType.INTEGER, 0);
		//this.speed = speed;
		mob.getPersistentDataContainer().set(speedKey, PersistentDataType.INTEGER, speed);
		//this.dmg = dmg;
		mob.getPersistentDataContainer().set(dmgKey, PersistentDataType.INTEGER, dmg);
		//this.stats = stats;
		mob.getPersistentDataContainer().set(statsKey, PersistentDataType.STRING, new Gson().toJson(stats));
		mob.setAware(isAware);
		Skyland.getInstance().getMobs().add(this);

		mob.getPersistentDataContainer().set(nameKey, PersistentDataType.STRING, name);

		mob.getPersistentDataContainer().set(rarityKey, PersistentDataType.STRING, rarity.toString());



	}

	public FollowingMob(Mob mob){
		this.mob = mob;
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
		if (targetL != null || targetE != null) {

			if (getClosestPlayer() == null){
				Random r = new Random();
				if (isTargetLoc){
					if (mob.getLocation().distance(getTargetL()) < 2){
						setTargetL(mob.getLocation().add(r.nextInt(9)-5, 0 , r.nextInt(9)-5));
					}else {


						if (mob.getLocation().add(mob.getLocation().getDirection().multiply(1/mob.getLocation().distance(targetL))).getBlock().getType().equals(
								org.bukkit.Material.AIR)){
							setTargetL(mob.getLocation().add(r.nextInt(9)-5, 0 , r.nextInt(9)-5));
							//todo prob remove this if
						}
						//todo calc where mob is looking
					}
				}else {
					setTargetL(mob.getLocation().add(r.nextInt(9)-5, 0 , r.nextInt(9)-5));
				}

			}else {
				setTargetE(getClosestPlayer());
			}

			if (isTargetLoc){
				mob.getPathfinder().moveTo(targetL, getSpeed());
			}else {
				mob.getPathfinder().moveTo(targetE, getSpeed());
			}

		}else {
			setTargetL(mob.getLocation());
		}

		if (abilities == null){
			abilities = fetchAbilities();
		}

		for (MonsterAbility ability : abilities) {
			ability.trigger(this);
		}

	}

	@Override
	public EntityType getType() {
		return entityType;
	}

	@Override
	public List<Material> getLootTable() {
		ArrayList<Material> out = new ArrayList<>();
		out.add(Material.DRAGON_SCALE);
		out.add(Material.TESTING_IRON);

		return out;
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

	@Override
	public int getAttackDmg() {
		return mob.getPersistentDataContainer().get(dmgKey, PersistentDataType.INTEGER);
	}

	public void setMob(Mob mob) {
		this.mob = mob;
	}

	public Location getTargetL() {
		return targetL;
	}

	public void setTargetL(Location targetL) {
		this.targetL = targetL;
		isTargetLoc = true;
	}

	public LivingEntity getTargetE() {
		return targetE;
	}

	public void setTargetE(LivingEntity targetE) {
		this.targetE = targetE;
		isTargetLoc = false;
	}

	public void setDmg(int dmg){
		mob.getPersistentDataContainer().set(dmgKey, PersistentDataType.INTEGER, dmg);
	}

	public int getSpeed() {
		return mob.getPersistentDataContainer().get(speedKey, PersistentDataType.INTEGER);
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

	public MonsterAbility[] fetchAbilities(){
		//todo fix error
		return gson.fromJson(mob.getPersistentDataContainer().get(abilitiesKey, PersistentDataType.STRING), MonsterAbility[].class);
	}

	public void saveMonsterAbilities(){
		mob.getPersistentDataContainer().set(abilitiesKey, PersistentDataType.STRING, gson.toJson(abilities));
	}

	private class AbilityEntity {
		Class<MonsterAbility> clazz;
		NamespacedKey key;

		private AbilityEntity(Class clazz, String name){
			this.clazz = clazz;
			this.key = new NamespacedKey(Skyland.getInstance(), "ability." + name);
		}

		private MonsterAbility loadAbility(String json, Gson gson){
			return gson.fromJson(json, clazz);
		}

	}
}
