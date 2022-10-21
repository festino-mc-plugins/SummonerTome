package com.festp;

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

import com.festp.handlers.TomeCraftHandler;

public class CraftManager implements Listener {
	public enum CraftTag { KEEP_DATA, ONLY_SPECIFIC };
	
	Server server;
	Main plugin;
	
	List<NamespacedKey> recipeKeys = new ArrayList<>();
	
	public CraftManager(Main plugin, Server server) {
		this.plugin = plugin;
		this.server = server;
	}
	
	public void addCrafts() {
		TomeCraftHandler.addTomeCrafts(plugin, this);
	}
	private void giveRecipe(HumanEntity player, NamespacedKey key) {
		player.discoverRecipe(key);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		for (NamespacedKey recipeKey : recipeKeys) {
			giveRecipe(p, recipeKey);
		}
	}
	
	public boolean addRecipe(NamespacedKey key, Recipe recipe) {
		if (recipeKeys.contains(key))
			return false;
		server.addRecipe(recipe);
		recipeKeys.add(key);
		return true;
	}
}
