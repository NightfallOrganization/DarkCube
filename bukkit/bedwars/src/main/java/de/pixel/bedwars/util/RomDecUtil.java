/*
 * Copyright (c) 2022. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package de.pixel.bedwars.util;

public class RomDecUtil {

	/**
	 * !!! NUMBERS ASCENDING IMPORTANT !!!
	 */
	private static final int[] DEC = new int[] {
			1, 5, 10, 50, 100, 500, 1000
	};
	private static final char[] ROM = new char[] {
			'I', 'V', 'X', 'L', 'C', 'D', 'M'
	};
	private static final String[] ROM_REAL;
	private static final int[] DEC_REAL;
	private static final int max;

	static {
		int maxi = DEC[DEC.length - 1];
		if (DEC.length % 2 == 0) {
			maxi = maxi / 5 * 8;
		} else {
			maxi = maxi * 4;
		}
		max = maxi;
		ROM_REAL = new String[ROM.length * 2 - 1];
		DEC_REAL = new int[ROM.length * 2 - 1];

		try {
			char lastRom10 = 0;
			int lastDec10 = 0;
			for (int index = 0, realIndex = 0; index < ROM.length; index++, realIndex += 2) {
				if (index != 0) {
					ROM_REAL[ROM_REAL.length - realIndex] = Character.toString(lastRom10) + ROM[index];
					DEC_REAL[DEC_REAL.length - realIndex] = DEC[index] - lastDec10;
				}
				ROM_REAL[ROM_REAL.length - realIndex - 1] = Character.toString(ROM[index]);
				DEC_REAL[DEC_REAL.length - realIndex - 1] = DEC[index];
				if (Integer.toString(DEC[index]).charAt(0) == '1') {
					lastRom10 = ROM[index];
					lastDec10 = DEC[index];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.out.println(dec2rom(99));
	}

	public static final String dec2rom(int dec) {
		if (!(dec > 0 && dec < max)) {
			return "number." + Integer.toString(dec);
		}

		StringBuilder out = new StringBuilder();
		for (int i = 0; i < ROM_REAL.length; i++) {
			while (dec >= DEC_REAL[i]) {
				dec = dec - DEC_REAL[i];
				out.append(ROM_REAL[i]);
			}
		}

		return out.toString();
	}

}
