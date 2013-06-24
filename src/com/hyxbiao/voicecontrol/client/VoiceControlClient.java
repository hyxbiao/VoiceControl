package com.hyxbiao.voicecontrol.client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.hyxbiao.voicecontrol.protocol.Packet;

public class VoiceControlClient implements Runnable {

	private String IP = "192.168.1.100";
	private int PORT = 8300;

	private Socket mSocket;
	private Packet mPacket;
	
	public VoiceControlClient(Packet packet) {
		mPacket = packet;
	}
	@Override
	public void run() {
		
		try {
			mSocket = new Socket(IP, PORT);
			DataOutputStream out = new DataOutputStream(mSocket.getOutputStream());
			int cmd = 0;
			out.writeInt(cmd);
			mSocket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
