package com.festp.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.festp.config.Config;
import com.festp.config.IConfig;
import com.festp.config.LangConfig;
import com.festp.crafting.TomeItemBuilder;
import com.festp.tome.ComponentManager;
import com.festp.tome.MissingComponent;
import com.festp.utils.Utils;

public class MainCommand  implements CommandExecutor, TabCompleter
{
	public final static String COMMAND = "tome";
	private final static String SUBCOMMAND_GET = "get";
	private final static String SUBCOMMAND_CONFIG = "config";
	private final static String CONFIG_RELOAD = "reload";
	
	private final static String CODE_ALL = "all";
	private final static String CODE_CUSTOM_ALL = "custom_all";
	
	private final static String COMMAND_USAGE = ChatColor.GRAY+"Usage: /" + COMMAND + " " + SUBCOMMAND_GET + " "+ChatColor.GRAY+"type";
	private final static String COMMAND_EXAMPLES = ChatColor.GRAY + "Example:"
			+ "\n  /" + COMMAND + " " + SUBCOMMAND_GET + " boat"
			+ "\n  /" + COMMAND + " " + SUBCOMMAND_CONFIG + " " + Config.Key.EFFECTS_PLAYSOUND.toString() + " true";

	IConfig config;
	LangConfig lang;
	ComponentManager componentManager;
	
	List<String> aliases = new ArrayList<>();
	
	public MainCommand(IConfig config, LangConfig lang, ComponentManager componentManager) {
		this.config = config;
		this.lang = lang;
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
				sender.sendMessage(String.format(lang.command_noPerm, Permissions.GET));
				return false;
			}
			
			if (!(sender instanceof Player)) {
				sender.sendMessage(lang.get_noPlayer);
				return false;
			}
			Player p = (Player)sender;
			
			GetItemResult itemRes = getItem(getComponentString(args));
			ItemStack item = itemRes.getItem();
			if (item == null) {
				sender.sendMessage(String.format(lang.get_components_error, itemRes.getMissingComponentCode()));
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
				sender.sendMessage(lang.get_space_error);
				return false;
			}
			return true;
		}
		if (subcommand.equalsIgnoreCase(SUBCOMMAND_CONFIG))
		{
			if (!sender.hasPermission(Permissions.CONFIGURE)) {
				sender.sendMessage(String.format(lang.command_noPerm, Permissions.CONFIGURE));
				return false;
			}
			if (args.length == 1) {
				sender.sendMessage(lang.command_noArgs);
				return true;
			}
			
			IConfig.Key key = null;
			for (IConfig.Key k : getAllowedKeys())
				if (k.toString().equalsIgnoreCase(args[1])) {
					key = k;
					break;
				}
			if (key != null)
			{
				if (args.length == 2) {
					sender.sendMessage(String.format(lang.config_getOk, key.toString(), config.get(key)));
					return true;
				}
				Object val = key.validateValue(args[2]);
				if (val == null) {
					sender.sendMessage(String.format(lang.config_value_error, key));
					return false;
				}
				
				config.set(key, val);
				sender.sendMessage(String.format(lang.config_setOk, key.toString(), val));
				
				return true;
			}
			else if (args[1].equalsIgnoreCase(CONFIG_RELOAD))
			{
				config.load();
				lang.load();
				sender.sendMessage(lang.config_reloadOk);
				return true;
			}
			
			sender.sendMessage(String.format(lang.config_key_error, args[1]));
			
			return true;
		}
		/*if (subcommand.equals("scan")) {
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

	private static class GetItemResult {
		private final ItemStack item;
		private final String missingComponentCode;
		
		public GetItemResult(ItemStack item, String missingComponentCode) {
			this.item = item;
			this.missingComponentCode = missingComponentCode;
		}
		public ItemStack getItem() {
			return item;
		}
		public String getMissingComponentCode() {
			return missingComponentCode;
		}
	}
	// all/custom_all or <list>, e.g. "minecart boat"
	private GetItemResult getItem(String str)
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
				return new GetItemResult(null, componentNames[i]);
		}
		
		ItemStack item = TomeItemBuilder.getNewTome(components);
		return new GetItemResult(item, null);
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
	
	
	
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args)
	{
		List<String> options = new ArrayList<>();

		if (args.length == 1)
		{
			options.add(SUBCOMMAND_GET);
			options.add(SUBCOMMAND_CONFIG);
		}
		
		if (args.length >= 2)
		{
			if (args[0].equalsIgnoreCase(SUBCOMMAND_GET)) {
				if (!(sender instanceof Player) || !sender.hasPermission(Permissions.GET)) {
					return options;
				}
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
			else if (args[0].equalsIgnoreCase(SUBCOMMAND_CONFIG)) {
				if (!sender.hasPermission(Permissions.CONFIGURE)) {
					return options;
				}
				
				if (args.length == 2)
				{
					String arg = args[1].toLowerCase();
					if (CONFIG_RELOAD.startsWith(arg))
						options.add(CONFIG_RELOAD);
					for (IConfig.Key k : getAllowedKeys()) {
						String name = k.toString();
						if (name.startsWith(arg)) {
							int endIndex = name.indexOf(".", arg.length()) + 1;
							if (endIndex == 0)
								endIndex = name.length();
							options.add(name.substring(0, endIndex));
						}
					}
				}
				else if (args.length == 3)
				{
					String arg = args[1].toLowerCase();
					for (IConfig.Key k : getAllowedKeys())
						if (k.toString().equalsIgnoreCase(arg)) {
							if (k.getValueClass() == Boolean.class) {
								options.add("true");
								options.add("false");
							} else {
								options.add(k.getDefault() == null ? "null" : k.getDefault().toString());
							}
							break;
						}
				}
			}
		}
		return options;
	}
	
	private List<IConfig.Key> getAllowedKeys()
	{
		List<IConfig.Key> res = new ArrayList<>();
		Set<String> leafPrefixes = new HashSet<>();
		for (String name : config.getKeys()) {
			res.add(config.getKey(name));
			if (name.contains(".")) {
				leafPrefixes.add(name.substring(0, name.lastIndexOf(".")));
			}
		}
		// remove all non-leaf nodes
		for (int i = res.size() - 1; i >= 0; i--) {
			IConfig.Key k = res.get(i);
			if (leafPrefixes.contains(k.toString()))
				res.remove(i);
		}
		return res;
	}
}
