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

import com.festp.handlers.TomeItemHandler;
import com.festp.tome.TomeType;
import com.google.common.collect.Lists;

public class MainCommand  implements CommandExecutor, TabCompleter {
	
	public final static String COMMAND = "sumtome";
	String COMMAND_USAGE = ChatColor.GRAY+"Usage: /" + COMMAND + " "+ChatColor.GRAY+"type";
	String COMMAND_EXAMPLES = ChatColor.GRAY + "Example:\n"
			+ "  /" + COMMAND + " boat";
	
	private final static String NO_PLAYER = ChatColor.RED + "You must be a player to perform this command.";
	private final static String NO_OP = ChatColor.RED + "You must be an operator to perform this command.";
	private final static String NO_SPACE = ChatColor.RED + "There are no space in the inventory.";

	List<String> enNames = new ArrayList<>();
	
	public MainCommand() {
		enNames = Lists.asList("", new String[] { "minecart", "boat", "horse", "custom_horse", "all", "custom_all" });
	}

	// TODO any combination of vehicles
	// all/custom_all or {<list>}, e.g. {minecart, boat}
	private static ItemStack getItem(String name)
	{
		name = name.toLowerCase();
		ItemStack item = null;
		if (name.equalsIgnoreCase("minecart")) {
			item = TomeItemHandler.getNewTome(TomeType.MINECART);
		} else if (name.equalsIgnoreCase("boat")) {
			item = TomeItemHandler.getNewTome(TomeType.BOAT);
		} else if (name.equalsIgnoreCase("horse")) {
			item = TomeItemHandler.getNewTome(TomeType.HORSE);
		} else if (name.contains("horse")) {
			item = TomeItemHandler.getNewTome(TomeType.CUSTOM_HORSE);
		} else if (name.equalsIgnoreCase("all")) {
			item = TomeItemHandler.getNewTome(TomeType.getAll());
		} else if (name.equalsIgnoreCase("custom_all")) {
			item = TomeItemHandler.getNewTome(TomeType.getCustomAll());
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
			sender.sendMessage(NO_PLAYER);
			return false;
		}
		if (!sender.isOp()) {
			sender.sendMessage(NO_OP);
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
				sender.sendMessage(NO_SPACE);
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
		}
		return options;
	}
}
