package org.eclipse.kura.protocol.inemo.message;

public class SyncPollingRequestMessage extends SetPollingMessage{

	public SyncPollingRequestMessage() {
		super();
		this.setLenght(0x06);
		int[] data= new int[6];
		data[0]= super.messageId;
		data[1]= 0x00; //sync from internal RTC
		data[2]= 0x00; //The next four ints represents the current time
		data[3]= 0x00; //For demo purposes are set to 0.
		data[4]= 0x00;
		data[5]= 0x00;
		this.setData(data);
	}
}
