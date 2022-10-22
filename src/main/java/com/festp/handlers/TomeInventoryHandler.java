package com.festp.handlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Boat;
import org.bukkit.entity.ChestBoat;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Entity;
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

import com.festp.components.BoatComponent;
import com.festp.components.BoatData;
import com.festp.components.CustomHorseComponent;
import com.festp.components.HorseFormat;
import com.festp.tome.SummonerTome;
import com.festp.utils.SummonUtils;
import com.festp.utils.UtilsType;

public class TomeInventoryHandler implements Listener
{
	private List<Entity> saveEntity = new ArrayList<>();
	private List<Player> savePlayer = new ArrayList<>();
	
	public void addSavingTome(Entity horse, Player p) {
		saveEntity.add(horse);
		savePlayer.add(p);
	}
	
	public void tick() {
		for (int i = saveEntity.size() - 1; i >= 0; i--) {
			if (!saveEntityData(saveEntity.get(i), savePlayer.get(i)))
				saveEntity.get(i).remove();
			saveEntity.remove(i);
			savePlayer.remove(i);
		}
	}
	@EventHandler
	public void onBoatInventoryClick(InventoryClickEvent event)
	{
		if (!startEvent(event))
			return;
		Inventory topInv = event.getView().getTopInventory();
		if (!(topInv.getHolder() instanceof ChestBoat))
			return;
		trySaveEntity((Player) event.getWhoClicked(), (Entity)topInv.getHolder());
	}
	@EventHandler
	public void onBoatInventoryDrag(InventoryDragEvent event)
	{
		if (!startEvent(event))
			return;
		Inventory topInv = event.getView().getTopInventory();
		if (!(topInv.getHolder() instanceof ChestBoat))
			return;
		trySaveEntity((Player) event.getWhoClicked(), (Entity)topInv.getHolder());
	}

	// Horse slots and move tome to other inventories
	@EventHandler
	public void onHorseInventoryClick(InventoryClickEvent event)
	{
		if (!startEvent(event))
			return;
		
		Inventory topInv = event.getView().getTopInventory();
		if (!(topInv instanceof AbstractHorse))
			return;
		AbstractHorse horse = (AbstractHorse)topInv.getHolder();

		InventoryAction action = event.getAction();
		if (action == InventoryAction.CLONE_STACK)
			return;
		
		int slot = event.getRawSlot();
		if (slot < 0) return;
		
		boolean illegal = false;
		Inventory inv = event.getClickedInventory();
		if (inv instanceof AbstractHorseInventory)
		{
			// removing the saddle is always illegal
			if (slot == 0)
			{
				if (action != InventoryAction.UNKNOWN)
					illegal = true;
			}
			else if (!SummonUtils.isCustomHorse(horse))
			{
				// is saddle/armor or is chested horse inventory
				if (slot < 2 || slot < 17 && horse instanceof ChestedHorse && ((ChestedHorse)horse).isCarryingChest())
					if (action != InventoryAction.UNKNOWN)
						illegal = true;
			}
		}
		// check if try move horse armor to non-custom horse
		else if (inv instanceof PlayerInventory)
		{
			Material m = event.getCurrentItem().getType();
			ItemStack horseArmor = event.getView().getItem(1);
			if ((horseArmor == null || horseArmor.getType() == Material.AIR) && UtilsType.isHorseArmor(m)) {
				if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
					if (!SummonUtils.isCustomHorse(horse))
						illegal = true;
				}
			}
		}

		if (illegal)
			event.setCancelled(true);
		else
			trySaveEntity((Player) event.getWhoClicked(), horse);
	}
	
	@EventHandler
    public void onHorseDrag(InventoryDragEvent event)
	{
		if (!startEvent(event))
			return;
		
		Inventory topInv = event.getView().getTopInventory();
		if (!(topInv instanceof AbstractHorse))
			return;
		AbstractHorse horse = (AbstractHorse)topInv.getHolder();
		
		boolean affectsHorse = false;
		int maxHorseSlot = topInv.getSize();
		for (Integer slot : event.getInventorySlots())
			if (slot < maxHorseSlot) {
				affectsHorse = true;
				break;
			}
		
		boolean illegal = false;
		if (affectsHorse)
		{
			if (!SummonUtils.isCustomHorse(horse))
			{
				illegal = true;
			}
		}

		if (illegal)
			event.setCancelled(true);
		else
			trySaveEntity((Player) event.getWhoClicked(), horse);
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
	private void trySaveEntity(Player player, Entity entity)
	{
		// delayed tome update (player has modified inventory)
		if (SummonUtils.isCustomHorse(entity) || entity instanceof ChestBoat)
			addSavingTome(entity, player);
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
	private static boolean saveEntityData(Entity entity, Player p)
	{
		if (p.isOnline()) {
			ItemStack[] playerInv = p.getInventory().getContents();
			int slot = findTomeSlot(playerInv, entity);
			if (slot < 0) {
				return false;
			}
			else {
				ItemStack modifiedTome = null;
				if (entity instanceof AbstractHorse)
					modifiedTome = setHorseData(playerInv[slot], HorseFormat.fromHorse((AbstractHorse)entity));
				else if (entity instanceof Boat)
					modifiedTome = setBoatData(playerInv[slot], BoatData.fromBoat((Boat)entity));
				playerInv[slot] = modifiedTome;
				p.getInventory().setContents(playerInv);
				return true;
			}
		}
		return false;
	}
	
	// TODO refactor this code
	public static ItemStack setHorseData(ItemStack item, HorseFormat horseData)
	{
		SummonerTome tome = SummonerTome.getTome(item);
	    CustomHorseComponent comp = new CustomHorseComponent();
	    comp.setHorseData(horseData);
	    tome.replaceOrAdd(comp);
		return tome.setTome(item);
	}
	public static ItemStack setBoatData(ItemStack item, BoatData boatData)
	{
		SummonerTome tome = SummonerTome.getTome(item);
	    BoatComponent comp = new BoatComponent();
	    comp.setBoatData(boatData);
	    tome.replaceOrAdd(comp);
		return tome.setTome(item);
	}
}
