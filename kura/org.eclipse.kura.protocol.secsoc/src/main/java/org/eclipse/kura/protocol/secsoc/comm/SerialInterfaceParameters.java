package org.eclipse.kura.protocol.secsoc.comm;

public class SerialInterfaceParameters {
	private String device;
	private int baudrate;
	private int dataBits;
	private int stopBits;
	private int parity;
	private int timeout;
	
	
	public String getDevice() {
		return device;
	}
	public int getBaudrate() {
		return baudrate;
	}
	public int getDataBits() {
		return dataBits;
	}
	public int getStopBits() {
		return stopBits;
	}
	public int getParity() {
		return parity;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public void setBaudrate(int baudrate) {
		this.baudrate = baudrate;
	}
	public void setDataBits(int dataBits) {
		this.dataBits = dataBits;
	}
	public void setStopBits(int stopBits) {
		this.stopBits = stopBits;
	}
	public void setParity(int parity) {
		this.parity = parity;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
}
