package com.hyxbiao.voicecontrol.ui;

import com.hyxbiao.voicecontrol.client.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class QQControlFragment extends Fragment {
	
	
	public QQControlFragment() { 
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getActivity().setTitle(getResources().getString(R.string.qq_control_title));
		return inflater.inflate(R.layout.qq_content, null);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
}
