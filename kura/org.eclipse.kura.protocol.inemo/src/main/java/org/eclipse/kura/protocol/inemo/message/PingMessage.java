package org.eclipse.kura.protocol.inemo.message;

public class PingMessage extends INemoMessage {
	protected int messageId= 0x00;
	
	public String getStatus() {
		int[] data= getData();
		if (data[0] == 0x00) {
			return "Ok";
		} 
		return "Error";
	}

}
