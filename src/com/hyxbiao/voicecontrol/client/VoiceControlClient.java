package com.hyxbiao.voicecontrol.client;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.os.Bundle;
import android.util.Log;

import com.hyxbiao.voicecontrol.protocol.Packet;

public class VoiceControlClient implements Runnable {
	private final static String TAG = "VoiceControlClient";

	public final static String KEY_VERSION = "version";
	public final static String KEY_TARGET = "target";
	public final static String KEY_TYPE = "type";
	public final static String KEY_COMMAND = "command";
	public final static String KEY_REMAIN = "remain";
	public final static String KEY_BODYLEN = "bodylen";
	public final static String KEY_PARAMS = "params";
	
//	private String IP = "127.0.0.1";
//	private String IP = "192.168.1.101";
	private String mIp = "192.168.1.102";
	private int mPort = 8300;
	
	private Socket mSocket;
	private Bundle mBundle;
	
	public VoiceControlClient(Bundle bundle) {
		mBundle = bundle;
	}
	@Override
	public void run() {
		
		try {
//			mIp = InetAddress.getLocalHost().getHostAddress();
			Log.d(TAG, "ip: " + mIp + ", port: " + mPort);
			mSocket = new Socket(mIp, mPort);
			DataOutputStream out = new DataOutputStream(mSocket.getOutputStream());
			out.writeInt(mBundle.getInt(KEY_VERSION));
			out.writeInt(mBundle.getInt(KEY_TARGET));
			out.writeInt(mBundle.getInt(KEY_TYPE));
			out.writeInt(mBundle.getInt(KEY_COMMAND));
			out.writeInt(mBundle.getInt(KEY_REMAIN, 0));
			String params = mBundle.getString(KEY_PARAMS, null);
			if(params == null) {
				out.writeLong(mBundle.getInt(KEY_BODYLEN, 0));
			} else {
				byte[] buf = params.getBytes();
				out.writeLong(mBundle.getInt(KEY_BODYLEN, buf.length));
				out.write(buf);
			}
			out.flush();
			Log.d(TAG, "send command from client");
			
			BufferedReader in = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
			String msg = in.readLine();
			Log.d(TAG, "server response: " + msg);
			mSocket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			Log.w(TAG, "UnknownHostException: " + e);
		} catch (IOException e) {
			e.printStackTrace();
			Log.w(TAG, "IOException: " + e);
		} 
	}

}
