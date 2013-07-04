package com.hyxbiao.voicecontrol.command;

import android.content.Context;
import android.os.Bundle;

import com.baidu.android.speech.tts.TextToSpeech;
import com.baidu.android.speech.tts.UtteranceProgressListener;
import com.hyxbiao.voicecontrol.client.R;
import com.hyxbiao.voicecontrol.client.VoiceControlClient;
import com.hyxbiao.voicecontrol.exception.UnRecognitionException;
import com.hyxbiao.voicecontrol.protocol.Packet;

public class VoiceCommandManager implements UtteranceProgressListener{

	
    private TextToSpeech mTextToSpeech;
    private VoiceCommand mVoiceCommand;
    private VoiceCommandListener mVoiceCommandListener;
    
    public VoiceCommandManager(Context context) {
    	mTextToSpeech = new TextToSpeech(context);
        mTextToSpeech.setOnUtteranceProgressListener(this);
        
        mVoiceCommand = new VoiceCommand(context);
        
        VoiceCommandListener systemCommandListenner = new SystemCommandListener();
        mVoiceCommand.addCommand(
        		context.getString(R.string.system_volume_down), Packet.CMD_SYSTEM_VOLUME_DOWN, systemCommandListenner);
        mVoiceCommand.addCommand(
        		context.getString(R.string.system_volume_up), Packet.CMD_SYSTEM_VOLUME_UP, systemCommandListenner);
        mVoiceCommand.addCommand(
        		context.getString(R.string.qq_open), Packet.CMD_SYSTEM_OPEN_QQ, systemCommandListenner);
        mVoiceCommand.addCommand(
        		context.getString(R.string.qiyi_open), Packet.CMD_SYSTEM_OPEN_QIYI, systemCommandListenner);
        mVoiceCommand.addCommand(
        		context.getString(R.string.system_home), Packet.CMD_SYSTEM_HOME, systemCommandListenner);
        mVoiceCommand.addCommand(
        		context.getString(R.string.system_back), Packet.CMD_SYSTEM_BACK, systemCommandListenner);
        
        mVoiceCommandListener = new DefaultCommandListener();
        mVoiceCommand.addCommand(
        		context.getString(R.string.video_previous), Packet.CMD_VIDEO_PREVIOUS, mVoiceCommandListener);
        mVoiceCommand.addCommand(
        		context.getString(R.string.video_next), Packet.CMD_VIDEO_NEXT, mVoiceCommandListener);
        mVoiceCommand.addCommand(
        		context.getString(R.string.video_play), Packet.CMD_VIDEO_PLAY, mVoiceCommandListener);
        mVoiceCommand.addCommand(
        		context.getString(R.string.video_pause), Packet.CMD_VIDEO_PAUSE, mVoiceCommandListener);
        
        VoiceCommandListener qqCommandListenner = new QQCommandListener();
        mVoiceCommand.addCommand(
        		context.getString(R.string.qq_video_baobao), Packet.CMD_QQ_VIDEO_BAOBAO, qqCommandListenner);
        mVoiceCommand.addCommand(
        		context.getString(R.string.qq_video_test), Packet.CMD_QQ_VIDEO_TEST, qqCommandListenner);
        mVoiceCommand.addCommand(
        		context.getString(R.string.qq_video_screen_max), Packet.CMD_QQ_VIDEO_SCREEN_MAX, qqCommandListenner);
        mVoiceCommand.addCommand(
        		context.getString(R.string.qq_video_screen_min), Packet.CMD_QQ_VIDEO_SCREEN_MIN, qqCommandListenner);
        mVoiceCommand.addCommand(
        		context.getString(R.string.qq_video_close), Packet.CMD_QQ_VIDEO_CLOSE, qqCommandListenner);
        
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
		public void onExecute(Context context, String cmd, int code) {
			mTextToSpeech.speak(cmd, TextToSpeech.QUEUE_FLUSH, null);
			Bundle bundle = new Bundle();
			bundle.putInt(VoiceControlClient.KEY_VERSION, Packet.VERSION);
			bundle.putInt(VoiceControlClient.KEY_TARGET, Packet.TARGET_TPMINI);
			bundle.putInt(VoiceControlClient.KEY_TYPE, Packet.TYPE_VIDEO);
			bundle.putInt(VoiceControlClient.KEY_COMMAND, code);
			VoiceControlClient client = new VoiceControlClient(context, bundle);
			Thread thread = new Thread(client);
			thread.start();
		}
	}
}
