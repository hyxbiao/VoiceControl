package com.hyxbiao.voicecontrol.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.util.Log;

public class VoiceCommand {
	private static final String TAG = "VoiceCommand";
	
	public static final int UNKNOWN_TEXT = 0;
	
	protected HashMap<String, Command> mCommandMap;
	public VoiceCommand() {
		mCommandMap = new HashMap<String, Command>();
	}
	
	public void addCommand(String desc, VoiceCommandListener listener) {
		mCommandMap.put(desc, new Command(desc, listener));
	}
	
	protected void onCommandError(int error) {
		
	}
	public void execute(String txt) {
		String desc = recognize(txt);
		if(desc == null) {
			Log.w(TAG, "no recognize cmd: " + txt);
			onCommandError(UNKNOWN_TEXT);
			return;
		}
		Command cmd = mCommandMap.get(desc);
		cmd.action();
	}
	
	public void execute(ArrayList<String> txtList) {
		if(txtList.get(txtList.size() - 1) == "，") {
			txtList.remove(txtList.size() - 1);
		}
		String desc = recognize(txtList);
		if(desc == null) {
			Log.w(TAG, "no recognize cmd: " + txtList);
			return;
		}
		Command cmd = mCommandMap.get(desc);
		cmd.action();
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
	
	private String recognize(ArrayList<String> txtList) {
		String best = null;
		float bestScore = 0;
		
		Iterator<Entry<String, Command>> iter = mCommandMap.entrySet().iterator(); 
		while(iter.hasNext()) {
			Entry<String, Command> pair = (Entry<String, Command>)iter.next();
			String desc = (String)pair.getKey();
			Command cmd = (Command)pair.getValue();
			String cmdPinyin = cmd.getPinyin();
			Log.d(TAG, "cmd[" + desc + "], pinyin[" + cmdPinyin + "]");
			float score = 0;
			for(String txt : txtList) {
				String pinyin = PinyinUtil.spell(txt);
				float similarity = Algorithm.levenshtein(cmdPinyin, pinyin);
				Log.d(TAG, "txt[" + txt + "], pinyin[" + pinyin + "], similarity[" + similarity + "]");
				score += similarity;
			}
			score /= txtList.size();
			Log.d(TAG, "cmd[" + desc + "], score: [" + score + "]");
			if(score > bestScore) {
				bestScore = score;
				best = desc;
			}
		}
		return best;
	}
	
	private class Command {
		private String mCmd;
		private VoiceCommandListener mListener;
		
		private String mPinyin;
		
		@SuppressWarnings("unused")
		public Command(String cmd, VoiceCommandListener listener) {
			mCmd = cmd;
			mListener = listener;
			mPinyin = PinyinUtil.spell(cmd);
		}
		public String getPinyin() {
			return mPinyin;
		}
		public void action() {
			mListener.onAction(mCmd);
		}
	}
}
