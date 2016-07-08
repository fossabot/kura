package org.eclipse.kura.protocol.inemo.message;

import org.eclipse.kura.KuraErrorCode;
import org.eclipse.kura.KuraException;

public class SyncPollingMessage extends INemoMessage {

	protected int messageId= 0x04;
	
	public int getEventIndex() {
		int[] data= getData();
		return data[0];
	}
	
	public String getStatus() throws KuraException {
		int[] data= getData();
		if (data[1] == 0x00) {
			return "Ok";
		} 
		throw new KuraException(KuraErrorCode.INVALID_MESSAGE_EXCEPTION);
	}
}
