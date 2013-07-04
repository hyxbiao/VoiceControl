package com.hyxbiao.voicecontrol.ui;

import com.hyxbiao.voicecontrol.client.R;
import com.hyxbiao.voicecontrol.command.VoiceCommandManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class QQControlFragment extends Fragment implements OnClickListener {
	
	
	public QQControlFragment() { 
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getActivity().setTitle(getResources().getString(R.string.qq_control_title));
		View v = inflater.inflate(R.layout.qq_content, null);
		v.findViewById(R.id.btn_qq_open).setOnClickListener(this);
		v.findViewById(R.id.btn_qq_close).setOnClickListener(this);
		v.findViewById(R.id.btn_qq_video_baobao).setOnClickListener(this);
		v.findViewById(R.id.btn_qq_video_test).setOnClickListener(this);
		v.findViewById(R.id.btn_qq_video_screen_max).setOnClickListener(this);
		v.findViewById(R.id.btn_qq_video_screen_min).setOnClickListener(this);
		v.findViewById(R.id.btn_qq_video_screen_max).setOnClickListener(this);
		v.findViewById(R.id.btn_qq_video_close).setOnClickListener(this);
		return v;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onClick(View v) {
		VoiceCommandManager commandManager = ((MyApp) getActivity().getApplicationContext()).getCommandManager();
		int id = v.getId();
        switch (id) {
	        case R.id.btn_qq_open:
	        	commandManager.execute(getString(R.string.qq_open));
	        	break;
	        case R.id.btn_qq_close:
	        	commandManager.execute(getString(R.string.system_home));
	        	break;
	        case R.id.btn_qq_video_baobao:
	        	commandManager.execute(getString(R.string.qq_video_baobao));
	        	break;
	        case R.id.btn_qq_video_test:
	        	commandManager.execute(getString(R.string.qq_video_test));
	        	break;
	        case R.id.btn_qq_video_screen_max:
	        	commandManager.execute(getString(R.string.qq_video_screen_max));
	        	break;
	        case R.id.btn_qq_video_screen_min:
	        	commandManager.execute(getString(R.string.qq_video_screen_min));
	        	break;
	        case R.id.btn_qq_video_close:
	        	commandManager.execute(getString(R.string.qq_video_close));
	        	break;
        };
	}
	
}
