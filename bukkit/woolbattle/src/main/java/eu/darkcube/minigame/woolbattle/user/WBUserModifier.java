/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.user;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.mysql.MySQL;
import eu.darkcube.minigame.woolbattle.old.UserData;
import eu.darkcube.minigame.woolbattle.perk.Perk.ActivationType;
import eu.darkcube.minigame.woolbattle.perk.PerkName;
import eu.darkcube.minigame.woolbattle.perk.perks.other.*;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.data.UserModifier;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class WBUserModifier implements UserModifier {

	static final Key USER = new Key(WoolBattle.instance(), "user");
	private static final Logger logger = Logger.getLogger("WBUserModifier");
	private static final Key DATA_VERSION = new Key(WoolBattle.instance(), "dataVersion");

	@Override
	public void onLoad(User user) {
		DefaultWBUser u = new DefaultWBUser(user);
		int oldDataVersion = 0;
		int dataVersion = 0;
		if (user.getPersistentDataStorage().has(DATA_VERSION)) {
			oldDataVersion = dataVersion =
					user.getPersistentDataStorage().get(DATA_VERSION, PersistentDataTypes.INTEGER);
		}
		if (dataVersion == 0) {
			UserData data = MySQL.loadUserData(user.getUniqueId());
			if (data != null) {
				migrateFrom0To1(data, u);
			}
			dataVersion = 1;
		}
		if (dataVersion == 1) {
			migrateFrom1To2(u);
			dataVersion = 2;
		}
		if (oldDataVersion != dataVersion) {
			user.getPersistentDataStorage()
					.set(DATA_VERSION, PersistentDataTypes.INTEGER, dataVersion);
		}
		user.getMetaDataStorage().set(USER, u);
		u.perks().reloadFromStorage();
	}

	@Override
	public void onUnload(User user) {
		user.getMetaDataStorage().remove(USER);
	}

	private void migrateFrom1To2(DefaultWBUser user) {
		logger.info(
				"[WoolBattle] Migrating user " + user.user().getName() + " from version 1 to 2");
		PlayerPerks perks = user.perksStorage();
		perks.perk(ActivationType.DOUBLE_JUMP, 0, DoubleJumpPerk.DOUBLE_JUMP);
		perks.perkInvSlot(ActivationType.DOUBLE_JUMP, 0, -1);
		user.perksStorage(perks);
	}

	private void migrateFrom0To1(UserData data, DefaultWBUser user) {
		logger.info(
				"[WoolBattle] Migrating user " + user.user().getName() + " from version 0 to 1");
		PlayerPerks perksStorage = user.perksStorage();
		perksStorage.perk(ActivationType.ACTIVE, 0, data.getPerks().getActivePerk1());
		perksStorage.perkInvSlot(ActivationType.ACTIVE, 0, data.getPerks().getSlotActivePerk1());
		perksStorage.perk(ActivationType.ACTIVE, 1, data.getPerks().getActivePerk2());
		perksStorage.perkInvSlot(ActivationType.ACTIVE, 1, data.getPerks().getSlotActivePerk2());
		perksStorage.perk(ActivationType.PASSIVE, 0, data.getPerks().getPassivePerk());
		perksStorage.perkInvSlot(ActivationType.PASSIVE, 0, data.getPerks().getSlotPassivePerk());
		perksStorage.perk(ActivationType.MISC, 0, EnderPearlPerk.ENDERPEARL);
		perksStorage.perkInvSlot(ActivationType.MISC, 0, data.getPerks().getSlotPearl());
		perksStorage.perk(ActivationType.PRIMARY_WEAPON, 0, BowPerk.BOW);
		perksStorage.perkInvSlot(ActivationType.PRIMARY_WEAPON, 0, data.getPerks().getSlotBow());
		perksStorage.perk(ActivationType.SECONDARY_WEAPON, 0, ShearsPerk.SHEARS);
		perksStorage.perkInvSlot(ActivationType.SECONDARY_WEAPON, 0,
				data.getPerks().getSlotShears());
		perksStorage.perk(ActivationType.ARROW, 0, ArrowPerk.ARROW);
		perksStorage.perkInvSlot(ActivationType.ARROW, 0, data.getPerks().getSlotArrow());

		Map<PerkName, PerkName> perkMigrationMap = new HashMap<>();
		perkMigrationMap.put(new PerkName("RONJAS_TOILET_SPLASH"),
				new PerkName("RONJAS_TOILET_FLUSH"));
		perkMigrationMap.put(new PerkName("ROCKETJUMP"), new PerkName("ROCKET_JUMP"));
		perkMigrationMap.put(new PerkName("LONGJUMP"), new PerkName("LONG_JUMP"));
		for (ActivationType type : ActivationType.values()) {
			PerkName[] perks = perksStorage.perks(type);
			for (Map.Entry<PerkName, PerkName> entry : perkMigrationMap.entrySet()) {
				for (int i = 0; i < perks.length; i++) {
					if (perks[i].equals(entry.getKey())) {
						perks[i] = entry.getValue();
					}
				}
			}
		}
		user.perksStorage(perksStorage);
		user.particles(data.isParticles());
		user.woolSubtractDirection(data.getWoolSubtractDirection());
		user.heightDisplay(data.getHeightDisplay());
	}
}
