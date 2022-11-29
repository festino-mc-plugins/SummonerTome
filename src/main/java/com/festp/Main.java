package com.festp;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.festp.commands.MainCommand;
import com.festp.components.BoatComponent;
import com.festp.components.BoatDataExtractor;
import com.festp.components.CustomHorseComponentFactory;
import com.festp.components.CustomHorseDataExtractor;
import com.festp.components.HorseComponent;
import com.festp.components.MinecartComponent;
import com.festp.components.PigComponent;
import com.festp.components.StriderComponent;
import com.festp.config.Config;
import com.festp.config.FeedbackEffects;
import com.festp.config.LangConfig;
import com.festp.crafting.CraftManager;
import com.festp.crafting.TomeItemBuilder;
import com.festp.handlers.TomeClickHandler;
import com.festp.handlers.TomeEntityHandler;
import com.festp.handlers.TomeInventoryHandler;
import com.festp.tome.ComponentInfo;
import com.festp.tome.ComponentManager;
import com.festp.tome.SimpleComponentFactory;
import com.festp.tome.TomeSerializer;
import com.festp.utils.NBTUtils;
import com.festp.utils.UtilsVersion;

public class Main extends JavaPlugin
{
	CraftManager craftManager;
	
	// TODO external ComponentManager access
	
	public void onEnable()
	{
		Logger.setLogger(getLogger());
    	PluginManager pm = getServer().getPluginManager();
    	
    	NBTUtils.setPlugin(this);

		LangConfig lang = new LangConfig(new File(getDataFolder(), "lang.yml"));
		lang.load();
    	Config config = new Config(this, lang);
    	config.load();
    	config.save();

    	ComponentManager componentManager = new ComponentManager(config, lang);
    	componentManager.register(new SimpleComponentFactory(MinecartComponent.class));
    	ComponentInfo boatInfo = new ComponentInfo(
    			new SimpleComponentFactory(BoatComponent.class),
    			new ComponentInfo.BehaviourInfo(27, new BoatDataExtractor()));
    	componentManager.register(boatInfo);
    	componentManager.register(new SimpleComponentFactory(PigComponent.class));
    	componentManager.register(new SimpleComponentFactory(HorseComponent.class));
    	ComponentInfo customHorseInfo = new ComponentInfo(
    			new CustomHorseComponentFactory(),
    			new ComponentInfo.LanguageInfo("custom horse", "Advanced %s", "Advanced horse tome"),
    			new ComponentInfo.BehaviourInfo(27, new CustomHorseDataExtractor()));
    	componentManager.register(customHorseInfo);
    	if (UtilsVersion.SUPPORTS_STRIDER)
    		componentManager.register(new SimpleComponentFactory(StriderComponent.class));
    	componentManager.stopRegisterAsNative();

    	// TODO rework bad design
    	TomeSerializer.setComponentManager(componentManager);
    	TomeItemBuilder.setComponentManager(componentManager);
    	
    	craftManager = new CraftManager(this, getServer(), config, componentManager);
    	craftManager.registerEvents(pm);
    	craftManager.addCrafts();
    	
    	MainCommand command = new MainCommand(config, lang, componentManager);
    	getCommand(MainCommand.COMMAND).setExecutor(command);
    	
    	FeedbackEffects feedback = new FeedbackEffects(config);
    	TomeInventoryHandler inventoryHandler = new TomeInventoryHandler();
    	pm.registerEvents(inventoryHandler, this);
    	TomeClickHandler clickHandler = new TomeClickHandler(feedback, componentManager, inventoryHandler);
    	pm.registerEvents(clickHandler, this);
    	TomeEntityHandler entityHandler = new TomeEntityHandler(feedback);
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
