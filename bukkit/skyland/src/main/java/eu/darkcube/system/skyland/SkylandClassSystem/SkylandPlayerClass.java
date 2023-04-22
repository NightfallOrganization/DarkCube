/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.SkylandClassSystem;

import eu.darkcube.system.skyland.Equipment.PlayerStats;

import java.util.ArrayList;
import java.util.List;

public class

SkylandPlayerClass {

	SkylandClassTemplate sClass;
	int lvl;
	List<PlayerStats> baseStats;

	public SkylandPlayerClass(SkylandClassTemplate sClass, int lvl, List<PlayerStats> baseStats) {
		this.sClass = sClass;
		this.lvl = lvl;
		this.baseStats = baseStats;
	}

	public static SkylandPlayerClass parseString(String s, SkylandPlayer sp) {

		SkylandClassTemplate template;
		ArrayList<PlayerStats> pStat = new ArrayList<>();
		int lvl;

		String[] temp = s.split("´´´´");
		template = SkylandClassTemplate.valueOf(temp[0]);
		for (String str : temp[1].split("´´")) {
			pStat.add(PlayerStats.parseString(str));
		}
		lvl = Integer.parseInt(temp[2]);

		return new SkylandPlayerClass(template, lvl, pStat);
	}

	@Override
	public String toString() {

		String out = sClass.toString() + "´´´´";

		for (PlayerStats ps : baseStats) {

			out = out + "´´";

		}
		out = out.substring(0, out.length() - 3);
		out = out + "´´´´";
		out = out + lvl;

		return out;
	}

	public SkylandClassTemplate getsClass() {
		return sClass;
	}
}
