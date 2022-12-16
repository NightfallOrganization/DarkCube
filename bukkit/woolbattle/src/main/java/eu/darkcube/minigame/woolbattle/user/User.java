/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.user;

import java.util.UUID;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import eu.darkcube.minigame.woolbattle.nbt.DataStorage;
import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkNumber;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.util.InventoryId;
import eu.darkcube.system.language.core.Language;
import net.minecraft.server.v1_8_R3.Packet;

public interface User {
	/**
	 * The {@link DataStorage} returned by this method gets deleted once the user is unloaded
	 * 
	 * @return the temporary data storage for this user
	 */
	DataStorage getTemporaryDataStorage();

	/**
	 * @return the UUID for this player
	 */
	UUID getUniqueId();

	/**
	 * @return the players name
	 */
	String getPlayerName();

	/**
	 * @return the team color combined with the players name
	 */
	String getTeamPlayerName();

	/**
	 * @return this player's language
	 */
	Language getLanguage();

	/**
	 * Sets the language for this player
	 * 
	 * @param language
	 */
	void setLanguage(Language language);

	/**
	 * @return the team this player is in
	 */
	Team getTeam();

	/**
	 * Sets the team this player is in
	 * 
	 * @param team
	 */
	void setTeam(Team team);

	/**
	 * @return the userData for this player
	 */
	UserData getData();

	/**
	 * Loads all perks for this player
	 */
	void loadPerks();

	/**
	 * @param number
	 * @return the perk on the given {@link PerkNumber}
	 */
	Perk getPerk(PerkNumber number);

	/**
	 * @return the first active perk of this player
	 */
	Perk getActivePerk1();

	/**
	 * @return the second active perk of this player
	 */
	Perk getActivePerk2();

	/**
	 * @return the passive perk of this player
	 */
	Perk getPassivePerk();

	/**
	 * @return the ender pearl perk of this player
	 */
	Perk getEnderPearl();

	/**
	 * @param itemId
	 * @return a perk this player has given by the itemid, null if no perk is found
	 */
	Perk getPerkByItemId(String itemId);

	/**
	 * Sets the first active perk for this player
	 * 
	 * @param perk
	 */
	void setActivePerk1(Perk perk);

	/**
	 * Sets the second active perk for this player
	 * 
	 * @param perk
	 */
	void setActivePerk2(Perk perk);

	/**
	 * Sets the passive perk for this player
	 * 
	 * @param perk
	 */
	void setPassivePerk(Perk perk);

	/**
	 * Sets the ender pearl perk for this player
	 * 
	 * @param perk
	 */
	void setEnderPearl(Perk perk);

	/**
	 * Sends this player a packet
	 * 
	 * @param packet
	 */
	void sendPacket(Packet<?> packet);

	/**
	 * @return the max amount of wool a player can carry
	 */
	int getMaxWoolSize();

	/**
	 * @return the amount of wool a player gets added to his inventory when he breaks a wool block
	 */
	int getWoolBreakAmount();

	/**
	 * @return the spawn protection ticks of the player
	 */
	int getSpawnProtectionTicks();

	/**
	 * @return if the player has spawn protection
	 */
	boolean hasSpawnProtection();

	/**
	 * Sets the spawn protection ticks for the player
	 * 
	 * @param ticks
	 */
	void setSpawnProtectionTicks(int ticks);

	/**
	 * Sets the troll mode for the player
	 * 
	 * @param trollmode
	 */
	void setTrollMode(boolean trollmode);

	/**
	 * @return if the player has the troll mode activated
	 */
	boolean isTrollMode();

	/**
	 * @return the bukkit {@link Player} object
	 */
	CraftPlayer getBukkitEntity();

	/**
	 * @return the open inventory of the player
	 */
	InventoryId getOpenInventory();

	/**
	 * Sets the open inventory for the player
	 * 
	 * @param id
	 */
	void setOpenInventory(InventoryId id);

	/**
	 * @return an ItemStack with a single wool item colored in the players team color
	 */
	ItemStack getSingleWoolItem();

	/**
	 * @return the last player that hit this player
	 */
	User getLastHit();

	/**
	 * Sets the last player that hit this player
	 * 
	 * @param user
	 */
	void setLastHit(User user);

	/**
	 * @return the amount of ticks passed since this player was last hit
	 */
	int getTicksAfterLastHit();

	/**
	 * Sets the amount of ticks passed since this player was last hit
	 * 
	 * @param ticks
	 */
	void setTicksAfterLastHit(int ticks);

	/**
	 * @return the amount of kills this player made
	 */
	int getKills();

	/**
	 * @return the number of times this player died
	 */
	int getDeaths();

	/**
	 * @return the kill/death ratio of this player
	 */
	double getKD();

	/**
	 * Sets the number of kills this player made
	 * 
	 * @param kills
	 */
	void setKills(int kills);

	/**
	 * Sets the number of times this player died
	 * 
	 * @param deaths
	 */
	void setDeaths(int deaths);

}
