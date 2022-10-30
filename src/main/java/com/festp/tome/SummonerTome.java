package com.festp.tome;

import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.festp.components.ITomeComponent;

public class SummonerTome
{
	private ITomeComponent[] components;
	
	public SummonerTome(ITomeComponent[] components)
	{
		this.components = components;
	}

	public static boolean isTome(ItemStack item)
	{
		return TomeItemBuilder.hasTag(item);
	}
	
	public static SummonerTome getTome(ItemStack item)
	{
        if (!isTome(item))
    		return null;
    	String data = TomeItemBuilder.getTag(item);
		return new SummonerTome(TomeFormatter.deserialize(data));
	}
	public ItemStack setTome(ItemStack item)
	{
		String data = TomeFormatter.serialize(components);
		return TomeItemBuilder.setTag(item, data);
	}

	public boolean trySwap(Entity entity)
	{
		for (ITomeComponent comp : components)
			if (comp.trySwap(entity))
				return true;
		return false;
	}
	
	public Entity trySummon(Player summoner)
	{
		Location playerLoc = summoner.getLocation();
		Arrays.sort(components, new ITomeComponent.PriorityComparator());
		
		ITomeComponent resComponent = null;
		Location curLoc = null;
		for (int i = 0; i < components.length; i++)
		{
			ITomeComponent component = components[i];
			if (resComponent != null && component.getPriority() != resComponent.getPriority()) {
				break;
			}
			
			if (!component.canSummon(summoner))
				continue;
			
			Location loc = component.getSummonLocation(playerLoc.clone());
			if (loc != null && (curLoc == null || playerLoc.distanceSquared(loc) < playerLoc.distanceSquared(curLoc))) {
				curLoc = loc;
				resComponent = component;
			}
		}
		
		if (resComponent == null)
			return null;
		
		return resComponent.summon(summoner, curLoc);
	}

	public void replaceOrAdd(ITomeComponent component)
	{
	    if (components == null)
	    	components = new ITomeComponent[0];

	    int index = -1;
	    for (int i = 0; i < components.length; i++) {
	    	ITomeComponent comp = components[i];
	    	if (comp.getClass() == component.getClass()) { // TODO test if bad condition
	    		index = i;
	    		break;
	    	}
	    }
	    
	    if (index < 0)
	    {
	    	index = components.length;
	    	components = Arrays.copyOf(components, components.length + 1);
	    }
	    components[index] = component;
	}

	/** @return null if there is no component of the desired class */
	public <T extends ITomeComponent> T getComponent(Class<T> clazz)
	{
		for (ITomeComponent comp : components)
			if (clazz.isInstance(comp))
				return clazz.cast(comp);
		return null;
	}

	public <T extends ITomeComponent> boolean hasComponent(Class<T> clazz)
	{
		return getComponent(clazz) != null;
	}

	public void replace(Class<? extends ITomeComponent> replacingClass, ITomeComponent newComponent) {
		for (int i = 0; i < components.length; i++) {
			ITomeComponent comp = components[i];
			if (replacingClass.isInstance(comp)) {
				components[i] = newComponent;
				break;
			}
		}
	}

	// Undesirable but necessary; SummonerTome knows nothing about incompatible components
	public ITomeComponent[] getComponents() {
		return components;
	}
	/*public static SummonerTome tryCombine(Collection<SummonerTome> tomes) {
		return null;
	}*/
}
