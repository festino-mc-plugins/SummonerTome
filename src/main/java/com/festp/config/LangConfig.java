package com.festp.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.festp.commands.MainCommand;

public class LangConfig implements IConfig
{
	private final File configFile;
	private final HashMap<String, Object> map = new HashMap<>();
	private final HashMap<String, IConfig.Key> keyMap = new HashMap<>();
	private final List<IConfigListener> listeners = new ArrayList<>();
	
	public LangConfig(File configFile)
	{
		this.configFile = configFile;
		for (LangKey key : LangKey.values()) {
			setNoSave(key, key.defaultValue);
		}
	}
	
	public void load()
	{
		FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		map.putAll(config.getValues(true)); // ignore keyMap
		save();
		onUpdate();
	}
	
	public void save()
	{
		FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		for (String name : getKeys()) {
			if (getKey(name) != null)
				config.set(name, get(name));
		}
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void onUpdate()
	{
		for (IConfigListener listener : listeners)
			listener.onUpdate(this);
	}
	public void addListener(IConfigListener listener)
	{
		listeners.add(listener);
	}



	public Set<String> getKeys() {
		return keyMap.keySet();
	}
	public IConfig.Key getKey(String name) {
		return keyMap.get(name);
	}
	
	public void set(IConfig.Key key, Object value) {
		setNoSave(key, value);
		save();
	}
	private void setNoSave(IConfig.Key key, Object value) {
		String keyStr = key.toString();
		if (!keyMap.containsKey(keyStr)) {
			keyMap.put(keyStr, key);
			if (map.containsKey(keyStr))
				return; // loaded from file, later added by ComponentManager
		}
		map.put(keyStr, value);
	}
	
	public Object get(String keyName, Object defaultValue) {
		IConfig.Key key = getKey(keyName);
		applyDefault(key, defaultValue);
		
		return map.getOrDefault(key.toString(), defaultValue);
	}
	public Object get(String key) {
		return get(key, null);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Object> T get(IConfig.Key key, T defaultValue) {
		applyDefault(key, defaultValue);
		
		Class<?> clazz;
		if (defaultValue != null) {
			clazz = defaultValue.getClass();
		} else if (key.getDefault() != null) {
			clazz = key.getDefault().getClass();
		} else {
			clazz = String.class;
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
	
	
	
	private void applyDefault(IConfig.Key key, Object defaultValue) {
		if (defaultValue == null)
			defaultValue = key.getDefault();
		if (!map.containsKey(key.toString())) {
			set(key, defaultValue);
		}
	}
	
	public enum LangKey implements IConfig.Key {
		COMMAND_NO_PERM("command-no-perm",
				ChatColor.RED + "You must be an operator or have " + ChatColor.WHITE + "%s" + ChatColor.RED + " permission to perform this command."),
		COMMAND_NO_ARGS("command-no-args",
				ChatColor.GRAY + "Usage: "
				+ "\n    /" + MainCommand.COMMAND + " get boat"
				+ "\n    /" + MainCommand.COMMAND + " config <option> [val]"
				+ "\n    /" + MainCommand.COMMAND + " config reload"),
		COMMAND_NO_PLAYER("command-no-player",
				ChatColor.RED + "You must be a player to perform this command."),
		COMMAND_INVALID_COMPONENTS("command-invalid-components",
				ChatColor.RED + "Couldn't give the tome item, wrong type: %s"),
		COMMAND_NO_SPACE("command-no-space",
				ChatColor.RED + "There are no space in the inventory."),
		COMMAND_CONFIG_KEY_ERROR("config-key-error",
				ChatColor.RED + "\"%s\" is an invalid option. Please follow tab-completion."),
		COMMAND_CONFIG_VAL_ERROR("config-val-error",
				ChatColor.RED + "\"%s\" is an invalid value. Please follow tab-completion."),
		COMMAND_CONFIG_GET_OK("config-get-ok",
				ChatColor.GREEN + "Option %s is equal to %s."),
		COMMAND_CONFIG_SET_OK("config-set-ok",
				ChatColor.GREEN + "Option %s was set to %s."),
		COMMAND_CONFIG_RELOAD_OK("command-config-reload-ok",
				ChatColor.GREEN + "Config reloaded."),
		CONFIG_RELOAD("config-reload",
				"Config reloaded."),
		CONFIG_SAVE("config-save",
				"Config successfully saved.");
		
		private final String name;
		private final Object defaultValue;

		LangKey(String name, Object defaultValue) {
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
		
		public static LangKey getKey(String keyStr) {
			for (LangKey key : LangKey.values())
				if (key.name.equalsIgnoreCase(keyStr))
					return key;
			return null;
		}
		
		public static List<String> getKeys() {
			List<String> keys = new ArrayList<>();
			for (LangKey key : LangKey.values()) {
				keys.add(key.name);
			}
			return keys;
		}
	}
}
