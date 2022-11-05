package eu.darkcube.system.commandapi.v3;

import java.util.Random;
import java.util.UUID;
import java.util.function.IntPredicate;

public class MathHelper {

	public static final float SQRT_2 = MathHelper.sqrt(2.0F);

	private static final Random RANDOM = new Random();

	private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION = new int[] {
			0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11,
			5, 10, 9
	};

	private static final double FRAC_BIAS = Double.longBitsToDouble(4805340802404319232L);

	private static final double[] ASINE_TAB = new double[257];

	private static final double[] COS_TAB = new double[257];

	public static float sin(float value) {
		return (float) Math.sin(value);
	}

	public static float cos(float value) {
		return (float) Math.cos(value);
	}

	public static float sqrt(float value) {
		return (float) Math.sqrt(value);
	}

	public static float sqrt(double value) {
		return (float) Math.sqrt(value);
	}

	public static int floor(float value) {
		int i = (int) value;
		return value < i ? i - 1 : i;
	}

	public static int fastFloor(double value) {
		return (int) (value + 1024.0D) - 1024;
	}

	public static int floor(double value) {
		int i = (int) value;
		return value < i ? i - 1 : i;
	}

	public static long lfloor(double value) {
		long i = (long) value;
		return value < i ? i - 1L : i;
	}

	public static float abs(float value) {
		return Math.abs(value);
	}

	public static int abs(int value) {
		return Math.abs(value);
	}

	public static int ceil(float value) {
		int i = (int) value;
		return value > i ? i + 1 : i;
	}

	public static int ceil(double value) {
		int i = (int) value;
		return value > i ? i + 1 : i;
	}

	public static int clamp(int num, int min, int max) {
		if (num < min) {
			return min;
		}
		return num > max ? max : num;
	}

	public static long clamp(long num, long min, long max) {
		if (num < min) {
			return min;
		}
		return num > max ? max : num;
	}

	public static float clamp(float num, float min, float max) {
		if (num < min) {
			return min;
		}
		return num > max ? max : num;
	}

	public static double clamp(double num, double min, double max) {
		if (num < min) {
			return min;
		}
		return num > max ? max : num;
	}

	public static double clampedLerp(double lowerBnd, double upperBnd, double slide) {
		if (slide < 0.0D) {
			return lowerBnd;
		}
		return slide > 1.0D ? upperBnd : MathHelper.lerp(slide, lowerBnd, upperBnd);
	}

	public static double absMax(double x, double y) {
		if (x < 0.0D) {
			x = -x;
		}

		if (y < 0.0D) {
			y = -y;
		}

		return x > y ? x : y;
	}

	public static int intFloorDiv(int x, int y) {
		return Math.floorDiv(x, y);
	}

	public static int nextInt(Random random, int minimum, int maximum) {
		return minimum >= maximum ? minimum : random.nextInt(maximum - minimum + 1) + minimum;
	}

	public static float nextFloat(Random random, float minimum, float maximum) {
		return minimum >= maximum ? minimum : random.nextFloat() * (maximum - minimum) + minimum;
	}

	public static double nextDouble(Random random, double minimum, double maximum) {
		return minimum >= maximum ? minimum : random.nextDouble() * (maximum - minimum) + minimum;
	}

	public static double average(long[] values) {
		long i = 0L;

		for (long j : values) {
			i += j;
		}

		return (double) i / (double) values.length;
	}

	public static boolean epsilonEquals(float x, float y) {
		return Math.abs(y - x) < 1.0E-5F;
	}

	public static boolean epsilonEquals(double x, double y) {
		return Math.abs(y - x) < 1.0E-5F;
	}

	public static int normalizeAngle(int x, int y) {
		return Math.floorMod(x, y);
	}

	public static float positiveModulo(float numerator, float denominator) {
		return (numerator % denominator + denominator) % denominator;
	}

	public static double positiveModulo(double numerator, double denominator) {
		return (numerator % denominator + denominator) % denominator;
	}

	public static int wrapDegrees(int angle) {
		int i = angle % 360;
		if (i >= 180) {
			i -= 360;
		}

		if (i < -180) {
			i += 360;
		}

		return i;
	}

