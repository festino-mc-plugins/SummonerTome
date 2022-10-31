package com.festp.handlers;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.festp.Permissions;
import com.festp.tome.SummonerTome;
import com.festp.utils.SummonUtils;
import com.festp.utils.UtilsType;

public class TomeClickHandler implements Listener
{
	// Customization (swap an entity with the tome entity)
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) //PlayerInteractAtEntityEvent
	{
		Entity entity = event.getRightClicked();
		if (SummonUtils.wasSummoned(entity)) {
			if (entity.getPassengers().size() > 0) {
				// disable nametags, breeding, etc
				event.setCancelled(true);
			}
			return;
		}
		
		Player player = event.getPlayer();
		if (!player.hasPermission(Permissions.USE))
			return;
		
		boolean mainHand = true;
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item == null) {
			mainHand = false;
			item = player.getInventory().getItemInOffHand();
			if (item == null)
				return;
		}

		SummonerTome tome = SummonerTome.getTome(item);
		if (tome == null)
			return;
		if (SummonUtils.hasSummoned(item))
			return;
		
		if (!tome.trySwap(entity))
			return;

		event.setCancelled(true);

		ItemStack updatedTome = tome.setTome(item);
		if (mainHand)
			player.getInventory().setItemInMainHand(updatedTome);
		else
			player.getInventory().setItemInOffHand(updatedTome);
	}
	
	// Summoning
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (!event.getPlayer().hasPermission(Permissions.USE))
			return;
		
		Player player = event.getPlayer();
		if (player.isInsideVehicle())
			return;
		if (!(event.getAction() == Action.RIGHT_CLICK_AIR
				|| event.getAction() == Action.RIGHT_CLICK_BLOCK && !UtilsType.isInteractable(event.getClickedBlock().getType()) ))
			return;

		boolean inMainHand = true;
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item == null) {
			inMainHand = false;
			item = player.getInventory().getItemInOffHand();
			if (item == null)
				return;
		}
		
		SummonerTome tome = SummonerTome.getTome(item);
		if (tome == null) return;

		Entity summoned = tome.trySummon(player);
		if (summoned == null)
			return;
		
		event.setCancelled(true);
		SummonUtils.setSummoned(summoned);
		SummonUtils.setHasSummoned(player, inMainHand, summoned);
		String customName = getCustomName(item);
		if (customName != null) {
			summoned.setCustomName(customName);
		}
	}

	private static String getCustomName(ItemStack tome)
	{
		if (!tome.getItemMeta().hasDisplayName())
			return null;
		String name = tome.getItemMeta().getDisplayName();
		boolean wasRenamed = !name.endsWith(" tome"); // TODO improve renaming detection
		if (!wasRenamed)
			return null;
		return name;
	}
}
