package com.eurotech.example.modbus.slave;

import java.util.Map;
import java.util.Properties;

import org.eclipse.kura.configuration.ConfigurableComponent;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.io.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModbusSlaveDevice implements ConfigurableComponent, ModbusSlaveDeviceService{
	private static final Logger s_logger = LoggerFactory.getLogger(ModbusSlaveDevice.class);

	public final String POWER_OUT= "power.out";
	public final String TIME_TO_RECHARGE= "time.to.recharge";
	public final String ENERGY_OUT= "energy.out";
	public final String FAULT_FLAG= "fault.flag";
	public final String RECHARGE_AVAILABLE= "recharge.available";
	public final String RECHARGE_IN_PROGRESS= "recharge.in.progress";
	public final String PV_SYSTEM_ACTIVE= "pv.system.active";
	public final String AUX_CHARGER_ACTIVE= "aux.charger.active";
	public final String STORAGE_BATTERY_CONTACTOR_STATUS= "storage.battery.contactor.status";
	public final String CONVERTER_CONTACTOR_STATUS= "converter.contactor.status";
	public final String FAULT_STRING_1= "fault.string.1";
	public final String FAULT_STRING_2= "fault.string.2";
	public final String IGBT_TEMP= "igbt.temp";
	public final String STORAGE_TEMP= "storage.temp";
	public final String STORAGE_BATTERY_SOC= "storage.battery.soc";
	public final String V_OUT= "v.out";
	public final String STORAGE_BATTERY_V= "storage.battery.v";
	public final String PV_SYSTEM_V= "pv.system.v";
	public final String I_OUT= "i.out";
	public final String STORAGE_BATTERY_I = "storage.battery.i";

	private ModbusProtocolSlaveComm m_communication;
	private ModbusSlavePreferences m_slavePrefs;

	private ConnectionFactory m_connectionFactory;

	private Thread t;

	private Map<String, Object> m_properties;

	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.m_connectionFactory = connectionFactory;
	}

	public void unsetConnectionFactory(ConnectionFactory connectionFactory) {
		this.m_connectionFactory = null;
	}

	protected void activate(ComponentContext componentContext, Map<String,Object> properties) 
	{
		s_logger.info("activate...");
		m_properties = properties;
		m_slavePrefs = getSlavePrefs();

		try {
			m_communication= new ModbusProtocolSlaveComm(m_slavePrefs);
			connect();
		} catch (ModbusProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void deactivate(ComponentContext componentContext) 
	{
		s_logger.info("deactivate...");
		try {
			disconnect();
		} catch (ModbusProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updated(Map<String,Object> properties)
	{
		s_logger.info("updated...");		
		m_properties = properties;
		m_slavePrefs = getSlavePrefs();

		try {
			disconnect();
			m_communication= new ModbusProtocolSlaveComm(m_slavePrefs);
			connect();
		} catch (ModbusProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	


	@Override
	public String getProtocolName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void configureConnection(Properties connectionConfig)
			throws ModbusProtocolException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getConnectStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void connect() throws ModbusProtocolException {
		m_communication.connect();
		t= new Thread(m_communication);
		t.start();
	}

	@Override
	public void disconnect() throws ModbusProtocolException {
		// TODO Auto-generated method stub
		t.interrupt();
		m_communication.disconnect();
		m_communication= null;
	}

	private ModbusSlavePreferences getSlavePrefs() {
		int powerOut = Integer.parseInt((String) m_properties.get(POWER_OUT));
		int timeToRecharge = Integer.parseInt((String) m_properties.get(TIME_TO_RECHARGE));
		int energyOut = Integer.parseInt((String) m_properties.get(ENERGY_OUT));
		boolean faultFlag = Integer.parseInt((String) m_properties.get(FAULT_FLAG)) == 1 ? true: false;
		boolean rechargeAvailable = Integer.parseInt((String) m_properties.get(RECHARGE_AVAILABLE)) == 1 ? true: false;
		boolean rechargeInProgress = Integer.parseInt((String) m_properties.get(RECHARGE_IN_PROGRESS)) == 1 ? true: false;
		boolean pvSystemActive = Integer.parseInt((String) m_properties.get(PV_SYSTEM_ACTIVE)) == 1 ? true: false;
		boolean auxChargerActive = Integer.parseInt((String) m_properties.get(AUX_CHARGER_ACTIVE)) == 1 ? true: false;
		boolean storageBatteryContactorStatus = Integer.parseInt((String) m_properties.get(STORAGE_BATTERY_CONTACTOR_STATUS)) == 1 ? true: false;
		boolean converterContactorStatus = Integer.parseInt((String) m_properties.get(CONVERTER_CONTACTOR_STATUS)) == 1 ? true: false;
		int faultString1 = Integer.parseInt((String) m_properties.get(FAULT_STRING_1));
		int faultString2 = Integer.parseInt((String) m_properties.get(FAULT_STRING_2));
		int igbtTemp = Integer.parseInt((String)  m_properties.get(IGBT_TEMP));
		int storageTemp = Integer.parseInt((String)  m_properties.get(STORAGE_TEMP));
		int storageBatterySoc = Integer.parseInt((String)  m_properties.get(STORAGE_BATTERY_SOC));
		int vOut = Integer.parseInt((String)  m_properties.get(V_OUT));
		int storageBatteryV = Integer.parseInt((String)  m_properties.get(STORAGE_BATTERY_V));
		int pvSystemV = Integer.parseInt((String)  m_properties.get(PV_SYSTEM_V));
		int iOut = Integer.parseInt((String) m_properties.get(I_OUT));
		int storageBatteryI = Integer.parseInt((String) m_properties.get(STORAGE_BATTERY_I));
		ModbusSlavePreferences prefs= new ModbusSlavePreferences();
		prefs.setPowerOut(powerOut);
		prefs.setTimeToRecharge(timeToRecharge);
		prefs.setEnergyOut(energyOut);
		prefs.setFaultFlag(faultFlag);
		prefs.setRechargeAvailable(rechargeAvailable);
		prefs.setRechargeInProgress(rechargeInProgress);
		prefs.setPvSystemActive(pvSystemActive);
		prefs.setAuxChargerActive(auxChargerActive);
		prefs.setStorageBatteryContactorStatus(storageBatteryContactorStatus);
		prefs.setConverterContactorStatus(converterContactorStatus);
		prefs.setFaultString1(faultString1);
		prefs.setFaultString2(faultString2);
		prefs.setIgbtTemp(igbtTemp);
		prefs.setStorageTemp(storageTemp);
		prefs.setStorageBatterySoc(storageBatterySoc);
		prefs.setvOut(vOut);
		prefs.setStorageBatteryV(storageBatteryV);
		prefs.setPvSystemV(pvSystemV);
		prefs.setiOut(iOut);
		prefs.setStorageBatteryI(storageBatteryI);

		return prefs;
	}

}
