/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.worldGen.Structures;

import eu.darkcube.system.skyland.Skyland;
import eu.darkcube.system.skyland.worldGen.SkylandBiomes;
import org.bukkit.NamespacedKey;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;

import java.util.List;
import java.util.Map;

public class SkylandStructure {

	NamespacedKey namespacedKey;

	SkylandStructureModifiers[] modifier;

	public SkylandStructure(NamespacedKey namespacedKey, SkylandStructureModifiers[] modifier) {
		this.namespacedKey = namespacedKey;
		this.modifier = modifier;
		Skyland.getInstance().getStructures().add(this);
		Skyland.getInstance().saveStructure(this);
	}

	public SkylandStructureModifiers getModifier(SkylandBiomes biome) {
		for (int i = 0; i < modifier.length; i++) {
			if (biome == modifier[i].getBiome()) {
				return modifier[i];
			}
		}
		return null;
	}

	public Structure getStructure(){
		StructureManager structureManager = Skyland.getInstance().getServer().getStructureManager();
		return structureManager.getStructure(namespacedKey);
	}

	public boolean shouldPlace(int x, int z){
		SkylandBiomes skylandBiomes = SkylandBiomes.getBiome(x, z);

		SkylandStructureModifiers modifiers = getModifier(skylandBiomes);
		if (modifiers == null){
			return false;
		}else {
			if (SkylandBiomes.getBiomeIntensity(x, z ) >= modifiers.getIntensity()){
				System.out.println("intent good");
				if (Skyland.getInstance().getCustomChunkGenerator().isIsland(x, z) || !modifiers.isSpawnOnlyOnIsland()){
					System.out.println("should always follow");
					if (Skyland.getInstance().getCustomChunkGenerator().getIslandIntensity(x, z) <= modifiers.islandThiccness){
						System.out.println("now true");
						return true;
					}

				}

			}
		}
		return false;
	}

	public NamespacedKey getNamespacedKey() {
		return namespacedKey;
	}

}
