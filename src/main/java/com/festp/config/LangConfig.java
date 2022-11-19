package com.festp.config;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.festp.commands.MainCommand;

public class LangConfig
{
	private File configFile;
	
	private static final String KEY_COMMAND_NO_PERM = "command-no-perm";
	public String command_noPerm = ChatColor.RED + "You must be an operator or have " + ChatColor.WHITE + "%s" + ChatColor.RED + " permission to perform this command.";
	private static final String KEY_COMMAND_NO_ARGS = "command-no-args";
	public String command_noArgs = ChatColor.GRAY + "Usage: "
			+ "\n    /" + MainCommand.COMMAND + " get boat"
			+ "\n    /" + MainCommand.COMMAND + " config <option> [val]"
			+ "\n    /" + MainCommand.COMMAND + " config reload";
	private static final String KEY_COMMAND_NO_PLAYER = "command-no-player";
	public String get_noPlayer = ChatColor.RED + "You must be a player to perform this command.";
	private static final String KEY_COMMAND_INVALID_COMPONENTS = "command-invalid-components";
	public String get_components_error = ChatColor.RED + "Couldn't give the tome item, wrong type: %s";
	private static final String KEY_COMMAND_NO_SPACE = "command-no-space";
	public String get_space_error = ChatColor.RED + "There are no space in the inventory.";
	private static final String KEY_COMMAND_CONFIG_KEY_ERROR = "config-key-error";
	public String config_key_error = ChatColor.RED + "\"%s\" is an invalid option. Please follow tab-completion.";
	private static final String KEY_COMMAND_CONFIG_VAL_ERROR = "config-val-error";
	public String config_value_error = ChatColor.RED + "\"%s\" is an invalid value. Please follow tab-completion.";
	private static final String KEY_COMMAND_CONFIG_GET_OK = "config-get-ok";
	public String config_getOk = ChatColor.GREEN + "Option %s is equal to %s.";
	private static final String KEY_COMMAND_CONFIG_SET_OK = "config-set-ok";
	public String config_setOk = ChatColor.GREEN + "Option %s was set to %s.";
	private static final String KEY_COMMAND_RELOAD_OK = "command-config-reload-ok";
	public String config_reloadOk = ChatColor.GREEN + "Config reloaded.";
	private static final String KEY_CONFIG_RELOAD = "config-reload";
	public String config_reload = "Config reloaded.";
	private static final String KEY_CONFIG_SAVE = "config-save";
	public String config_save = "Config successfully saved.";
	
	public LangConfig(File configFile)
	{
		this.configFile = configFile;
	}
	
	public void load()
	{
		LangConfig defaults = new LangConfig(configFile);
		FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		command_noPerm = config.getString(KEY_COMMAND_NO_PERM, defaults.command_noPerm);
		command_noArgs = config.getString(KEY_COMMAND_NO_ARGS, defaults.command_noArgs);
		
		get_noPlayer = config.getString(KEY_COMMAND_NO_PLAYER, defaults.get_noPlayer);
		get_components_error = config.getString(KEY_COMMAND_INVALID_COMPONENTS, defaults.get_components_error);
		get_space_error = config.getString(KEY_COMMAND_NO_SPACE, defaults.get_space_error);
		
		config_key_error = config.getString(KEY_COMMAND_CONFIG_KEY_ERROR, defaults.config_key_error);
		config_value_error = config.getString(KEY_COMMAND_CONFIG_VAL_ERROR, defaults.config_value_error);
		config_getOk = config.getString(KEY_COMMAND_CONFIG_GET_OK, defaults.config_getOk);
		config_setOk = config.getString(KEY_COMMAND_CONFIG_SET_OK, defaults.config_setOk);
		config_reloadOk = config.getString(KEY_COMMAND_RELOAD_OK, defaults.config_reloadOk);
		
		config_reload = config.getString(KEY_CONFIG_RELOAD, defaults.config_reload);
		config_save = config.getString(KEY_CONFIG_SAVE, defaults.config_save);
		save();
	}
	
	public void save()
	{
		FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		config.set(KEY_COMMAND_NO_PERM, command_noPerm);
		config.set(KEY_COMMAND_NO_ARGS, command_noArgs);
		
		config.set(KEY_COMMAND_NO_PLAYER, get_noPlayer);
		config.set(KEY_COMMAND_INVALID_COMPONENTS, get_components_error);
		config.set(KEY_COMMAND_NO_SPACE, get_space_error);
		
		config.set(KEY_COMMAND_CONFIG_KEY_ERROR, config_key_error);
		config.set(KEY_COMMAND_CONFIG_VAL_ERROR, config_value_error);
		config.set(KEY_COMMAND_CONFIG_GET_OK, config_getOk);
		config.set(KEY_COMMAND_CONFIG_SET_OK, config_setOk);
		config.set(KEY_COMMAND_RELOAD_OK, config_reloadOk);
		config.set(KEY_CONFIG_RELOAD, config_reload);
		config.set(KEY_CONFIG_SAVE, config_save);
		try {
			config.save(configFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
