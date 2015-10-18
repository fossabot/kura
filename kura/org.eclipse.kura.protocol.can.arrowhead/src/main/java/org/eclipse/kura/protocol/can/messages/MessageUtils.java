package org.eclipse.kura.protocol.can.messages;

public class MessageUtils {
	
	public static int buildShort(byte high, byte low){
		return ((0xFF & (int) high) << 8) + ((0xFF & (int) low));
	}
}
