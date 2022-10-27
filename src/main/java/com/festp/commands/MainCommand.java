package com.festp.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import com.festp.handlers.TomeItemHandler;
import com.festp.tome.TomeType;
import com.festp.utils.UtilsType;
import com.google.common.collect.Lists;

public class MainCommand  implements CommandExecutor, TabCompleter
{
	public final static String COMMAND = "sumtome";
	String COMMAND_USAGE = ChatColor.GRAY+"Usage: /" + COMMAND + " "+ChatColor.GRAY+"type";
	String COMMAND_EXAMPLES = ChatColor.GRAY + "Example:\n"
			+ "  /" + COMMAND + " boat";
	
	private final static String NO_PLAYER = ChatColor.RED + "You must be a player to perform this command.";
	private final static String NO_OP = ChatColor.RED + "You must be an operator to perform this command.";
	private final static String NO_SPACE = ChatColor.RED + "There are no space in the inventory.";

	private static Map<Material, Boolean> isTransparent = new HashMap<>(); // entity can stay in (no liquids)
	private static Map<Material, Boolean> isSolid = new HashMap<>(); // entity can stay on (no liquids)

	// errors: gates(opened/closed)
	private static void checkBlock(Block b)
	{
		Material m = b.getType();
		if (isTransparent.containsKey(m))
			return;
		
		double eps = 0.001;
		Vector[] localStarts = new Vector[] {
				new Vector(eps, eps, eps), new Vector(1 - eps, eps, eps),
				new Vector(eps, 1 - eps, eps), new Vector(1 - eps, 1 - eps, eps),
				new Vector(0, 0.5, 0.5), new Vector(0.5, 0, 0.5), new Vector(0.5, 0.5, 0) };
		Vector[] directions = new Vector[] {
				new Vector(1, 1, 1), new Vector(-1, 1, 1),
				new Vector(1, -1, 1), new Vector(-1, -1, 1),
				new Vector(1, 0, 0), new Vector(0, 1, 0), new Vector(0, 0, 1)};
		double sqrt3 = Math.sqrt(3);
		double[] dists = new double[] { sqrt3, sqrt3, sqrt3, sqrt3, 1, 1, 1 };
		for (int i = 0; i < localStarts.length; i++)
		{
			Location start = b.getLocation().add(localStarts[i]);
			double dist = dists[i] - 2 * eps;
			RayTraceResult result = b.getWorld().rayTraceBlocks(start, directions[i], dist, FluidCollisionMode.NEVER, true);
			if (result != null) {
				isSolid.put(m, true);
				isTransparent.put(m, false);
				return;
			}
			else {
				result = b.getWorld().rayTraceBlocks(start, directions[i], dist, FluidCollisionMode.NEVER, false);
				if (result != null || UtilsType.isAir(m)) {
					isTransparent.put(m, true);
					isSolid.put(m, false);
					return;
				}
			}
		}
		isTransparent.put(m, false);
		isSolid.put(m, false);
	}
	
	List<String> enNames = new ArrayList<>();
	
	public MainCommand() {
		// TODO localization
		// TODO if 1.15-, remove "strider"
		enNames = Lists.asList("", new String[] { "minecart", "boat", "horse", "strider", "custom_horse", "all", "custom_all" });
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

			Player p = (Player)sender;
			if (name.equals("scan")) {
				Block b = p.getLocation().getBlock();
				int yMin = Math.max(p.getWorld().getMinHeight() - b.getY(), -10);
				int yMax = Math.min(p.getWorld().getMaxHeight() - 1 - b.getY(), 10);
				for (int dy = yMin; dy <= yMax; dy++)
					for (int dx = -10; dx <= 10; dx++)
						for (int dz = -10; dz <= 10; dz++)
							checkBlock(b.getRelative(dx, dy, dz));
				
				sender.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Transparent | Solid:");
				for (Entry<Material, Boolean> entry : isTransparent.entrySet())
					sender.sendMessage(ChatColor.GREEN + "    " + entry.getValue() + " | " + isSolid.get(entry.getKey()) + " : " + entry.getKey());
				return true;
			}

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
