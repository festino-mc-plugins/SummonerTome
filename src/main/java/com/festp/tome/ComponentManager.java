package com.festp.tome;

import java.util.ArrayList;
import java.util.List;

import com.festp.components.CustomHorseComponent;
import com.festp.components.HorseComponent;
import com.festp.components.ITomeComponent;
import com.festp.config.Config;
import com.festp.config.IConfig;
import com.festp.handlers.IDataExtractor;
import com.festp.tome.ComponentInfo.LanguageInfo;

public class ComponentManager
{
	private final IConfig config;
	private final IConfig langConfig;
	
	private boolean registerNativeComponents = true;
	private final List<String> nativeComponents = new ArrayList<>();
	private final List<ComponentInfo> components = new ArrayList<>();
	
	public ComponentManager(IConfig config, IConfig langConfig) {
		this.config = config;
		this.langConfig = langConfig;
	}
	
	public void stopRegisterAsNative() {
		registerNativeComponents = false;
	}

	public void register(IComponentFactory factory) {
		register(new ComponentInfo(factory));
	}
	public void register(ComponentInfo info)
	{
		IComponentFactory factory = info.getComponentFactory();
		String code = factory.getCode();
		
		if (info.getLanguageInfo() == null) {
			LanguageInfo newInfo = new LanguageInfo(code);
			info.setLanguageInfo(newInfo);
		}
		
		if (!TomeSerializer.canSerialize(code))
			throw new IllegalArgumentException("Couldn't register code \"" + code + "\"");
		if (getInfo(code) != null)
			throw new IllegalArgumentException("Repeating register code \"" + code + "\"");
		components.add(info);
		if (registerNativeComponents)
			nativeComponents.add(code);
		// try recipe
		addConfigKey(code, ComponentKey.ALLOW_CRAFTING, true);
		addConfigKey(code, ComponentKey.ALLOW_USING, true);
		addConfigKey(code, ComponentKey.BAN_SLOTS_FROM, info.getBehaviourInfo().banSlotsFrom);
		// TODO custom properties like searching radius
		LanguageInfo langInfo = info.getLanguageInfo();
		addLangConfigKey(code, ComponentKey.LANG_LORE_PET_NAME, langInfo.lorePetName);
		addLangConfigKey(code, ComponentKey.LANG_TOME_NAME_FORMAT, langInfo.tomeNameFormat);
		addLangConfigKey(code, ComponentKey.LANG_SOLO_TOME_NAME, langInfo.soloTomeName);
	}

	private void addConfigKey(String code, String property, Object val) {
		if (config.getKey(ComponentKey.getPropertyName(code, property)) == null)
			config.set(new ComponentKey(code, property, val), val);
	}

	private void addLangConfigKey(String code, String property, Object val) {
		if (langConfig.getKey(ComponentKey.getPropertyName(code, property)) == null)
			langConfig.set(new ComponentKey(code, property, val), val);
	}

	public ComponentInfo getInfo(String code) {
		for (ComponentInfo componentInfo : components)
			if (code.equalsIgnoreCase(componentInfo.getComponentFactory().getCode()))
				return componentInfo;
		return null;
	}
	
	private IComponentFactory getFactory(String code) {
		ComponentInfo componentInfo = getInfo(code);
		if (componentInfo == null)
			return null;
		return componentInfo.getComponentFactory();
	}
	public ComponentInfo.LanguageInfo getLangInfo(String code) {
		ComponentInfo componentInfo = getInfo(code);
		if (componentInfo == null)
			return null;
		// awful code
		String key_lorePetName = ComponentKey.getPropertyName(code, ComponentKey.LANG_LORE_PET_NAME);
		String key_tomeNameFormat = ComponentKey.getPropertyName(code, ComponentKey.LANG_TOME_NAME_FORMAT);
		String key_soloTomeName = ComponentKey.getPropertyName(code, ComponentKey.LANG_SOLO_TOME_NAME);
		LanguageInfo newInfo = new LanguageInfo(
				langConfig.get(langConfig.getKey(key_lorePetName)),
				langConfig.get(langConfig.getKey(key_tomeNameFormat)),
				langConfig.get(langConfig.getKey(key_soloTomeName)));
		componentInfo.setLanguageInfo(newInfo);
		
		return componentInfo.getLanguageInfo();
	}
	public void updateLangInfo(String code, LanguageInfo info) {
		ComponentInfo componentInfo = getInfo(code);
		if (componentInfo == null)
			return;
		componentInfo.setLanguageInfo(info);
	}
	
	public ITomeComponent fromCode(String code)
	{
		if (isDisabled(code))
			return new DisabledComponent(code);
		IComponentFactory factory = getFactory(code);
		if (factory == null)
			return new MissingComponent(code);
		//throw new IllegalArgumentException("Unknown component code. Try install new versions of the plugin.");
		return factory.create();
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
			res[i] = components.get(i).getComponentFactory().getCode();
		return res;
	}

	// incompatibilities (on craft)
	public boolean isCompatible(ITomeComponent comp1, ITomeComponent comp2)
	{
		// no factory => MissingComponent
		IComponentFactory fact1 = getFactory(comp1.getCode());
		if (fact1 == null)
			return softDependenciesMode();
		IComponentFactory fact2 = getFactory(comp2.getCode());
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
		IConfig.Key key = config.getKey(property);
		if (key == null)
			return false;
		return !config.get(key, false);
	}

	public int getBanSlotsFrom(String code) {
		String property = ComponentKey.getPropertyName(code, ComponentKey.BAN_SLOTS_FROM);
		return config.get(config.getKey(property), 0);
	}

	public IDataExtractor getDataExtractor(String code) {
		return getInfo(code).getBehaviourInfo().dataExtractor;
	}
	
	// TODO craft (stylish only, or just register to check for codes)
	// TODO ban slots (on summon) - config.get(new ComponentKey(code, ComponentKey.BAN_SLOTS_FROM), 0);
	
	
	public static class ComponentKey implements IConfig.Key
	{
		public static final String ALLOW_CRAFTING = "craft";
		public static final String ALLOW_USING = "use";
		public static final String BAN_SLOTS_FROM = "ban-slots-from";
		
		public static final String LANG_LORE_PET_NAME = "lore-entity-name";
		public static final String LANG_TOME_NAME_FORMAT = "tome-name-format";
		public static final String LANG_SOLO_TOME_NAME = "solo-tome-name";
		
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
