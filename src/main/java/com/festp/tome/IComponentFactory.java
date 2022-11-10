package com.festp.tome;

import com.festp.components.ITomeComponent;

public interface IComponentFactory
{
	public String getCode();
	public ITomeComponent create();
	public String[] getIncompatibleComponents();
}
