package com.festp;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.festp.commands.MainCommand;
import com.festp.handlers.TomeClickHandler;
import com.festp.handlers.TomeEntityHandler;
import com.festp.handlers.TomeInventoryHandler;
import com.festp.utils.NBTUtils;

public class Main extends JavaPlugin
{
	CraftManager craftManager;
	
	long t1;
	public void onEnable()
	{
		Logger.setLogger(getLogger());
    	PluginManager pm = getServer().getPluginManager();
    	
    	NBTUtils.setPlugin(this);
		
    	craftManager = new CraftManager(this, getServer());
    	craftManager.registerEvents(pm);
    	craftManager.addCrafts();
    	
    	MainCommand command = new MainCommand();
    	getCommand(MainCommand.COMMAND).setExecutor(command);
    	
    	TomeClickHandler clickHandler = new TomeClickHandler();
    	pm.registerEvents(clickHandler, this);
    	TomeInventoryHandler inventoryHandler = new TomeInventoryHandler();
    	pm.registerEvents(inventoryHandler, this);
    	TomeEntityHandler entityHandler = new TomeEntityHandler();
    	pm.registerEvents(entityHandler, this);
    	
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this,
			new Runnable() {
				public void run() {
					TaskList.tick();
					
					// save boat and horse data to tomes
					inventoryHandler.tick();
				}
			}, 0L, 1L);
		
	}
}
