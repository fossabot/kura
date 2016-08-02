package org.eclipse.kura.protocol.can.arrowhead.control;

public class ControlMessage {

	private int messageType;
	
	public ControlMessage(int messageType) {
		this.messageType = messageType;
	}
	
	public int getMessageType() {
		return messageType;
	}

	@SuppressWarnings("serial")
	public class InvalidControlMessageException extends Throwable {
		public InvalidControlMessageException(String message) {
			super(message);
		}
	}
}
