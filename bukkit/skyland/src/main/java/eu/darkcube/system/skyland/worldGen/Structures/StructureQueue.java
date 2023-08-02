/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.worldGen.Structures;

import eu.darkcube.system.skyland.Skyland;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class StructureQueue {

	private static StructureQueue instance;

	private StructureQueue(){
		new BukkitRunnable(){
			@Override
			public void run() {
				if (!structures.isEmpty()){
					System.out.println("started building");
					structures.get(0).getSkylandStructure().place(structures.get(0).getLoc());
					structures.remove(structures.get(0));
					System.out.println("building finished. structures remaining: " +structures.size());
				}
			}
		}.runTaskTimer(Skyland.getInstance(), 5, 5);
	}
	List<StrucWrapper> structures = new ArrayList<>();

	public static StructureQueue getInstance() {
		if (instance == null){
			instance = new StructureQueue();
		}
		return instance;
	}

	public List<StrucWrapper> getStructures() {
		return structures;
	}
}
