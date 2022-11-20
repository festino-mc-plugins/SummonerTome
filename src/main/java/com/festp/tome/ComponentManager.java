package com.festp.tome;

import java.util.ArrayList;
import java.util.List;

import com.festp.components.CustomHorseComponent;
import com.festp.components.HorseComponent;
import com.festp.components.ITomeComponent;
import com.festp.config.Config;
import com.festp.config.IConfig;

public class ComponentManager
{
	private final IConfig config;
	
	private boolean registerNativeComponents = true;
	private final List<String> nativeComponents = new ArrayList<>();
	private final List<IComponentFactory> components = new ArrayList<>();
	private final List<ComponentInfo> componentInfo = new ArrayList<>();
	
	public ComponentManager(IConfig config) {
		this.config = config;
	}
	
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
		addConfigKey(code, ComponentKey.ALLOW_CRAFTING, true);
		addConfigKey(code, ComponentKey.ALLOW_USING, true);
		addConfigKey(code, ComponentKey.BAN_SLOTS_FROM, 0);
		// TODO custom properties like searching radius
	}

	private void addConfigKey(String code, String property, Object val) {
		config.set(new ComponentKey(code, property, val), val);
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
		if (isDisabled(code))
			return new DisabledComponent(code);
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
	public boolean isCompatible(ITomeComponent comp1, ITomeComponent comp2)
	{
		// no factory => MissingComponent
		IComponentFactory fact1 = tryFind(comp1.getCode());
		if (fact1 == null)
			return softDependenciesMode();
		IComponentFactory fact2 = tryFind(comp2.getCode());
		if (fact2 == null)
			return softDependenciesMode();
		
		if (comp1 instanceof DisabledComponent || comp2 instanceof DisabledComponent)
			if (!softDisablingMode())
				return false;
		
		for (String code : fact1.getIncompatibleComponents())
			if (code.equalsIgnoreCase(comp2.getCode()))
				return false;
		for (String code : fact2.getIncompatibleComponents())
			if (code.equalsIgnoreCase(comp1.getCode()))
				return false;
		return true;
	}
	
	private boolean softDependenciesMode() {
		return config.get(Config.Key.SOFT_DEPENDENCY, false);
	}
	
	private boolean softDisablingMode() {
		return config.get(Config.Key.SOFT_DISABLING, false);
	}

	public boolean isCraftable(String code) {
		String property = ComponentKey.getPropertyName(code, ComponentKey.ALLOW_CRAFTING);
		return config.get(config.getKey(property), false);
	}

	private boolean isDisabled(String code) {
		String property = ComponentKey.getPropertyName(code, ComponentKey.ALLOW_USING);
		return !config.get(config.getKey(property), false);
	}

	public int getBanSlotsFrom(String code) {
		String property = ComponentKey.getPropertyName(code, ComponentKey.BAN_SLOTS_FROM);
		return config.get(config.getKey(property), 0);
	}
	
	// TODO craft (stylish only, or just register to check for codes)
	// TODO ban slots (on summon) - config.get(new ComponentKey(code, ComponentKey.BAN_SLOTS_FROM), 0);
	
	private IComponentFactory tryFind(String code) {
		for (IComponentFactory component : components)
			if (code.equalsIgnoreCase(component.getCode()))
				return component;
		return null;
	}
	
	public static class ComponentKey implements IConfig.Key
	{
		public static final String ALLOW_CRAFTING = "craft";
		public static final String ALLOW_USING = "use";
		public static final String BAN_SLOTS_FROM = "ban-slots-from";
		
		private final String name;
		private final Object defaultValue;

		ComponentKey(String componentName, String propertyName, Object defaultValue) {
			this.name = getPropertyName(componentName, propertyName);
			this.defaultValue = defaultValue;
		}
		
		public static String getPropertyName(String componentName, String propertyName) {
			return "components." + componentName + "." + propertyName;
		}
		
		public Object getDefault() { return defaultValue; }
		@Override
		public String toString() { return name; }
		
		public Object validateValue(String valueStr) {
			try {
				if (defaultValue instanceof Boolean) {
					return Boolean.parseBoolean(valueStr);
				}
				if (defaultValue instanceof Integer) {
					return Integer.parseInt(valueStr);
				}
				if (defaultValue instanceof Double) {
					return Double.parseDouble(valueStr);
				}
				if (defaultValue instanceof String) {
					return valueStr;
				}
			} catch (Exception e) {}
			return null;
		}
		
		public Class<?> getValueClass() {
			if (defaultValue instanceof Boolean) {
				return Boolean.class;
			}
			if (defaultValue instanceof Integer) {
				return Integer.class;
			}
			if (defaultValue instanceof Double) {
				return Double.class;
			}
			if (defaultValue instanceof String) {
				return String.class;
			}
			if (defaultValue instanceof List<?>) {
				return defaultValue.getClass();
			}
			return null;
		}
	}
}
