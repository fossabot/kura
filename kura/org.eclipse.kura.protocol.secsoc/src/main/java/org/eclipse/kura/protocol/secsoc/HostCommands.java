package org.eclipse.kura.protocol.secsoc;

import java.io.IOException;

import org.eclipse.kura.KuraException;
import org.eclipse.kura.protocol.secsoc.comm.SecSocConnectionService;

public class HostCommands {
	
	private static int CMD_START_STREAMING= 0x00;
	private static int CMD_STOP_STREAMING= 0x10;
	
	private static int CMD_JPEG_STREAMING_DISABLED= 0x20;
	private static int CMD_JPEG_STREAMING_CURRENT= 0x21;
	private static int CMD_JPEG_STREAMING_REFERENCE= 0x22;
	private static int CMD_JPEG_STREAMING_ANNOTATED= 0x23;
	
	private static int CMD_BIN_STREAMING_DISABLED= 0x30;
	private static int CMD_BIN_STREAMING_BINARY_MASK= 0x31;
	private static int CMD_BIN_STREAMING_ROI_MASK= 0x32;
	
	private static int CMD_KP_STREAMING_DISABLED= 0x40;
	private static int CMD_KP_STREAMING_ACTIVATED= 0x41;
	
	private static int CMD_BLOBS_DISABLED= 0x60;
	private static int CMD_BLOBS_ACTIVATED= 0x61;
	
	private static int CMD_SWITCH= 0x80;
	
	private static int CMD_RESET_REF= 0x90;
	
	
	static void secSocStreamingStart(SecSocConnectionService connService){
		byte[] message= new byte[1];
		message[0]= (byte) CMD_START_STREAMING;
		
		try {
			connService.sendSecSocMessage(message);
		} catch (KuraException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void secSocStreamingStop(SecSocConnectionService connService){
		byte[] message= new byte[1];
		message[0]= (byte) CMD_STOP_STREAMING;
		
		try {
			connService.sendSecSocMessage(message);
		} catch (KuraException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void secSocJpegStreamingDisable(SecSocConnectionService connService){
		byte[] message= new byte[1];
		message[0]= (byte) CMD_JPEG_STREAMING_DISABLED;
		
		try {
			connService.sendSecSocMessage(message);
		} catch (KuraException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void secSocJpegStreamingCurrent(SecSocConnectionService connService){
		byte[] message= new byte[1];
		message[0]= (byte) CMD_JPEG_STREAMING_CURRENT;
		
		try {
			connService.sendSecSocMessage(message);
		} catch (KuraException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void secSocJpegStreamingReference(SecSocConnectionService connService){
		byte[] message= new byte[1];
		message[0]= (byte) CMD_JPEG_STREAMING_REFERENCE;
		
		try {
			connService.sendSecSocMessage(message);
		} catch (KuraException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void secSocJpegStreamingAnnotated(SecSocConnectionService connService){
		byte[] message= new byte[1];
		message[0]= (byte) CMD_JPEG_STREAMING_ANNOTATED;
		
		try {
			connService.sendSecSocMessage(message);
		} catch (KuraException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void secSocBinStreamingDisable(SecSocConnectionService connService){
		byte[] message= new byte[1];
		message[0]= (byte) CMD_BIN_STREAMING_DISABLED;
		
		try {
			connService.sendSecSocMessage(message);
		} catch (KuraException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void secSocBinStreamingBinaryMask(SecSocConnectionService connService){
		byte[] message= new byte[1];
		message[0]= (byte) CMD_BIN_STREAMING_BINARY_MASK;
		
		try {
			connService.sendSecSocMessage(message);
		} catch (KuraException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void secSocBinStreamingRoiMask(SecSocConnectionService connService){
		byte[] message= new byte[1];
		message[0]= (byte) CMD_BIN_STREAMING_ROI_MASK;
		
		try {
			connService.sendSecSocMessage(message);
		} catch (KuraException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static void secSocKpStreamingDisable(SecSocConnectionService connService){
		byte[] message= new byte[1];
		message[0]= (byte) CMD_KP_STREAMING_DISABLED;
		
		try {
			connService.sendSecSocMessage(message);
		} catch (KuraException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void secSocKpStreamingEnable(SecSocConnectionService connService){
		byte[] message= new byte[1];
		message[0]= (byte) CMD_KP_STREAMING_ACTIVATED;
		
		try {
			connService.sendSecSocMessage(message);
		} catch (KuraException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void secSocBlobDisable(SecSocConnectionService connService){
		byte[] message= new byte[1];
		message[0]= (byte) CMD_BLOBS_DISABLED;
		
		try {
			connService.sendSecSocMessage(message);
		} catch (KuraException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void secSocBlobEnable(SecSocConnectionService connService){
		byte[] message= new byte[1];
		message[0]= (byte) CMD_BLOBS_ACTIVATED;
		
		try {
			connService.sendSecSocMessage(message);
		} catch (KuraException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void secSocSwitch(SecSocConnectionService connService){
		byte[] message= new byte[1];
		message[0]= (byte) CMD_SWITCH;
		
		try {
			connService.sendSecSocMessage(message);
		} catch (KuraException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static void secSocResetRef(SecSocConnectionService connService){
		byte[] message= new byte[1];
		message[0]= (byte) CMD_RESET_REF;
		
		try {
			connService.sendSecSocMessage(message);
		} catch (KuraException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
