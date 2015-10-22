package org.eclipse.kura.protocol.secsoc.comm;

public class SecSocMessage {
	
	private int packetLength;
	private int frameNum;
	private int mesgType;
	private int extraData;
	
	public SecSocMessage(byte[] data){
		
	}
	
	public int getPacketLength() {
		return packetLength;
	}
	public int getFrameNum() {
		return frameNum;
	}
	public int getMesgType() {
		return mesgType;
	}
	public int getExtraData() {
		return extraData;
	}
	public void setPacketLength(int packetLength) {
		this.packetLength = packetLength;
	}
	public void setFrameNum(int frameNum) {
		this.frameNum = frameNum;
	}
	public void setMesgType(int mesgType) {
		this.mesgType = mesgType;
	}
	public void setExtraData(int extraData) {
		this.extraData = extraData;
	}
	
	public void parseMessage(byte[] data){
		byte[] packetLength= new byte[4];
		int index= 0;
		for (int i=4; i<8; i++, index++){
			packetLength[index]= data[i];
		}
	}
	
}
