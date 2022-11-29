package com.festp.crafting;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Recipe;
import org.bukkit.plugin.PluginManager;

import com.festp.Main;
import com.festp.config.IConfig;
import com.festp.config.LangConfig;
import com.festp.tome.ComponentManager;

public class CraftManager implements Listener {
	public enum CraftTag { KEEP_DATA, ONLY_SPECIFIC };
	
	Server server;
	Main plugin;
	TomeCraftHandler craftHandler;
	
	List<NamespacedKey> recipeKeys = new ArrayList<>();
	
	public CraftManager(Main plugin, Server server, IConfig config, LangConfig langConfig, ComponentManager componentManager) {
		this.plugin = plugin;
		this.server = server;
    	this.craftHandler = new TomeCraftHandler(plugin, config, this, componentManager);
    	langConfig.addListener(craftHandler);
	}

	public void registerEvents(PluginManager pm) {
    	pm.registerEvents(this, plugin);
    	pm.registerEvents(craftHandler, plugin);
    	// server reload command
    	for (Player player : server.getOnlinePlayers())
    		giveRecipes(player);
	}
	
	public void addCrafts() {
		craftHandler.addTomeCrafts();
	}
	private void giveRecipe(HumanEntity player, NamespacedKey key) {
		player.discoverRecipe(key);
	}
	private void giveRecipes(HumanEntity player) {
		for (NamespacedKey recipeKey : recipeKeys) {
			giveRecipe(player, recipeKey);
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		giveRecipes(player);
	}
	
	public boolean addRecipe(NamespacedKey key, Recipe recipe) {
		if (recipeKeys.contains(key)) {
			recipeKeys.remove(key);
	    	for (Player player : server.getOnlinePlayers())
	    		player.undiscoverRecipe(key);
			server.removeRecipe(key);
		}
		
		if (!server.addRecipe(recipe))
			return false;
    	for (Player player : server.getOnlinePlayers())
    		player.discoverRecipe(key);
		recipeKeys.add(key);
		return true;
	}
}
