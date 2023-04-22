/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.SkylandClassSystem;

import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.skyland.Equipment.PlayerStats;
import eu.darkcube.system.util.data.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class

SkylandPlayerClass {

	public static final PersistentDataType<SkylandPlayerClass> TYPE = new PersistentDataType<SkylandPlayerClass>() {
		@Override
		public SkylandPlayerClass deserialize(
				de.dytanic.cloudnet.common.document.gson.JsonDocument doc, String key) {
			return null;
		}

		@Override
		public void serialize(de.dytanic.cloudnet.common.document.gson.JsonDocument doc,
				String key,
				SkylandPlayerClass data) {

		}

		@Override
		public SkylandPlayerClass clone(SkylandPlayerClass object) {
			return null;
		}
	}
	SkylandClassTemplate sClass;
	int lvl;
	List<PlayerStats> baseStats;

	public SkylandPlayerClass(SkylandClassTemplate sClass, int lvl, List<PlayerStats> baseStats) {
		this.sClass = sClass;
		this.lvl = lvl;
		this.baseStats = baseStats;
	}

	public SkylandClassTemplate getsClass() {
		return sClass;
	}


}
