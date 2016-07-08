package org.eclipse.kura.protocol.inemo.message;

public class GetEventInfoRequestMessage extends GetEventInfoMessage{
	public GetEventInfoRequestMessage(int eventIndex) {
		super();
		this.setLenght(0x02);
		int[] data= new int[2];
		data[0]= super.messageId;
		data[1]= eventIndex;
		this.setData(data);
	}

}
