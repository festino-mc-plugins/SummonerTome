package com.festp.utils;

import java.util.Random;

public class UtilsRandom {
	private static Random random = new Random();
	
	public static double getDouble() {
		return random.nextDouble();
	}
	
	public static int getInt(int bound) {
		return random.nextInt(bound);
	}

	public static int getInt(int min, int max) {
		return min + random.nextInt(max + 1 - min);
	}

	public static double getDouble(double min, double max) {
		return min + random.nextDouble() * (max - min);
	}

	public static <T> T get(T[] values) {
		return values[getInt(values.length)];
	}
}
