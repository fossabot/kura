package org.eclipse.kura.protocol.can.arrowhead.rest;

import java.io.IOException;
import java.net.URI;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestRequestThread implements Runnable {

	private LinkedList<Request> requestQueue = new LinkedList<Request>();
	private DefaultHttpClient httpClient;
	private Logger logger;
	private Thread requestThread;
	
	private static final int CONNECTION_TIMEOUT_MS = 5000;
	
	private class HttpEntityEnclosingRequest extends HttpEntityEnclosingRequestBase {

		private String method;
		
		HttpEntityEnclosingRequest(String uri, String method) {
			this.setURI(URI.create(uri));
			this.method = method;
		}

		@Override
		public String getMethod() {
			return method;
		}

	}

	public RestRequestThread() {
		logger = LoggerFactory.getLogger("ArrowheadRestRequestThread");
		httpClient = new DefaultHttpClient();
		
		this.requestThread = new Thread(this);
		this.requestThread.start();
		
		httpClient.getParams().setParameter("http.socket.timeout", CONNECTION_TIMEOUT_MS);
		httpClient.getParams().setParameter("http.connection.timeout", CONNECTION_TIMEOUT_MS);
	}

	public interface RequestCompletionListener {
		public void onCompleted(int status, JsonObject respnse);
	}
	
	private void notifyListenerSafe(RequestCompletionListener listener, int responseStatus, JsonObject data) {
		try {
			if (listener != null)
				listener.onCompleted(responseStatus, data);
		} catch (Exception e) {
			logger.error("got exception: ", e);
		}
	}

	@Override
	public void run() {

		Request req = null;

		while (!requestThread.isInterrupted()) {

			synchronized (this) {
				while (requestQueue.size() == 0)
					try {
						this.wait();
					} catch (InterruptedException e) {
						logger.info("interrupted, exiting...");
						return;
					}
				logger.info("Executing request"); // TODO remove me
				req = requestQueue.poll();
			}
			
			int responseStatus = -1;
			JsonObject data = null;
			try {

				HttpEntityEnclosingRequest httpRequest = new HttpEntityEnclosingRequest(req.getUri(), req.getMethod());
				httpRequest.setHeader("Accept", "application/json");

				Map<String, String> payload = req.getPayload();

				if (payload != null && payload.size() > 0) {
					JsonObjectBuilder builder = Json.createObjectBuilder();

					for (Entry<String, String> pair : payload.entrySet())
						builder.add(pair.getKey(), pair.getValue());

					String jsonStr = builder.build().toString();
					logger.info(jsonStr); // TODO remove me
					
					StringEntity entity = new StringEntity(jsonStr);
					entity.setContentType("application/json");
					httpRequest.setEntity(entity);
				}

				HttpResponse response = httpClient.execute(httpRequest);
				logger.info("got response, status code: " + response.getStatusLine().getStatusCode()); // TODO remove me

				responseStatus = response.getStatusLine().getStatusCode();
				HttpEntity e = response.getEntity();

				if (e != null && e.getContentType().getValue().equals("application/json")) {
					logger.info("got body");
					JsonReader reader = Json.createReader(e.getContent());
					data = reader.readObject();
					logger.info("" + data); // TODO remove me
					reader.close();
					e.consumeContent();
				}

				
				

			} catch (IOException e) {
				logger.error("got IOException", e);
			} catch (Exception e) {
				logger.error("got unexpected exception", e);
			} finally {
				notifyListenerSafe(req.listener, responseStatus, data);
			}
		}
		logger.info("REST request thread interrupted, exiting");
	}

	public synchronized void runRequest(String uri, String method, Map<String, String> body,
			RequestCompletionListener listener) {
		if (!requestThread.isAlive()) {
			logger.info("request thread died, restarting..");
			requestThread = new Thread(this);
			requestThread.start();
		}
		this.requestQueue.add(new Request(uri, method, body, listener));
		this.notify();
	}

	public void interrupt() {
		this.requestThread.interrupt();
	}
	
	public class Request {

		private String uri;
		private String method;
		private Map<String, String> payload;
		private RequestCompletionListener listener;

		public Request(String uri, String method, Map<String, String> payload, RequestCompletionListener listener) {
			this.uri = uri;
			this.method = method;
			this.payload = payload;
			this.listener = listener;
		}

		public String getUri() {
			return uri;
		}

		public String getMethod() {
			return method;
		}

		public Map<String, String> getPayload() {
			return payload;
		}

		public RequestCompletionListener getListener() {
			return listener;
		}
	}
}
