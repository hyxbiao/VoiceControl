package com.hyxbiao.voicecontrol.command;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.hyxbiao.voicecontrol.exception.UnRecognitionException;
import com.hyxbiao.voicecontrol.lib.Algorithm;
import com.hyxbiao.voicecontrol.lib.PinyinUtil;

import android.util.Log;

public class VoiceCommand {
	private static final String TAG = "VoiceCommand";
	
	protected HashMap<String, Command> mCommandMap;
	
	public VoiceCommand() {
		mCommandMap = new HashMap<String, Command>();
	}
	
	public void addCommand(String desc, int code, VoiceCommandListener listener) {
		mCommandMap.put(desc, new Command(desc, code, listener));
	}
	
	public void execute(String txt) throws UnRecognitionException {
		Command cmd = mCommandMap.get(txt);
		if(cmd == null) {
			String desc = recognize(txt);
			if(desc == null) {
				Log.w(TAG, "no recognize cmd: " + txt);
				throw new UnRecognitionException(txt);
			}
			cmd = mCommandMap.get(desc);
		}
		cmd.execute();
	}
	
	private String recognize(String txt) {
		String best = null;
		float bestScore = 0;
		
		String pinyin = PinyinUtil.spell(txt);
		Iterator<Entry<String, Command>> iter = mCommandMap.entrySet().iterator(); 
		while(iter.hasNext()) {
			Entry<String, Command> pair = (Entry<String, Command>)iter.next();
			String desc = (String)pair.getKey();
			Command cmd = (Command)pair.getValue();
			String cmdPinyin = cmd.getPinyin();
			Log.d(TAG, "cmd[" + desc + "], pinyin[" + cmdPinyin + "]");
			float score = 0;
			score = Algorithm.levenshtein(cmdPinyin, pinyin);
			Log.d(TAG, "txt[" + txt + "], pinyin[" + pinyin + "], similarity[" + score + "]");
			if(score > bestScore) {
				bestScore = score;
				best = desc;
			}
		}
		return best;
	}
	
	
	private class Command {
		private String mCmd;
		private int mCode;
		private VoiceCommandListener mListener;
		
		private String mPinyin;
		
		public Command(String cmd, int code, VoiceCommandListener listener) {
			mCmd = cmd;
			mCode = code;
			mListener = listener;
			mPinyin = PinyinUtil.spell(cmd);
		}
		public String getPinyin() {
			return mPinyin;
		}
		public void execute() {
			mListener.onExecute(mCmd, mCode);
		}
	}
}
