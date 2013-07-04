package com.hyxbiao.voicecontrol.command;

import android.content.Context;
import android.os.Bundle;

import com.hyxbiao.voicecontrol.client.VoiceControlClient;
import com.hyxbiao.voicecontrol.protocol.Packet;

public class SystemCommandListener implements VoiceCommandListener {

	@Override
	public void onExecute(Context context, String cmd, int code) {
//		mTextToSpeech.speak(cmd, TextToSpeech.QUEUE_FLUSH, null);
		
		Bundle bundle = new Bundle();
		bundle.putInt(VoiceControlClient.KEY_VERSION, Packet.VERSION);
		bundle.putInt(VoiceControlClient.KEY_TARGET, Packet.TARGET_TPMINI);
		bundle.putInt(VoiceControlClient.KEY_TYPE, Packet.TYPE_SYSTEM);
		bundle.putInt(VoiceControlClient.KEY_COMMAND, code);
		VoiceControlClient client = new VoiceControlClient(context, bundle);
		Thread thread = new Thread(client);
		thread.start();
	}

}
