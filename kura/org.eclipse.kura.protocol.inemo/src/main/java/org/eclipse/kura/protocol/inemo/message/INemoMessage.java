package org.eclipse.kura.protocol.inemo.message;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.eclipse.kura.KuraException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class INemoMessage {
	private static final Logger s_logger = LoggerFactory.getLogger(INemoMessage.class);
	
	private static final int TYPE_JPEG  = 0x01;
	private static final int TYPE_BIN   = 0x02;
	private static final int TYPE_KP    = 0x04;
	private static final int TYPE_AREAS = 0x08;
	private static final int TYPE_LINES = 0x10;
	private static final int TYPE_BLOBS = 0x20;
	
	private byte[] messageData;
	
	private int packetLength;
	private int frameNum;
	private int mesgType;
	private int extraData;

	
	public INemoMessage(byte[] data){
		this.messageData= data;
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

	public void parseHeader(){
		byte[] headerData= Arrays.copyOfRange(messageData, 0, 15);
		byte[] packetLength= new byte[4];
		int index= 0;
		for (int i=4; i<8; i++, index++){
			packetLength[index]= headerData[i];
		}
		ByteBuffer wrapped = ByteBuffer.wrap(packetLength); // big-endian by default --> check!!!!
		int packetLengthInt= wrapped.getInt();
		setPacketLength(packetLengthInt);
		
		
		byte[] frameNumber= new byte[2];
		index= 0;
		for (int i=8; i<10; i++, index++){
			frameNumber[index]= headerData[i];
		}
		wrapped = ByteBuffer.wrap(frameNumber);
		int frameNumberInt= wrapped.getShort();
		setFrameNum(frameNumberInt);
		
		
		byte[] messageType= new byte[1];
		messageType[0]= headerData[10];
		wrapped = ByteBuffer.wrap(messageType); // big-endian by default --> check!!!!
		int messageTypeInt = wrapped.getShort(); 
		setMesgType(messageTypeInt);
		
		
		byte[] extraData= new byte[4];
		index= 0;
		for (int i=11; i<15; i++, index++){
			extraData[index]= headerData[i];
		}
		wrapped = ByteBuffer.wrap(extraData); // big-endian by default --> check!!!!
		int extraDataInt = wrapped.getInt(); 
		setExtraData(extraDataInt);
	}
	
	public void parseBody() throws KuraException{
		byte[] bodyData= Arrays.copyOfRange(messageData, 15, messageData.length - 8);
		int messageType= getMesgType();
		switch (messageType) {
			case TYPE_BIN:
			case TYPE_KP:
			case TYPE_AREAS:
			case TYPE_LINES:
			case TYPE_BLOBS:
			default: s_logger.info("Unsupported inemo message payload");
					 break;
		}
	}
}
