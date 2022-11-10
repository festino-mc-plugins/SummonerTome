package com.festp.tome;

import java.util.ArrayList;
import java.util.List;

import com.festp.components.CustomHorseComponent;
import com.festp.components.HorseComponent;
import com.festp.components.ITomeComponent;

public class ComponentManager
{
	private boolean registerNativeComponents = true;
	private final List<String> nativeComponents = new ArrayList<>();
	private final List<IComponentFactory> components = new ArrayList<>();
	private final List<ComponentInfo> componentInfo = new ArrayList<>();
	
	public void stopRegisterAsNative() {
		registerNativeComponents = false;
	}

	public void register(IComponentFactory factory) {
		ComponentInfo newInfo = new ComponentInfo(factory.getCode());
		register(factory, newInfo);
	}
	public void register(IComponentFactory factory, ComponentInfo info)
	{
		String code = factory.getCode();
		if (!TomeSerializer.canSerialize(code))
			throw new IllegalArgumentException("Couldn't register code \"" + code + "\"");
		for (IComponentFactory component : components)
			if (code.equalsIgnoreCase(component.getCode()))
				throw new IllegalArgumentException("Repeating register code \"" + code + "\"");
		components.add(factory);
		componentInfo.add(info);
		if (registerNativeComponents)
			nativeComponents.add(code);
		// try recipe
	}

	public ComponentInfo getInfo(String code) {
		for (int i = 0; i < components.size(); i++) {
			if (code.equalsIgnoreCase(components.get(i).getCode())) {
				return componentInfo.get(i);
			}
		}
		return null;
	}
	public void updateInfo(String code, ComponentInfo info) {
		for (int i = 0; i < components.size(); i++) {
			if (code.equalsIgnoreCase(components.get(i).getCode())) {
				componentInfo.set(i, info);
				return;
			}
		}
	}
	
	public ITomeComponent fromCode(String code)
	{
		IComponentFactory component = tryFind(code);
		if (component == null)
			return new MissingComponent(code);
		//throw new IllegalArgumentException("Unknown component code. Try install new versions of the plugin.");
		return component.create();
	}

	public String[] getNativeComponents() {
		return nativeComponents.toArray(new String[0]);
	}
	public String[] getAll() {
		List<String> res = new ArrayList<>(nativeComponents);
		res.remove(CustomHorseComponent.CODE);
		return res.toArray(new String[0]);
	}
	public String[] getCustomAll() {
		List<String> res = new ArrayList<>(nativeComponents);
		res.remove(HorseComponent.CODE);
		return res.toArray(new String[0]);
	}

	public String[] getLoadedComponents() {
		String[] res = new String[components.size()];
		for (int i = 0; i < res.length; i++)
			res[i] = components.get(i).getCode();
		return res;
	}

	// incompatibilities (on craft)
	public boolean isCompatible(ITomeComponent comp1, ITomeComponent comp2) {
		IComponentFactory fact1 = tryFind(comp1.getCode());
		if (fact1 == null)
			return false; // TODO return true if soft
		IComponentFactory fact2 = tryFind(comp2.getCode());
		if (fact2 == null)
			return false; // TODO return true if soft
		
		for (String code : fact1.getIncompatibleComponents())
			if (code.equalsIgnoreCase(comp2.getCode()))
				return false;
		for (String code : fact2.getIncompatibleComponents())
			if (code.equalsIgnoreCase(comp1.getCode()))
				return false;
		return true;
	}
	// TODO craft (stylish only, or just register to check for codes)
	// TODO ban slots (on summon)
	
	private IComponentFactory tryFind(String code) {
		for (IComponentFactory component : components)
			if (code.equalsIgnoreCase(component.getCode()))
				return component;
		return null;
	}
}
