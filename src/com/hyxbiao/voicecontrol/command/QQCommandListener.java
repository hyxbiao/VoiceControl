package com.hyxbiao.voicecontrol.command;

import android.os.Bundle;

import com.hyxbiao.voicecontrol.client.VoiceControlClient;
import com.hyxbiao.voicecontrol.protocol.Packet;

public class QQCommandListener implements VoiceCommandListener {

	@Override
	public void onExecute(String cmd, int code) {
		// TODO Auto-generated method stub
		Bundle bundle = new Bundle();
		bundle.putInt(VoiceControlClient.KEY_VERSION, Packet.VERSION);
		bundle.putInt(VoiceControlClient.KEY_TARGET, Packet.TARGET_TPMINI);
		bundle.putInt(VoiceControlClient.KEY_TYPE, Packet.TYPE_QQ);
		bundle.putInt(VoiceControlClient.KEY_COMMAND, code);
		VoiceControlClient client = new VoiceControlClient(bundle);
		Thread thread = new Thread(client);
		thread.start();
	}

}
