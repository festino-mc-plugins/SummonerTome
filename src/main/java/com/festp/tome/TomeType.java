package com.festp.tome;

import java.util.EnumSet;

import com.festp.components.BoatComponent;
import com.festp.components.CustomHorseComponent;
import com.festp.components.HorseComponent;
import com.festp.components.ITomeComponent;
import com.festp.components.MinecartComponent;

// TODO check if a new component was added :D
// or use reflection?
public enum TomeType
{
	MINECART, BOAT, HORSE, CUSTOM_HORSE;
	
	public ITomeComponent getComponent()
	{
		switch (this) {
		case MINECART:
			return new MinecartComponent();
		case BOAT:
			return new BoatComponent();
		case HORSE:
			return new HorseComponent();
		case CUSTOM_HORSE:
			return new CustomHorseComponent();
		default:
			return null;
		}
	}

	public Class<? extends ITomeComponent> getComponentClass() {
		switch (this) {
		case MINECART:
			return MinecartComponent.class;
		case BOAT:
			return BoatComponent.class;
		case HORSE:
			return HorseComponent.class;
		case CUSTOM_HORSE:
			return CustomHorseComponent.class;
		default:
			return null;
		}
	}
	
	public static TomeType fromChar(char componentCode)
	{
		if (componentCode == MinecartComponent.CODE)
			return MINECART;
		if (componentCode == BoatComponent.CODE)
			return BOAT;
		if (componentCode == HorseComponent.CODE)
			return HORSE;
		if (componentCode == CustomHorseComponent.CODE)
			return CUSTOM_HORSE;
		throw new IllegalArgumentException("Unknown component code. Try install new versions of the plugin.");
	}

	public static EnumSet<TomeType> getAll()
	{
		EnumSet<TomeType> res = EnumSet.allOf(TomeType.class);
		res.remove(CUSTOM_HORSE);
		return res;
	}

	public static EnumSet<TomeType> getCustomAll()
	{
		EnumSet<TomeType> res = EnumSet.allOf(TomeType.class);
		res.remove(HORSE);
		return res;
	}
}
