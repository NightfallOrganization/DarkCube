/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.equipment;

public class Component {

	Material materialType;
	ComponentType compType;

	public Component(Material type, ComponentType compType) {
		this.materialType = type;
		this.compType = compType;
	}

	public static Component parseFromString(String s) {
		String[] temp = s.split("\\\\");
		return new Component(Material.valueOf(temp[1]), ComponentType.valueOf(temp[3]));
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

	public ComponentType getCompType() {
		return compType;
	}

	public Material getMaterialType() {
		return materialType;
	}
}
