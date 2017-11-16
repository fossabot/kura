package org.eclipse.kura.driver;

public class DriverDescriptor {

    String pid;
    String factoryPid;
    Object channelDescriptor;

    public String getPid() {
        return pid;
    }

    public String getFactoryPid() {
        return factoryPid;
    }

    public Object getChannelDescriptor() {
        return channelDescriptor;
    }
}
