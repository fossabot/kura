package org.eclipse.kura.protocol.inemo.message;

public class GetEventRequestMessage extends GetEventMessage {

	public GetEventRequestMessage(int eventIndex) { //for the first request
		super();
		this.setLenght(0x02);
		int[] data= new int[2];
		data[0]= super.messageId;
		data[1]= eventIndex;
		this.setData(data);
	}
	
	public GetEventRequestMessage(int eventIndex, int chunk, int nChunks) { //for the other requests
		super();
		this.setLenght(0x06);
		int[] data= new int[6];
		data[0]= super.messageId;
		data[1]= eventIndex;
		data[2]= (chunk >> 8) & 0xFF; //to check this
		data[3]= chunk & 0xFF;
		data[4]= (nChunks >> 8) & 0xFF; //to check this
		data[5]= nChunks & 0xFF;
		this.setData(data);
	}
}
