package eu.darkcube.minigame.woolbattle.user;

import java.util.UUID;

import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.inventory.ItemStack;

import eu.darkcube.minigame.woolbattle.perk.Perk;
import eu.darkcube.minigame.woolbattle.perk.PerkNumber;
import eu.darkcube.minigame.woolbattle.team.Team;
import eu.darkcube.minigame.woolbattle.util.InventoryId;
import eu.darkcube.system.language.core.Language;
import net.minecraft.server.v1_8_R3.Packet;

public interface User {

	UUID getUniqueId();

	String getPlayerName();

	String getTeamPlayerName();

	Language getLanguage();

	void setLanguage(Language language);

	Team getTeam();

	void setTeam(Team team);

	UserData getData();

	void loadPerks();
	
	Perk getPerk(PerkNumber number);
	
	Perk getActivePerk1();
	
	Perk getActivePerk2();
	
	Perk getPassivePerk();
	
	Perk getEnderPearl();

	Perk getPerkByItemId(String itemId);
	
	void setActivePerk1(Perk perk);
	
	void setActivePerk2(Perk perk);
	
	void setPassivePerk(Perk perk);
	
	void setEnderPearl(Perk perk);
	
	void sendPacket(Packet<?> packet);

	int getMaxWoolSize();

	int getWoolBreakAmount();

	int getSpawnProtectionTicks();

	boolean hasSpawnProtection();

	void setSpawnProtectionTicks(int ticks);

	void setTrollMode(boolean trollmode);

	boolean isTrollMode();

	CraftPlayer getBukkitEntity();

	InventoryId getOpenInventory();

	void setOpenInventory(InventoryId id);

	ItemStack getSingleWoolItem();
	
	User getLastHit();
	
	void setLastHit(User user);
	
	int getTicksAfterLastHit();
	
	void setTicksAfterLastHit(int ticks);
	
	int getKills();

	int getDeaths();
	
	double getKD();

	void setKills(int kills);

	void setDeaths(int deaths);

}