package com.festp.config;

import java.util.Set;

public interface IConfig {
	interface Key {
		Object getDefault();
		Class<?> getValueClass();
		Object validateValue(String string);
	}
	public Set<String> getKeys();
	public Key getKey(String name);
	public void set(Key key, Object val);
	public <T extends Object> T get(IConfig.Key key, T defaultValue);
	public <T extends Object> T get(IConfig.Key key);
	public Object getObject(Key key);
	public Object getObject(Key key, Object defaultValue);
	public void load();
	
}
