package com.hyxbiao.voicecontrol.command;

import android.content.Context;

public interface VoiceCommandListener {

	public void onExecute(Context context, String cmd, int code);
}
