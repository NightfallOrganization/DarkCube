/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.Listener;

import eu.darkcube.system.skyland.Equipment.Materials;
import eu.darkcube.system.skyland.Equipment.PlayerStats;
import eu.darkcube.system.skyland.Equipment.PlayerStatsType;
import eu.darkcube.system.skyland.Skyland;
import eu.darkcube.system.skyland.SkylandClassSystem.SkylandClassTemplate;
import eu.darkcube.system.skyland.SkylandClassSystem.SkylandEntity;
import eu.darkcube.system.skyland.SkylandClassSystem.SkylandPlayer;
import eu.darkcube.system.skyland.SkylandClassSystem.SkylandPlayerModifier;
import eu.darkcube.system.skyland.inventoryUI.UINewClassSelect;
import eu.darkcube.system.skyland.mobs.CustomMob;
import eu.darkcube.system.skyland.mobs.FollowingMob;
import eu.darkcube.system.skyland.worldGen.SkylandBiomes;
import eu.darkcube.system.skyland.worldGen.Structures.SkylandStructure;
import eu.darkcube.system.skyland.worldGen.Structures.SkylandStructureModifiers;
import eu.darkcube.system.userapi.UserAPI;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import javax.swing.text.html.HTML.Tag;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

public class SkylandListener implements Listener {

	boolean userAPI = false;
	Skyland skyland;
	NamespacedKey skylandPlayer = new NamespacedKey(Skyland.getInstance(), "SkylandPlayer");
	SimplexOctaveGenerator structureGen = new SimplexOctaveGenerator(new Random(12452), 8);

	public SkylandListener(Skyland skyland) {
		this.skyland = skyland;
		structureGen.setScale(0.1D);
	}

	public int calculateDmgAfterDefense(SkylandEntity attacker, SkylandEntity defender) {

		PlayerStats[] e2Stats = defender.getStats();

		int dmgE1 = attacker.getAttackDmg();
		System.out.println("attack pre mitigation: " + dmgE1);

		int armorE2 = 0;

		for (PlayerStats ps : e2Stats) {
			System.out.println(ps.getType() + ": " + ps.getMenge());
			if (ps.getType() == PlayerStatsType.ARMOR) {
				armorE2 += ps.getMenge();
			}
		}
		System.out.println("armor: " + armorE2);
		System.out.println("attack post mitigation: " + dmgE1 / (1 + (armorE2 / 100)));
		return dmgE1 / (1 + (armorE2 / 100));
	}


	@EventHandler
	public void handleDmg(EntityDamageByEntityEvent e) {

		if (e.getDamager() instanceof Player attacker) {

			SkylandPlayer attackerSkp = SkylandPlayerModifier.getSkylandPlayer(attacker);
			int dmg = attackerSkp.getAttackDmg();

			if (e.getEntity() instanceof Player defender) {

				SkylandPlayer defenderSkp = SkylandPlayerModifier.getSkylandPlayer(defender);
				dmg = calculateDmgAfterDefense(attackerSkp, defenderSkp);

			} else {
				if (e.getEntity().getPersistentDataContainer().has(CustomMob.getCustomMobTypeKey())){

					if (Skyland.getInstance().getCustomMob((Mob) e.getEntity()) instanceof FollowingMob fm){
						attacker.sendMessage("you hit skyland entity!");
						dmg = calculateDmgAfterDefense(attackerSkp, fm);
						fm.setTargetE(attacker);
					}else {

					}

				}
			}

			e.setDamage(dmg);

		}else if (e.getDamager() instanceof Mob mob){
			int dmg = 0;

			if (e.getEntity().getPersistentDataContainer().has(CustomMob.getCustomMobTypeKey())){
				CustomMob customMob = Skyland.getInstance().getCustomMob(mob);
				dmg = customMob.getAttackDmg();

				if (e.getEntity() instanceof Player p){
					SkylandPlayer skp = SkylandPlayerModifier.getSkylandPlayer(p);

					dmg = calculateDmgAfterDefense(customMob, skp);

				}else if (e.getEntity().getPersistentDataContainer().has(CustomMob.getCustomMobTypeKey())){

					if (e.getEntity() instanceof Mob mob1){
						CustomMob cm2 = Skyland.getInstance().getCustomMob(mob1);

						dmg = calculateDmgAfterDefense(customMob, cm2);
					}


				}


			}

			e.setDamage(dmg);

		}
	}

