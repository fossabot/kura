package org.eclipse.kura.protocol.can.arrowhead;

import org.eclipse.kura.KuraException;
import org.eclipse.kura.protocol.can.arrowhead.control.ControlMessage;
import org.eclipse.kura.protocol.can.arrowhead.rest.ArrowheadRestClient;
import org.eclipse.kura.protocol.can.cs.data.MotoTronDataSnapshot;
import org.eclipse.kura.protocol.can.cs.data.PrivateCSDataSnapshot;
import org.eclipse.kura.protocol.can.cs.data.PublicCSDataSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class T312ApplicationLogic implements ArrowheadCanSocketImpl.ApplicationLogic {

	private static final String BASE_URI = "http://mml.arces.unibo.it:10750"; // TODO make this configurable
	
	private ArrowheadCanSocketImpl impl;
	private ArrowheadRestClient client = new ArrowheadRestClient(BASE_URI);
	private Logger logger = LoggerFactory.getLogger(T312ApplicationLogic.class);
	
	public T312ApplicationLogic(ArrowheadCanSocketImpl impl) throws KuraException {
		this.impl = impl;
	}
	
	@Override
	public void onPrivateCSMessage(int code, PrivateCSDataSnapshot data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPublicCSMessage(int code, PublicCSDataSnapshot snapshot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMotoTronCSMessage(int code, MotoTronDataSnapshot snapshot) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onControlMessage(ControlMessage message) {
		logger.info(message.toString());
	}

}
