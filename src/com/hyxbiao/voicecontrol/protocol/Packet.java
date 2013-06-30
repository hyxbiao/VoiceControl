package com.hyxbiao.voicecontrol.protocol;

/*
 *     4    |    4   |  4   |    4    |   4    |    8     |
 *  Version | Target | Type | Command | Remain | body_len |  
 */

public class Packet {
	public final static int HEAD_LEN				=	28;
	public final static int VERSION					=	1;
	
	public final static int TARGET_UNKNOWN			=	0;
	public final static int TARGET_TPMINI			=	1;
	
	public final static int TYPE_UNKNOWN			=	0;
	public final static int TYPE_SYSTEM				=	1;
	public final static int TYPE_VIDEO				=	2;
	public final static int TYPE_QQ					=	3;
	
	//command for system
	public final static int CMD_SYSTEM_UNKNOWN		=	0;
	public final static int CMD_SYSTEM_HOME			=	1;
	public final static int CMD_SYSTEM_BACK			=	2;
	public final static int CMD_SYSTEM_VOLUME_UP	=	3;
	public final static int CMD_SYSTEM_VOLUME_DOWN	=	4;
	
	public final static int CMD_SYSTEM_OPEN			=	10;
	public final static int CMD_SYSTEM_OPEN_QQ		=	11;
	public final static int CMD_SYSTEM_OPEN_QIYI	=	12;
	
	//command for video
	public final static int CMD_VIDEO_UNKNOWN		=	100;
	public final static int CMD_VIDEO_PLAY			=	101;	
	public final static int CMD_VIDEO_PAUSE			=	102;	
	public final static int CMD_VIDEO_PREVIOUS		=	103;	
	public final static int CMD_VIDEO_NEXT			=	104;	
	public final static int CMD_VIDEO_VOLUME		=	105;	
	
	
	public final static int CMD_QQ_VIDEO_UNKNOWN	=	200;
	public final static int CMD_QQ_VIDEO_BAOBAO		=	201;
	public final static int CMD_QQ_VIDEO_SCREEN_MAX	=	202;
	public final static int CMD_QQ_VIDEO_SCREEN_MIN	=	203;
	public final static int CMD_QQ_VIDEO_CLOSE		=	204;
	public final static int CMD_QQ_VIDEO_TEST		=	210;
}
