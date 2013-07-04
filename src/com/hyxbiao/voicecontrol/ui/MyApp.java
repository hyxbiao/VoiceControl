package com.hyxbiao.voicecontrol.ui;

import com.hyxbiao.voicecontrol.command.VoiceCommandManager;

import android.app.Application;

public class MyApp extends Application{
	
	private VoiceCommandManager mCommandManager = null;
	private String mServerIp = "127.0.0.1";

	public VoiceCommandManager getCommandManager() {
		return mCommandManager;
	}

	public void setCommandManager(VoiceCommandManager commandManager) {
		this.mCommandManager = commandManager;
	}

	public String getServerIp() {
		return mServerIp;
	}

	public void setServerIp(String mServerIp) {
		this.mServerIp = mServerIp;
	}
	
	
}
