package org.activityinfo.ui.vdom.client.flux.store;

public interface Loader  {


    public LoadingStatus getStatus();

    public Throwable getLoadingException();


}
