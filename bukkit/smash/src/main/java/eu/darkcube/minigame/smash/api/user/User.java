package eu.darkcube.minigame.smash.api.user;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import eu.darkcube.minigame.smash.character.Character;
import eu.darkcube.minigame.smash.user.Language;

public interface User {

	boolean isShieldActive();

	void setShieldActive(boolean shieldActive);

	int getShieldTimeUsed();

	void setShieldTimeUsed(int shieldTimeUsed);

	int getJumpUses();

	void setJumpUses(int jumpUses);

	UUID getUniqueId();

	Language getLanguage();

	void setLanguage(Language language);

	int getKills();

	int getDeaths();

	Player getPlayer();

	OfflinePlayer getOfflinePlayer();

	boolean hasAddon();

	boolean isOnline();

	int getPercent();

	Character getCharacter();

	void setItems();

	void damage(int damage);

	void stun(int ticks);

	int getStunTicks();

	void setSpawnProtectionTicks(int ticks);

	int getSpawnProtectionTicks();

}
