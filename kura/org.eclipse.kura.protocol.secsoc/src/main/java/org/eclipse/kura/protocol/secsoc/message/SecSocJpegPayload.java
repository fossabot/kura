package org.eclipse.kura.protocol.secsoc.message;

public class SecSocJpegPayload {
	
	private int   jpegSize;
	private byte[] jpegPic;
	
	public int getJpegSize() {
		return jpegSize;
	}
	public byte[] getJpegPic() {
		return jpegPic;
	}
	public void setJpegSize(int jpegSize) {
		this.jpegSize = jpegSize;
	}
	public void setJpegPic(byte[] jpegPic2) {
		this.jpegPic = jpegPic2;
	}
}
