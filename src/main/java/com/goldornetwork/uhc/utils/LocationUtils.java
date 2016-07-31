package com.goldornetwork.uhc.utils;

import java.util.Random;

public class LocationUtils {

	private static Random random = new Random();
	
	public static CoordXZ locationInRadius(int centerX, int centerZ, int radius){
		int variationX = ((random.nextInt((radius * 2) + 1) - radius)) + centerX;
		int variationZ = ((random.nextInt((radius * 2)+ 1) - radius)) + centerZ;
		return new CoordXZ(variationX, variationZ);
	}
	
	public static CoordXZ locationInRadius(int radius){
		int variationX = ((random.nextInt((radius * 2) + 1) - radius));
		int variationZ = ((random.nextInt((radius * 2)+ 1) - radius));
		return new CoordXZ(variationX, variationZ);
	}
}
