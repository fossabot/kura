package org.eclipse.kura.protocol.inemo.message;

public class TakeSnapshotRequestMessage extends TakeSnapshotMessage{
	
	public TakeSnapshotRequestMessage() {
		super();
		this.setLenght(0x01);
		int[] data= new int[1];
		data[0]= super.messageId;
		this.setData(data);
	}
}
