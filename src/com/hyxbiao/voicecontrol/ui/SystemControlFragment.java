package com.hyxbiao.voicecontrol.ui;

import com.hyxbiao.voicecontrol.client.R;
import com.hyxbiao.voicecontrol.command.VoiceCommandManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class SystemControlFragment extends Fragment implements OnClickListener {
	
	public SystemControlFragment() { 
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getActivity().setTitle(getResources().getString(R.string.system_control_title));
		View v = inflater.inflate(R.layout.system_content, null);
		v.findViewById(R.id.btn_exit).setOnClickListener(this);
		v.findViewById(R.id.btn_back).setOnClickListener(this);
		v.findViewById(R.id.btn_volume_down).setOnClickListener(this);
		v.findViewById(R.id.btn_volume_up).setOnClickListener(this);
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
	        case R.id.btn_exit:
	        	commandManager.execute(getString(R.string.system_home));
	        	break;
	        case R.id.btn_back:
	        	commandManager.execute(getString(R.string.system_back));
	        	break;
	        case R.id.btn_volume_down:
	        	commandManager.execute(getString(R.string.system_volume_down));
	        	break;
	        case R.id.btn_volume_up:
	        	commandManager.execute(getString(R.string.system_volume_up));
	        	break;
        };
	}
	
}
