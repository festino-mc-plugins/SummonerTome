package com.festp.tome;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Boat;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AbstractHorseInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.festp.tome.TomeItemHandler.TomeType;
import com.festp.utils.UtilsType;
import com.festp.utils.UtilsWorld;

public class TomeClickHandler implements Listener {

	public static final double searching_radius_minecart_tome = 1.5;
	public static final double searching_radius_boat_tome = 2.5;

	private List<AbstractHorse> saveHorse = new ArrayList<>();
	private List<Player> savePlayer = new ArrayList<>();
	
	public void addSavingTome(AbstractHorse horse, Player p) {
		saveHorse.add(horse);
		savePlayer.add(p);
	}
	
	public void tick() {
		for (int i = saveHorse.size() - 1; i >= 0; i--) {
			processCustomHorse(saveHorse.get(i), savePlayer.get(i));
			saveHorse.remove(i);
			savePlayer.remove(i);
		}
	}
	
	//Customization
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) //PlayerInteractAtEntityEvent
	{
		if (SummonUtils.wasSummoned(event.getRightClicked())) return;
		
		boolean mainHand = true;
		ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		if (item == null) {
			mainHand = false;
			item = event.getPlayer().getInventory().getItemInOffHand();
			if (item == null)
				return;
		}

		TomeType type = TomeFormatter.getTomeType(item);
		if (type == null) return;
		if (SummonUtils.hasSummoned(item)) return;
		
		Entity entity = event.getRightClicked();
		if (entity instanceof Boat && (type == TomeType.BOAT || type == TomeType.ALL || type == TomeType.CUSTOM_ALL)
				&& TomeFormatter.getBoatType(item) != ((Boat)entity).getWoodType()) {
			TreeSpecies prevWood = TomeFormatter.getBoatType(item);
			if (mainHand)
				event.getPlayer().getInventory().setItemInMainHand(TomeFormatter.setBoatType(item, ((Boat)entity).getWoodType()));
			else
				event.getPlayer().getInventory().setItemInOffHand(TomeFormatter.setBoatType(item, ((Boat)entity).getWoodType()));
			((Boat)entity).setWoodType(prevWood);
			event.setCancelled(true);
		}
		else if (entity instanceof AbstractHorse && (type == TomeType.CUSTOM_HORSE || type == TomeType.CUSTOM_ALL)) {
			AbstractHorse horse = (AbstractHorse) entity;
			if (horse.getInventory().getSaddle() != null) {
				HorseFormat oldData = TomeFormatter.getHorseData(item);
				HorseFormat newData = HorseFormat.fromHorse(horse);
				if (oldData == null)
					horse.remove();
				else
					oldData.applyToHorse(horse);
				
				ItemStack updatedTome = TomeFormatter.setHorseData(item, newData);
				if (mainHand)
					event.getPlayer().getInventory().setItemInMainHand(updatedTome);
				else
					event.getPlayer().getInventory().setItemInOffHand(updatedTome);
				event.setCancelled(true);
			}
		}
	}
	
	//Summoning
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getPlayer().isInsideVehicle()) return;
		if (!(event.getAction() == Action.RIGHT_CLICK_AIR
				|| event.getAction() == Action.RIGHT_CLICK_BLOCK && !UtilsType.isInteractable(event.getClickedBlock().getType()) ))
			return;

		boolean inMainHand = true;
		ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		if (item == null) {
			inMainHand = false;
			item = event.getPlayer().getInventory().getItemInOffHand();
			if (item == null)
				return;
		}
		TomeType type = TomeFormatter.getTomeType(item);
		if (type == null) return;

		Location playerLoc = event.getPlayer().getLocation();
		if (type == TomeType.MINECART) {
			Location l = SummonUtils.tryFindForMinecart(playerLoc, searching_radius_minecart_tome);
			if (l == null) return;
			SummonUtils.summonMinecart(l, event.getPlayer(), inMainHand);
		}
		else if (type == TomeType.BOAT) {
			Location l = SummonUtils.tryFindForBoat(playerLoc, searching_radius_boat_tome);
			if (l == null) return;
			SummonUtils.summonBoat(l, event.getPlayer(), inMainHand, TomeFormatter.getBoatType(item));
		}
		else if (type == TomeType.HORSE) {
			Location l = SummonUtils.tryFindForHorse(playerLoc);
			if (l == null) return;
			l.setY(playerLoc.getY());
			SummonUtils.summonHorse(l, event.getPlayer(), inMainHand);
		}
		else if (type == TomeType.CUSTOM_HORSE) {
			Location l = UtilsWorld.findHorseSpace(playerLoc);
			if (l == null) return;
			l.setY(playerLoc.getY());
			SummonUtils.summonCustomHorse(l, event.getPlayer(), inMainHand);
		}
		else if (type == TomeType.ALL || type == TomeType.CUSTOM_ALL)
		{
			Location locMc = SummonUtils.tryFindForMinecart(playerLoc, searching_radius_minecart_tome);
			Location locBoat = SummonUtils.tryFindForBoat(playerLoc, searching_radius_boat_tome);
			TreeSpecies boatWood = TomeFormatter.getBoatType(item);
			if (locMc != null && locBoat != null) {
				if (playerLoc.distanceSquared(locMc) < playerLoc.distanceSquared(locBoat)) {
					SummonUtils.summonMinecart(locMc, event.getPlayer(), inMainHand);
				}
				else {
					SummonUtils.summonBoat(locBoat, event.getPlayer(), inMainHand, boatWood);
				}
			}
			else if (locMc != null && locBoat == null) {
				SummonUtils.summonMinecart(locMc, event.getPlayer(), inMainHand);
			}
			else if (locMc == null && locBoat != null) {
				SummonUtils.summonBoat(locBoat, event.getPlayer(), inMainHand, boatWood);
			}
			else {
				Location locHorse = UtilsWorld.findHorseSpace(playerLoc);
				if (locHorse == null)
					return;
					
				if (type == TomeType.ALL)
					SummonUtils.summonHorse(locHorse, event.getPlayer(), inMainHand);
				else
					SummonUtils.summonCustomHorse(locHorse, event.getPlayer(), inMainHand);
			}
		}
	}
	

	
	//Horse slots and move tome to other inventories
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.isCancelled()) return;
		
		if (event.getView().getTopInventory().getHolder() instanceof AbstractHorse) {
			Player p = (Player) event.getWhoClicked();
			AbstractHorse horse = (AbstractHorse)event.getView().getTopInventory().getHolder();
			if (SummonUtils.wasSummoned(horse))
				addSavingTome(horse, p);
		}
		
		int slot = event.getRawSlot();
		if (slot < 0) return;
		
		boolean illegal = false;
		Inventory inv = event.getClickedInventory();
		InventoryAction action = event.getAction();
		
		if (inv instanceof AbstractHorseInventory) {
			AbstractHorseInventory hinv = (AbstractHorseInventory) inv;
			AbstractHorse horse = (AbstractHorse) hinv.getHolder();
			
			if (horse != null && isSummonable(horse) && SummonUtils.wasSummoned(horse))
				if (slot == 0)
				{
					if(action != InventoryAction.CLONE_STACK && action != InventoryAction.UNKNOWN)
						illegal = true;
				}
				else if (!SummonUtils.isCustomHorse((AbstractHorse) hinv.getHolder())) {
					if (slot < 2 || slot < 17 && hinv.getHolder() instanceof ChestedHorse && ((ChestedHorse)hinv.getHolder()).isCarryingChest())
						if (action != InventoryAction.CLONE_STACK && action != InventoryAction.UNKNOWN)
							illegal = true;
				}
				else {
					//change custom tome
					illegal = isIllegalCustomHorse(horse, (Player) horse.getOwner());
				}
		}
		else if (inv instanceof PlayerInventory && event.getView().getTopInventory() instanceof AbstractHorseInventory
				&& SummonUtils.wasSummoned((Entity) event.getView().getTopInventory().getHolder())) {
			Material m = event.getCurrentItem().getType();
			if ( (event.getView().getItem(1) == null || event.getView().getItem(1).getType() == Material.AIR)
					&& UtilsType.isHorseArmor(m)) {
					if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
						if (SummonUtils.isCustomHorse((AbstractHorse)event.getView().getTopInventory().getHolder())) {
							AbstractHorse horse = (AbstractHorse)event.getView().getTopInventory().getHolder();
							illegal = isIllegalCustomHorse(horse, (Player) horse.getOwner());
						}
						else
							illegal = true;
					}
				}
		}
		
		if (illegal)
			event.setCancelled(true);
	}
	
	private static int findTomeSlot(ItemStack[] inv, Entity entity) {
		for (int i = 0; i < inv.length; i++) {
			if (inv[i] != null && inv[i].getType() == Material.ENCHANTED_BOOK) {
				if (TomeFormatter.isTome(inv[i]) && SummonUtils.getHasSummoned(inv[i]) == entity) {
					return i;
				}
			}
		}
		return -1;
	}
	private boolean isIllegalCustomHorse(AbstractHorse horse, Player p) {
		if (p.isOnline()) {
			int slot = findTomeSlot(p.getInventory().getContents(), horse);
			if (slot < 0) {
				horse.remove();
				return true;
			}
			else {
				addSavingTome(horse, p);
				return false;
			}
		}
		else
			return true;
	}
	private static void processCustomHorse(AbstractHorse horse, Player p) {
		if (p.isOnline()) {
			int slot = findTomeSlot(p.getInventory().getContents(), horse);
			if (slot < 0) {
				horse.remove();
			}
			else {
				ItemStack[] playerInv = p.getInventory().getContents();
				ItemStack modifiedTome = TomeFormatter.setHorseData(playerInv[slot], HorseFormat.fromHorse(horse));
				playerInv[slot] = modifiedTome;
				p.getInventory().setContents(playerInv);
			}
		}
	}
	
	private static boolean isSummonable(Entity e) {
		return e instanceof Boat || e instanceof Minecart || e instanceof AbstractHorse;
	}
}