	public static float wrapDegrees(float value) {
		float f = value % 360.0F;
		if (f >= 180.0F) {
			f -= 360.0F;
		}

		if (f < -180.0F) {
			f += 360.0F;
		}

		return f;
	}

	public static double wrapDegrees(double value) {
		double d0 = value % 360.0D;
		if (d0 >= 180.0D) {
			d0 -= 360.0D;
		}

		if (d0 < -180.0D) {
			d0 += 360.0D;
		}

		return d0;
	}

	public static float wrapSubtractDegrees(float p_203302_0_, float p_203302_1_) {
		return MathHelper.wrapDegrees(p_203302_1_ - p_203302_0_);
	}

	public static float degreesDifferenceAbs(float p_203301_0_, float p_203301_1_) {
		return MathHelper.abs(MathHelper.wrapSubtractDegrees(p_203301_0_, p_203301_1_));
	}

	public static float func_219800_b(float p_219800_0_, float p_219800_1_, float p_219800_2_) {
		float f = MathHelper.wrapSubtractDegrees(p_219800_0_, p_219800_1_);
		float f1 = MathHelper.clamp(f, -p_219800_2_, p_219800_2_);
		return p_219800_1_ - f1;
	}

	public static float approach(float p_203300_0_, float p_203300_1_, float p_203300_2_) {
		p_203300_2_ = MathHelper.abs(p_203300_2_);
		return p_203300_0_ < p_203300_1_ ? MathHelper.clamp(p_203300_0_ + p_203300_2_, p_203300_0_, p_203300_1_)
				: MathHelper.clamp(p_203300_0_ - p_203300_2_, p_203300_1_, p_203300_0_);
	}

	public static float approachDegrees(float p_203303_0_, float p_203303_1_, float p_203303_2_) {
		float f = MathHelper.wrapSubtractDegrees(p_203303_0_, p_203303_1_);
		return MathHelper.approach(p_203303_0_, p_203303_0_ + f, p_203303_2_);
	}

	public static int getInt(String value, int defaultValue) {
		return MathHelper.toInt(value, defaultValue);
	}

