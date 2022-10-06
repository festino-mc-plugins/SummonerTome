package com.festp.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class UtilsWorld
{
	public static Location searchBlock(Location loc, Material[] blocks, double horRadius, boolean playerCanStay)
	{
		boolean xPriority = false, zPriority = false;
		if (getBlockCenterOffset(loc.getX()) > 0) xPriority = true;
		if (getBlockCenterOffset(loc.getZ()) > 0) zPriority = true;
		boolean xPriorierZ = true;
		if (Math.abs(getBlockCenterOffset(loc.getX())) < Math.abs(getBlockCenterOffset(loc.getZ())))
			xPriorierZ = false;
		Block start_block = loc.getBlock();
		Block foundBlock = null;
		boolean player_cant_stay = !playerCanStay;
		searching :
		{
			for (int r = 0; r <= 1.1 * horRadius; r++) {
				for (int dy = 0; dy <= r / 2; dy++) {
					int temp = r - dy;
					for (int d = 0; d <= temp; d++) {
						int[] dxPool = (xPriority ? new int[] {    d,    -d} : new int[] {   -d,     d}),
							  dzPool = (zPriority ? new int[] {r - d, d - r} : new int[] {d - r, r - d});
						if (xPriorierZ)  //low dependency on priority
							for (int dx : dxPool)
								for (int dz : dzPool) {
									foundBlock = start_block.getRelative(dx, dy, dz);
									if (Utils.contains(blocks, foundBlock.getType()) && (player_cant_stay || UtilsType.playerCanStay(foundBlock)))
										break searching;
									foundBlock = start_block.getRelative(dx, -dy, dz);
									if (Utils.contains(blocks, foundBlock.getType()) && (player_cant_stay || UtilsType.playerCanStay(foundBlock)))
										break searching;
								}
						else
							for (int dz : dzPool)
								for (int dx : dxPool) {
									foundBlock = start_block.getRelative(dx, dy, dz);
									if (Utils.contains(blocks, foundBlock.getType()) && (player_cant_stay || UtilsType.playerCanStay(foundBlock)))
										break searching;
									foundBlock = start_block.getRelative(dx, -dy, dz);
									if (Utils.contains(blocks, foundBlock.getType()) && (player_cant_stay || UtilsType.playerCanStay(foundBlock)))
										break searching;
								}
					}
				}
			}
			return null;
		}
		return foundBlock.getLocation().add(0.5, 0.5, 0.5);
	}
	
	/** Checks 2x2 area for 3x2x3 clear area and specific block under the center */
	public static Location searchArea_3x3(Location loc, Material[] blocks)
	{
		boolean xPriority = false, zPriority = false;
		if (getBlockCenterOffset(loc.getX()) > 0) xPriority = true;
		if (getBlockCenterOffset(loc.getZ()) > 0) zPriority = true;
		double x = loc.getX();
		double dx = xPriority ? 1 : -1;
		double dz = zPriority ? 1 : -1;
		loc.setX(Math.floor(loc.getX()) + 0.5);
		loc.setZ(Math.floor(loc.getZ()) + 0.5);
		if (Utils.contains(blocks, loc.getBlock().getType()))
			if (UtilsWorld.isEmptyAbove_3x2x3(loc.getBlock()))
				return loc.clone().add(0, 1, 0);
		
		loc.add(dx, 0, 0);
		if (Utils.contains(blocks, loc.getBlock().getType()))
			if (UtilsWorld.isEmptyAbove_3x2x3(loc.getBlock()))
				return loc.clone().add(0, 1, 0);
		
		loc.setX(x);
		loc.add(0, 0, dz);
		if (Utils.contains(blocks, loc.getBlock().getType()))
			if (UtilsWorld.isEmptyAbove_3x2x3(loc.getBlock()))
				return loc.clone().add(0, 1, 0);
		
		loc.add(dx, 0, 0);
		if (Utils.contains(blocks, loc.getBlock().getType()))
			if (UtilsWorld.isEmptyAbove_3x2x3(loc.getBlock()))
				return loc.clone().add(0, 1, 0);
		return null;
	}
	
	private static boolean isEmptyAbove_3x2x3(Block block)
	{
		boolean empty = true;
		for (int dx = -1; dx <= 1; dx++)
			for (int dz = -1; dz <= 1; dz++)
				if (!UtilsType.playerCanFlyOn(block.getRelative(dx, 0, dz))) {
					empty = false;
					break;
				}
		return empty;
	}

	public static Location searchBlock22Platform(Location loc, Material[] blocks, double horRadius, boolean full)
	{
		// TODO fix wrong priorities
		boolean xPositive = false, zPositive = false;
		if (getBlockCenterOffset(loc.getX()) > 0) xPositive = true;
		if (getBlockCenterOffset(loc.getZ()) > 0) zPositive = true;
		boolean xPriorierZ = true;
		if (Math.abs(getBlockCenterOffset(loc.getX())) < Math.abs(getBlockCenterOffset(loc.getZ())))
			xPriorierZ = false;
		Block start_block = loc.getBlock();
		Block found_block = null;
		searching :
		{
			for (int r = 0; r <= 1.1 * horRadius; r++) {
				for (int dy = 0; dy <= r / 2; dy++) {
					int temp = r - dy;
					for (int d = 0; d <= temp; d++) {
						int[] dxPool = xPositive ? new int[] {d, -d} : new int[] {-d, d},
							  dzPool = zPositive ? new int[] {d, -d} : new int[] {-d, d};
						if (xPriorierZ)
							for (int dz : dzPool)
								for (int dx : dxPool) {
									found_block = start_block.getRelative(dx, dy, r-Math.abs(dz));
									if (is22Platform(blocks, found_block, xPositive, zPositive, full))
										break searching;
									found_block = start_block.getRelative(dx, dy, dz);
									if (is22Platform(blocks, found_block, xPositive, zPositive, full))
										break searching;
								}
						else
							for (int dx : dxPool)
								for (int dz : dzPool) {
									found_block = start_block.getRelative(dx, dy, r-Math.abs(dz));
									if (is22Platform(blocks, found_block, xPositive, zPositive, full))
										break searching;
									found_block = start_block.getRelative(dx, dy, dz);
									if (is22Platform(blocks, found_block, xPositive, zPositive, full))
										break searching;
								}
					}
				}
			}
			return null;
		}
		return found_block.getLocation().add(
				xPositive ? 1 : 0,
				1,
				zPositive ? 1 : 0);
	}
	
	private static boolean is22Platform(Material[] valid_materials, Block start_block, boolean positiveX, boolean positiveZ, boolean full)
	{
		int dx = positiveX ? 1 : -1;
		int dz = positiveZ ? 1 : -1;
		int countBlocks = 0, countFlyable = 0;
		if (UtilsType.playerCanFlyOn(start_block)) {
			countFlyable++;
			if (Utils.contains(valid_materials, start_block.getType()))
				countBlocks++;
		}
		Block block = start_block.getRelative(0, 0, dz);
		if (UtilsType.playerCanFlyOn(block)) {
			countFlyable++;
			if (Utils.contains(valid_materials, block.getType()))
				countBlocks++;
		}
		block = start_block.getRelative(dx, 0, 0);
		if (UtilsType.playerCanFlyOn(block)) {
			countFlyable++;
			if (Utils.contains(valid_materials, block.getType()))
				countBlocks++;
		}
		block = start_block.getRelative(dx, 0, dz);
		if (UtilsType.playerCanFlyOn(block)) {
			countFlyable++;
			if (Utils.contains(valid_materials, block.getType()))
				countBlocks++;
		}
		if (countFlyable < 2 * 2)
			return false;
		if (countBlocks == 2 * 2)
			return true;
		if (!full && countBlocks >= 1)
			return true;
		return false;
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
			if (UtilsType.playerCanStay(b.getRelative(0, 1, 0))) 
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
				// 0+++ = priority cells at first
				cells = new int[] {4, 4 + xStep, 4 + zStep, 4 + xStep + zStep};
			else if (i == 3)
				// 0--- = non-priority cells at last
				cells = new int[]{4, 4 - xStep, 4 - zStep, 4 - xStep - zStep};
			else if (i == 1 && xPriorierZ || i == 2 && !xPriorierZ) // priority matters here
				// 0+-+-
				cells = new int[]{4, 4 + xStep, 4 - zStep, 4 + xStep - zStep};
			else
				// 0-+-+
				cells = new int[]{4, 4 - xStep, 4 + zStep, 4 - xStep + zStep};
			
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
