/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.worldgen.structures;

import eu.darkcube.system.skyland.Skyland;
import eu.darkcube.system.skyland.worldgen.SkylandBiome;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.structure.Structure;
import org.bukkit.structure.StructureManager;
import org.bukkit.util.BlockVector;

import java.util.Random;

public class SkylandStructure {

	NamespacedKey namespacedKey;

	SkylandStructureModifiers[] modifier;

	int xOffset;
	int zOffset;
	int yOffset;

	public SkylandStructure(NamespacedKey namespacedKey, SkylandStructureModifiers[] modifier) {
		this.namespacedKey = namespacedKey;
		this.modifier = modifier;
		Skyland.getInstance().getStructures().add(this);
		Skyland.getInstance().saveStructure(this);
	}

	public SkylandStructureModifiers getModifier(SkylandBiome biome) {
		for (int i = 0; i < modifier.length; i++) {
			if (biome == modifier[i].getBiome()) {
				return modifier[i];
			}
		}
		return null;
	}

	public Structure getStructure() {
		StructureManager structureManager =
				Skyland.getInstance().getServer().getStructureManager();
		if (structureManager.getStructure(namespacedKey) == null) {
			structureManager.loadStructure(namespacedKey);
		}

		if (structureManager.getStructure(namespacedKey) == null){
			System.out.println();
		}

		return structureManager.getStructure(namespacedKey);
	}

	public boolean shouldPlace(int x, int z) {
		SkylandBiome skylandBiome = SkylandBiome.getBiome(x, z);

		SkylandStructureModifiers modifiers = getModifier(skylandBiome);
		if (modifiers == null) {
			return false;
		} else {
			if (SkylandBiome.getBiomeIntensity(x, z) >= modifiers.getIntensity()) {
				System.out.println("intent good");
				if (Skyland.getInstance().getCustomChunkGenerator().isIsland(x, z)
						|| !modifiers.isSpawnOnlyOnIsland()) {
					System.out.println("should always follow");
					if (Skyland.getInstance().getCustomChunkGenerator().getIslandIntensity(x, z)
							<= modifiers.islandThiccness) {
						System.out.println("now true");
						return true;
					}

				}

			}
		}
		return false;
	}

	public void unloadStructure(NamespacedKey nsk){
		Skyland.getInstance().getServer().getStructureManager().unregisterStructure(namespacedKey);
	}
	public void enqueuStructures(Location startLoc) {
		startLoc.add(xOffset, yOffset, zOffset);

		StructureQueue.getInstance().getStructures().add(new StrucWrapper(this, startLoc));

		Structure structure = getStructure();
		if (structure == null){
			System.out.println("struc null. nsk: " + namespacedKey.getKey());
			return;
		}
		BlockVector size = structure.getSize();

		unloadStructure(namespacedKey);


		String[] nskOrigi = namespacedKey.getKey().split("_");
		String nskNewBase = nskOrigi[0];
		if (nskOrigi.length < 4) {
			return;
		}
		for (int i = 1; i < nskOrigi.length - 3; i++) {
			nskNewBase += "_" + nskOrigi[i];
		}
		NamespacedKey nskNew1 = new NamespacedKey(Skyland.getInstance(),
				nskNewBase + "_" + (Integer.parseInt(nskOrigi[nskOrigi.length - 3]) + 1) + "_"
						+ nskOrigi[nskOrigi.length - 2] + "_" + nskOrigi[nskOrigi.length - 1]);
		NamespacedKey nskNew2 = new NamespacedKey(Skyland.getInstance(),
				nskNewBase + "_" + nskOrigi[nskOrigi.length - 3] + "_" + (
						Integer.parseInt(nskOrigi[nskOrigi.length - 2]) + 1) + "_" + nskOrigi[
						nskOrigi.length - 1]);
		NamespacedKey nskNew3 = new NamespacedKey(Skyland.getInstance(),
				nskNewBase + "_" + nskOrigi[nskOrigi.length - 3] + "_" + nskOrigi[nskOrigi.length
						- 2] + "_" + (Integer.parseInt(nskOrigi[nskOrigi.length - 1]) + 1));

		System.out.println("nsk new Base: " + nskNewBase);
		System.out.println("1:" + nskNew1.getKey());
		System.out.println("2:" + nskNew2.getKey());
		System.out.println("3:" + nskNew3.getKey());

		SkylandStructure nStruc1 = Skyland.getInstance().getStructure(nskNew1);
		if (nStruc1 != null) {

			System.out.println("X struc found for: " + namespacedKey.getKey());
			nStruc1.enqueuStructures(
					new Location(startLoc.getWorld(), startLoc.x(), startLoc.y(), startLoc.z()).add(
							size.getX(), 0, 0));



		}

		SkylandStructure nStruc2 = Skyland.getInstance().getStructure(nskNew2);
		if (nStruc2 != null) {
			System.out.println("Y struc found for: " + namespacedKey.getKey());
			nStruc2.enqueuStructures(
					new Location(startLoc.getWorld(), startLoc.x(), startLoc.y(), startLoc.z()).add(
							0,size.getX(), 0));

		}

		SkylandStructure nStruc3 = Skyland.getInstance().getStructure(nskNew3);
		if (nStruc3 != null) {
			System.out.println("Z struc found for: " + namespacedKey.getKey());
			nStruc3.enqueuStructures(
					new Location(startLoc.getWorld(), startLoc.x(), startLoc.y(), startLoc.z()).add(
							0, 0, size.getX()));
		}

		//todo test
	}



