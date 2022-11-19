package com.festp.config;

public interface IConfig {
	interface Key {
		Object getDefault();
	}
	public <T extends Object> T get(IConfig.Key key, T defaultValue);
	public <T extends Object> T get(IConfig.Key key);
	public Object getObject(Key key);
	public Object getObject(Key key, Object default_value);
	
}
