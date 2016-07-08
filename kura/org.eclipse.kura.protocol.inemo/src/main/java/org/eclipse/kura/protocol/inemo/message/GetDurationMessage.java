package org.eclipse.kura.protocol.inemo.message;

import org.eclipse.kura.KuraErrorCode;
import org.eclipse.kura.KuraException;

public class GetDurationMessage extends INemoMessage {
	
	protected int messageId= 0x10;

	public String getStatus() throws KuraException {
		int[] data= getData();
		if (data[2] == 0x00) {
			return "Ok";
		} 
		throw new KuraException(KuraErrorCode.INVALID_MESSAGE_EXCEPTION);
	}
}
