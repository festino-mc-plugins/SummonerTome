package com.festp.handlers;

import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import com.festp.components.ITomeComponent;

public interface IDataExtractor
{
	public ITomeComponent extract(ItemStack oldTome, Entity entity);
}
