package com.hyxbiao.voicecontrol.ui;

import com.hyxbiao.voicecontrol.command.VoiceCommandManager;

import android.app.Application;

public class MyApp extends Application{
	
	private VoiceCommandManager mCommandManager = null;

	public VoiceCommandManager getCommandManager() {
		return mCommandManager;
	}

	public void setCommandManager(VoiceCommandManager commandManager) {
		this.mCommandManager = commandManager;
	}
	
	
}
