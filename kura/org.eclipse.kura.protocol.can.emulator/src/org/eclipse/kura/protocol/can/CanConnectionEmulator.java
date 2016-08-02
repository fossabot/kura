package org.eclipse.kura.protocol.can;

import java.util.LinkedList;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CanConnectionEmulator implements BundleActivator {

	private Logger logger;
	private ServiceTracker<CanConnectionService, ServiceReference<CanConnectionService>> tracker;
	private static CanConnectionEmulator instance = null;
	private LinkedList<CanConnectionServiceImpl> services = new LinkedList<CanConnectionServiceImpl>();

	protected static CanConnectionEmulator getInstance() {
		return instance;
	}

	public synchronized void addService(CanConnectionServiceImpl impl) {
		services.add(impl);
	}
	
	public synchronized void removeService(CanConnectionServiceImpl impl) {
		services.remove(impl);
	}
	
	protected void sendMessage(CanConnectionServiceImpl sender, CanMessage message) {
		
		for (CanConnectionServiceImpl impl : services) {
			if (impl != sender)
				impl.postMessage(message);
		}
	}

	@Override
	public void start(BundleContext context) throws Exception {
		logger = LoggerFactory.getLogger(CanConnectionEmulator.class);
		logger.info("Starting up...");
		tracker = new ServiceTracker<CanConnectionService, ServiceReference<CanConnectionService>>(
				context, CanConnectionService.class.getName(), null);
		tracker.open(true);
		instance = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		logger.info("Deactivating...");
		instance = null;
		logger = null;
		tracker.close();
		tracker = null;
	}

}
