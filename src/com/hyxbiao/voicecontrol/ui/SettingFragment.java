package com.hyxbiao.voicecontrol.ui;

import com.hyxbiao.voicecontrol.client.R;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class SettingFragment extends Fragment implements OnClickListener {
	
	private final static String TAG = "SettingFragment";
	
	private Handler mHandle = new Handler();
	private TextView mTestTextView = null;
	private EditText mServerIpTextView = null;
	
	public SettingFragment() { 
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getActivity().setTitle(getResources().getString(R.string.setting_title));
		View v = inflater.inflate(R.layout.setting, null);
		v.findViewById(R.id.btn_settting_serverip).setOnClickListener(this);
		mTestTextView = (TextView) v.findViewById(R.id.textView1);
		mServerIpTextView = (EditText) v.findViewById(R.id.EditText1);
		
		try {
			MyApp myApp = (MyApp) getActivity().getApplicationContext();
			mServerIpTextView.setText(myApp.getServerIp());
			
			SensorManager sensorMgr = (SensorManager) myApp.getSystemService(Context.SENSOR_SERVICE);
			Sensor sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); 
			SensorEventListener lsn = new SensorEventListener() {
				
			    public void onSensorChanged(SensorEvent e) {
			    	float x, y, z;
			    	x = e.values[SensorManager.DATA_X];   
			    	y = e.values[SensorManager.DATA_Y];   
			    	z = e.values[SensorManager.DATA_Z];
			    	
					mTestTextView.setText("x="+(int)x+","+"y="+(int)y+","+"z="+(int)z);
			    }

				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {
					// TODO Auto-generated method stub
				}

			};
			//注册listener，第三个参数是检测的精确度
			sensorMgr.registerListener(lsn, sensor, SensorManager.SENSOR_DELAY_GAME);
		} catch (Exception e) {
			Log.d(TAG, "set server ip fail", e);
		}
		return v;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		MyApp myApp = (MyApp) getActivity().getApplicationContext();
		Log.d(TAG, "server ip :" + mServerIpTextView.getText().toString());
		myApp.setServerIp(mServerIpTextView.getText().toString());
	}

}
