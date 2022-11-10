package com.festp;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.festp.commands.MainCommand;
import com.festp.components.BoatComponent;
import com.festp.components.CustomHorseComponentFactory;
import com.festp.components.HorseComponent;
import com.festp.components.MinecartComponent;
import com.festp.components.PigComponent;
import com.festp.components.StriderComponent;
import com.festp.crafting.CraftManager;
import com.festp.crafting.TomeItemBuilder;
import com.festp.handlers.TomeClickHandler;
import com.festp.handlers.TomeEntityHandler;
import com.festp.handlers.TomeInventoryHandler;
import com.festp.tome.ComponentInfo;
import com.festp.tome.ComponentManager;
import com.festp.tome.SimpleComponentFactory;
import com.festp.tome.TomeFormatter;
import com.festp.utils.NBTUtils;

public class Main extends JavaPlugin
{
	CraftManager craftManager;
	
	// TODO external ComponentManager access
	
	public void onEnable()
	{
		Logger.setLogger(getLogger());
    	PluginManager pm = getServer().getPluginManager();
    	
    	NBTUtils.setPlugin(this);

    	ComponentManager componentManager = new ComponentManager();
    	// TODO localization? - use only updateInfo()
    	componentManager.register(new SimpleComponentFactory(MinecartComponent.class));
    	componentManager.register(new SimpleComponentFactory(BoatComponent.class));
    	componentManager.register(new SimpleComponentFactory(PigComponent.class));
    	componentManager.register(new SimpleComponentFactory(HorseComponent.class));
    	componentManager.register(new CustomHorseComponentFactory(), new ComponentInfo("custom horse", "Advanced %s", "Advanced horse tome"));
		// TODO if 1.15-, remove "strider" (add UtilsVesrion)
    	componentManager.register(new SimpleComponentFactory(StriderComponent.class));
    	componentManager.stopRegisterAsNative();

    	// bad design?
    	TomeFormatter.setComponentManager(componentManager);
    	TomeItemBuilder.setComponentManager(componentManager);
    	
    	craftManager = new CraftManager(this, getServer(), componentManager);
    	craftManager.registerEvents(pm);
    	craftManager.addCrafts();
    	
    	MainCommand command = new MainCommand(componentManager);
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
