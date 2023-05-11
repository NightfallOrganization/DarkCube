/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.worldGen;

import SkylandUtil.SebUtil;
import eu.darkcube.system.skyland.mobs.CustomMob;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum SkylandBiomes {


	//color green
	FOREST(Biome.FOREST, new ArrayList<>(), new AllBiomeBlocks(
			new BiomeBLock[]{new BiomeBLock(Material.GRASS_BLOCK), new BiomeBLock(Material.SLIME_BLOCK)},
			new BiomeBLock[] {new BiomeBLock(Material.DIRT), new BiomeBLock(Material.SLIME_BLOCK)},
			new BiomeBLock[] {new BiomeBLock(Material.STONE,100), new BiomeBLock(Material.ANDESITE,50),
					new BiomeBLock(Material.DIORITE,50), new BiomeBLock(Material.DIAMOND_ORE,5)},
			new TreeSet[] {new TreeSet(Material.OAK_LEAVES, Material.OAK_LOG)}), -14503604),

//color red
	LAVA(Biome.BADLANDS, new ArrayList<>(), new AllBiomeBlocks(new BiomeBLock[]{
			new BiomeBLock(Material.NETHERRACK), new BiomeBLock(Material.NETHER_WART_BLOCK)},
			new BiomeBLock[] {new BiomeBLock(Material.NETHER_BRICKS), new BiomeBLock(Material.NETHERRACK)},
			new BiomeBLock[] {new BiomeBLock(Material.NETHER_BRICKS,100), new BiomeBLock(Material.BONE_BLOCK,50),
					new BiomeBLock(Material.ANCIENT_DEBRIS,5)},
		new TreeSet[] {new TreeSet(Material.OAK_LEAVES, Material.OAK_LOG)}), -1237980),


//color black
	TEST1(Biome.BAMBOO_JUNGLE, new ArrayList<>(), new AllBiomeBlocks(
			new BiomeBLock[]{new BiomeBLock(Material.ICE), new BiomeBLock(Material.NETHER_WART_BLOCK)},
			new BiomeBLock[] {new BiomeBLock(Material.NETHER_BRICKS), new BiomeBLock(Material.NETHERRACK)},
			new BiomeBLock[] {new BiomeBLock(Material.PACKED_ICE,100),
					new BiomeBLock(Material.BONE_BLOCK,50),  new BiomeBLock(Material.ANCIENT_DEBRIS,5)},
			new TreeSet[] {new TreeSet(Material.OAK_LEAVES, Material.OAK_LOG)}), -16777216),


//color grey
	TEST2(Biome.BEACH, new ArrayList<>(), new AllBiomeBlocks(
			new BiomeBLock[]{new BiomeBLock(Material.CARVED_PUMPKIN), new BiomeBLock(Material.NETHER_WART_BLOCK)},
			new BiomeBLock[] {new BiomeBLock(Material.NETHER_BRICKS), new BiomeBLock(Material.NETHERRACK)},
			new BiomeBLock[] {new BiomeBLock(Material.COAL_BLOCK,100), new BiomeBLock(Material.BONE_BLOCK,50),
					new BiomeBLock(Material.ANCIENT_DEBRIS,5)}, new TreeSet[] {new TreeSet(Material.OAK_LEAVES, Material.OAK_LOG)}),
			new BiomeGenModifiers(70, 50, 0.5D, 1, 2, 1, 1,1 ,1),
		-8421505),




	;

	final Biome biome;
	final List<CustomMob> mobs;
	final AllBiomeBlocks biomeBlock;
	final BiomeGenModifiers biomeGenModifiers;
	int colorCode;
	static SimplexOctaveGenerator biomeGen = new SimplexOctaveGenerator(new Random(1133423454), 6);
	static SimplexOctaveGenerator biomeGen2 = new SimplexOctaveGenerator(new Random(124245), 6);
	SkylandBiomes(Biome biome, List<CustomMob> mobs, AllBiomeBlocks biomeBlock, int colorCode) {
		this.biome = biome;
		this.mobs = mobs;
		this.biomeBlock = biomeBlock;
		biomeGenModifiers = new BiomeGenModifiers();
		this.colorCode = colorCode;
	}

	SkylandBiomes(Biome biome, List<CustomMob> mobs, AllBiomeBlocks biomeBlock, BiomeGenModifiers biomeGenModifiers, int colorCode) {
		this.biome = biome;
		this.mobs = mobs;
		this.biomeBlock = biomeBlock;
		this.biomeGenModifiers = biomeGenModifiers;
		this.colorCode = colorCode;

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

	public int getColorCode() {
		return colorCode;
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

	static final int[][] colors;

	static {
		try {
			colors = SebUtil.convertTo3DWithoutUsingGetRGB(ImageIO.read(new File("biomes.png")));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	static int[][] intensity = null;

	public static SkylandBiomes getBiome(int x, int z){

		biomeGen.setScale(0.002D);
		biomeGen2.setScale(0.002D);

		int test = (int) ((biomeGen.noise(x, z, 0.2D, 0.5D, true) +1 )*colors.length/2);
		int test2 = (int) ((biomeGen2.noise(x, z, 0.2D, 0.5D, true) +1 )*colors[0].length/2);

		for (SkylandBiomes skylandBiomes : values()){
			if (skylandBiomes.getColorCode() == colors[test][test2]){
				return skylandBiomes;
			}
		}


		return SkylandBiomes.FOREST;
	}

	public static int getColor(int x, int y){
		return colors[y][x];
	}

	public static void calcIntensity(){
		intensity = new int[colors.length][colors[0].length];
		
		for (int i = 0; i < colors.length; i++) {
			for (int j = 0; j < colors[i].length; j++) {
				boolean isNotFound = true;
				
				while (isNotFound){

					for (int k = 0; k < ; k++) {
						
					}
					
					
				}
				
			}
		}
		
	}

	public static int getBiomeIntensity(int x, int y){
		if (intensity == null) {
			calcIntensity();
		}
		return intensity[x][y];
	}
}
