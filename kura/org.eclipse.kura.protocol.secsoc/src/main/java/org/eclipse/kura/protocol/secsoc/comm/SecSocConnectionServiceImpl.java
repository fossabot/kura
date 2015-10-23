package org.eclipse.kura.protocol.secsoc.comm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.kura.KuraException;
import org.eclipse.kura.comm.CommConnection;
import org.eclipse.kura.comm.CommURI;
import org.eclipse.kura.protocol.secsoc.message.SecSocMessage;
import org.osgi.service.io.ConnectionFactory;

public class SecSocConnectionServiceImpl implements SecSocConnectionService{

	private CommConnection m_connection;
	private InputStream m_iStream;
	private OutputStream m_oStream;

	@Override
	public void openSecSocConnection(SerialInterfaceParameters serialParams,
			ConnectionFactory connectionFactory) throws IOException {
		String device= serialParams.getDevice();
		int baudRate= serialParams.getBaudrate();
		int dataBits= serialParams.getDataBits();
		int stopBits= serialParams.getStopBits();
		int parity= serialParams.getParity();
		int timeout= serialParams.getTimeout();

		String uri = new CommURI.Builder(device).withBaudRate(baudRate).withDataBits(dataBits).withStopBits(stopBits).withParity(parity).withTimeout(timeout).build().toString();
		m_connection = (CommConnection) connectionFactory.createConnection(uri, 1, false);
		m_iStream = m_connection.openInputStream();
		m_oStream = m_connection.openOutputStream();
	}

	@Override
	public void closeSecSocConnection() throws IOException {
		if(m_iStream != null){
			m_iStream.close();
		}
		if(m_oStream != null){
			m_oStream.close();
		}
		m_iStream = null;
		m_oStream = null;

		if(m_connection != null){
			m_connection.close();
		}
		m_connection = null;
	}

	@Override
	public void sendSecSocMessage(byte[] message)
			throws KuraException, IOException {

	}

	@Override
	public SecSocMessage receiveSecSocMessage()
			throws KuraException, IOException {
		// TODO Auto-generated method stub
		boolean echo= false;
		
		if (m_iStream != null) {
			

			int c = -1;
			StringBuilder sb = new StringBuilder();

			while (m_iStream != null) {

				if (m_iStream.available() != 0) {
					c = m_iStream.read();
				} else {
					try {
						Thread.sleep(100);
						continue;
					} catch (InterruptedException e) {
						return null;
					}
				}
				
				if (echo && m_oStream != null) {
					m_oStream.write((char) c);
				}

				
				
				if (true) { //ended message

					SecSocMessage ssm= new SecSocMessage(messageData);

					ssm.parseHeader();
					ssm.parseBody();

					sb = new StringBuilder();

				} else if (c!=10) {
					sb.append((byte) c);
				}					
			}
		} 

		return null;
	}
}
