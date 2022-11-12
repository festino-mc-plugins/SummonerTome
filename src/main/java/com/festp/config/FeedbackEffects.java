package com.festp.config;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class FeedbackEffects
{
	private static final float VOLUME = 0.15f;
	private static boolean isSoundEnabled = true;
	private static boolean isParticlesEnabled = true;
	
	public static void playSummonSuccess(Location entityLoc) {
		World world = entityLoc.getWorld();
		Location effectLocation = entityLoc.add(0, 0.15, 0);
		spawnParticle(world, Particle.ENCHANTMENT_TABLE, effectLocation, 30, 0.1, 0.1, 0.1, 0.8);
		playSound(world, effectLocation, Sound.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, VOLUME, 0.8f);
	}
	public static void playSummonFail(Player player) {
		World world = player.getWorld();
		Location effectLocation = player.getLocation().add(0, 0.15, 0);
		playSound(world, effectLocation, Sound.BLOCK_CHAIN_PLACE, SoundCategory.PLAYERS, 0.8f * VOLUME, 0.9f);
	}
	
	public static void playDespawn(Location entityLoc) {
		World world = entityLoc.getWorld();
		Location effectLocation = entityLoc.add(0, 0.15, 0);
		//world.spawnParticle(Particle.WHITE_ASH, effectLocation, 100, 0.12, 0.12, 0.12);
		spawnParticle(world, Particle.END_ROD, effectLocation, 50, 0.12, 0.12, 0.12, 0.05);
		playSound(world, effectLocation, Sound.ITEM_BOOK_PUT, SoundCategory.PLAYERS, 3f * VOLUME, 0.5f);
	}
	
	public static void playSwapSuccess(Location entityLoc) {
		World world = entityLoc.getWorld();
		Location effectLocation = entityLoc.add(0, 0.15, 0);
		playSound(world, effectLocation, Sound.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 0.6f * VOLUME, 0.5f);
	}
	public static void playSwapFail(Player player) {
		Location effectLocation = player.getLocation().add(0, 0.15, 0);
		playSound(player, effectLocation, Sound.ENTITY_PLAYER_ATTACK_NODAMAGE, SoundCategory.PLAYERS, VOLUME, 0.5f);
	}
	
	
	
	private static void playSound(World world, Location loc, Sound sound, SoundCategory category, float volume, float pitch) {
		if (isSoundEnabled)
			world.playSound(loc, sound, category, volume, pitch);
	}
	private static void playSound(Player player, Location loc, Sound sound, SoundCategory category, float volume, float pitch) {
		if (isSoundEnabled)
			player.playSound(loc, sound, category, volume, pitch);
	}
	private static void spawnParticle(World world, Particle particle, Location loc, int amount, double xOffset, double yOffset, double zOffset, double speed) {
		if (isParticlesEnabled)
			world.spawnParticle(particle, loc, amount, xOffset, yOffset, zOffset, speed);
	}
}
