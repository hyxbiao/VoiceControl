package com.hyxbiao.voicecontrol.ui;

import com.hyxbiao.voicecontrol.client.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SystemFragment extends Fragment {
	
	
	public SystemFragment() { 
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// construct the RelativeLayout
		
//		RelativeLayout v = new RelatieLayout(getActivity());
//		return v;
		return inflater.inflate(R.layout.system_content, container);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
}
