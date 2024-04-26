/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.worldGen;

import org.bukkit.Material;

import java.util.Random;

public class AllBiomeBlocks {

	BiomeBLock[] surfaceBlocks;
	int totalSurfaceWeight = 0;
	BiomeBLock[] belowSurfaceBlocks;
	int totalBelowSurfaceWeight= 0;
	BiomeBLock[] stoneBlocks;
	int totalStoneWeight= 0;
	TreeSet[]  treeSets;
	int totalTreeWeight= 0;

	public AllBiomeBlocks(BiomeBLock[] surfaceBlocks, BiomeBLock[] belowSurfaceBlocks,
			BiomeBLock[] stoneBlocks, TreeSet[] treeSets) {
		this.surfaceBlocks = surfaceBlocks;
		for (BiomeBLock surfaceBlock : surfaceBlocks) {
			totalSurfaceWeight+= surfaceBlock.getWeight();
		}
		this.belowSurfaceBlocks = belowSurfaceBlocks;
		for (BiomeBLock surfaceBlock : belowSurfaceBlocks) {
			totalBelowSurfaceWeight+= surfaceBlock.getWeight();
		}
		this.stoneBlocks = stoneBlocks;
		for (BiomeBLock surfaceBlock : stoneBlocks) {
			totalStoneWeight+= surfaceBlock.getWeight();
		}
		this.treeSets = treeSets;
		for (TreeSet surfaceBlock : treeSets) {
			totalTreeWeight += surfaceBlock.getWeight();
		}
	}

	public BiomeBLock getNextBelowSurfaceBlock(Random random) {

		return getBiomeBLock(random, totalBelowSurfaceWeight, belowSurfaceBlocks);
	}

	public BiomeBLock getNextSurfaceBlock(Random random) {
		return getBiomeBLock(random, totalSurfaceWeight, surfaceBlocks);
	}
	public BiomeBLock getNextStone(Random random) {
		return getBiomeBLock(random, totalStoneWeight, stoneBlocks);
	}

	private BiomeBLock getBiomeBLock(Random random, int totalStoneWeight,
			BiomeBLock[] stoneBlocks) {
		int roll = random.nextInt(0, totalStoneWeight);

		for (int i = 0; i < stoneBlocks.length; i++) {
			if (roll <= stoneBlocks[i].getWeight()){
				return stoneBlocks[i];
			}else {
				roll = roll- stoneBlocks[i].getWeight();
			}
		}
		return stoneBlocks[0];
	}

	public TreeSet getNextTreeSet(Random random) {
		int roll = random.nextInt(0, totalTreeWeight);

		for (int i = 0; i < treeSets.length; i++) {
			if (roll <=treeSets[i].getWeight()){
				return treeSets[i];
			}else {
				roll = roll-treeSets[i].getWeight();
			}
		}
		return treeSets[0];
	}
}
