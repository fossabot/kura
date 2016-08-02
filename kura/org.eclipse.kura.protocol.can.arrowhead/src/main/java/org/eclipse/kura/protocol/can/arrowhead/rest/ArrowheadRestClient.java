package org.eclipse.kura.protocol.can.arrowhead.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.json.JsonObject;

import org.eclipse.kura.protocol.can.arrowhead.rest.RestRequestThread.RequestCompletionListener;

public class ArrowheadRestClient {

	private RestRequestThread requestThread;
	private String baseUri;
	
	public ArrowheadRestClient(String baseUri) {
		this.baseUri = baseUri;
		this.requestThread = new RestRequestThread();
	}
	
	public void getEVSEStatus(String evseId, ArrowheadRestResponseListener<EVSEGetStatusResponse> listener) {
		try {
			EVSEGetStatusResponse response = new EVSEGetStatusResponse();
			requestThread.runRequest(this.baseUri + "/evses/" + URLEncoder.encode(evseId, Charset.defaultCharset().name()),
					"GET", 
					null,
					new Parser<EVSEGetStatusResponse>(response, listener));
		} catch (UnsupportedEncodingException e) {
			// ignore
		}
	}
	
	public void requestRechargeAuthorization(String evseId, String userId, int toleranceMs, ArrowheadRestResponseListener<EVSEStatusResponse> listener) {
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("user_id", userId);
			params.put("tolerance", toleranceMs + "");
			
			EVSEStatusResponse response = new EVSEStatusResponse();
			requestThread.runRequest(this.baseUri + "/evses/" + URLEncoder.encode(evseId, Charset.defaultCharset().name()) + "/check",
					"GET", 
					params,
					new Parser<EVSEStatusResponse>(response, listener));
		} catch (UnsupportedEncodingException e) {
			// ignore
		}
	}
	
	public void requestOTFRechargeAuthorization(String evseId, String userId, ArrowheadRestResponseListener<EVSEStatusResponse> listener) {
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("user_id", userId);
			
			EVSEStatusResponse response = new EVSEStatusResponse();
			requestThread.runRequest(this.baseUri + "/evses/" + URLEncoder.encode(evseId, Charset.defaultCharset().name()) + "/treservations/onthefly",
					"GET", 
					params,
					new Parser<EVSEStatusResponse>(response, listener));
		} catch (UnsupportedEncodingException e) {
			// ignore
		}
	}
	
	public enum RechargeStatus {RECHARGE_STARTED, RECHARGE_STOPPED};
	
	public void notifyRechargeStateChange(String evseId, RechargeStatus newStatus, String reservationId, ArrowheadRestResponseListener<EVSEStatusResponse> listener) {
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("status", (newStatus == RechargeStatus.RECHARGE_STARTED ? "start" : "stop"));
			params.put("reservation", reservationId);
			
			EVSEStatusResponse response = new EVSEStatusResponse();
			requestThread.runRequest(this.baseUri + "/evses/" + URLEncoder.encode(evseId, Charset.defaultCharset().name()),
					"PUT", 
					params,
					new Parser<EVSEStatusResponse>(response, listener));
		} catch (UnsupportedEncodingException e) {
			// ignore
		}
	}
	
	public void close() {
		this.requestThread.interrupt();
	}
	
	public interface ArrowheadRestResponseListener<T> {
		public void onResponse(T data);
	}
	
	private class Parser<T extends ArrowheadRestResponse> implements RequestCompletionListener {

		T response;
		ArrowheadRestResponseListener<T> listener;
		
		public Parser (T response, ArrowheadRestResponseListener<T> listener) {
			this.response = response;
			this.listener = listener;
		}
		
		@Override
		public void onCompleted(int status, JsonObject responseObject) {
			if (status / 100 != 2) {
				listener.onResponse(null);
				return;
			}
			response.init(responseObject);
			listener.onResponse(response);
		}
	} 
	
	private interface ArrowheadRestResponse {
		public void init(JsonObject object);
	}
	
	public class EVSEGetStatusResponse implements ArrowheadRestResponse {
		private boolean isReservedNow = false;
		private long nextReservationIn = -1;
		private String nextUser;
		
		public boolean isReservedNow() {
			return isReservedNow;
		}
		
		public long getNextReservationMs() {
			return nextReservationIn;
		}
		
		public String getNextUser() {
			return nextUser;
		}

		@Override
		public void init(JsonObject object) {
			if (!object.isNull("isReservedNow"))
				this.isReservedNow = object.getBoolean("isReservedNow");
			
			if (!object.isNull("nextUser"))
				this.nextUser = object.getString("nextUser");
			
			if (!object.isNull("nextReservationIn"))
				nextReservationIn = object.getJsonNumber("nextReservationIn").bigIntegerValue().longValue();
		}
	}
	
	public class EVSEStatusResponse implements ArrowheadRestResponse {
		
		private boolean status;
		
		public boolean getStatus() {
			return status;
		}

		@Override
		public void init(JsonObject object) {
			if (!object.isNull("status"))
				this.status = object.getBoolean("status");
		}
	}
}
