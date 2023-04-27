/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.Equipment;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Components {

	Materials materialType;
	ComponentTypes compType;

	public Components(Materials type, ComponentTypes compType) {
		this.materialType = type;
		this.compType = compType;
	}

	public static Components parseFromString(String s) {
		String[] temp = s.split("\\\\");
		return new Components(Materials.valueOf(temp[1]), ComponentTypes.valueOf(temp[3]));
	}

	public PlayerStats[] getPStats() {
		return PlayerStats.mergePstats(materialType.getStats(), compType.getStats());

		/*
		HashMap<PlayerStatsType, Integer> out = new HashMap<>();
		for (PlayerStats p : materialType.getStats()) {
			if (out.containsKey(p.getType())) {
				out.put(p.getType(), out.get(p.getType()) + p.getMenge());
			} else {
				out.put(p.getType(), p.getMenge());
			}
		}

		for (PlayerStats p : compType.getStats()) {
			if (out.containsKey(p.getType())) {
				out.put(p.getType(), out.get(p.getType()) + p.getMenge());
			} else {
				out.put(p.getType(), p.getMenge());
			}
		}
		PlayerStats[] fout = new PlayerStats[out.keySet().size()];
		AtomicInteger i = new AtomicInteger();
		out.forEach((playerStatsType, integer) -> {
			fout[i.get()] = new PlayerStats(playerStatsType, integer);
			i.getAndIncrement();
		});
		return fout;

		 */


	}

	@Override
	public String toString() {
		return "Components{" + "type=\\\\" + materialType + "\\\\ compType=\\\\" + compType + "\\\\" + '}';
	}

	public int lvl(){
		return materialType.getLvlReq();
	}

	public ComponentTypes getCompType() {
		return compType;
	}

	public Materials getMaterialType() {
		return materialType;
	}
}