	@EventHandler
	public void onChunkLoad(ChunkLoadEvent e){
		NamespacedKey nk = new NamespacedKey(Skyland.getInstance(), "test");
		if (e.getChunk().getPersistentDataContainer().has(nk)){
			return;
		}



		if (e.getWorld().getName().equals("world")){
			return;
		}
		for (SkylandStructure skylandStructure : Skyland.getInstance().getStructures()){

			if (skylandStructure.getStructure() != null){
				//todo make sure to see if there is space
				//for (int x = 0; x < 16; x++) {
				//	for (int z = 0; x < 16; x++) {
						//todo
				//	}
				//}
				e.getChunk().getPersistentDataContainer().set(nk, PersistentDataType.STRING, "");

				Random random = new Random();
				int x = random.nextInt(16) + e.getChunk().getX() * 16;
				int z = random.nextInt(16) + e.getChunk().getZ() * 16;
				SkylandStructureModifiers mod = skylandStructure.getModifier(SkylandBiomes.getBiome(x, z));


				if (mod != null && skylandStructure.shouldPlace(x, z)){
					int roll = random.nextInt(0, mod.getRarity());
					if (roll == 0){

						int y = Skyland.getInstance().getCustomChunkGenerator().getFinalTopY(x, z);
						skylandStructure.getStructure().place(new Location(e.getWorld(), x, y+1 , z), true, StructureRotation.NONE, Mirror.NONE, -1, 1, new Random());


					}
				}





			}


		}




	}

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent e){
		Biome biome = e.getLocation().getBlock().getBiome();

		//e.setCancelled(true);
		//todo debug statement
	}

	@EventHandler
	public void onDeath(EntityDeathEvent e){
		System.out.println("entity died");
		//todo add custom drops
		if (e.getEntity() instanceof Mob mob){
			System.out.println("mob died");
			e.getDrops().removeAll(e.getDrops());

			if (mob.getPersistentDataContainer().has(CustomMob.getCustomMobTypeKey())){

				Materials drop = Materials.getRandomMaterial(Skyland.getInstance().getCustomMob(mob).getLootTable());
				System.out.println("drop: " + drop.toString());
				e.getDrops().add(drop.getModel());

				Skyland.getInstance().removeCustomMob(mob);
			}


		}
	}
	@EventHandler
	public void onEntityDmg(EntityDamageEvent e){
		//todo add different dmg calcs
		if (!e.getCause().equals(DamageCause.ENTITY_ATTACK)){
			if (e.getEntity() instanceof Player p){

				//todo
			}
		}
	}

	@EventHandler
	public void onMobLoad(ChunkLoadEvent cle){
		for (Entity e : cle.getChunk().getEntities()){
			if (e instanceof Mob mob){
				Skyland.getInstance().getCustomMob(mob);
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {

		if (!userAPI){
			userAPI = true;
			UserAPI.getInstance().addModifier(new SkylandPlayerModifier());
		}


		e.getPlayer().sendMessage("welcome");
		e.getPlayer().setMetadata("spawnProt", new FixedMetadataValue(Skyland.getInstance(),
				false));//todo set to true just false to debug

		if (e.getPlayer().getMetadata("spawnProt").isEmpty()) {
			e.getPlayer().sendMessage("no meta");
		} else {
			e.getPlayer().sendMessage(e.getPlayer().getMetadata("spawnProt").get(0).asString());
			//e.getPlayer().sendMessage(e.getPlayer().getMetadata("spawnProt").get(1).asString());
		}

		SkylandPlayer skp = SkylandPlayerModifier.getSkylandPlayer(e.getPlayer());

		if (!skp.hastActiveClass()) {

			UINewClassSelect uiNewClassSelect = new UINewClassSelect(e.getPlayer());
			uiNewClassSelect.openInv();

			//todo create command to open inv with instructions

		}

		BukkitScheduler scheduler = Bukkit.getScheduler();

		scheduler.runTaskTimer(Skyland.getInstance(), () -> {
			String text = switch (e.getPlayer().getInventory().getHeldItemSlot()) {
				case 0 -> "Ⲓ";
				case 1 -> "Ⲕ";
				case 2 -> "⳥";
				case 3 -> "⳦";
				case 4 -> "⳧";
				case 5 -> "⳨";
				case 6 -> "⳩";
				case 7 -> "⳪";
				case 8 -> "Ⳬ";
				default -> "";
			};
			e.getPlayer()
					.sendActionBar(Component.text(text).color(TextColor.color(0x4e, 0x5c, 0x24)));
		}, 1L /*<-- the initial delay */, 0L /*<-- the interval */);//times in ticks

		//todo if player joined make him choose class
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {

	}

	@EventHandler
	public void onMessageSend(AsyncChatEvent e) {

		/*
		List<MetadataValue> mv = e.getPlayer().getMetadata("selectingClass");
		if (!mv.isEmpty()) {
			if (mv.get(0).asBoolean()) {
				int nr;
				try {
					nr = Integer.parseInt(e.message().toString());
					nr--;
					if (SkylandClassTemplate.values().length > nr) {
						e.getPlayer().sendMessage(
								"you selected: " + SkylandClassTemplate.values()[nr].toString());
						e.getPlayer().setMetadata("selectingClass",
								new FixedMetadataValue(Skyland.getInstance(), false));
					} else {
						e.getPlayer().sendMessage("invalid choice");
					}
				} catch (Exception ex) {
					e.getPlayer().sendMessage("Please input a number");
				}

			}
		}

		 */
	}

	@EventHandler
	public void onDuraDmg(PlayerItemDamageEvent e) {

		e.setCancelled(true);

		//todo change the dura of stuff
	}

	@EventHandler
	public void onScrollEvent(PlayerItemHeldEvent e) {
		//String text = "Ⳮ⳯⳰⳱";

		//e.getPlayer().showTitle(Title.title(Component.text(""), Component.text(text)color
		// (TextColor.color(0x4e, 0x5c, 0x24)), Times.times(
		//		Duration.of(0, ChronoUnit.SECONDS), Duration.of(10, ChronoUnit.SECONDS),Duration
		//		.of(0, ChronoUnit.SECONDS))));

	}

}
