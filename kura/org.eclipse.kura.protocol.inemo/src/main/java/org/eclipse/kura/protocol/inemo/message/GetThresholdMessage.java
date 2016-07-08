package org.eclipse.kura.protocol.inemo.message;

import org.eclipse.kura.KuraErrorCode;
import org.eclipse.kura.KuraException;

public class GetThresholdMessage extends INemoMessage{

	protected int messageId= 0x06;
	
	public String getStatus() throws KuraException {
		int[] data= getData();
		if (data[2] == 0x00) {
			return "Ok";
		} 
		throw new KuraException(KuraErrorCode.INVALID_MESSAGE_EXCEPTION);
	}
}
