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
import com.festp.components.ITomeComponent;
import com.festp.crafting.TomeItemBuilder;
import com.festp.tome.ComponentManager;
import com.festp.tome.MissingComponent;
import com.festp.utils.Utils;

public class MainCommand  implements CommandExecutor, TabCompleter
{
	private final static String SUBCOMMAND_GET = "get";
	
	public final static String COMMAND = "tome";
	private final static String COMMAND_USAGE = ChatColor.GRAY+"Usage: /" + COMMAND + " " + SUBCOMMAND_GET + " "+ChatColor.GRAY+"type";
	private final static String COMMAND_EXAMPLES = ChatColor.GRAY + "Example:\n"
			+ "  /" + COMMAND + " " + SUBCOMMAND_GET + " boat";
	
	private final static String CODE_ALL = "all";
	private final static String CODE_CUSTOM_ALL = "custom_all";
	
	private final static String NO_PERM = ChatColor.RED + "You must have " + ChatColor.WHITE + "%s" + ChatColor.RED + " permission to perform this command.";
	private final static String NO_PLAYER = ChatColor.RED + "You must be a player to perform this command.";
	private final static String NO_ITEM = ChatColor.RED + "Couldn't give the tome item, wrong type: %s";
	private final static String NO_SPACE = ChatColor.RED + "There are no space in the inventory.";
	
	ComponentManager componentManager;
	
	List<String> aliases = new ArrayList<>();
	
	public MainCommand(ComponentManager componentManager) {
		this.componentManager = componentManager;
		// TODO localization
		// name can't contain spaces
		aliases.add(CODE_ALL);
		aliases.add(CODE_CUSTOM_ALL);
		for (String name : componentManager.getLoadedComponents())
			if (!name.contains(" "))
				aliases.add(name);
	}

	/** /tome get {@literal<type>} */
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
			
			ItemStack item = getItem(getComponentString(args));
			if (item == null) {
				sender.sendMessage(String.format(NO_ITEM, "not implemented"));
				return true;
			}
			
			// give item
			PlayerInventory playerInv = p.getInventory();
			if (playerInv.getItemInMainHand() == null || playerInv.getItemInMainHand().getType() == Material.AIR)
				playerInv.setItemInMainHand(item);
			else if (playerInv.getItemInOffHand() == null || playerInv.getItemInOffHand().getType() == Material.AIR)
				playerInv.setItemInOffHand(item);
			else {
				for (int i = 0; i < 36; i++) {
					if (playerInv.getItem(i) == null) {
						playerInv.setItem(i, item);
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
		if (!(sender instanceof Player) || !sender.hasPermission(Permissions.GET))
			return options;

		if (args.length == 1)
		{
			options.add(SUBCOMMAND_GET);
		}
		
		if (args.length >= 2)
		{
			if (args[0].equalsIgnoreCase(SUBCOMMAND_GET)) {
				String[] components = getComponents(getComponentString(args));
				String arg = args[args.length - 1].toLowerCase();
				for (String tomeAlias : aliases)
					if (tomeAlias.contains(arg)) {
						if (Utils.containsIgnoreCase(components, tomeAlias))
							continue;
						if (tomeAlias == CODE_ALL || tomeAlias == CODE_CUSTOM_ALL) {
							String[] componentList;
							if (tomeAlias == CODE_ALL)
								componentList = componentManager.getAll();
							else
								componentList = componentManager.getCustomAll();
							/*boolean containsList = true;
							for (String component : componentList)
								if (!Utils.containsIgnoreCase(components, component))
									containsList = false;
							if (containsList)
								continue;*/
							boolean intersectsList = false;
							for (String component : componentList)
								if (Utils.containsIgnoreCase(components, component))
									intersectsList = true;
							if (intersectsList)
								continue;
							// may be check incompatible components
						}
						options.add(tomeAlias);
					}
			}
		}
		return options;
	}

	// all/custom_all or <list>, e.g. "minecart boat"
	private ItemStack getItem(String str)
	{
		str = str.toLowerCase();
		String[] componentNames;
		if (str.equalsIgnoreCase(CODE_ALL)) {
			componentNames = componentManager.getAll();
		} else if (str.equalsIgnoreCase(CODE_CUSTOM_ALL)) {
			componentNames = componentManager.getCustomAll();
		} else {
			componentNames = getComponents(str);
		}
		ITomeComponent[] components = new ITomeComponent[componentNames.length];
		for (int i = 0; i < componentNames.length; i++) {
			components[i] = componentManager.fromCode(componentNames[i]);
			if (components[i] instanceof MissingComponent)
				return null;
		}
		
		return TomeItemBuilder.getNewTome(components);
	}

	
	private static String getComponentString(String[] args) {
		String res = "";
		if (args.length >= 2) {
			res = args[1];
			for (int i = 2; i < args.length; i++)
				res += " " + args[i];
		}
		return res;
	}
	
	private static String[] getComponents(String str) {
		return str.split(" ");
	}
}
