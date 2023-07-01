package com.festp.utils;

import java.util.function.Predicate;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BoundingBox;

public class UtilsWorld
{
	public static Location searchBlock(Location loc, Predicate<Block> predicate, double horRadius)
	{
		double locX = getBlockCenterOffset(loc.getX());
		double locZ = getBlockCenterOffset(loc.getZ());
		Block startBlock = loc.getBlock();
		int radius = (int)Math.ceil(horRadius);
		int horRadiusSquared = (int) Math.ceil(horRadius * horRadius);
		int vertRadius = 0;
		Block b;
		Block foundBlock = null;
		double distSquared = horRadiusSquared;
		for (int dz = -radius; dz <= radius; dz++)
			for (int dy = -vertRadius; dy <= vertRadius; dy++)
				for (int dx = -radius; dx <= radius; dx++)
				{
					if (dx * dx + dz * dz >= horRadiusSquared)
						continue;
					b = startBlock.getRelative(dx, dy, dz);
					if (!predicate.test(b))
						continue;
					double xDist = locX - dx;
					double zDist = locZ - dz;
					double dist2 = xDist * xDist + zDist * zDist;
					if (dist2 < distSquared) {
						foundBlock = startBlock.getRelative(dx, dy, dz);
						distSquared = dist2;
					}
				}
		if (foundBlock == null)
			return null;
		return foundBlock.getLocation().add(0.5, 0, 0.5);
	}
	
	/** Try find nearest NxN area player can fly with at least one required block<br>
	 * if softMode is <b>true</b>, allows blocks other than predicated and air (1/16+) */
	public static Location searchArea_NxN(Location loc, final int N, double horRadius, Predicate<Block> predicate, boolean softMode)
	{
		// fill the integer grid: 0, 1, 2
		// check areas: product is >= 2
		// calculate distance, find minimal
		// return area center if found
		final Block startBlock = loc.getBlock();
		int radius = (int)Math.ceil(horRadius) + (N + 1) / 2;
		int horRadiusSquared = (int) Math.ceil(horRadius * horRadius);
		int width = radius * 2 + 1;
		int vertRadius = 0;
		int height = vertRadius * 2 + 1;
		int minProduct = 2;
		int[][][] grid = new int[width][height][width];
		Block b;
		BoundingBox softBB = new BoundingBox(-1, 15.1 / 16, -1, 2, 2, 2);
		for (int dz = -radius; dz <= radius; dz++)
			for (int dy = -vertRadius; dy <= vertRadius; dy++)
				for (int dx = -radius; dx <= radius; dx++)
				{
					int x = dx + radius;
					int y = dy + vertRadius;
					int z = dz + radius;
					if (dx * dx + dz * dz >= horRadiusSquared)
						continue;
					b = startBlock.getRelative(dx, dy, dz);
					grid[x][y][z] += UtilsType.playerCanFlyOn(b) ? 1 : 0;
					if (grid[x][y][z] > 0) {
						grid[x][y][z] += predicate.test(b) ? 1 : 0;
						// in soft mode, entity may stay on one block of the area, ignoring other
						// in hard mode, entity must either stay on predicated blocks or fly on predicated blocks
						if (!softMode && grid[x][y][z] == 1)
							if (!b.getType().isAir() && b.getCollisionShape().overlaps(softBB))
								grid[x][y][z] = 0;
					}
				}
		Location foundLoc = null;
		
		boolean isInitLocValid = true;
		int minX = (int)Math.floor(loc.getX() - 0.5 * N) - startBlock.getX();
		int minZ = (int)Math.floor(loc.getZ() - 0.5 * N) - startBlock.getZ();
		int maxX = (int)Math.ceil(loc.getX() + 0.5 * N) - startBlock.getX();
		int maxZ = (int)Math.ceil(loc.getZ() + 0.5 * N) - startBlock.getZ();
		int product = 1;
		for (int dz = minZ; dz < maxZ; dz++)
			for (int dx = minX; dx < maxX; dx++)
				product *= grid[radius + dx][vertRadius][radius + dz];
		if (product < minProduct)
			isInitLocValid = false;
		if (isInitLocValid)
			return loc;
		
		double distSquared = horRadiusSquared;
		for (int dz = -radius + N; dz <= radius; dz++)
			for (int dy = -vertRadius; dy <= vertRadius; dy++)
				for (int dx = -radius + N; dx <= radius; dx++)
				{
					int x = dx + radius;
					int y = dy + vertRadius;
					int z = dz + radius;
					product = 1;
					for (int j = -N; j < 0; j++)
						for (int i = -N; i < 0; i++)
							product *= grid[x + i][y][z + j];
									
					// has non-flyable blocks (==0) or no required blocks(==0 or ==1)
					if (product < minProduct)
						continue;
					
					Location l = startBlock.getLocation().add(dx - 0.5 * N, dy, dz - 0.5 * N);
					double dist2 = l.distanceSquared(loc);
					if (dist2 < distSquared) {
						foundLoc = l;
						distSquared = dist2;
					}
				}
		return foundLoc;
	}
	
	public static Location findEjectBlock2x2(Location playerLoc)
	{
		double xOffset = getBlockCenterOffset(playerLoc.getX());
		double zOffset = getBlockCenterOffset(playerLoc.getZ());
		int dx = 1, dz = 1;
		if (xOffset < 0)
			dx = -1;
		if (zOffset < 0)
			dz = -1;
		boolean xFirst = Math.abs(xOffset) > Math.abs(zOffset);

		Block startBlock = playerLoc.getBlock();
		Block blockStayIn = findEjectBlock2x2(startBlock, dx, dz, xFirst);
		if (blockStayIn == null) {
			startBlock = startBlock.getRelative(BlockFace.DOWN);
			blockStayIn = findEjectBlock2x2(startBlock, dx, dz, xFirst);
			if (blockStayIn == null) {
				startBlock = startBlock.getRelative(BlockFace.UP).getRelative(BlockFace.UP);
				blockStayIn = findEjectBlock2x2(startBlock, dx, dz, xFirst);
			}
		}
		if (blockStayIn == null)
			return null;
		return blockStayIn.getLocation().add(0.5, 0, 0.5);
	}
	private static Block findEjectBlock2x2(Block start, int dx, int dz, boolean xFirst)
	{
		if (UtilsType.playerCanStayIn(start))
			return start;
		if (xFirst) {
			start = start.getRelative(dx, 0, 0);
			if (UtilsType.playerCanStayIn(start))
				return start;
			start = start.getRelative(-dx, 0, dz);
			if (UtilsType.playerCanStayIn(start))
				return start;
			start = start.getRelative(dx, 0, 0);
		}
		else {
			start = start.getRelative(0, 0, dz);
			if (UtilsType.playerCanStayIn(start))
				return start;
			start = start.getRelative(dx, 0, -dz);
			if (UtilsType.playerCanStayIn(start))
				return start;
			start = start.getRelative(0, 0, dz);
		}
		if (UtilsType.playerCanStayIn(start))
			return start;
		return null;
	}
	
	private static double getBlockCenterOffset(double x)
	{
		return x - Math.floor(x) - 0.5;
	}
}
