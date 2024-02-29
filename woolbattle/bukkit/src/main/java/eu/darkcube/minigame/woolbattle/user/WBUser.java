/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.user;

import eu.darkcube.minigame.woolbattle.perk.user.UserPerks;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.util.TimeUnit;
import eu.darkcube.minigame.woolbattle.util.WoolSubtractDirection;
import eu.darkcube.system.bukkit.inventoryapi.v1.IInventory;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.Language;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public interface WBUser {

    static Collection<WBUser> onlineUsers() {
        return Bukkit.getOnlinePlayers().stream().map(WBUser::getUser).collect(Collectors.toList());
    }

    static WBUser getUser(Player player) {
        return getUser(UserAPI.instance().user(player.getUniqueId()));
    }

    static WBUser getUser(User user) {
        return user.metadata().get(WBUserModifier.USER);
    }

    /**
     * @return the user from the {@link eu.darkcube.system.userapi.UserAPI UserAPI}
     */
    User user();

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
    Component getTeamPlayerName();

    /**
     * @return this player's language
     */
    Language getLanguage();

    /**
     * Sets the language for this player
     *
     * @param language the {@link Language}
     */
    void setLanguage(Language language);

    /**
     * @return the team this player is in
     */
    Team getTeam();

    /**
     * Sets the team this player is in
     *
     * @param team the new {@link Team}
     */
    void setTeam(Team team);

    /**
     * @return the in-game perks. This will <b>ONLY</b> affect the in-game perks but storage will
     * not be updated and the perks will not persist over games. Use {@link #perksStorage()} and
     * {@link #perksStorage(PlayerPerks)} for that functionality
     */
    UserPerks perks();

    /**
     * @return a copy of this user's {@link PlayerPerks} for storage
     */
    PlayerPerks perksStorage();

    /**
     * Set the player's perks. This will only affect storage (which will persist over games), and
     * not the current in-game perks. Use {@link #perks()} for that functionality
     *
     * @param perks the perks to store
     */
    void perksStorage(PlayerPerks perks);

    boolean particles();

    void particles(boolean particles);

    HeightDisplay heightDisplay();

    void heightDisplay(HeightDisplay heightDisplay);

    WoolSubtractDirection woolSubtractDirection();

    void woolSubtractDirection(WoolSubtractDirection woolSubtractDirection);

    /**
     * @return the amount of wool the player has at his disposal
     */
    int woolCount();

    /**
     * Gives the player wool
     *
     * @param count how much wool to add
     * @return the actual amount of wool added, may be less than requested if the player's inventory
     * is full
     */
    int addWool(int count);

    /**
     * Gives the player wool
     *
     * @param count      how much wool to add
     * @param dropIfFull whether we should drop the wool in the world if the player's inventory is
     *                   full
     * @return the amount of wool processed. This is all the wool that is put into the world,
     * inventory AND world, if dropping is requested
     */
    int addWool(int count, boolean dropIfFull);

    /**
     * Takes wool from the player
     *
     * @param count how much wool to remove
     * @return the amount of wool removed. This may be less if the player has no more wool or if
     * somehow the removal was blocked.
     */
    int removeWool(int count);

    /**
     * Takes wool from the player
     *
     * @param count           how much wool to remove
     * @param updateInventory whether the inventory should be affected by this command. This is used
     *                        for dropping items where we want to update the wool count silently,
     *                        cuz the inventory is already up-to-date
     * @return the amount of wool removed. This may be less if the player has no more wool or if
     * somehow the removal was blocked.
     */
    int removeWool(int count, boolean updateInventory);

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
     * Sets the spawn protection ticks for the player
     *
     * @param ticks the number of spawn protection ticks
     */
    void setSpawnProtectionTicks(int ticks);

    /**
     * @return if the player has spawn protection
     */
    boolean hasSpawnProtection();

    /**
     * @return if the player has the troll mode activated
     */
    boolean isTrollMode();

    /**
     * Sets the troll mode for the player
     *
     * @param trollmode whether troll is active
     */
    void setTrollMode(boolean trollmode);

    /**
     * @return the bukkit {@link Player} object
     */
    @Nullable CraftPlayer getBukkitEntity();

    /**
     * @return the open inventory of the player
     */
    IInventory getOpenInventory();

    /**
     * Sets the open inventory for the player
     *
     * @param id the {@link IInventory}
     */
    void setOpenInventory(IInventory id);

    /**
     * @return an ItemStack with a single wool item colored in the players team color
     */
    ItemStack getSingleWoolItem();

    /**
     * @return the last player that hit this player
     */
    WBUser getLastHit();

    /**
     * Sets the last player that hit this player
     *
     * @param user the user that last hit this user
     */
    void setLastHit(WBUser user);

    /**
     * @return the amount of ticks passed since this player was last hit
     */
    int getTicksAfterLastHit();

    /**
     * Sets the amount of ticks passed since this player was last hit
     *
     * @param ticks the number of ticks
     */
    void setTicksAfterLastHit(int ticks);

    default void resetTicksAfterLastHit() {
        setTicksAfterLastHit(TimeUnit.SECOND.itoTicks(60));
    }

    /**
     * @return the amount of ticks this user is immune against projectiles
     */
    int projectileImmunityTicks();

    /**
     * @param ticks the amount of ticks this user is immune against projectiles
     */
    void projectileImmunityTicks(int ticks);

    /**
     * @return the amount of kills this player made
     */
    int getKills();

    /**
     * Sets the number of kills this player made
     *
     * @param kills the number of kills
     */
    void setKills(int kills);

    /**
     * @return the number of times this player died
     */
    int getDeaths();

    /**
     * Sets the number of times this player died
     *
     * @param deaths the number of deaths
     */
    void setDeaths(int deaths);

    /**
     * @return the kill/death ratio of this player
     */
    double getKD();
}
