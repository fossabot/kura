package org.eclipse.kura.protocol.inemo.message;

/**
 * 
 * @author matteo.maiero
 * Ping message Class: extends INemo message and uses its methods to construct a simple 
 * message used to ping the iNemo device
 * 
 */
public class PingRequestMessage extends PingMessage {
	/**
	 * This constructor generates a Ping message
	 */
	public PingRequestMessage() {
		super();
		this.setLenght(0x01);
		int[] data= new int[1];
		data[0] = super.messageId;
		this.setData(data);
	}
}
