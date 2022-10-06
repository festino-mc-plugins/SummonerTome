package com.festp.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import com.festp.tome.TomeItemHandler;
import com.festp.tome.TomeItemHandler.TomeType;
import com.google.common.collect.Lists;

public class MainCommand  implements CommandExecutor, TabCompleter {
	
	public final static String COMMAND = "sumtome";
	String COMMAND_USAGE = ChatColor.GRAY+"Usage: /" + COMMAND + " "+ChatColor.GRAY+"type";
	String COMMAND_EXAMPLES = ChatColor.GRAY + "Example:\n"
			+ "  /" + COMMAND + " boat";
	
	List<String> enNames = new ArrayList<>();
	List<String> ruNames = new ArrayList<>();
	
	public MainCommand() {
		enNames = Lists.asList("", new String[] { "minecart", "boat", "horse", "custom_horse", "all", "custom_all" });
		ruNames = Lists.asList("", new String[] { "вагонетки", "лодки", "коня", "любого_коня", "всего", "любого_всего" });
	}

	// TODO any combination of vehicles
	private static ItemStack getItem(String name)
	{
		name = name.toLowerCase();
		ItemStack item = null;
		if (name.equalsIgnoreCase("minecart") || name.equalsIgnoreCase("вагонет")) {
			item = TomeItemHandler.getTome(TomeType.MINECART);
		} else if (name.contains("boat") || name.equalsIgnoreCase("лод")) {
			item = TomeItemHandler.getTome(TomeType.BOAT);
		} else if (name.equalsIgnoreCase("horse") || name.equalsIgnoreCase("кон")) {
			item = TomeItemHandler.getTome(TomeType.HORSE);
		} else if (name.equalsIgnoreCase("horse") || name.equalsIgnoreCase("любого_кон")) {
			item = TomeItemHandler.getTome(TomeType.CUSTOM_HORSE);
		} else if (name.equalsIgnoreCase("all") || name.equalsIgnoreCase("всего")) {
			item = TomeItemHandler.getTome(TomeType.ALL);
		} else if (name.equalsIgnoreCase("custom_all") || name.equalsIgnoreCase("любого_всего")) {
			item = TomeItemHandler.getTome(TomeType.CUSTOM_ALL);
		}
		return item;
	}

	/** /sumtome {@literal<type>} */
	@EventHandler
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args)
	{
		if (!cmd.getName().equalsIgnoreCase(COMMAND))
			return false;

		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You must be a player to perform this command.");
			return false;
		}
		if (!sender.isOp()) {
			sender.sendMessage(ChatColor.RED + "You must be an operator to perform this command.");
			return false;
		}
		if (args.length > 0) {
			//Material
			String name = args[0];
			if (name.equals("?")) {
				sender.sendMessage(COMMAND_USAGE);
				sender.sendMessage(COMMAND_EXAMPLES);
				return true;
			}

			ItemStack item = getItem(name);
			
			// give item
			Player p = (Player)sender;
			PlayerInventory player_inv = p.getInventory();
			if (player_inv.getItemInMainHand() == null || player_inv.getItemInMainHand().getType() == Material.AIR)
				player_inv.setItemInMainHand(item);
			else if (player_inv.getItemInOffHand() == null || player_inv.getItemInOffHand().getType() == Material.AIR)
				player_inv.setItemInOffHand(item);
			else {
				for (int i = 0; i < 36; i++) {
					if (player_inv.getItem(i) == null) {
						player_inv.setItem(i, item);
						return true;
					}
				}
				sender.sendMessage(ChatColor.RED + "There are no space in the inventory.");
				return false;
			}
			return true;
		}
		sender.sendMessage(COMMAND_USAGE);
		sender.sendMessage(COMMAND_EXAMPLES);
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args)
	{
		List<String> options = new ArrayList<>();
		if (!(sender instanceof Player) || !sender.isOp())
			return options;
		
		if (args.length == 1) {
			String arg = args[0].toLowerCase();
			for (String itemName : enNames)
				if (itemName.contains(arg))
					options.add(itemName);
			for (String itemName : ruNames)
				if (itemName.contains(arg))
					options.add(itemName);
		}
		return options;
	}
}
