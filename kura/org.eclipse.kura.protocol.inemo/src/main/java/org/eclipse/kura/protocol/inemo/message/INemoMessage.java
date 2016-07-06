package org.eclipse.kura.protocol.inemo.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class INemoMessage {
	@SuppressWarnings("unused")
	private static final Logger s_logger = LoggerFactory.getLogger(INemoMessage.class);

	private static final int HEADER     = 0x76;
	
	private int    lenght;
	private int[]  data;

	
	protected INemoMessage(){
	}
	
	public int getLenght() {
		return lenght;
	}
	public void setLenght(int lenght) {
		this.lenght = lenght;
	}
	public int getHeader() {
		return HEADER;
	}
	public int[] getData() {
		return data;
	}
	public void setData(int[] data) {
		this.data = data;
	}
	public int getChecksum() {
		return evalChecksum();
	}
	public int[] getMessageAsIntArray() {
		int[] tempMessage= new int[data.length + 3];
		tempMessage[0] = HEADER;
		tempMessage[1] = getLenght();
		
		for (int i= 0; i < data.length; i++) {
			tempMessage[i + 2] = data[i];
		}
		tempMessage[tempMessage.length - 1] = getChecksum();
		return tempMessage;
	}
	
	public byte[] getMessageAsByteArray() {
		int[] tempMessageInt = getMessageAsIntArray();
		byte[] tempMessage = new byte[tempMessageInt.length];
		for (int i= 0; i < tempMessage.length; i++) {
			tempMessage[i]= (byte) tempMessageInt[i];
		}
		return tempMessage;
	}

	private int evalChecksum() {
		int checksum = HEADER + lenght;
		for (int i= 0; i < data.length; i++) {
			checksum+= data[i];
		}
		checksum= checksum & 0xFF;
		return checksum;
	}
}
