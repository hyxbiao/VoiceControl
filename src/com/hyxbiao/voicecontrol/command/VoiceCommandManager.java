package com.hyxbiao.voicecontrol.command;

import android.content.Context;
import android.os.Bundle;

import com.baidu.android.speech.tts.TextToSpeech;
import com.baidu.android.speech.tts.UtteranceProgressListener;
import com.hyxbiao.voicecontrol.client.VoiceControlClient;
import com.hyxbiao.voicecontrol.exception.UnRecognitionException;
import com.hyxbiao.voicecontrol.protocol.Packet;

public class VoiceCommandManager implements UtteranceProgressListener{

	private final static int CMD_UNKNOWN	= 0;
	private final static int CMD_PLAY		= 1;
	private final static int CMD_PAUSE		= 2;
	private final static int CMD_PREVIOUS	= 3;
	private final static int CMD_NEXT		= 4;
	
    private TextToSpeech mTextToSpeech;
    private VoiceCommand mVoiceCommand;
    private VoiceCommandListener mVoiceCommandListener;
    
    public VoiceCommandManager(Context context) {
    	mTextToSpeech = new TextToSpeech(context);
        mTextToSpeech.setOnUtteranceProgressListener(this);
        
        mVoiceCommand = new VoiceCommand();
        mVoiceCommandListener = new DefaultCommandListener();
        mVoiceCommand.addCommand("上一集", Packet.CMD_VIDEO_PREVIOUS, mVoiceCommandListener);
        mVoiceCommand.addCommand("下一集", Packet.CMD_VIDEO_NEXT, mVoiceCommandListener);
        mVoiceCommand.addCommand("播放", Packet.CMD_VIDEO_PLAY, mVoiceCommandListener);
        mVoiceCommand.addCommand("暂停", Packet.CMD_VIDEO_PAUSE, mVoiceCommandListener);
        
        VoiceCommandListener systemCommandListenner = new SystemCommandListener();
        mVoiceCommand.addCommand("放小点声音", Packet.CMD_SYSTEM_VOLUME_DOWN, systemCommandListenner);
        mVoiceCommand.addCommand("放大点声音", Packet.CMD_SYSTEM_VOLUME_UP, systemCommandListenner);
        mVoiceCommand.addCommand("打开QQ", Packet.CMD_SYSTEM_OPEN_QQ, systemCommandListenner);
        mVoiceCommand.addCommand("打开奇艺", Packet.CMD_SYSTEM_OPEN_QIYI, systemCommandListenner);
    } 
    
	public void execute(String txt) {
		try {
			mVoiceCommand.execute(txt);
		} catch (UnRecognitionException e) {
			String msg = "无法识别指令";
			mTextToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
		}
	}

	@Override
	public void onDone(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStart(String arg0) {
		// TODO Auto-generated method stub
		
	}
	

	private class DefaultCommandListener implements VoiceCommandListener {

		public DefaultCommandListener() {
		}
		@Override
		public void onExecute(String cmd, int code) {
			mTextToSpeech.speak(cmd, TextToSpeech.QUEUE_FLUSH, null);
			Bundle bundle = new Bundle();
			bundle.putInt(VoiceControlClient.KEY_VERSION, Packet.VERSION);
			bundle.putInt(VoiceControlClient.KEY_TARGET, Packet.TARGET_TPMINI);
			bundle.putInt(VoiceControlClient.KEY_TYPE, Packet.TYPE_VIDEO);
			bundle.putInt(VoiceControlClient.KEY_COMMAND, code);
			VoiceControlClient client = new VoiceControlClient(bundle);
			Thread thread = new Thread(client);
			thread.start();
		}
	}
}
