/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.worldGen;

import eu.darkcube.system.skyland.mobs.CustomMob;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum SkylandBiomes {

	FOREST(Biome.FOREST, new ArrayList<>(), new AllBiomeBlocks(new BiomeBLock[]{new BiomeBLock(Material.GRASS_BLOCK), new BiomeBLock(Material.SLIME_BLOCK)}, new BiomeBLock[] {new BiomeBLock(Material.DIRT), new BiomeBLock(Material.SLIME_BLOCK)}, new BiomeBLock[] {new BiomeBLock(Material.STONE,100), new BiomeBLock(Material.ANDESITE,50), new BiomeBLock(Material.DIORITE,50), new BiomeBLock(Material.DIAMOND_ORE,5)}, new TreeSet[] {new TreeSet(Material.OAK_LEAVES, Material.OAK_LOG)})),
	LAVA(Biome.BADLANDS, new ArrayList<>(), new AllBiomeBlocks(new BiomeBLock[]{new BiomeBLock(Material.NETHERRACK), new BiomeBLock(Material.NETHER_WART_BLOCK)}, new BiomeBLock[] {new BiomeBLock(Material.NETHER_BRICKS), new BiomeBLock(Material.NETHERRACK)}, new BiomeBLock[] {new BiomeBLock(Material.NETHER_BRICKS,100), new BiomeBLock(Material.BONE_BLOCK,50),  new BiomeBLock(Material.ANCIENT_DEBRIS,5)}, new TreeSet[] {new TreeSet(Material.OAK_LEAVES, Material.OAK_LOG)})),
	TEST1(Biome.BAMBOO_JUNGLE, new ArrayList<>(), new AllBiomeBlocks(new BiomeBLock[]{new BiomeBLock(Material.ICE), new BiomeBLock(Material.NETHER_WART_BLOCK)}, new BiomeBLock[] {new BiomeBLock(Material.NETHER_BRICKS), new BiomeBLock(Material.NETHERRACK)}, new BiomeBLock[] {new BiomeBLock(Material.PACKED_ICE,100), new BiomeBLock(Material.BONE_BLOCK,50),  new BiomeBLock(Material.ANCIENT_DEBRIS,5)}, new TreeSet[] {new TreeSet(Material.OAK_LEAVES, Material.OAK_LOG)})),
	TEST2(Biome.BEACH, new ArrayList<>(), new AllBiomeBlocks(new BiomeBLock[]{new BiomeBLock(Material.CARVED_PUMPKIN), new BiomeBLock(Material.NETHER_WART_BLOCK)}, new BiomeBLock[] {new BiomeBLock(Material.NETHER_BRICKS), new BiomeBLock(Material.NETHERRACK)}, new BiomeBLock[] {new BiomeBLock(Material.COAL_BLOCK,100), new BiomeBLock(Material.BONE_BLOCK,50),  new BiomeBLock(Material.ANCIENT_DEBRIS,5)}, new TreeSet[] {new TreeSet(Material.OAK_LEAVES, Material.OAK_LOG)}),
			new BiomeGenModifiers(30, 30, 3, 1, 2, 1, 1,1 ,1)),




	;

	final Biome biome;
	final List<CustomMob> mobs;
	final AllBiomeBlocks biomeBlock;
	final BiomeGenModifiers biomeGenModifiers;
	static SimplexOctaveGenerator biomeGen = new SimplexOctaveGenerator(new Random(1133423454), 6);
	static SimplexOctaveGenerator biomeGen2 = new SimplexOctaveGenerator(new Random(124245), 6);
	SkylandBiomes(Biome biome, List<CustomMob> mobs, AllBiomeBlocks biomeBlock) {
		this.biome = biome;
		this.mobs = mobs;
		this.biomeBlock = biomeBlock;
		biomeGenModifiers = new BiomeGenModifiers();
	}

	SkylandBiomes(Biome biome, List<CustomMob> mobs, AllBiomeBlocks biomeBlock, BiomeGenModifiers biomeGenModifiers) {
		this.biome = biome;
		this.mobs = mobs;
		this.biomeBlock = biomeBlock;
		this.biomeGenModifiers = biomeGenModifiers;

	}

	public Biome getBiome() {
		return biome;
	}
	public AllBiomeBlocks getBiomeBlock() {
		return biomeBlock;
	}
	public List<CustomMob> getMobs() {
		return mobs;
	}


/*	public static SkylandBiomes getBiome(Biome biome){
		for (SkylandBiomes skb : values()) {
			if (skb.getBiome().equals(biome)){
				return skb;
			}
		}
		return SkylandBiomes.LAVA;
	}

 */

	public BiomeGenModifiers getBiomeGenModifiers() {
		return biomeGenModifiers;
	}

	public static SkylandBiomes getBiome(int x, int z){

		biomeGen.setScale(0.002D);
		biomeGen2.setScale(0.002D);

		int test = (int) ((biomeGen.noise(x, z, 0.2D, 0.5D, true) +1 )*1);
		int test2 = (int) ((biomeGen2.noise(x, z, 0.2D, 0.5D, true) +1 )*1);


		switch (test){
			case 0: switch (test2){
				case 0: return SkylandBiomes.FOREST;
				case 1: return SkylandBiomes.LAVA;
			}
			case 1: switch (test2){
				case 0: return SkylandBiomes.TEST1;
				case 1: return SkylandBiomes.TEST2;
			}
			default: return SkylandBiomes.FOREST;
		}
	}
}
