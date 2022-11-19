package com.festp.config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.festp.Logger;
import com.festp.config.FileUtils;

public class Config implements IConfig
{
	private JavaPlugin plugin;
	private LangConfig lang;
	private MemoryConfiguration config;
	private final HashMap<String, Object> map = new HashMap<>();
	
	public Config(JavaPlugin jp, LangConfig lang) {
		this.plugin = jp;
		this.lang = lang;
	}
	
	public void load() {
		File configFile = new File(plugin.getDataFolder(), "config.yml");
		if (!configFile.exists())
			FileUtils.copyFileFromResource(configFile, "config.yml");
		plugin.reloadConfig();
		config = plugin.getConfig();
		map.putAll(config.getValues(true));
		saveSilently();
		Logger.info(lang.config_reload);
	}

	public void save() {
		saveSilently();
		Logger.info(lang.config_save);
	}
	public void saveSilently() {
		for (Key key : Key.values()) {
			config.set(key.name, get(key));
		}
		plugin.saveConfig();
	}
	
	
	
	public void set(IConfig.Key key, Object value) {
		map.put(key.toString(), value);
		save();
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Object> T get(IConfig.Key key, T defaultValue) {
		applyDefault(key, defaultValue);
		
		Class<?> clazz;
		if (defaultValue != null) {
			clazz = defaultValue.getClass();
		} else {
			clazz = key.getDefault().getClass();
		}
		
		Object res = map.getOrDefault(key.toString(), defaultValue);
		if (clazz.isInstance(res)) {
			return (T) res;
		}
		return defaultValue;
	}
	public <T extends Object> T get(IConfig.Key key) {
		return get(key, null);
	}
	
	

	public Object getObject(IConfig.Key key, Object defaultValue) {
		return map.getOrDefault(key.toString(), defaultValue);
	}

	public Object getObject(IConfig.Key key) {
		return getObject(key, key.getDefault());
	}
	
	
	
	private void applyDefault(String key, Object defaultValue) {
		if (!map.containsKey(key)) {
			map.put(key, defaultValue);
		}
	}
	private void applyDefault(IConfig.Key key, Object defaultValue) {
		if (defaultValue == null)
			applyDefault(key);
		else
			applyDefault(key.toString(), defaultValue);
	}
	private void applyDefault(IConfig.Key key) {
		applyDefault(key.toString(), key.getDefault());
	}
	
	
	
	// TODO create set for every component, options: enable craft, enable functionality, banned slots
	//TEST("components.minecart.craft", true),
	//TEST("components.minecart.use", true),
	//TEST("components.horse.ban-slots-from", 1),
	//TEST("components.boat.ban-slots-from", -1),
	public class ComponentKey implements IConfig.Key {
		private final String name;
		private final Object defaultValue;

		ComponentKey(String name, Object defaultValue) {
			this.name = name;
			this.defaultValue = defaultValue;
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
	
	public enum Key implements IConfig.Key {
		EFFECTS_PLAYSOUND("effects-sounds", true),
		EFFECTS_SPAWNPARTICLE("effects-particles", true),
		MAX_COMPONENTS("max-components", 0),
		///LOG_DEBUG("log-debug-info", false),
		COMPONENT_NAMES("component-names", new String[0]);
		
		private final String name;
		private final Object defaultValue;

		Key(String name, Object defaultValue) {
			this.name = name;
			this.defaultValue = defaultValue;
		}
		public Object getDefault() { return defaultValue; }
		@Override
		public String toString() { return name; }
		
		public Object validateValue(String valueStr) {
			try {
				if (defaultValue instanceof Boolean) {
					if (valueStr.equalsIgnoreCase("true"))
						return true;
					if (valueStr.equalsIgnoreCase("false"))
						return false;
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
		
		public static boolean isValidKey(String keyStr) {
			return getKey(keyStr) != null;
		}
		
		public static Key getKey(String keyStr) {
			for (Key key : Key.values())
				if (key.name.equalsIgnoreCase(keyStr))
					return key;
			return null;
		}
		
		public static List<String> getKeys() {
			List<String> keys = new ArrayList<>();
			for (Key key : Key.values()) {
				keys.add(key.name);
			}
			return keys;
		}
	}
}
