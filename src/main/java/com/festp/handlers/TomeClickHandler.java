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
import com.festp.config.FeedbackEffects;
import com.festp.tome.ComponentManager;
import com.festp.tome.SummonerTome;
import com.festp.utils.SummonUtils;
import com.festp.utils.UtilsType;

public class TomeClickHandler implements Listener
{
	private static class InteractResult {
		public final boolean hasTome;
		public final boolean failed;
		public final Entity entity;
		
		public InteractResult(boolean hasTome, boolean failed) {
			this.hasTome = hasTome;
			this.failed = failed;
			this.entity = null;
		}
		
		public InteractResult(boolean hasTome, boolean failed, Entity entity) {
			this.hasTome = hasTome;
			this.failed = failed;
			this.entity = entity;
		}
	}

	private final FeedbackEffects feedbackEffects;
	private final ComponentManager componentManager;
	private final TomeInventoryHandler inventoryHandler;
	
	public TomeClickHandler(FeedbackEffects feedbackEffects, ComponentManager componentManager, TomeInventoryHandler inventoryHandler) {
		this.feedbackEffects = feedbackEffects;
		this.componentManager = componentManager;
		this.inventoryHandler = inventoryHandler;
	}
	
	// Customization (swap an entity with the tome entity)
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) //PlayerInteractAtEntityEvent
	{
		InteractResult res = getSwapResult(event);
		if (!res.hasTome)
			return;
		if (res.failed)
			feedbackEffects.playSwapFail(event.getPlayer());
		else
			feedbackEffects.playSwapSuccess(res.entity.getLocation());
	}
	
	private InteractResult getSwapResult(PlayerInteractEntityEvent event)
	{
		InteractResult res = new InteractResult(false, true);
		Entity entity = event.getRightClicked();
		Player player = event.getPlayer();
		boolean entityIsSummoned = SummonUtils.wasSummoned(entity);
		if (entityIsSummoned) {
			// disable nametags, breeding, etc
			event.setCancelled(true);
		}
		
		boolean mainHand = true;
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item == null) {
			mainHand = false;
			item = player.getInventory().getItemInOffHand();
			if (item == null)
				return res;
		}

		SummonerTome tome = SummonerTome.getTome(item);
		if (tome == null)
			return res;
		res = new InteractResult(true, true);
		
		if (!player.hasPermission(Permissions.USE))
			return res;
		
		if (entityIsSummoned)
			return res;
		if (SummonUtils.hasSummoned(item))
			return res;
		
		boolean failed = !tome.trySwap(entity);
		res = new InteractResult(true, failed, entity);
		if (failed)
			return res;

		event.setCancelled(true);

		ItemStack updatedTome = tome.setTome(item);
		if (mainHand)
			player.getInventory().setItemInMainHand(updatedTome);
		else
			player.getInventory().setItemInOffHand(updatedTome);
		return res;
	}
	
	// Summoning
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		InteractResult res = getSummonResult(event);
		if (!res.hasTome)
			return;
		if (res.failed)
			feedbackEffects.playSummonFail(event.getPlayer());
		else
			feedbackEffects.playSummonSuccess(res.entity.getLocation());
	}
	private InteractResult getSummonResult(PlayerInteractEvent event)
	{	
		InteractResult res = new InteractResult(false, true);
		Player player = event.getPlayer();
		if (player.isInsideVehicle())
			return res;
		if (!(event.getAction() == Action.RIGHT_CLICK_AIR
				|| event.getAction() == Action.RIGHT_CLICK_BLOCK && !UtilsType.isInteractable(event.getClickedBlock().getType()) ))
			return res;

		boolean inMainHand = true;
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item == null) {
			inMainHand = false;
			item = player.getInventory().getItemInOffHand();
			if (item == null)
				return res;
		}
		
		SummonerTome tome = SummonerTome.getTome(item);
		if (tome == null)
			return res;
		res = new InteractResult(true, true);

		if (!event.getPlayer().hasPermission(Permissions.USE))
			return res;

		Entity summoned = tome.trySummon(player);
		boolean failed = summoned == null;
		res = new InteractResult(true, failed, summoned);
		if (failed)
			return res;
		
		event.setCancelled(true);
		SummonUtils.setHasSummoned(player, inMainHand, summoned);
		String customName = getCustomName(item);
		if (customName != null) {
			summoned.setCustomName(customName);
		}
		String code = SummonUtils.getCode(summoned);
		if (code != null) {
			int banSlotsFrom = componentManager.getBanSlotsFrom(code);
			IDataExtractor dataExtractor = componentManager.getDataExtractor(code);
			inventoryHandler.listenInventory(summoned, banSlotsFrom, dataExtractor);
		}
		return res;
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
