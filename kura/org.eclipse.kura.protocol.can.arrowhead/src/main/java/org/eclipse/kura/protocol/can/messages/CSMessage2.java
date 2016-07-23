package org.eclipse.kura.protocol.can.messages;

import org.eclipse.kura.protocol.can.CanMessage;
import org.eclipse.kura.protocol.can.arrowhead.CanSocketTest;
import org.eclipse.kura.protocol.can.cs.data.CSDataSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSMessage2 {
	private static final Logger s_logger = LoggerFactory.getLogger(CanSocketTest.class);
	
	public static void parseCanMessage(CanMessage cm, CSDataSnapshot csReceivedData){
		byte[] b = null;
		b = cm.getData();
		if(b!=null && b.length == 5){
			StringBuilder sb = new StringBuilder("received : ");

			int faultFlag= b[0] & 0x01;
			int rechargeAvailable= (b[0] & 0x02) >> 1;
			int rechargeInProgress= (b[0] & 0x04) >> 2;
			int pvSystemActive= (b[0] & 0x08) >> 3;
			int auxChargerActive= (b[0] & 0x10) >> 4;
			int storageBatteryConcactorSts= (b[0] & 0x20) >> 5;
			int converterConcactorSts= (b[0] & 0x40) >> 6;

			int faultString= b[1];
			int igbtTemperature= b[2];
			int storageBatteryTemperature= b[3];
			int storageBatterySOC= b[4];

			sb.append("Fault flag: " + faultFlag + ", ");
			sb.append("Recharge available: " + rechargeAvailable + ", ");
			sb.append("Recharge in progress: " + rechargeInProgress + ", ");
			sb.append("PV System active: " + pvSystemActive + ", ");
			sb.append("Aux charger active: " + auxChargerActive + ", ");
			sb.append("Storage Battery Concactor Sts: " + storageBatteryConcactorSts + ", ");
			sb.append("Converter Contactor Sts: " + converterConcactorSts + ", ");

			sb.append("Fault string: " + faultString + ", ");
			sb.append("IGBT Temperature: " + igbtTemperature + " celsius, ");
			sb.append("Storage Battery Temperature: " + storageBatteryTemperature + " celsius, ");
			sb.append("Storage Battery SOC: " + storageBatterySOC + "celsius");

			sb.append(" on id = ");
			sb.append(cm.getCanId());			
			s_logger.debug(sb.toString());

			csReceivedData.setRechargeAvail(rechargeAvailable);
			csReceivedData.setRechargeInProgress(rechargeInProgress);
			csReceivedData.setPVSystemActive(pvSystemActive);
			csReceivedData.setAuxChargerActive(auxChargerActive);
			csReceivedData.setStorageBatterySts(storageBatteryConcactorSts);
			csReceivedData.setConverterContactorSts(converterConcactorSts);
			csReceivedData.setIGBTTemp(igbtTemperature);
			csReceivedData.setStorageBatteryTemp(storageBatteryTemperature);
			csReceivedData.setStorageBatterySOC(storageBatterySOC);
		}
	}

}
