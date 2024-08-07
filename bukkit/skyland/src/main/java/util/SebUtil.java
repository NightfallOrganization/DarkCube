/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class SebUtil {

	public static Random random = new Random();
	public static final String IMAGE_URL = "/resources/biomes.png";


	protected static int[][] pixels;
	protected static int width, height;
	protected static Set<Integer> uniqueColors;
	protected static Map<Integer, Integer> colorToID;
	// protected static Map<Integer, LinkedList<int[]>> IDtoPixels; // pls remove
	protected static int[][] pixelToID;


	public static final boolean DUMP_NEIGHBOURS = false;
	public static final boolean VIEW_BIOME = false;
	public static final boolean VIEW_UNIQUE_COLORS = false;
	public static final boolean REFACTOR_MAP = false;
	public static final boolean COLOR_BASED_TERRAIN_LOADING = false;



	public static final Scanner scanner = new Scanner(System.in);


	/*
	 * static { int count = 2; for (int c = 0; c < count; c++) { new Thread(() -> {
	 * while (true) { if (!colorQueue1.isEmpty()) {
	 *
	 * int original = colorQueue1.poll(); int dst = colorQueue2.poll();
	 *
	 * for (int x = 0; x < width; x++) { for (int y = 0; y < height; y++) { if
	 * (pixels[y][x] == original) { GamePanel.myPicture.setRGB(x, y, dst); } } } }
	 * else { try { Thread.sleep(200); } catch (InterruptedException e) {
	 * e.printStackTrace(); } } } }).start(); } }
	 */


	public static int[][] convertTo3DWithoutUsingGetRGB(BufferedImage image) {

		final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		width = image.getWidth();
		height = image.getHeight();
		final boolean hasAlphaChannel = image.getAlphaRaster() != null;

		int[][] result = new int[height][width];
		if (hasAlphaChannel) {
			final int pixelLength = 4;
			for (int pixel = 0, row = 0, col = 0; pixel + 3 < pixels.length; pixel += pixelLength) {
				int argb = 0;
				argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
				argb += ((int) pixels[pixel + 1] & 0xff); // blue
				argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
				argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
				result[row][col] = argb;
				col++;
				if (col == width) {
					col = 0;
					row++;
				}
			}
		} else {
			final int pixelLength = 3;
			for (int pixel = 0, row = 0, col = 0; pixel + 2 < pixels.length; pixel += pixelLength) {
				int argb = 0;
				argb += -16777216; // 255 alpha
				argb += ((int) pixels[pixel] & 0xff); // blue
				argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
				argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
				result[row][col] = argb;
				col++;
				if (col == width) {
					col = 0;
					row++;
				}
			}
		}

		return result;
	}

	public static Set<Integer> getUniqueColors(int[][] pixels) {
		Set<Integer> out = new TreeSet<Integer>();
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[i].length; j++) {
				out.add(pixels[i][j]);
			}
		}
		return out;
	}




	public static int generateColorCode(int alpha, int r, int g, int b) {
		int argb = 0;
		argb += (((int) alpha & 0xff) << 24); // alpha
		argb += ((int) b & 0xff); // blue
		argb += (((int) g & 0xff) << 8); // green
		argb += (((int) r & 0xff) << 16); // red
		return argb;
	}


	public static void loadBiomeByColor() {

	}

}

