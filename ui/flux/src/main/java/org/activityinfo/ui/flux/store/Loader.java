package org.activityinfo.ui.flux.store;

public interface Loader  {


    public LoadingStatus getStatus();

    public Throwable getLoadingException();


}
