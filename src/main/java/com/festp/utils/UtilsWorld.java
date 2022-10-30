package com.festp.utils;

import java.util.function.Predicate;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

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
	
	/** Try find nearest NxN area player can fly with at least one required block */
	public static Location searchArea_NxN(Location loc, int N, double horRadius, Material[] blocks)
	{
		// fill the integer grid: 0, 1, 2
		// check areas: product is >= 2
		// calculate distance, find minimal
		// return area center if found
		Block startBlock = loc.getBlock();
		int radius = (int)Math.ceil(horRadius) + (N + 1) / 2;
		int horRadiusSquared = (int) Math.ceil(horRadius * horRadius);
		int width = radius * 2 + 1;
		int vertRadius = 0;
		int height = vertRadius * 2 + 1;
		int[][][] grid = new int[width][height][width];
		Block b;
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
					//grid[x][y][z] = predicate.test(b) ? 0 : 1;
					grid[x][y][z] += Utils.contains(blocks, b.getType()) ? 1 : 0;
				}
		Location foundLoc = null;
		double distSquared = horRadiusSquared;
		for (int dz = -radius + N; dz <= radius; dz++)
			for (int dy = -vertRadius; dy <= vertRadius; dy++)
				for (int dx = -radius + N; dx <= radius; dx++)
				{
					int x = dx + radius;
					int y = dy + vertRadius;
					int z = dz + radius;
					int product = 1;
					for (int j = -N; j < 0; j++)
						for (int i = -N; i < 0; i++)
							product *= grid[x + i][y][z + j];
									
					// has non-flyable blocks (==0) or no required blocks(==0 or ==1)
					if (product < 2)
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
		int dx = 1, dz = 1;
		if (getBlockCenterOffset(playerLoc.getX()) < 0)
			dx = -1;
		if (getBlockCenterOffset(playerLoc.getZ()) < 0)
			dz = -1;

		Block startBlock = playerLoc.getBlock();
		Block blockStayIn = findEjectBlock2x2(startBlock, dx, dz);
		if (blockStayIn == null) {
			startBlock = startBlock.getRelative(BlockFace.DOWN);
			blockStayIn = findEjectBlock2x2(startBlock, dx, dz);
			if (blockStayIn == null) {
				startBlock = startBlock.getRelative(BlockFace.UP).getRelative(BlockFace.UP);
				blockStayIn = findEjectBlock2x2(startBlock, dx, dz);
			}
		}
		if (blockStayIn == null)
			return null;
		return blockStayIn.getLocation().add(0.5, 0, 0.5);
	}
	private static Block findEjectBlock2x2(Block start, int dx, int dz)
	{
		if (UtilsType.playerCanStayIn(start))
			return start;
		start = start.getRelative(dx, 0, 0);
		if (UtilsType.playerCanStayIn(start))
			return start;
		start = start.getRelative(-dx, 0, dz);
		if (UtilsType.playerCanStayIn(start))
			return start;
		start = start.getRelative(dx, 0, 0);
		if (UtilsType.playerCanStayIn(start))
			return start;
		return null;
	}
	
	public static Location findHorseSpace(Location loc)
	{
		Block startBlock = loc.add(0, -1, 0).getBlock();
		loc.add(0, 1, 0);
		if (!UtilsType.playerCanFlyOn(startBlock))
			return null;
		// grid 3x3, has x first and z second, therefore xStep is +-1, zStep is +-3
		int xStep = -1, zStep = -3;
		if (getBlockCenterOffset(loc.getX()) > 0) xStep = 1;
		if (getBlockCenterOffset(loc.getZ()) > 0) zStep = 3;
		boolean xPriorierZ = true;
		if (Math.abs(getBlockCenterOffset(loc.getX())) < Math.abs(getBlockCenterOffset(loc.getZ())))
			xPriorierZ = false;
		
		int[] grid = new int[9];
		// fill the grid
		for (int i = 0; i < 9; i++) {
			Block b = startBlock.getRelative(i % 3 - 1, 0, i / 3 - 1);
			if (UtilsType.playerCanStayIn(b.getRelative(0, 1, 0))) 
				grid[i] = 2;
			else if (UtilsType.playerCanFlyOn(b)) 
				grid[i] = 1;
			else
				grid[i] = 0;
		}
		
		for (int i = 0; i < 4; i++)
		{
			int[] cells;
			// 4 is center index
			if (i == 0)
				// 0++ ++ = priority cells at first
				cells = new int[] {4, 4 + xStep, 4 + zStep, 4 + xStep + zStep};
			else if (i == 3)
				// 0-- -- = non-priority cells at last
				cells = new int[] {4, 4 - xStep, 4 - zStep, 4 - xStep - zStep};
			else if (i == 1 && xPriorierZ || i == 2 && !xPriorierZ) // priority matters here
				// 0+- +-
				cells = new int[] {4, 4 + xStep, 4 - zStep, 4 + xStep - zStep};
			else
				// 0-+ -+
				cells = new int[] {4, 4 - xStep, 4 + zStep, 4 - xStep + zStep};
			
			boolean hasGround = grid[cells[0]] == 2 || grid[cells[1]] == 2 || grid[cells[2]] == 2 || grid[cells[3]] == 2;
			boolean isClearArea = grid[cells[0]] > 0 && grid[cells[1]] > 0 && grid[cells[2]] > 0 && grid[cells[3]] > 0;
			if (hasGround && isClearArea)
			{
				// grid indices to world offsets (horse would spawn in 2x2 center => offsets are 0 or 1)
				// cells[3] is the farthest cell from the center
				double dx = ((cells[3] + 3) % 3 - 1) < 0 ? 0 : 1;
				double dz = cells[3] - 4 < 0 ? 0 : 1;
				return startBlock.getLocation().add(dx, 1, dz);
			}
		}
		return null;
	}
	
	private static double getBlockCenterOffset(double x)
	{
		return x - Math.floor(x) - 0.5;
	}
}
