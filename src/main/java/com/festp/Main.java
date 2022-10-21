package com.festp;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.festp.commands.MainCommand;
import com.festp.handlers.TomeClickHandler;
import com.festp.handlers.TomeCraftHandler;
import com.festp.handlers.TomeEntityHandler;
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
    	
    	MainCommand command = new MainCommand();
    	getCommand(MainCommand.COMMAND).setExecutor(command);
    	
    	TomeCraftHandler craftHandler = new TomeCraftHandler();
    	pm.registerEvents(craftHandler, this);
    	TomeClickHandler clickHandler = new TomeClickHandler();
    	pm.registerEvents(clickHandler, this);
    	TomeEntityHandler entityHandler = new TomeEntityHandler();
    	pm.registerEvents(entityHandler, this);
    	
    	craftManager.addCrafts();
    	pm.registerEvents(craftManager, this);
    	
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this,
			new Runnable() {
				public void run() {
					TaskList.tick();
					
					//save horse data to tome
					clickHandler.tick();
				}
			}, 0L, 1L);
		
	}
}
