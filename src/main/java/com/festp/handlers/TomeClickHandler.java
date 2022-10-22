package com.festp.handlers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.ChestedHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AbstractHorseInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.festp.components.HorseFormat;
import com.festp.tome.SummonerTome;
import com.festp.utils.SummonUtils;
import com.festp.utils.UtilsType;

public class TomeClickHandler implements Listener
{

	private List<AbstractHorse> saveHorse = new ArrayList<>();
	private List<Player> savePlayer = new ArrayList<>();
	
	public void addSavingTome(AbstractHorse horse, Player p) {
		saveHorse.add(horse);
		savePlayer.add(p);
	}
	
	public void tick() {
		for (int i = saveHorse.size() - 1; i >= 0; i--) {
			if (!saveCustomHorse(saveHorse.get(i), savePlayer.get(i)))
				saveHorse.get(i).remove();
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

		SummonerTome tome = SummonerTome.getTome(item);
		if (tome == null) return;
		if (SummonUtils.hasSummoned(item)) return;
		
		Entity entity = event.getRightClicked();
		if (!tome.trySwap(entity))
			return;

		event.setCancelled(true);

		ItemStack updatedTome = tome.setTome(item);
		if (mainHand)
			event.getPlayer().getInventory().setItemInMainHand(updatedTome);
		else
			event.getPlayer().getInventory().setItemInOffHand(updatedTome);
	}
	
	//Summoning
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
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
		
		SummonerTome components = SummonerTome.getTome(item);
		if (components == null) return;

		Entity summoned = components.trySummon(player);
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
	
	// Horse slots and move tome to other inventories
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		if (!startEvent(event))
			return;

		InventoryAction action = event.getAction();
		if (action == InventoryAction.CLONE_STACK)
			return;
		
		int slot = event.getRawSlot();
		if (slot < 0) return;
		
		Inventory topInv = event.getView().getTopInventory();
		AbstractHorse horse = (AbstractHorse)topInv.getHolder();
		
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
			trySaveHorse((Player) event.getWhoClicked(), horse);
	}
	
	@EventHandler
    public void onDrag(InventoryDragEvent event)
	{
		if (!startEvent(event))
			return;

		Inventory topInv = event.getView().getTopInventory();
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
			trySaveHorse((Player) event.getWhoClicked(), horse);
	}
	
	/** Checks if the top inventory is an inventory of a summoned horse. If the horse is illegal, it will be removed. */
	private boolean startEvent(InventoryInteractEvent event)
	{
		if (event.isCancelled())
			return false;

		Inventory topInv = event.getView().getTopInventory();
		if (!(topInv.getHolder() instanceof AbstractHorse))
			return false;
		
		AbstractHorse horse = (AbstractHorse)topInv.getHolder();
		if (!SummonUtils.wasSummoned(horse))
			return false;
		
		if (isIllegalCustomHorse(horse, (Player) horse.getOwner())) {
			event.setCancelled(true);
			horse.remove();
			return false;
		}
		
		return true;
	}
	private void trySaveHorse(Player player, AbstractHorse horse)
	{
		// delayed tome update (player has modified inventory)
		if (SummonUtils.isCustomHorse(horse))
			addSavingTome(horse, player);
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
	private boolean isIllegalCustomHorse(AbstractHorse horse, Player p)
	{
		if (p.isOnline()) {
			ItemStack[] playerInv = p.getInventory().getContents();
			int slot = findTomeSlot(playerInv, horse);
			return slot < 0;
		}
		return true;
	}
	private static boolean saveCustomHorse(AbstractHorse horse, Player p)
	{
		if (p.isOnline()) {
			ItemStack[] playerInv = p.getInventory().getContents();
			int slot = findTomeSlot(playerInv, horse);
			if (slot < 0) {
				return false;
			}
			else {
				ItemStack modifiedTome = SummonerTome.setHorseData(playerInv[slot], HorseFormat.fromHorse(horse));
				playerInv[slot] = modifiedTome;
				p.getInventory().setContents(playerInv);
				return true;
			}
		}
		return false;
	}
}
