package org.eclipse.kura.protocol.secsoc.message;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.eclipse.kura.KuraErrorCode;
import org.eclipse.kura.KuraException;

public class SecSocJpegPayloadMapper {
	private final static int JPEG_MAX_SIZE= 65530;
	
	public static SecSocJpegPayload parseJpegPayload(byte[] payloadData) throws KuraException{
		SecSocJpegPayload jpegPayload= new SecSocJpegPayload();
		
		byte[] jpegSize= new byte[4];
		for (int i= 0; i < 4; i++){
			jpegSize[i]= payloadData[i];
		}
		ByteBuffer wrapped = ByteBuffer.wrap(jpegSize); // big-endian by default --> check!!!!
		int jpegSizeInt= wrapped.getInt();
		
		if(jpegSizeInt > JPEG_MAX_SIZE){
			throw new KuraException(KuraErrorCode.DECODER_ERROR);
		}
		
		jpegPayload.setJpegSize(jpegSizeInt);
		
		byte[] jpegPic= new byte[jpegSizeInt];
		jpegPic= Arrays.copyOfRange(payloadData, 4, payloadData.length);
		jpegPayload.setJpegPic(jpegPic);
		
		return jpegPayload;
	}

}
