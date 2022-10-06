package com.festp;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.festp.commands.MainCommand;
import com.festp.tome.TomeClickHandler;
import com.festp.tome.TomeEntityHandler;
import com.festp.tome.TomeItemHandler;
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
    	
    	TomeItemHandler summonerTomes = new TomeItemHandler();
    	pm.registerEvents(summonerTomes, this);
    	TomeClickHandler clickTomes = new TomeClickHandler();
    	pm.registerEvents(clickTomes, this);
    	TomeEntityHandler entityTomes = new TomeEntityHandler();
    	pm.registerEvents(entityTomes, this);
    	
    	craftManager.addCrafts();
    	pm.registerEvents(craftManager, this);
    	
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this,
			new Runnable() {
				public void run() {
					TaskList.tick();
					
					//save horse data to tome
					clickTomes.tick();
				}
			}, 0L, 1L);
		
	}
}
