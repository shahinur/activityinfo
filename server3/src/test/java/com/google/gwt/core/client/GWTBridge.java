package com.google.gwt.core.client;

import com.google.gwt.core.server.ServerGwtBridge;
import org.easymock.EasyMock;

import static org.easymock.EasyMock.replay;

/**
 * Stub for client-side GWTBridge
 */
public class GWTBridge extends com.google.gwt.core.shared.GWTBridge {

    public static void init() {
        GWT.setBridge(new GWTBridge());
    }

    @Override
    public <T> T create(Class<?> aClass) {
        T mock = (T)EasyMock.createNiceMock(aClass.getSimpleName(), aClass);
        replay(mock);
        return mock;
    }

    @Override
    public String getVersion() {
        return ServerGwtBridge.getInstance().getVersion();
    }

    @Override
    public boolean isClient() {
        return true;
    }

    @Override
    public void log(String s, Throwable throwable) {
        System.out.print("LOG: " + s);
        if(throwable != null) {
            throwable.printStackTrace();
        }
    }
}
