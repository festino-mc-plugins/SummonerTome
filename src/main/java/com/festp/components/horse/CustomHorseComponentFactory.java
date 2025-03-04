package com.festp.components.horse;

import com.festp.components.ITomeComponent;
import com.festp.tome.IComponentFactory;

public class CustomHorseComponentFactory implements IComponentFactory
{
	public String getCode() {
		return CustomHorseComponent.CODE;
	}

	public ITomeComponent create() {
		return new CustomHorseComponent();
	}

	public String[] getIncompatibleComponents() {
		return new String[] { CustomHorseComponent.CODE, HorseComponent.CODE };
	}

}
