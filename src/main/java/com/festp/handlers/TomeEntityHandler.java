package com.festp.handlers;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.InventoryHolder;

import com.festp.DelayedTask;
import com.festp.TaskList;
import com.festp.config.FeedbackEffects;
import com.festp.utils.SummonUtils;
import com.festp.utils.UtilsWorld;

public class TomeEntityHandler implements Listener {
	
	@EventHandler
	public void onPlayerDropTome(PlayerDropItemEvent event) {
		Entity summoned = SummonUtils.getHasSummoned(event.getItemDrop().getItemStack());
		if (summoned != null) {
			removeEntity(summoned);
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (SummonUtils.wasSummoned(event.getEntity())) {
			event.setDroppedExp(0);
			event.getDrops().clear();
		}
	}
	
	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {
		for (Entity e : event.getChunk().getEntities())
			if (SummonUtils.wasSummoned(e) && e.getPassengers().size() == 0)
				removeEntity(e);
	}

	@EventHandler
	public void onVehicleDestroy(VehicleDestroyEvent event) {
		if (SummonUtils.wasSummoned(event.getVehicle())) {
			event.setCancelled(true);
			removeEntity(event.getVehicle());
		}
	}

	@EventHandler
	public void onVehicleExit(VehicleExitEvent event) {
		Vehicle vehicle = event.getVehicle();
		if (vehicle.getPassengers().get(0) == event.getExited()) // probably driver
			if (SummonUtils.wasSummoned(vehicle)) {
				removeEntity(vehicle);
			}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		final Player joined = event.getPlayer();
		TaskList.add(new DelayedTask(1, new Runnable() {
			@Override
			public void run() {
				if (joined.isOnline() && joined.isInsideVehicle())
				{
					Entity vehicle = joined.getVehicle();
					if (SummonUtils.wasSummoned(vehicle)) {
						removeEntity(vehicle);
					}
				}
			}
		}));
	}
	
	public static void removeEntity(Entity entity)
	{
		// ChestBoat workaround, horses are fine without it
		// may collapse with TomeInventoryHandler saving while lagging
		// (change inv -> close inv -> exit)
		if (entity instanceof InventoryHolder)
			((InventoryHolder)entity).getInventory().clear();
		// entity.eject(); and passenger.leaveVehicle(); are not working; critical for striders
		eject(entity);
		FeedbackEffects.playDespawn(entity.getLocation());
		entity.remove();
	}
	
	private static void eject(Entity entity)
	{
		for (Entity passenger : entity.getPassengers())
		{
			double saddleHeight = (entity instanceof LivingEntity) ? ((LivingEntity)entity).getEyeHeight() : 0.0;
			Location passengerloc = entity.getLocation().add(0.0, saddleHeight, 0.0);
			Location loc = UtilsWorld.findEjectBlock2x2(passengerloc);
			if (loc == null) {
				loc = passengerloc;
			}
			loc.setDirection(passenger.getLocation().getDirection());
			passenger.teleport(loc);
		}
	}
}
