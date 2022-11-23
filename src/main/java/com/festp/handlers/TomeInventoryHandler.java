package com.festp.handlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.AbstractHorseInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.festp.components.ITomeComponent;
import com.festp.tome.SummonerTome;
import com.festp.utils.SummonUtils;
import com.festp.utils.UtilsType;

public class TomeInventoryHandler implements Listener
{
	private static class EntityInfo {
		public final Entity summoned;
		public final int banSlotsFrom;
		public final IDataExtractor dataExtractor;
		
		public EntityInfo(Entity summoned, int banSlotsFrom, IDataExtractor dataExtractor) {
			this.summoned = summoned;
			this.banSlotsFrom = banSlotsFrom;
			this.dataExtractor = dataExtractor;
		}
		
	}
	private final List<EntityInfo> entities = new ArrayList<>();
	
	private final List<EntityInfo> saveEntity = new ArrayList<>();
	private final List<Player> savePlayer = new ArrayList<>();
	
	public void tick() {
		for (int i = entities.size() - 1; i >= 0; i--) {
			if (!entities.get(i).summoned.isValid())
				entities.remove(i);
		}
		for (int i = saveEntity.size() - 1; i >= 0; i--) {
			EntityInfo info = saveEntity.get(i);
			if (!saveEntityData(info, savePlayer.get(i)))
				info.summoned.remove();
			saveEntity.remove(i);
			savePlayer.remove(i);
		}
	}

	// TODO allowed slots (horses ban 0): "0..26", "1.."
	public void listenInventory(Entity summoned, int banSlotsFrom, IDataExtractor dataExtractor) {
		entities.add(new EntityInfo(summoned, banSlotsFrom, dataExtractor));
	}
	
	private EntityInfo findEntity(Entity holder) {
		for (EntityInfo info : entities)
			if (info.summoned.equals(holder))
				return info;
		return null;
	}
	
	private void addSavingTome(EntityInfo info, Player p) {
		saveEntity.add(info);
		savePlayer.add(p);
	}

	// Horse slots and move tome to other inventories
	@EventHandler
	public void onClick(InventoryClickEvent event)
	{
		if (!startEvent(event))
			return;

		InventoryAction action = event.getAction();
		if (action == InventoryAction.CLONE_STACK)
			return;
		
		Inventory topInv = event.getView().getTopInventory();
		if (!(topInv.getHolder() instanceof Entity))
			return;
		
		EntityInfo info = findEntity((Entity)topInv.getHolder());
		if (info == null) {
			event.setCancelled(true);
			return;
		}
		
		int slot = event.getRawSlot();
		if (slot < 0)
			return;

		boolean illegal = false;
		Inventory inv = event.getClickedInventory();
		// check if try move horse armor to non-custom horse
		if (inv instanceof PlayerInventory)
		{
			if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
				if (topInv.getHolder() instanceof Horse && info.banSlotsFrom <= 1) {
					Material m = event.getCurrentItem().getType();
					ItemStack horseArmor = event.getView().getItem(1);
					if ((horseArmor == null || horseArmor.getType() == Material.AIR) && UtilsType.isHorseArmor(m)) {
						illegal = true;
					}
				}
				else {
					if (info.banSlotsFrom < topInv.getSize()) {
						illegal = true;
					}
				}
			}
		}
		else
		{
			if (action != InventoryAction.UNKNOWN)
				if (info.banSlotsFrom <= slot)
					illegal = true;
			// removing the saddle is always illegal
			if (inv instanceof AbstractHorseInventory) {
				if (slot == 0)
					illegal = true;
			}
		}

		if (illegal)
			event.setCancelled(true);
		else
			trySaveEntity((Player) event.getWhoClicked(), info);
	}

	@EventHandler
    public void onDrag(InventoryDragEvent event)
	{
		if (!startEvent(event))
			return;
		
		Inventory topInv = event.getView().getTopInventory();
		if (!(topInv.getHolder() instanceof Entity))
			return;

		EntityInfo info = findEntity((Entity)topInv.getHolder());
		if (info == null) {
			event.setCancelled(true);
			return;
		}

		boolean illegal = false;
		int maxSlot = topInv.getSize();
		for (Integer slot : event.getInventorySlots())
			if (slot < maxSlot && info.banSlotsFrom <= slot) {
				illegal = true;
				break;
			}

		if (illegal)
			event.setCancelled(true);
		else
			trySaveEntity((Player) event.getWhoClicked(), info);
	}
	
	/** Checks if the top inventory is an inventory of a summoned entity. If the entity is illegal, it will be removed. */
	private boolean startEvent(InventoryInteractEvent event)
	{
		if (event.isCancelled())
			return false;

		Inventory topInv = event.getView().getTopInventory();
		if (!(topInv.getHolder() instanceof Entity))
			return false;
		
		Entity entity = (Entity)topInv.getHolder();
		if (!SummonUtils.wasSummoned(entity))
			return false;
		
		if (isIllegalSummonedEntity(entity)) {
			event.setCancelled(true);
			entity.remove();
			return false;
		}
		
		return true;
	}
	/** delayed tome update (player has modified inventory) */
	private void trySaveEntity(Player player, EntityInfo info)
	{
		if (info.dataExtractor != null)
			addSavingTome(info, player);
	}
	
	private static int findTomeSlot(ItemStack[] inv, Entity entity)
	{
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null && inv[i].getType() == Material.ENCHANTED_BOOK) {
				if (SummonerTome.isTome(inv[i]) && SummonUtils.getHasSummoned(inv[i]) == entity) {
					return i;
				}
			}
		}
		return -1;
	}
	private boolean isIllegalSummonedEntity(Entity entity)
	{
		if (entity.getPassengers().size() == 0)
			return true;
		Entity passenger = entity.getPassengers().get(0);
		if (!(passenger instanceof Player))
			return true;
		Player p = (Player) passenger;
		if (p.isOnline()) {
			ItemStack[] playerInv = p.getInventory().getContents();
			int slot = findTomeSlot(playerInv, entity);
			return slot < 0;
		}
		return true;
	}
	
	private static boolean saveEntityData(EntityInfo info, Player p)
	{
		if (p.isOnline()) {
			ItemStack[] playerInv = p.getInventory().getContents();
			int slot = findTomeSlot(playerInv, info.summoned);
			if (slot < 0) {
				return false;
			}
			else {
				ItemStack oldTome = playerInv[slot];
				ITomeComponent modifiedComponent = info.dataExtractor.extract(oldTome, info.summoned);
				ItemStack modifiedTome = addOrReplaceComponent(oldTome, modifiedComponent);
				playerInv[slot] = modifiedTome;
				p.getInventory().setContents(playerInv);
				return true;
			}
		}
		return false;
	}
	private static ItemStack addOrReplaceComponent(ItemStack oldTome, ITomeComponent replacedComponent)
	{
		SummonerTome tome = SummonerTome.getTome(oldTome);
	    tome.replaceOrAdd(replacedComponent);
		return tome.setTome(oldTome);
	}
}
