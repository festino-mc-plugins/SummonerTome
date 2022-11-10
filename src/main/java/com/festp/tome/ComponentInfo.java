package com.festp.tome;

public class ComponentInfo
{
	public final String lorePetName;
	public final String tomeNameFormat;
	public final String soloTomeName;
	
	public ComponentInfo(String lorePetName) {
		this.lorePetName = lorePetName;
		this.tomeNameFormat = "%s";
		this.soloTomeName = null;
	}
	
	public ComponentInfo(String lorePetName, String tomeNameFormat) {
		this.lorePetName = lorePetName;
		this.tomeNameFormat = tomeNameFormat;
		this.soloTomeName = null;
	}
	
	public ComponentInfo(String lorePetName, String tomeNameFormat, String soloTomeName) {
		this.lorePetName = lorePetName;
		this.tomeNameFormat = tomeNameFormat;
		this.soloTomeName = soloTomeName;
	}
}
