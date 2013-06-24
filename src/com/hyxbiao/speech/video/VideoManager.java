package com.hyxbiao.speech.video;

import java.util.ArrayList;

import android.content.Context;

import com.baidu.android.speech.tts.TextToSpeech;
import com.baidu.android.speech.tts.UtteranceProgressListener;
import com.hyxbiao.speech.lib.VoiceCommand;
import com.hyxbiao.speech.lib.VoiceCommandListener;

public class VideoManager extends VoiceCommand implements UtteranceProgressListener{

    //private VoiceCommand mVoiceCommand;
    
    private TextToSpeech mTextToSpeech;
    
    public VideoManager(Context context) {
    	mTextToSpeech = new TextToSpeech(context);
        mTextToSpeech.setOnUtteranceProgressListener(this);
        
        //mVoiceCommand = new VoiceCommand();
        this.addCommand("上一集", new PreviousAction());
        this.addCommand("下一集", new NextAction());
        this.addCommand("播放", new PlayAction());
        this.addCommand("暂停", new PauseAction());
        this.addCommand("放小点声音", new MinusVolumeAction());
        this.addCommand("放大点声音", new PlusVolumeAction());
        this.addCommand("打开视频", new OpenVideoAction());
        this.addCommand("打开奇艺", new OpenQiyiAction());
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
	
	private class BaseAction implements VoiceCommandListener {

		@Override
		public void onAction(String cmd) {
			// TODO Auto-generated method stub
			mTextToSpeech.speak(cmd, TextToSpeech.QUEUE_FLUSH, null);
		}
		
	}
    private class PreviousAction extends BaseAction{
    }
    
    private class NextAction extends BaseAction{

		@Override
		public void onAction(String cmd) {
			// TODO Auto-generated method stub
			mTextToSpeech.speak(cmd, TextToSpeech.QUEUE_FLUSH, null);
		}
    	
    }
    private class PlayAction extends BaseAction{
    }
    private class PauseAction extends BaseAction{
    }
    private class MinusVolumeAction extends BaseAction{
    }
    private class PlusVolumeAction extends BaseAction{
    }
    private class OpenVideoAction extends BaseAction{
    }
    private class OpenQiyiAction extends BaseAction{
    }
}
