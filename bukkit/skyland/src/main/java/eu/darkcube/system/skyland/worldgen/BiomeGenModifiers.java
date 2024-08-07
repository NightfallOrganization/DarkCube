/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.system.skyland.worldgen;

public class BiomeGenModifiers {

	final int minTerrainHeight = 50;
	final int maxTerrainHeight = 50;
	final double terrainFrequency = 0.5;
	final double terrainAmplitude = 0.2;

	int minDetailsHeight = 50;
	int maxDetailsHeight = 50;
	double detailsFrequency = 1;
	double detailsAmplitude = 0.2;
	int detailsImpact = 4;


	int minSpikeHeight = 50;
	int maxSpikeHeight = 50;
	double spikeFrequency = 0.5;
	double spikeAmplitude = 0.2;

	public BiomeGenModifiers(int minDetailsHeight, int maxDetailsHeight,
			double detailsFrequency, double detailsAmplitude, int detailsImpact,
			int minSpikeHeight,
			int maxSpikeHeight, double spikeFrequency, double spikeAmplitude) {

		this.minDetailsHeight = minDetailsHeight;
		this.maxDetailsHeight = maxDetailsHeight;
		this.detailsFrequency = detailsFrequency;
		this.detailsAmplitude = detailsAmplitude;
		this.detailsImpact = detailsImpact;
		this.minSpikeHeight = minSpikeHeight;
		this.maxSpikeHeight = maxSpikeHeight;
		this.spikeFrequency = spikeFrequency;
		this.spikeAmplitude = spikeAmplitude;
	}

	public BiomeGenModifiers(){

	}



	public int getMinTerrainHeight() {
		return minTerrainHeight;
	}

	public int getMaxTerrainHeight() {
		return maxTerrainHeight;
	}

	public double getTerrainFrequency() {
		return terrainFrequency;
	}

	public double getTerrainAmplitude() {
		return terrainAmplitude;
	}

	public int getMinDetailsHeight() {
		return minDetailsHeight;
	}

	public int getMaxDetailsHeight() {
		return maxDetailsHeight;
	}

	public double getDetailsFrequency() {
		return detailsFrequency;
	}

	public double getDetailsAmplitude() {
		return detailsAmplitude;
	}

	public int getDetailsImpact() {
		return detailsImpact;
	}

	public int getMinSpikeHeight() {
		return minSpikeHeight;
	}

	public int getMaxSpikeHeight() {
		return maxSpikeHeight;
	}

	public double getSpikeFrequency() {
		return spikeFrequency;
	}

	public double getSpikeAmplitude() {
		return spikeAmplitude;
	}
}
