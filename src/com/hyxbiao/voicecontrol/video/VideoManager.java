package com.hyxbiao.voicecontrol.video;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;

import com.baidu.android.speech.tts.TextToSpeech;
import com.baidu.android.speech.tts.UtteranceProgressListener;
import com.hyxbiao.voicecontrol.client.VoiceControlClient;
import com.hyxbiao.voicecontrol.lib.VoiceCommand;
import com.hyxbiao.voicecontrol.lib.VoiceCommandListener;
import com.hyxbiao.voicecontrol.protocol.Packet;

public class VideoManager extends VoiceCommand implements UtteranceProgressListener{

	private final static int CMD_UNKNOWN	= 0;
	private final static int CMD_PLAY		= 1;
	private final static int CMD_PAUSE		= 2;
	private final static int CMD_PREVIOUS	= 3;
	private final static int CMD_NEXT		= 4;
	
    private TextToSpeech mTextToSpeech;
    
    public VideoManager(Context context) {
    	mTextToSpeech = new TextToSpeech(context);
        mTextToSpeech.setOnUtteranceProgressListener(this);
        
        //mVoiceCommand = new VoiceCommand();
        this.addCommand("上一集", new VideoAction(CMD_PREVIOUS));
        this.addCommand("下一集", new VideoAction(CMD_NEXT));
        this.addCommand("播放", new VideoAction(CMD_PLAY));
        this.addCommand("暂停", new VideoAction(CMD_PAUSE));
        this.addCommand("放小点声音", new VideoAction());
        this.addCommand("放大点声音", new VideoAction());
        this.addCommand("打开视频", new VideoAction());
        this.addCommand("打开奇艺", new VideoAction());
    } 
	protected void onCommandError(int error) {
		String msg = "无法识别指令";
		mTextToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null);
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
	
	private class VideoAction implements VoiceCommandListener {

		private int mCommandCode;

		public VideoAction() {
			mCommandCode = CMD_UNKNOWN;
		}
		public VideoAction(int commandCode) {
			mCommandCode = commandCode;
		}
		@Override
		public void onAction(String cmd) {
			//mTextToSpeech.speak(cmd, TextToSpeech.QUEUE_FLUSH, null);
			//if(mCommandCode != CMD_UNKNOWN) {
				Bundle bundle = new Bundle();
				bundle.putInt(VoiceControlClient.KEY_VERSION, Packet.VERSION);
				bundle.putInt(VoiceControlClient.KEY_TARGET, Packet.TARGET_TPMINI);
				bundle.putInt(VoiceControlClient.KEY_TYPE, Packet.TYPE_VIDEO);
				bundle.putInt(VoiceControlClient.KEY_COMMAND, Packet.CMD_VIDEO_PLAY);
				VoiceControlClient client = new VoiceControlClient(bundle);
				Thread thread = new Thread(client);
				thread.start();
			//}
		}
		
	}

}
