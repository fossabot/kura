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
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestRequestThread extends Thread {

	private LinkedList<Request> requestQueue = new LinkedList<Request>();
	private HttpClient httpClient;
	private Logger logger;

	private class HttpGetWithBody extends HttpEntityEnclosingRequestBase {

		public HttpGetWithBody(String uri) {
			this.setURI(URI.create(uri));
		}

		@Override
		public String getMethod() {
			return "GET";
		}

	}

	public RestRequestThread() {
		logger = LoggerFactory.getLogger("ArrowheadRestRequestThread");
		httpClient = HttpClients.createDefault();
		this.start();
	}

	public interface RequestCompletionListener {
		public void onCompleted(int status, JsonObject respnse);
	}

	@Override
	public void run() {

		synchronized (this) {

			while (requestQueue.size() == 0)
				try {
					this.wait();
				} catch (InterruptedException e) {
					logger.info("interrupted, exiting...");
					return;
				}

			
			logger.info("Executing request"); // TODO remove me
			Request req = requestQueue.poll();
			int responseStatus = -1;
			
			JsonObject data = null;
			try {

				HttpGetWithBody get = new HttpGetWithBody(req.getUri());
				get.setHeader("Accept", "application/json");

				Map<String, String> payload = req.getPayload();

				if (payload != null && payload.size() > 0) {
					JsonObjectBuilder builder = Json.createObjectBuilder();

					for (Entry<String, String> pair : payload.entrySet())
						builder.add(pair.getKey(), pair.getValue());

					String jsonStr = builder.build().toString();

					StringEntity entity = new StringEntity(jsonStr);
					entity.setContentType("application/json");
					get.setEntity(entity);
				}

				HttpResponse response = httpClient.execute(get);
				logger.info("got response"); // TODO remove me 
				
				responseStatus = response.getStatusLine().getStatusCode();
				HttpEntity e = response.getEntity();
				
				if (e != null && e.getContentType().getValue().equals("application/json")) {
					logger.info("got payload");
					JsonReader reader = Json.createReader(e.getContent());
					data = reader.readObject();
					logger.info("" + data); // TODO remove me
					reader.close();
				}
				
			} catch (IOException e) {
				logger.error(e + " " + e.getMessage());
			} finally {
				if (req.listener != null)
					req.listener.onCompleted(responseStatus, data);
			}
		}
	}

	public synchronized void runRequest(String uri, String method, Map<String, String> body, RequestCompletionListener listener) {
		this.requestQueue.add(new Request(uri, method, body, listener));
		this.notify();
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
