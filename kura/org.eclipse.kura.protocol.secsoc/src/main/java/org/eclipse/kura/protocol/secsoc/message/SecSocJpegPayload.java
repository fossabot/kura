package org.eclipse.kura.protocol.secsoc.message;

public class SecSocJpegPayload {
	
	private int   jpegSize;
	private int[] jpegPic;
	
	public int getJpegSize() {
		return jpegSize;
	}
	public int[] getJpegPic() {
		return jpegPic;
	}
	public void setJpegSize(int jpegSize) {
		this.jpegSize = jpegSize;
	}
	public void setJpegPic(int[] jpegPic) {
		this.jpegPic = jpegPic;
	}
}