	public void place(Location startLoc) {
		startLoc.add(xOffset, yOffset, zOffset);

		getStructure().place(startLoc, true, StructureRotation.NONE, Mirror.NONE, -1, 1,
				new Random());
		unloadStructure(namespacedKey);
		/*
		startLoc.add(xOffset, yOffset, zOffset);

		Structure structure = getStructure();

		new BukkitRunnable() {

			@Override
			public void run() {
				structure.place(startLoc, true, StructureRotation.NONE, Mirror.NONE, -1, 1,
						new Random());

				String[] nskOrigi = namespacedKey.getKey().split("_");
				String nskNewBase = nskOrigi[0];
				if (nskOrigi.length < 4) {
					return;
				}
				for (int i = 1; i < nskOrigi.length - 3; i++) {
					nskNewBase += "_" + nskOrigi[i];
				}
				NamespacedKey nskNew1 = new NamespacedKey(Skyland.getInstance(),
						nskNewBase + "_" + (Integer.parseInt(nskOrigi[nskOrigi.length - 3]) + 1)
								+ "_" + nskOrigi[nskOrigi.length - 2] + "_" + nskOrigi[
								nskOrigi.length - 1]);
				NamespacedKey nskNew2 = new NamespacedKey(Skyland.getInstance(),
						nskNewBase + "_" + nskOrigi[nskOrigi.length - 3] + "_" + (
								Integer.parseInt(nskOrigi[nskOrigi.length - 2]) + 1) + "_"
								+ nskOrigi[nskOrigi.length - 1]);
				NamespacedKey nskNew3 = new NamespacedKey(Skyland.getInstance(),
						nskNewBase + "_" + nskOrigi[nskOrigi.length - 3] + "_" + nskOrigi[
								nskOrigi.length - 2] + "_" + (
								Integer.parseInt(nskOrigi[nskOrigi.length - 1]) + 1));

				System.out.println("nsk new Base: " + nskNewBase);
				System.out.println("1:" + nskNew1.getKey());
				System.out.println("2:" + nskNew2.getKey());
				System.out.println("3:" + nskNew3.getKey());

				SkylandStructure nStruc1 = Skyland.getInstance().getStructure(nskNew1);
				if (nStruc1 != null) {

					System.out.println("X struc found for: " + namespacedKey.getKey());
					nStruc1.place(new Location(startLoc.getWorld(), startLoc.x(), startLoc.y(),
							startLoc.z()).add(structure.getSize().getX(), 0, 0));

				}

				SkylandStructure nStruc2 = Skyland.getInstance().getStructure(nskNew2);
				if (nStruc2 != null) {
					System.out.println("Y struc found for: " + namespacedKey.getKey());
					nStruc2.place(new Location(startLoc.getWorld(), startLoc.x(), startLoc.y(),
							startLoc.z()).add(0, structure.getSize().getX(), 0));

				}

				SkylandStructure nStruc3 = Skyland.getInstance().getStructure(nskNew3);
				if (nStruc3 != null) {
					System.out.println("Z struc found for: " + namespacedKey.getKey());
					nStruc3.place(new Location(startLoc.getWorld(), startLoc.x(), startLoc.y(),
							startLoc.z()).add(0, 0, structure.getSize().getX()));
				}
				StructureManager structureManager =
						Skyland.getInstance().getServer().getStructureManager();
				structureManager.unregisterStructure(namespacedKey);
				//todo test
			}

		}.runTaskLater(Skyland.getInstance(), 3);

		 */
	}

	public NamespacedKey getNamespacedKey() {
		return namespacedKey;
	}

}
