package org.eclipse.kura.protocol.can.arrowhead.control;

public class ControlMessageCodes {

	private ControlMessageCodes() {}
	
	public static final String MESSAGE_TYPE_METRIC_NAME = "messageType";
	
	public static final int T312_RECHARGE_REQUEST_MASK = 0x100;
	
	public static final int T312_START_RECHARGE = T312_RECHARGE_REQUEST_MASK | 0x01;
	public static final int T312_STOP_RECHARGE = T312_RECHARGE_REQUEST_MASK | 0x02;
	
}
