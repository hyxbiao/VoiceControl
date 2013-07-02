package com.hyxbiao.voicecontrol.ui;

import com.hyxbiao.voicecontrol.client.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingFragment extends Fragment {
	
	
	public SettingFragment() { 
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// construct the RelativeLayout
		
//		RelativeLayout v = new RelatieLayout(getActivity());
//		return v;
		return inflater.inflate(R.layout.setting, container);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
}
