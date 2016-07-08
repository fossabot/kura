package org.eclipse.kura.protocol.inemo.message;

public class GetEventInfoMessage extends INemoMessage {
	
	protected int messageId= 0x05;
	
	public int getSync() {
		int[] data= getData();
		return data[0];
	}
	
	public int getTime() { //TODO
		int[] data= getData();
		
		return -1;
	}
	
	public int getThreshold() {
		int[] data= getData();
		int result = buildShort(data[6], data[5]); //TODO: check
		return result;
	}

	public int getXMax() {
		int[] data= getData();
		int result = data[7];
		return result;
	}
	
	public int getYMax() {
		int[] data= getData();
		int result = data[8];
		return result;
	}
	
	public int getZMax() {
		int[] data= getData();
		int result = data[9];
		return result;
	}
	
	public int getXAve() {
		int[] data= getData();
		int result = data[10];
		return result;
	}
	
	public int getYAve() {
		int[] data= getData();
		int result = data[11];
		return result;
	}
	
	public int getZAve() {
		int[] data= getData();
		int result = data[12];
		return result;
	}
	
	public int getRange() {
		int[] data= getData();
		int result = data[13];
		return result;
	}
	
	public int getXSnap() {
		int[] data= getData();
		int result = buildShort(data[15], data[14]);
		return result;
	}
	
	public int getYSnap() {
		int[] data= getData();
		int result = buildShort(data[17], data[16]);
		return result;
	}
	
	public int getZSnap() {
		int[] data= getData();
		int result = buildShort(data[19], data[18]);
		return result;
	}
	
	public int getPhi() {
		int[] data= getData();
		int result = buildShort(data[21], data[20]);
		return result;
	}
	
	public int getTheta() {
		int[] data= getData();
		int result = buildShort(data[23], data[22]);
		return result;
	}
	
	public int getPsi() {
		int[] data= getData();
		int result = buildShort(data[25], data[24]);
		return result;
	}
	
	public int getSnapstat() { //TODO
		
		return -1;
	}
	
	public int getStatus() {
		int[] data= getData();
		int result = data[27];
		return result;
	}
	
	private int buildShort(int high, int low){
		return ((0xFF & high) << 8) + (0xFF & low);
	}
}
