package com.festp.tome;

import com.festp.handlers.IDataExtractor;

public class ComponentInfo
{
	private final IComponentFactory factory;
	private LanguageInfo langInfo;
	private BehaviourInfo behInfo;
	
	public ComponentInfo(IComponentFactory factory)
	{
		this.factory = factory;
	}
	
	public ComponentInfo(IComponentFactory factory, LanguageInfo langInfo)
	{
		this.factory = factory;
		this.langInfo = langInfo;
	}
	
	public ComponentInfo(IComponentFactory factory, BehaviourInfo behInfo)
	{
		this.factory = factory;
		this.behInfo = behInfo;
	}
	
	public ComponentInfo(IComponentFactory factory, LanguageInfo langInfo, BehaviourInfo behInfo)
	{
		this.factory = factory;
		this.langInfo = langInfo;
		this.behInfo = behInfo;
	}
	
	public IComponentFactory getComponentFactory() {
		return factory;
	}
	
	public LanguageInfo getLanguageInfo() {
		return langInfo;
	}
	
	/** Is non-null */
	public BehaviourInfo getBehaviourInfo() {
		if (behInfo == null)
			behInfo = new BehaviourInfo(0, null);
		return behInfo;
	}
	
	public void setLanguageInfo(LanguageInfo langInfo) {
		this.langInfo = langInfo;
	}
	
	public static class BehaviourInfo
	{
		public int banSlotsFrom;
		public IDataExtractor dataExtractor;
		
		public BehaviourInfo(int banSlotsFrom, IDataExtractor dataExtractor) {
			this.banSlotsFrom = banSlotsFrom;
			this.dataExtractor = dataExtractor;
		}
	}
	
	public static class LanguageInfo
	{
		public final String lorePetName;
		public final String tomeNameFormat;
		public final String soloTomeName;
		
		public LanguageInfo(String lorePetName) {
			this.lorePetName = lorePetName;
			this.tomeNameFormat = "%s";
			this.soloTomeName = null;
		}
		
		public LanguageInfo(String lorePetName, String tomeNameFormat) {
			this.lorePetName = lorePetName;
			this.tomeNameFormat = tomeNameFormat;
			this.soloTomeName = null;
		}
		
		public LanguageInfo(String lorePetName, String tomeNameFormat, String soloTomeName) {
			this.lorePetName = lorePetName;
			this.tomeNameFormat = tomeNameFormat;
			this.soloTomeName = soloTomeName;
		}
	}
}
