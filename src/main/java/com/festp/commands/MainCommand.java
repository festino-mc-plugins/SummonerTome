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

import com.festp.Permissions;
import com.festp.handlers.TomeItemHandler;
import com.festp.tome.TomeType;
import com.google.common.collect.Lists;

public class MainCommand  implements CommandExecutor, TabCompleter
{
	private final static String SUBCOMMAND_GET = "get";
	
	public final static String COMMAND = "tome";
	private final static String COMMAND_USAGE = ChatColor.GRAY+"Usage: /" + COMMAND + " " + SUBCOMMAND_GET + " "+ChatColor.GRAY+"type";
	private final static String COMMAND_EXAMPLES = ChatColor.GRAY + "Example:\n"
			+ "  /" + COMMAND + " " + SUBCOMMAND_GET + " boat";
	
	private final static String NO_PERM = ChatColor.RED + "You must have " + ChatColor.WHITE + "%s" + ChatColor.RED + " permission to perform this command.";
	private final static String NO_PLAYER = ChatColor.RED + "You must be a player to perform this command.";
	private final static String NO_SPACE = ChatColor.RED + "There are no space in the inventory.";
	
	List<String> enNames = new ArrayList<>();
	
	public MainCommand() {
		// TODO localization
		// TODO if 1.15-, remove "strider"
		enNames = Lists.asList("", new String[] { "minecart", "boat", "strider", "pig", "horse", "custom_horse", "all", "custom_all" });
	}

	// TODO any combination of vehicles
	// all/custom_all or <list>, e.g. "minecart, boat" / "minecart boat"
	private static ItemStack getItem(String name)
	{
		name = name.toLowerCase();
		ItemStack item = null;
		if (name.equalsIgnoreCase("minecart")) {
			item = TomeItemHandler.getNewTome(TomeType.MINECART);
		} else if (name.equalsIgnoreCase("boat")) {
			item = TomeItemHandler.getNewTome(TomeType.BOAT);
		} else if (name.equalsIgnoreCase("strider")) {
			item = TomeItemHandler.getNewTome(TomeType.STRIDER);
		} else if (name.equalsIgnoreCase("pig")) {
			item = TomeItemHandler.getNewTome(TomeType.PIG);
		} else if (name.equalsIgnoreCase("horse")) {
			item = TomeItemHandler.getNewTome(TomeType.HORSE);
		} else if (name.equalsIgnoreCase("custom_horse")) {
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
		if (args.length == 0 || args[0].equals("?")) {
			sender.sendMessage(COMMAND_USAGE);
			sender.sendMessage(COMMAND_EXAMPLES);
			return true;
		}
		String subcommand = args[0];
		if (subcommand.equalsIgnoreCase(SUBCOMMAND_GET))
		{
			if (!sender.hasPermission(Permissions.GET)) {
				sender.sendMessage(String.format(NO_PERM, Permissions.GET));
				return false;
			}
			
			if (!(sender instanceof Player)) {
				sender.sendMessage(NO_PLAYER);
				return false;
			}
			Player p = (Player)sender;
			
			String name = args[1];
			ItemStack item = getItem(name);
			
			// give item
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
		/*if (name.equals("scan")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(NO_PLAYER);
				return false;
			}
			Player p = (Player)sender;
			
			Block b = p.getLocation().getBlock();
			int yMin = Math.max(p.getWorld().getMinHeight() - b.getY(), -10);
			int yMax = Math.min(p.getWorld().getMaxHeight() - 1 - b.getY(), 10);
			for (int dy = yMin; dy <= yMax; dy++)
				for (int dx = -10; dx <= 10; dx++)
					for (int dz = -10; dz <= 10; dz++)
						UtilsType.isTransparent(b.getRelative(dx, dy, dz));
			
			sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Transparent | Solid:");
			for (Entry<Material, Boolean> entry : UtilsType.getIsTransparent().entrySet())
				sender.sendMessage(ChatColor.GREEN + "    " + entry.getValue() + " | " + UtilsType.getIsSolid().get(entry.getKey()) + " : " + entry.getKey());
			return true;
	    }*/
		
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