	private static int toInt(String s, int defaultV) {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			return defaultV;
		}
	}

	public static int smallestEncompassingPowerOfTwo(int value) {
		int i = value - 1;
		i = i | i >> 1;
		i = i | i >> 2;
		i = i | i >> 4;
		i = i | i >> 8;
		i = i | i >> 16;
		return i + 1;
	}

	public static boolean isPowerOfTwo(int value) {
		return value != 0 && (value & value - 1) == 0;
	}

	public static int log2DeBruijn(int value) {
		value = MathHelper.isPowerOfTwo(value) ? value : MathHelper.smallestEncompassingPowerOfTwo(value);
		return MathHelper.MULTIPLY_DE_BRUIJN_BIT_POSITION[(int) (value * 125613361L >> 27) & 31];
	}

	public static int log2(int value) {
		return MathHelper.log2DeBruijn(value) - (MathHelper.isPowerOfTwo(value) ? 0 : 1);
	}

	public static int roundUp(int number, int interval) {
		if (interval == 0) {
			return 0;
		} else if (number == 0) {
			return interval;
		} else {
			if (number < 0) {
				interval *= -1;
			}

			int i = number % interval;
			return i == 0 ? number : number + interval - i;
		}
	}

	public static int rgb(float rIn, float gIn, float bIn) {
		return MathHelper.rgb(MathHelper.floor(rIn * 255.0F), MathHelper.floor(gIn * 255.0F),
				MathHelper.floor(bIn * 255.0F));
	}

	public static int rgb(int rIn, int gIn, int bIn) {
		int lvt_3_1_ = (rIn << 8) + gIn;
		return (lvt_3_1_ << 8) + bIn;
	}

	public static float frac(float number) {
		return number - MathHelper.floor(number);
	}

	public static double frac(double number) {
		return number - MathHelper.lfloor(number);
	}

	public static long getPositionRandom(Vector3i pos) {
		return MathHelper.getCoordinateRandom(pos.getX(), pos.getY(), pos.getZ());
	}

	public static long getCoordinateRandom(int x, int y, int z) {
		long i = x * 3129871 ^ z * 116129781L ^ y;
		i = i * i * 42317861L + i * 11L;
		return i >> 16;
	}

	public static UUID getRandomUUID(Random rand) {
		long i = rand.nextLong() & -61441L | 16384L;
		long j = rand.nextLong() & 4611686018427387903L | Long.MIN_VALUE;
		return new UUID(i, j);
	}

	public static UUID getRandomUUID() {
		return MathHelper.getRandomUUID(MathHelper.RANDOM);
	}

	public static double func_233020_c_(double p_233020_0_, double p_233020_2_, double p_233020_4_) {
		return (p_233020_0_ - p_233020_2_) / (p_233020_4_ - p_233020_2_);
	}

	public static double atan2(double p_181159_0_, double p_181159_2_) {
		double d0 = p_181159_2_ * p_181159_2_ + p_181159_0_ * p_181159_0_;
		if (Double.isNaN(d0)) {
			return Double.NaN;
		}
		boolean flag = p_181159_0_ < 0.0D;
		if (flag) {
			p_181159_0_ = -p_181159_0_;
		}

		boolean flag1 = p_181159_2_ < 0.0D;
		if (flag1) {
			p_181159_2_ = -p_181159_2_;
		}

		boolean flag2 = p_181159_0_ > p_181159_2_;
		if (flag2) {
			double d1 = p_181159_2_;
			p_181159_2_ = p_181159_0_;
			p_181159_0_ = d1;
		}

		double d9 = MathHelper.fastInvSqrt(d0);
		p_181159_2_ = p_181159_2_ * d9;
		p_181159_0_ = p_181159_0_ * d9;
		double d2 = MathHelper.FRAC_BIAS + p_181159_0_;
		int i = (int) Double.doubleToRawLongBits(d2);
		double d3 = MathHelper.ASINE_TAB[i];
		double d4 = MathHelper.COS_TAB[i];
		double d5 = d2 - MathHelper.FRAC_BIAS;
		double d6 = p_181159_0_ * d4 - p_181159_2_ * d5;
		double d7 = (6.0D + d6 * d6) * d6 * 0.16666666666666666D;
		double d8 = d3 + d7;
		if (flag2) {
			d8 = (Math.PI / 2D) - d8;
		}

		if (flag1) {
			d8 = Math.PI - d8;
		}

		if (flag) {
			d8 = -d8;
		}

		return d8;
	}

	public static float fastInvSqrt(float number) {
		float f = 0.5F * number;
		int i = Float.floatToIntBits(number);
		i = 1597463007 - (i >> 1);
		number = Float.intBitsToFloat(i);
		return number * (1.5F - f * number * number);
	}

	public static double fastInvSqrt(double number) {
		double d0 = 0.5D * number;
		long i = Double.doubleToRawLongBits(number);
		i = 6910469410427058090L - (i >> 1);
		number = Double.longBitsToDouble(i);
		return number * (1.5D - d0 * number * number);
	}

	public static float fastInvCubeRoot(float number) {
		int i = Float.floatToIntBits(number);
		i = 1419967116 - i / 3;
		float f = Float.intBitsToFloat(i);
		f = 0.6666667F * f + 1.0F / (3.0F * f * f * number);
		return 0.6666667F * f + 1.0F / (3.0F * f * f * number);
	}

	public static int hsvToRGB(float hue, float saturation, float value) {
		int i = (int) (hue * 6.0F) % 6;
		float f = hue * 6.0F - i;
		float f1 = value * (1.0F - saturation);
		float f2 = value * (1.0F - f * saturation);
		float f3 = value * (1.0F - (1.0F - f) * saturation);
		float f4;
		float f5;
		float f6;
		switch (i) {
		case 0:
			f4 = value;
			f5 = f3;
			f6 = f1;
			break;
		case 1:
			f4 = f2;
			f5 = value;
			f6 = f1;
			break;
		case 2:
			f4 = f1;
			f5 = value;
			f6 = f3;
			break;
		case 3:
			f4 = f1;
			f5 = f2;
			f6 = value;
			break;
		case 4:
			f4 = f3;
			f5 = f1;
			f6 = value;
			break;
		case 5:
			f4 = value;
			f5 = f1;
			f6 = f2;
			break;
		default:
			throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", "
					+ saturation + ", " + value);
		}

		int j = MathHelper.clamp((int) (f4 * 255.0F), 0, 255);
		int k = MathHelper.clamp((int) (f5 * 255.0F), 0, 255);
		int l = MathHelper.clamp((int) (f6 * 255.0F), 0, 255);
		return j << 16 | k << 8 | l;
	}

	public static int hash(int p_188208_0_) {
		p_188208_0_ = p_188208_0_ ^ p_188208_0_ >>> 16;
		p_188208_0_ = p_188208_0_ * -2048144789;
		p_188208_0_ = p_188208_0_ ^ p_188208_0_ >>> 13;
		p_188208_0_ = p_188208_0_ * -1028477387;
		return p_188208_0_ ^ p_188208_0_ >>> 16;
	}

	public static int binarySearch(int min, int max, IntPredicate isTargetBeforeOrAt) {
		int i = max - min;

		while (i > 0) {
			int j = i / 2;
			int k = min + j;
			if (isTargetBeforeOrAt.test(k)) {
				i = j;
			} else {
				min = k + 1;
				i -= j + 1;
			}
		}

		return min;
	}

	public static float lerp(float pct, float start, float end) {
		return start + pct * (end - start);
	}

	public static double lerp(double pct, double start, double end) {
		return start + pct * (end - start);
	}

	public static double lerp2(double p_219804_0_, double p_219804_2_, double p_219804_4_, double p_219804_6_,
			double p_219804_8_, double p_219804_10_) {
		return MathHelper.lerp(p_219804_2_, MathHelper.lerp(p_219804_0_, p_219804_4_, p_219804_6_),
				MathHelper.lerp(p_219804_0_, p_219804_8_, p_219804_10_));
	}

	public static double lerp3(double p_219807_0_, double p_219807_2_, double p_219807_4_, double p_219807_6_,
			double p_219807_8_, double p_219807_10_, double p_219807_12_, double p_219807_14_, double p_219807_16_,
			double p_219807_18_, double p_219807_20_) {
		return MathHelper.lerp(p_219807_4_,
				MathHelper.lerp2(p_219807_0_, p_219807_2_, p_219807_6_, p_219807_8_, p_219807_10_, p_219807_12_),
				MathHelper.lerp2(p_219807_0_, p_219807_2_, p_219807_14_, p_219807_16_, p_219807_18_, p_219807_20_));
	}

	public static double perlinFade(double p_219801_0_) {
		return p_219801_0_ * p_219801_0_ * p_219801_0_ * (p_219801_0_ * (p_219801_0_ * 6.0D - 15.0D) + 10.0D);
	}

	public static int signum(double x) {
		if (x == 0.0D) {
			return 0;
		}
		return x > 0.0D ? 1 : -1;
	}

	public static float interpolateAngle(float p_219805_0_, float p_219805_1_, float p_219805_2_) {
		return p_219805_1_ + p_219805_0_ * MathHelper.wrapDegrees(p_219805_2_ - p_219805_1_);
	}

	@Deprecated
	public static float rotLerp(float p_226167_0_, float p_226167_1_, float p_226167_2_) {
		float f;
		for (f = p_226167_1_ - p_226167_0_; f < -180.0F; f += 360.0F) {
		}

		while (f >= 180.0F) {
			f -= 360.0F;
		}

		return p_226167_0_ + p_226167_2_ * f;
	}

	@Deprecated
	public static float rotWrap(double p_226168_0_) {
		while (p_226168_0_ >= 180.0D) {
			p_226168_0_ -= 360.0D;
		}

		while (p_226168_0_ < -180.0D) {
			p_226168_0_ += 360.0D;
		}

		return (float) p_226168_0_;
	}

	public static float func_233021_e_(float p_233021_0_, float p_233021_1_) {
		return (Math.abs(p_233021_0_ % p_233021_1_ - p_233021_1_ * 0.5F) - p_233021_1_ * 0.25F) / (p_233021_1_ * 0.25F);
	}

	public static float squareFloat(float value) {
		return value * value;
	}

	static {
		for (int i = 0; i < 257; ++i) {
			double d0 = i / 256.0D;
			double d1 = Math.asin(d0);
			MathHelper.COS_TAB[i] = Math.cos(d1);
			MathHelper.ASINE_TAB[i] = d1;
		}

	}

}
