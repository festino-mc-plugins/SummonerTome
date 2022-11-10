package com.festp.tome;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import com.festp.components.ITomeComponent;

public class SimpleComponentFactory implements IComponentFactory
{
	final String code;
	final Constructor<? extends ITomeComponent> constructor;
	
	public SimpleComponentFactory(Class<? extends ITomeComponent> clazz)
	{
		Constructor<? extends ITomeComponent> c = null;
		try {
			c = clazz.getConstructor();
			c.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		constructor = c;
		
		String codeRes = null;
		try {
			Field codeField = clazz.getDeclaredField("CODE");
			codeField.setAccessible(true);
			codeRes = (String)codeField.get(null);
		} catch (Exception e) { }
		
		if (codeRes == null)
			codeRes = create().getCode();
		code = codeRes;
	}
	
	public String getCode() {
		return code;
	}

	public ITomeComponent create() {
		try {
			return constructor.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String[] getIncompatibleComponents() {
		return new String[] { code };
	}

}
