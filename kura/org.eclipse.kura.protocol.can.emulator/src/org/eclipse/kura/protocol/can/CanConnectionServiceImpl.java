package org.eclipse.kura.protocol.can;

import java.io.IOException;
import java.util.LinkedList;

import org.eclipse.kura.KuraException;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CanConnectionServiceImpl implements CanConnectionService {
	
	private Logger logger = LoggerFactory.getLogger(CanConnectionService.class);
	private LinkedList<CanMessage> rxQueue = new LinkedList<CanMessage>();
	
	protected synchronized void postMessage(CanMessage message) {
		rxQueue.add(message);
		this.notifyAll();
	}
	
	protected void onActivate(ComponentContext context) {
		logger.info("CanConnectionServiceImpl " + this + " activating..");
		CanConnectionEmulator.getInstance().addService(this);
	}
	
	protected void onDeactivate(ComponentContext context) {
		logger.info("CanConnectionServiceImpl " + this + " deactivating..");
		CanConnectionEmulator.getInstance().removeService(this);
	}
	
	private synchronized CanMessage receiveMessage() throws InterruptedException {
		
		while (rxQueue.size() == 0)
			this.wait(); // TODO implement a configurable rx timeout

		return rxQueue.poll();
		
	}
	
	@Override
	public void sendCanMessage(String ifName, int canId, byte[] message) throws KuraException, IOException {
		CanMessage canMessage = new CanMessage();
		canMessage.setCanId(canId);
		canMessage.setData(message);

		CanConnectionEmulator.getInstance().sendMessage(this, canMessage);
	}
	
	@Override
	public CanMessage receiveCanMessage(int can_id, int can_mask) throws KuraException, IOException {
		while(true) {
			try {
				CanMessage msg = receiveMessage();
				if (can_id != -1) {
					if (msg.getCanId() == can_id)
						return msg;
				} else if ((msg.getCanId() & can_mask) != 0)
					return msg;
				
			} catch (InterruptedException e) {
				throw KuraException.internalError("Thread interrupted");
			}
			
		}
	}

}
