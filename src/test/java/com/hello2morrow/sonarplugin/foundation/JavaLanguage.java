package com.hello2morrow.sonarplugin.foundation;

import org.sonar.api.resources.AbstractLanguage;

public class JavaLanguage extends AbstractLanguage {

	public static final JavaLanguage INSTANCE = new JavaLanguage();
	
	private JavaLanguage()
	{
		super("java", "Java");
	}
	
	public String[] getFileSuffixes() {
		return new String[]{".java"};
	}

}
