package com.hyxbiao.voicecontrol.protocol;

/*
 *     4    |    4   |  4   |    4    |   4    |    8     |
 *  Version | Target | Type | Command | Remain | body_len |  
 */

public class Packet {
	public final static int HEAD_LEN			=	28;
	public final static int VERSION				=	1;
	
	public final static int TARGET_UNKNOWN		=	0;
	public final static int TARGET_TPMINI		=	1;
	
	public final static int TYPE_UNKNOWN		=	0;
	public final static int TYPE_DESKTOP		=	1;
	public final static int TYPE_VIDEO			=	2;
	
	
	//command for video
	public final static int CMD_VIDEO_UNKNOWN	=	0;
	public final static int CMD_VIDEO_PLAY		=	1;	
	public final static int CMD_VIDEO_PAUSE		=	2;	
	public final static int CMD_VIDEO_NEXT		=	3;	
}
